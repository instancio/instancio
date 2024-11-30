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
package org.instancio.internal.feed;

import org.instancio.Random;
import org.instancio.exception.InstancioApiException;
import org.instancio.feed.FeedSpec;
import org.instancio.feed.FeedSpecAnnotations;
import org.instancio.feed.FeedSpecAnnotations.TemplateSpec;
import org.instancio.feed.FeedSpecAnnotations.WithPostProcessor;
import org.instancio.feed.FeedSpecAnnotations.WithStringMapper;
import org.instancio.feed.FunctionProvider;
import org.instancio.feed.PostProcessor;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorContext;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.generator.misc.SupplierBackedGenerator;
import org.instancio.internal.util.ErrorMessageUtils;
import org.instancio.internal.util.Fail;
import org.instancio.internal.util.PropertyBitSet;
import org.instancio.internal.util.ReflectionUtils;
import org.instancio.internal.util.StringUtils;
import org.instancio.settings.FeedDataAccess;
import org.instancio.settings.FeedDataEndAction;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.instancio.internal.util.ErrorMessageUtils.errorInvokingFunctionSpecHandlerMethod;
import static org.instancio.internal.util.ErrorMessageUtils.errorMappingStringToTargetType;
import static org.instancio.internal.util.ErrorMessageUtils.feedComponentMethodNotFound;
import static org.instancio.internal.util.ErrorMessageUtils.feedWithInvalidMethodName;
import static org.instancio.internal.util.ErrorMessageUtils.invalidFunctionSpecHandlerMethod;
import static org.instancio.internal.util.ReflectionUtils.getZeroArgMethod;
import static org.instancio.internal.util.ReflectionUtils.setAccessible;
import static org.instancio.internal.util.StringConverters.getConverter;

@SuppressWarnings({"PMD.CouplingBetweenObjects", "PMD.ExcessiveImports", "PMD.GodClass"})
public abstract class AbstractFeed<R> implements InternalFeed {
    private static final boolean UPDATE_BITSET = true;

    private final DataStore<R> dataStore;
    private final InternalFeedContext<?> feedContext;
    private final String tagValue;
    private final GeneratorContext generatorContext;
    private final PropertyBitSet propertyBitSet;
    private final Class<?> feedClass;
    private final AtomicInteger sequentialAccessIndex = new AtomicInteger(-1);
    private R currentEntry;

    protected AbstractFeed(
            final InternalFeedContext<?> feedContext,
            final DataStore<R> dataStore) {

        this.dataStore = dataStore;
        this.feedContext = feedContext;
        this.generatorContext = feedContext.getGeneratorContext();
        this.propertyBitSet = new PropertyBitSet();
        this.tagValue = feedContext.getTagValue();
        this.feedClass = feedContext.getFeedClass();
    }

    /**
     * Returns a value associated with the given property key from the data store.
     *
     * @param propertyKey the key identifying the desired property
     * @return the value associated with the specified property key,
     * or {@code null} if a value is not present
     * @throws InstancioApiException if the data store does not contain the given property
     */
    protected abstract String getValue(String propertyKey);

    @Override
    public final InternalFeedContext<?> getFeedContext() {
        return feedContext;
    }

