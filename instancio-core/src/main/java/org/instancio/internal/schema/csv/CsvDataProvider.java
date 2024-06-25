/*
 * Copyright 2022-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.instancio.internal.schema.csv;

import org.instancio.Random;
import org.instancio.generator.Generator;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.generator.misc.NullGenerator;
import org.instancio.internal.generator.misc.SupplierBackedGenerator;
import org.instancio.internal.schema.AbstractSchemaDataProvider;
import org.instancio.internal.schema.DataStore;
import org.instancio.internal.schema.SchemaContext;
import org.instancio.internal.schema.SpecMethod;
import org.instancio.internal.schema.order.DataAccessStrategy;
import org.instancio.internal.schema.order.RandomDataAccessStrategy;
import org.instancio.internal.schema.order.SequentialDataAccessStrategy;
import org.instancio.internal.util.Fail;
import org.instancio.internal.util.ReflectionUtils;
import org.instancio.internal.util.StringUtils;
import org.instancio.schema.CombinatorMethod;
import org.instancio.schema.CombinatorProvider;
import org.instancio.schema.DerivedSpec;
import org.instancio.schema.PostProcessor;
import org.instancio.schema.SchemaSpec;
import org.instancio.schema.TemplateSpec;
import org.instancio.schema.WithPostProcessor;
import org.instancio.settings.Keys;
import org.instancio.settings.SchemaDataAccess;
import org.instancio.settings.Settings;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.instancio.internal.util.ErrorMessageUtils.errorInvokingCombinatorMethod;
import static org.instancio.internal.util.ErrorMessageUtils.invalidCombinatorMethod;
import static org.instancio.internal.util.ErrorMessageUtils.schemaComponentMethodNotFound;
import static org.instancio.internal.util.ErrorMessageUtils.schemaWithInvalidMethodName;
import static org.instancio.internal.util.ObjectUtils.defaultIfNull;
import static org.instancio.internal.util.ReflectionUtils.getMethodsAnnotated;
import static org.instancio.internal.util.ReflectionUtils.getZeroArgMethod;
import static org.instancio.internal.util.ReflectionUtils.setAccessible;

@SuppressWarnings({"PMD.GodClass", "PMD.ExcessiveImports"})
public class CsvDataProvider extends AbstractSchemaDataProvider {

    private final CsvDataStore dataStore;
    private final Class<?> schemaClass;
    private final PropertyBitSet propertyBitSet;
    private DataAccessStrategy dataAccessStrategy;
    private String[] currentEntry;

    public CsvDataProvider(
            final SchemaContext<?> schemaContext,
            final DataStore<?> dataStore) {

        super(schemaContext);
        this.dataStore = (CsvDataStore) dataStore;
        this.schemaClass = schemaContext.getSchemaClass();
        this.propertyBitSet = new PropertyBitSet();
    }

    @Override
    public Collection<String> getDataProperties() {
        return dataStore.getPropertyKeys();
    }

    @Override
    public <T> SchemaSpec<T> createSpec(final SpecMethod specMethod, final Object[] args) {
        return createSpec(specMethod, args, true);
    }

    private <T> SchemaSpec<T> createSpec(
            final SpecMethod specMethod,
            final Object[] args,
            final boolean updateBitSet) {

        if (getGeneratorContext().random().diceRoll(specMethod.hasNullableAnnotation())) {
            return new NullGenerator<>(getGeneratorContext());
        }

        final Supplier<T> resultSupplier = getSupplier(specMethod, args, updateBitSet);
        final List<PostProcessor<T>> postProcessors = getPostProcessors(specMethod);
        final Supplier<T> supplier = postProcessors.isEmpty() ? resultSupplier : () -> {
            T processed = resultSupplier.get();
            for (PostProcessor<T> p : postProcessors) {
                processed = p.process(processed, getGeneratorContext().random());
            }
            return processed;
        };

        return new SupplierBackedGenerator<>(getGeneratorContext(), supplier);
    }

    @Override
    public <T> SchemaSpec<T> resolveSpec(final String propertyName) {
        final Method method = defaultIfNull(getZeroArgMethod(schemaClass, propertyName),
                () -> ReflectionUtils.getDataSpecMethod(schemaClass, propertyName));

        if (method != null) {
            return createSpec(new SpecMethod(method), null);
        }

        final boolean updateBitSet = true;
        final Supplier<?> resultSupplier = () -> updateState(propertyName, updateBitSet)
                .getPropertyValue(propertyName, String.class);

        return (SchemaSpec<T>) new SupplierBackedGenerator<>(getGeneratorContext(), resultSupplier);
    }

    private <T> SchemaSpec<T> getSchemaSpec(final SpecMethod specMethod) {
        return createSpec(specMethod, /* args = */ null, /* updateBitSet */ false);
    }

    @SuppressWarnings("unchecked")
    private <T> Supplier<T> getSupplier(
            final SpecMethod specMethod,
            final Object[] args,
            final boolean updateBitSet) {

        if (specMethod.isDeclaredBySchemaInterface()) {
            final String propertyNameArg = (String) ApiValidator.notNull(
                    args[0], "'propertyName' must not be null");

            if (args.length == 1) {
                // Handle Schema methods with 1 parameter, e.g.
                // SchemaSpec<Long> longSpec(String propertyName)

                return () -> updateState(propertyNameArg, updateBitSet)
                        .getPropertyValue(propertyNameArg, specMethod.getTargetType());

            } else if (args.length == 2) {
                // Handle Schema methods with 2 parameters:
                // - <T> SchemaSpec<T> spec(String propertyName, Function<String, T> converter)
                // - <T> SchemaSpec<T> spec(String propertyName, Class<T> targetType)

                if (args[1] instanceof Function) {
                    final Function<String, T> converter = (Function<String, T>)
                            ApiValidator.notNull(args[1], "'converter' function must not be null");

                    return () -> converter.apply(
                            updateState(propertyNameArg, updateBitSet)
                                    .getPropertyValue(propertyNameArg, String.class));

                } else {
                    final Class<T> targetTypeParameter = (Class<T>)
                            ApiValidator.notNull(args[1], "'targetType' must not be null");

                    return () -> updateState(propertyNameArg, updateBitSet)
                            .getPropertyValue(propertyNameArg, targetTypeParameter);
                }
            }
        }

        // Handle user-defined spec methods define in a Schema subclass
        final String key = specMethod.getDataPropertyName();

        if (specMethod.getDataSpec() != null) {
            return () -> updateState(key, updateBitSet)
                    .getPropertyValue(key, specMethod.getTargetType());
        }
        if (specMethod.getGeneratedSpec() != null) {
            return () -> updateState(key, updateBitSet)
                    .getGeneratedPropertyValue(specMethod.getGeneratedSpec().value());
        }
        if (specMethod.getDerivedSpec() != null) {
            return () -> updateState(key, updateBitSet)
                    .getCombinedPropertyValue(specMethod, specMethod.getDerivedSpec());
        }
        if (specMethod.getTemplateSpec() != null) {
            return () -> updateState(key, updateBitSet)
                    .getDerivedPropertyValue(specMethod.getTemplateSpec());
        }
        return () -> updateState(key, updateBitSet)
                .getPropertyValue(key, specMethod.getTargetType());
    }

    private CsvDataProvider updateState(final String propertyName, final boolean updateBitSet) {
        if (currentEntry == null) {
            currentEntry = getNext();
        }
        if (updateBitSet) {
            if (propertyBitSet.get(propertyName)) {
                propertyBitSet.clear();
                currentEntry = getNext();
            }
            propertyBitSet.set(propertyName);
        }
        return this;
    }

    private static <T> List<PostProcessor<T>> getPostProcessors(final SpecMethod specMethod) {
        final WithPostProcessor withPostProcessor = specMethod.getAnnotation(WithPostProcessor.class);

        if (withPostProcessor == null) {
            return Collections.emptyList();
        }

        final Class<?>[] processorClasses = withPostProcessor.value();
        final List<PostProcessor<T>> list = new ArrayList<>(processorClasses.length);
        for (Class<?> c : processorClasses) {
            list.add((PostProcessor<T>) ReflectionUtils.newInstance(c));
        }
        return list;
    }

    private <T> T getPropertyValue(final String propertyKey, final Class<T> valueType) {
        final int index = dataStore.indexOf(propertyKey);
        if (index == -1) {
            throw Fail.withUsageError(schemaWithInvalidMethodName(
                    schemaClass, propertyKey, dataStore.getPropertyKeys()));
        }
        return StringConverters.mapValue(currentEntry[index], valueType);
    }

    @SuppressWarnings("unchecked")
    private <T> T getGeneratedPropertyValue(final Class<?> generatorClass) {
        final Generator<T> generator = (Generator<T>) ReflectionUtils.newInstance(generatorClass);
        return generator.generate(getGeneratorContext().random());
    }

    private <T> T getCombinedPropertyValue(
            final SpecMethod parentSpecMethod,
            final DerivedSpec derivedSpec) {

        final Class<? extends CombinatorProvider> providerClass = derivedSpec.by();
        final Method combinatorMethod = resolveCombinatorMethod(derivedSpec, providerClass);
        final Class<?>[] combinatorParams = combinatorMethod.getParameterTypes();
        final boolean declaresRandom = combinatorParams[combinatorParams.length - 1] == Random.class;
        final String[] componentProperties = derivedSpec.fromSpec();
        final int argsLength = declaresRandom ? componentProperties.length + 1 : componentProperties.length;
        final Object[] combinatorArgs = new Object[argsLength];

        if (declaresRandom) {
            combinatorArgs[argsLength - 1] = getGeneratorContext().random();
        }

        for (int i = 0; i < componentProperties.length; i++) {
            final String component = componentProperties[i];
            final Method method = getZeroArgMethod(schemaClass, component);

            if (method == null) {
                throw Fail.withUsageError(schemaComponentMethodNotFound(schemaClass, parentSpecMethod));
            }

            final SpecMethod specMethod = new SpecMethod(method);
            final Object arg = dataStore.contains(component)
                    ? getPropertyValue(component, specMethod.getTargetType())
                    : getSchemaSpec(specMethod).get();

            combinatorArgs[i] = arg;
        }

        try {
            final CombinatorProvider provider = ReflectionUtils.newInstance(providerClass);
            return (T) setAccessible(combinatorMethod).invoke(provider, combinatorArgs);
        } catch (IllegalArgumentException ex) {
            throw Fail.withUsageError(errorInvokingCombinatorMethod(
                    schemaClass, providerClass, combinatorMethod, ex), ex);
        } catch (Exception ex) {
            throw Fail.withFataInternalError("Error invoking @CombinatorMethod: %s", combinatorMethod, ex);
        }
    }

    @NotNull
    private Method resolveCombinatorMethod(
            final DerivedSpec derivedSpec,
            final Class<?> providerClass) {

        final List<Method> combinatorMethods = getMethodsAnnotated(
                providerClass, CombinatorMethod.class);

        if (combinatorMethods.size() == 1) {
            return combinatorMethods.get(0);
        }
        for (Method method : combinatorMethods) {
            if (method.getName().equals(derivedSpec.method())) {
                return method;
            }
        }
        throw Fail.withUsageError(invalidCombinatorMethod(schemaClass, providerClass));
    }

    @SuppressWarnings("unchecked")
    private <T> T getDerivedPropertyValue(final TemplateSpec templateSpec) {
        String pattern = templateSpec.value();
        final List<String> components = StringUtils.getTemplateKeys(pattern);

        for (String component : components) {

            final String value;
            if (dataStore.contains(component)) {
                value = getPropertyValue(component, String.class);
            } else {
                final Method method = getZeroArgMethod(schemaClass, component);
                value = (String) getSchemaSpec(new SpecMethod(method)).get();
            }

            pattern = pattern.replace("${" + component + "}", value);
        }
        return (T) pattern;
    }

    private String[] getNext() {
        final String tag = selectTag(dataStore.getAvailableTags());
        final List<String[]> tagData = dataStore.get(tag);
        ApiValidator.notNull(tagData, () -> String.format("no data found with tag value: '%s'", tag));

        lazyInitDataAccessStrategy(tagData);

        final int nextIndex = dataAccessStrategy.nextIndex();
        return tagData.get(nextIndex);
    }

    private void lazyInitDataAccessStrategy(final List<String[]> tagData) {
        if (dataAccessStrategy == null) {
            final Settings settings = getGeneratorContext().getSettings();
            final SchemaDataAccess access = settings.get(Keys.SCHEMA_DATA_ACCESS);

            if (access == SchemaDataAccess.RANDOM) {
                dataAccessStrategy = new RandomDataAccessStrategy(getGeneratorContext().random(), tagData.size());
            } else {
                dataAccessStrategy = new SequentialDataAccessStrategy(schemaClass, tagData.size(), settings);
            }
        }
    }
}
