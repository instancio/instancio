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
package org.instancio.internal.feed.csv;

import org.instancio.Random;
import org.instancio.feed.FeedSpec;
import org.instancio.feed.FeedSpecAnnotations.FunctionSpec;
import org.instancio.feed.FeedSpecAnnotations.TemplateSpec;
import org.instancio.feed.FeedSpecAnnotations.WithPostProcessor;
import org.instancio.feed.FunctionProvider;
import org.instancio.feed.PostProcessor;
import org.instancio.generator.Generator;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.feed.AbstractFeed;
import org.instancio.internal.feed.DataStore;
import org.instancio.internal.feed.InternalFeedContext;
import org.instancio.internal.feed.SpecMethod;
import org.instancio.internal.feed.order.DataAccessStrategy;
import org.instancio.internal.feed.order.RandomDataAccessStrategy;
import org.instancio.internal.feed.order.SequentialDataAccessStrategy;
import org.instancio.internal.generator.misc.NullGenerator;
import org.instancio.internal.generator.misc.SupplierBackedGenerator;
import org.instancio.internal.util.Fail;
import org.instancio.internal.util.ReflectionUtils;
import org.instancio.internal.util.StringUtils;
import org.instancio.settings.FeedDataAccess;
import org.instancio.settings.Settings;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.instancio.internal.util.ErrorMessageUtils.errorInvokingFunctionSpecHandlerMethod;
import static org.instancio.internal.util.ErrorMessageUtils.feedComponentMethodNotFound;
import static org.instancio.internal.util.ErrorMessageUtils.feedWithInvalidMethodName;
import static org.instancio.internal.util.ErrorMessageUtils.invalidFunctionSpecHandlerMethod;
import static org.instancio.internal.util.ReflectionUtils.getZeroArgMethod;
import static org.instancio.internal.util.ReflectionUtils.setAccessible;

@SuppressWarnings({"PMD.GodClass", "PMD.ExcessiveImports"})
public class CsvFeed extends AbstractFeed {

    private static final boolean UPDATE_BITSET = true;

    private final CsvDataStore dataStore;
    private final Class<?> feedClass;
    private final PropertyBitSet propertyBitSet;
    private DataAccessStrategy dataAccessStrategy;
    private String[] currentEntry;

    public CsvFeed(
            final InternalFeedContext<?> feedContext,
            final DataStore<?> dataStore) {

        super(feedContext);
        this.dataStore = (CsvDataStore) dataStore;
        this.feedClass = feedContext.getFeedClass();
        this.propertyBitSet = new PropertyBitSet();
    }

    @Override
    public Set<String> getDataProperties() {
        return dataStore.getPropertyKeys();
    }

    @Override
    public <T> FeedSpec<T> createSpec(final SpecMethod specMethod, final Object[] args) {
        return createSpecInternal(specMethod, args, UPDATE_BITSET);
    }

    @Override
    public <T> FeedSpec<T> createSpec(final String propertyName, final Class<T> targetType) {
        final Supplier<T> supplier = createDataSpecSupplier(propertyName, targetType, UPDATE_BITSET);
        return createSpecInternal(supplier, Collections.emptyList(), false);
    }

    private <T> FeedSpec<T> createSpecInternal(
            final SpecMethod specMethod,
            final Object[] args,
            final boolean updateBitSet) {

        final Supplier<T> resultSupplier = getSupplier(specMethod, args, updateBitSet);
        final List<PostProcessor<T>> postProcessors = getPostProcessors(specMethod);
        return createSpecInternal(resultSupplier, postProcessors, specMethod.hasNullableAnnotation());
    }

    private <T> FeedSpec<T> createSpecInternal(
            final Supplier<T> resultSupplier,
            final List<PostProcessor<T>> postProcessors,
            final boolean isNullable) {

        if (getGeneratorContext().random().diceRoll(isNullable)) {
            return new NullGenerator<>(getGeneratorContext());
        }

        final Supplier<T> supplier = postProcessors.isEmpty() ? resultSupplier : () -> {
            T processed = resultSupplier.get();
            for (PostProcessor<T> p : postProcessors) {
                processed = p.process(processed, getGeneratorContext().random());
            }
            return processed;
        };

        return new SupplierBackedGenerator<>(getGeneratorContext(), supplier);
    }

    private <T> FeedSpec<T> createFeedSpecInternal(final SpecMethod specMethod) {
        return createSpecInternal(specMethod, /* args = */ null, !UPDATE_BITSET);
    }

    @SuppressWarnings("unchecked")
    private <T> Supplier<T> getSupplier(
            final SpecMethod specMethod,
            final Object[] args,
            final boolean updateBitSet) {

        if (specMethod.isDeclaredByFeedInterface()) {
            final String propertyNameArg = (String) ApiValidator.notNull(
                    args[0], "'propertyName' must not be null");

            if (args.length == 1) {
                // Handle Feed methods with 1 parameter, e.g.
                // FeedSpec<Long> longSpec(String propertyName)

                return () -> updateState(propertyNameArg, updateBitSet)
                        .getDataSpecValue(propertyNameArg, specMethod.getTargetType());

            } else if (args.length == 2) {
                // Handle Feed methods with 2 parameters:
                // - <T> FeedSpec<T> spec(String propertyName, Function<String, T> converter)
                // - <T> FeedSpec<T> spec(String propertyName, Class<T> targetType)

                if (args[1] instanceof Function) {
                    final Function<String, T> converter = (Function<String, T>)
                            ApiValidator.notNull(args[1], "'converter' function must not be null");

                    return () -> converter.apply(
                            updateState(propertyNameArg, updateBitSet)
                                    .getDataSpecValue(propertyNameArg, String.class));

                } else {
                    final Class<T> targetTypeParameter = (Class<T>)
                            ApiValidator.notNull(args[1], "'targetType' must not be null");

                    return () -> updateState(propertyNameArg, updateBitSet)
                            .getDataSpecValue(propertyNameArg, targetTypeParameter);
                }
            }
        }

        // Handle user-defined spec methods define in a Feed subclass
        final String key = specMethod.getDataPropertyName();

        if (specMethod.getDataSpec() != null) {
            return () -> updateState(key, updateBitSet)
                    .getDataSpecValue(key, specMethod.getTargetType());
        }
        if (specMethod.getGeneratedSpec() != null) {
            return () -> updateState(key, updateBitSet)
                    .getGeneratedSpecValue(specMethod.getGeneratedSpec().value());
        }
        if (specMethod.getFunctionSpec() != null) {
            return () -> updateState(key, updateBitSet)
                    .getFunctionSpecValue(specMethod, specMethod.getFunctionSpec());
        }
        if (specMethod.getTemplateSpec() != null) {
            return () -> updateState(key, updateBitSet)
                    .getTemplateSpecValue(specMethod.getTemplateSpec());
        }
        return () -> updateState(key, updateBitSet)
                .getDataSpecValue(key, specMethod.getTargetType());
    }