    @Override
    public Set<String> getFeedProperties() {
        final Set<String> properties = new HashSet<>(dataStore.getPropertyKeys());
        final Method[] methods = feedClass.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getReturnType() == FeedSpec.class) {
                properties.add(method.getName());
            }
        }
        return properties;
    }

    @Override
    public final <T> FeedSpec<T> createSpec(final SpecMethod specMethod, final Object[] args) {
        return createSpecInternal(specMethod, args, UPDATE_BITSET);
    }

    @Override
    public final <T> FeedSpec<T> createSpec(final String propertyName, final Class<T> targetType) {
        final Function<String, T> converter = getConverter(targetType);
        final Supplier<T> supplier = createSupplier(propertyName, converter, UPDATE_BITSET);
        return createSpecWithPostProcessors(supplier, Collections.emptyList(), false);
    }

    protected final R getCurrentEntry() {
        return currentEntry;
    }

    protected final GeneratorContext getGeneratorContext() {
        return generatorContext;
    }

    protected final Class<?> getFeedClass() {
        return feedClass;
    }

    protected final DataStore<R> getDataStore() {
        return dataStore;
    }

    protected final int getPropertyIndex(final String propertyKey) {
        final int index = getDataStore().indexOf(propertyKey);
        if (index != -1) {
            return index;
        }
        throw Fail.withUsageError(feedWithInvalidMethodName(
                getFeedClass(), propertyKey, getDataStore().getPropertyKeys()));
    }

    private <T> FeedSpec<T> createSpecInternal(
            final SpecMethod specMethod,
            final Object[] args,
            final boolean updateBitSet) {

        final Supplier<T> resultSupplier = getValueSupplier(specMethod, args, updateBitSet);
        final List<PostProcessor<T>> postProcessors = getPostProcessors(specMethod);
        return createSpecWithPostProcessors(resultSupplier, postProcessors, specMethod.hasNullableAnnotation());
    }

    private <T> FeedSpec<T> createSpecWithPostProcessors(
            final Supplier<T> resultSupplier,
            final List<PostProcessor<T>> postProcessors,
            final boolean isNullable) {

        final Supplier<T> supplier = postProcessors.isEmpty() ? resultSupplier : () -> {
            T processed = resultSupplier.get();
            for (PostProcessor<T> p : postProcessors) {
                processed = p.process(processed, getGeneratorContext().random());
            }
            return processed;
        };

        return new SupplierBackedGenerator<>(getGeneratorContext(), supplier)
                .nullable(isNullable);
    }

    private <T> FeedSpec<T> createFeedSpecInternal(final SpecMethod specMethod) {
        return createSpecInternal(specMethod, /* args = */ null, !UPDATE_BITSET);
    }

    /**
     * Returns a supplier for the given spec method.
     * The supplier will produce values with the target type,
     * but with no post-processors applied.
     */
    @SuppressWarnings("unchecked")
    private <T> Supplier<T> getValueSupplier(
            final SpecMethod specMethod,
            final Object[] args,
            final boolean updateBitSet) {

        // Handle built-in feed spec methods
        if (specMethod.isDeclaredByFeedInterface()) {
            final String propertyNameArg = (String) ApiValidator.notNull(
                    args[0], "'propertyName' must not be null");

            if (args.length == 1) {
                // Handle Feed methods with 1 parameter, e.g.
                // FeedSpec<Long> longSpec(String propertyName)

                final Function<String, T> converter = getConverter(specMethod.getTargetType());
                return createSupplier(propertyNameArg, converter, updateBitSet);

            } else if (args.length == 2) {
                // Handle Feed methods with 2 parameters:
                // - <T> FeedSpec<T> spec(String propertyName, Function<String, T> converter)
                // - <T> FeedSpec<T> spec(String propertyName, Class<T> targetType)

                if (args[1] instanceof Function) {
                    final Function<String, T> converter = (Function<String, T>)
                            ApiValidator.notNull(args[1], "'converter' function must not be null");

                    return createSupplier(propertyNameArg, converter, updateBitSet);

                } else {
                    final Class<T> targetTypeParameter = (Class<T>)
                            ApiValidator.notNull(args[1], "'targetType' must not be null");

                    final Function<String, T> converter = getConverter(targetTypeParameter);
                    return createSupplier(propertyNameArg, converter, updateBitSet);
                }
            }
        }

        // Handle user-defined spec methods declared in a Feed subclass
        final String key = specMethod.getDataPropertyName();

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

        final WithStringMapper withStringMapper = specMethod.getAnnotation(WithStringMapper.class);

        final Function<String, T> converter = withStringMapper != null
                ? (Function<String, T>) ReflectionUtils.newInstance(withStringMapper.value())
                : getConverter(specMethod.getTargetType());

        return createSupplier(key, converter, updateBitSet);
    }

    private <T> Supplier<T> createSupplier(
            final String propertyKey,
            final Function<String, T> converter,
            final boolean updateBitSet) {

        return () -> {
            final String value = updateState(propertyKey, updateBitSet).getValue(propertyKey);
            if (value == null) {
                return null;
            }
            try {
                return converter.apply(value);
            } catch (Exception ex) {
                throw Fail.withUsageError(errorMappingStringToTargetType(propertyKey, value, ex), ex);
            }
        };
    }

    private AbstractFeed<R> updateState(final String propertyName, final boolean updateBitSet) {
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

    @SuppressWarnings("unchecked")
    private <T> T getGeneratedSpecValue(final Class<?> generatorClass) {
        final Generator<T> generator = (Generator<T>) ReflectionUtils.newInstance(generatorClass);
        return generator.generate(getGeneratorContext().random());
    }

    private <T> T getFunctionSpecValue(
            final SpecMethod parentSpecMethod,
            final FeedSpecAnnotations.FunctionSpec functionSpec) {

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
            if (method != null) {
                final SpecMethod specMethod = new SpecMethod(method);
                args[i] = createFeedSpecInternal(specMethod).get();
            } else if (dataStore.contains(component)) {
                args[i] = createSpec(component, params[i]).get();
            } else {
                throw Fail.withUsageError(feedComponentMethodNotFound(
                        feedClass, component, parentSpecMethod));
            }
        }

        try {
            final FunctionProvider provider = ReflectionUtils.newInstance(providerClass);
            return (T) setAccessible(functionSpecHandler).invoke(provider, args);
        } catch (Exception ex) {
            throw Fail.withUsageError(errorInvokingFunctionSpecHandlerMethod(
                    feedClass, providerClass, parentSpecMethod, functionSpecHandler, ex), ex);
        }
    }

    @NotNull
    private Method resolveFunctionSpecHandler(
            final SpecMethod parentSpecMethod,
            final Class<?> providerClass) {

        Method resolvedMethod = null;
        for (Method method : providerClass.getDeclaredMethods()) {
            // The provider class should contain exactly one method
            // ignoring private methods as these could be helper methods
            if (Modifier.isPrivate(method.getModifiers())) {
                continue;
            }
            if (resolvedMethod == null) {
                resolvedMethod = method;
            } else {
                // fail if multiple methods are declared
                throw Fail.withUsageError(invalidFunctionSpecHandlerMethod(
                        feedClass, providerClass, parentSpecMethod));
            }
        }
        if (resolvedMethod == null) {
            throw Fail.withUsageError(invalidFunctionSpecHandlerMethod(
                    feedClass, providerClass, parentSpecMethod));
        }
        return resolvedMethod;
    }

    @SuppressWarnings("unchecked")
    private <T> T getTemplateSpecValue(final TemplateSpec templateSpec) {
        String template = templateSpec.value();
        final List<String> components = StringUtils.getTemplateKeys(template);

        for (String component : components) {
            final String value;
            if (dataStore.contains(component)) {
                value = getValue(component);
            } else {
                final Method method = getZeroArgMethod(feedClass, component);
                value = (String) createFeedSpecInternal(new SpecMethod(method)).get();
            }
            final String replacement = value == null ? "" : value;
            template = template.replace("${" + component + "}", replacement);
        }
        return (T) template;
    }

    private R getNext() {
        if (feedContext.getFeedDataAccess() == FeedDataAccess.RANDOM) {
            final String tag = tagValue == null
                    ? generatorContext.random().oneOf(dataStore.getTagKeys())
                    : tagValue;

            final List<R> tagData = dataStore.get(tag);
            return generatorContext.random().oneOf(tagData);
        }

        // sequential data access
        if (tagValue == null) {
            final int nextIndex = nextIndex(dataStore.size());
            return dataStore.get(nextIndex);
        }
        final List<R> tagData = dataStore.get(tagValue);
        final int next = nextIndex(tagData.size());
        return tagData.get(next);
    }

    private int nextIndex(final int dataSize) {
        sequentialAccessIndex.compareAndSet(-1, 0);
        int nextIndex = sequentialAccessIndex.getAndIncrement();

        if (nextIndex == dataSize) {
            if (feedContext.getFeedDataEndStrategy() == FeedDataEndAction.FAIL) {
                throw Fail.withUsageError(ErrorMessageUtils.feedDataEnd(feedClass, generatorContext.getSettings()));
            } else {
                sequentialAccessIndex.set(0);
                nextIndex = sequentialAccessIndex.getAndIncrement();
            }
        }
        return nextIndex;
    }
}