    private <T> Supplier<T> createDataSpecSupplier(
            final String propertyName,
            final Class<T> targetType,
            final boolean updateBitSet) {

        return () -> updateState(propertyName, updateBitSet)
                .getDataSpecValue(propertyName, targetType);
    }

    private CsvFeed updateState(final String propertyName, final boolean updateBitSet) {
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

    private <T> T getDataSpecValue(final String propertyKey, final Class<T> valueType) {
        final int index = dataStore.indexOf(propertyKey);
        if (index == -1) {
            throw Fail.withUsageError(feedWithInvalidMethodName(
                    feedClass, propertyKey, dataStore.getPropertyKeys()));
        }
        if (index < currentEntry.length) {
            return StringConverters.mapValue(currentEntry[index], valueType);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private <T> T getGeneratedSpecValue(final Class<?> generatorClass) {
        final Generator<T> generator = (Generator<T>) ReflectionUtils.newInstance(generatorClass);
        return generator.generate(getGeneratorContext().random());
    }

    private <T> T getFunctionSpecValue(
            final SpecMethod parentSpecMethod,
            final FunctionSpec functionSpec) {

        final Class<? extends FunctionProvider> providerClass = functionSpec.provider();
        final Method functionSpecHandler = resolveFunctionSpecHandler(parentSpecMethod, providerClass);
        final Class<?>[] params = functionSpecHandler.getParameterTypes();
        final boolean declaresRandom = params[params.length - 1] == Random.class;
        final String[] componentProperties = functionSpec.params();
        final int argsLength = declaresRandom ? componentProperties.length + 1 : componentProperties.length;
        final Object[] args = new Object[argsLength];

        if (declaresRandom) {
            args[argsLength - 1] = getGeneratorContext().random();
        }

        for (int i = 0; i < componentProperties.length; i++) {
            final String component = componentProperties[i];
            final Method method = getZeroArgMethod(feedClass, component);

            if (method == null) {
                throw Fail.withUsageError(feedComponentMethodNotFound(feedClass, parentSpecMethod));
            }

            final SpecMethod specMethod = new SpecMethod(method);
            final Object arg = dataStore.contains(component)
                    ? getDataSpecValue(component, specMethod.getTargetType())
                    : createFeedSpecInternal(specMethod).get();

            args[i] = arg;
        }

        try {
            final FunctionProvider provider = ReflectionUtils.newInstance(providerClass);
            return (T) setAccessible(functionSpecHandler).invoke(provider, args);
        } catch (IllegalArgumentException ex) {
            throw Fail.withUsageError(errorInvokingFunctionSpecHandlerMethod(
                    feedClass, providerClass, parentSpecMethod, functionSpecHandler, ex), ex);
        } catch (Exception ex) {
            throw Fail.withFataInternalError("Error invoking method: %s", functionSpecHandler, ex);
        }
    }

    @NotNull
    private Method resolveFunctionSpecHandler(
            final SpecMethod parentSpecMethod,
            final Class<?> providerClass) {

        Method result = null;
        for (Method method : providerClass.getDeclaredMethods()) {
            // ignore private methods as these could be helper methods
            if (Modifier.isPrivate(method.getModifiers())) {
                continue;
            }
            if (result == null) {
                result = method;
            } else {
                // fail if multiple methods are declared
                throw Fail.withUsageError(invalidFunctionSpecHandlerMethod(
                        feedClass, providerClass, parentSpecMethod));
            }
        }
        if (result == null) {
            throw Fail.withUsageError(invalidFunctionSpecHandlerMethod(
                    feedClass, providerClass, parentSpecMethod));
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private <T> T getTemplateSpecValue(final TemplateSpec templateSpec) {
        String template = templateSpec.value();
        final List<String> components = StringUtils.getTemplateKeys(template);

        for (String component : components) {
            final String value;
            if (dataStore.contains(component)) {
                value = getDataSpecValue(component, String.class);
            } else {
                final Method method = getZeroArgMethod(feedClass, component);
                value = (String) createFeedSpecInternal(new SpecMethod(method)).get();
            }
            final String replacement = value == null ? "" : value;
            template = template.replace("${" + component + "}", replacement);
        }
        return (T) template;
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
            final FeedDataAccess access = getFeedContext().getFeedDataAccess();

            dataAccessStrategy = access == FeedDataAccess.RANDOM
                    ? new RandomDataAccessStrategy(getGeneratorContext().random(), tagData.size())
                    : new SequentialDataAccessStrategy(feedClass, tagData.size(), settings);
        }
    }
}
