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
package org.instancio.internal.generation;

import org.instancio.exception.InstancioException;
import org.instancio.exception.InstancioTerminatingException;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorContext;
import org.instancio.internal.PrimitiveWrapperBiLookup;
import org.instancio.internal.annotation.AnnotationConsumer;
import org.instancio.internal.annotation.AnnotationConsumers;
import org.instancio.internal.annotation.AnnotationExtractor;
import org.instancio.internal.annotation.AnnotationMap;
import org.instancio.internal.context.ModelContext;
import org.instancio.internal.generator.GeneratorResolver;
import org.instancio.internal.generator.GeneratorResult;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.spi.ProviderEntry;
import org.instancio.internal.util.Fail;
import org.instancio.internal.util.ReflectionUtils;
import org.instancio.settings.Keys;
import org.instancio.spi.InstancioServiceProvider.AnnotationProcessor;
import org.instancio.spi.InstancioServiceProvider.AnnotationProcessor.AnnotationHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.instancio.internal.util.ErrorMessageUtils.annotationHandlerInvalidNumberOfParameters;
import static org.instancio.internal.util.ErrorMessageUtils.invalidAnnotationHandlerMethod;

/**
 * Entry point for processing Bean Validation and JPA annotations,
 * as well as {@link AnnotationProcessor} SPI implementations.
 *
 * <p>The main goals of this implementation are:
 * <ul>
 *   <li>support single annotations</li>
 *   <li>support combinations of annotations from different providers</li>
 *   <li>produce repeatable results regardless of declaration order</li>
 * </ul>
 *
 * <p>All of the above is done on a best-effort basis and is not guaranteed
 * to work in all circumstances.
 *
 * @see AnnotationMap
 */
@SuppressWarnings("PMD.ExcessiveImports")
final class AnnotationNodeHandler implements NodeHandler {

    private final List<AnnotationConsumer> annotationConsumers;
    private final AnnotationExtractor annotationExtractor;
    private final ModelContext<?> modelContext;
    private final GeneratorContext generatorContext;
    private final GeneratorResolver generatorResolver;
    private final GeneratedValuePostProcessor stringPostProcessor;
    private final Map<Class<?>, List<AnnotatedMethod>> annotatedMethodsMap;
    private final boolean beanValidationOrJpaEnabled;

    private AnnotationNodeHandler(
            final ModelContext<?> modelContext,
            final GeneratorResolver generatorResolver,
            final List<ProviderEntry<AnnotationProcessor>> annotationProcessors,
            final boolean beanValidationOrJpaEnabled) {

        this.modelContext = modelContext;
        this.generatorResolver = generatorResolver;
        this.annotationConsumers = AnnotationConsumers.get(modelContext);
        this.annotationExtractor = new AnnotationExtractor(modelContext);
        this.stringPostProcessor = new StringPrefixingPostProcessor(
                modelContext.getSettings().get(Keys.STRING_FIELD_PREFIX_ENABLED));
        this.generatorContext = new GeneratorContext(modelContext.getSettings(), modelContext.getRandom());
        this.annotatedMethodsMap = collectAnnotatedMethods(annotationProcessors);
        this.beanValidationOrJpaEnabled = beanValidationOrJpaEnabled;
    }

    static NodeHandler create(final ModelContext<?> context, final GeneratorResolver generatorResolver) {
        final boolean bvOrJpaEnabled = context.getSettings().get(Keys.BEAN_VALIDATION_ENABLED)
                || context.getSettings().get(Keys.JPA_ENABLED);

        final List<ProviderEntry<AnnotationProcessor>> annotationProcessors =
                context.getServiceProviders().getAnnotationProcessors();

        return bvOrJpaEnabled || !annotationProcessors.isEmpty()
                ? new AnnotationNodeHandler(context, generatorResolver, annotationProcessors, bvOrJpaEnabled)
                : NOOP_HANDLER;
    }

    private static Map<Class<?>, List<AnnotatedMethod>> collectAnnotatedMethods(
            final List<ProviderEntry<AnnotationProcessor>> annotationProcessors) {

        if (annotationProcessors.isEmpty()) {
            return Collections.emptyMap();
        }

        final Map<Class<?>, List<AnnotatedMethod>> results = new LinkedHashMap<>();

        for (ProviderEntry<AnnotationProcessor> entry : annotationProcessors) {
            final AnnotationProcessor processor = entry.getProvider();

            for (Method method : processor.getClass().getDeclaredMethods()) {
                ReflectionUtils.setAccessible(method);

                if (method.getDeclaredAnnotation(AnnotationHandler.class) != null) {
                    final Class<?>[] paramTypes = method.getParameterTypes();
                    final AnnotatedMethod annotatedMethod = new AnnotatedMethod(processor, method);

                    // Allowed number of args for @AnnotationHandler methods is either 2 or 3.
                    // The validation is deferred to just before the method is invoked.
                    if (paramTypes.length > 0) {
                        final Class<?> annotationType = paramTypes[0];
                        results.computeIfAbsent(annotationType, v -> new ArrayList<>()).add(annotatedMethod);
                    }
                }
            }
        }
        return Collections.unmodifiableMap(results);
    }

    @NotNull
    @Override
    public GeneratorResult getResult(@NotNull final InternalNode node) {
        final Annotation[] annotations = annotationExtractor.getAnnotations(node);

        if (annotations.length == 0) {
            return GeneratorResult.emptyResult();
        }

        Generator<?> generator;

        // AnnotationProcessor SPI can be used with or without
        // Bean Validation/JPA annotations
        if (beanValidationOrJpaEnabled) {
            final AnnotationMap annotationMap = new AnnotationMap(annotations);
            generator = getGenerator(node, annotations, annotationMap);

            for (AnnotationConsumer provider : annotationConsumers) {
                provider.consumeAnnotations(annotationMap, generator, node.getTargetClass());
            }
        } else {
            generator = generatorResolver.get(node);
        }

        if (generator == null) {
            return GeneratorResult.emptyResult();
        }

        // Invoke @AnnotationHandler methods defined via SPI
        invokeAnnotationHandlerMethods(node, annotations, generator);

        Object obj = generator.generate(modelContext.getRandom());

        // Some generators return a different type than the target class,
        // e.g. URLGenerator returns a java.net.URL object.
        // However, the @URL annotation is only applicable to strings.
        // There might be other types that need to be handled as well,
        // but so far string seems to be sufficient
        if (node.getTargetClass() == String.class) {
            obj = obj.toString();
        }
        // It's possible an annotation is placed on a field that
        // doesn't support it. To avoid an error in such cases,
        // re-generate the value for the given node using a generator
        // matching the target class, rather than the primary annotation
        else if (!isObjectAssignableToNode(node, obj)) {
            final Generator<?> builtInGenerator = generatorResolver.get(node);
            obj = builtInGenerator.generate(modelContext.getRandom());
        }

        final Object processed = stringPostProcessor.process(obj, node, generator);
        return GeneratorResult.create(processed, generator.hints());
    }

    private void invokeAnnotationHandlerMethods(
            final InternalNode node,
            final Annotation[] annotations,
            final Generator<?> generator) {

        for (Annotation annotation : annotations) {
            final List<AnnotatedMethod> annotatedMethods = annotatedMethodsMap.getOrDefault(
                    annotation.annotationType(), Collections.emptyList());

            for (AnnotatedMethod method : annotatedMethods) {
                method.invoke(annotation, generator, node);
            }
        }
    }

    private static final class AnnotatedMethod {
        private final AnnotationProcessor processor;
        private final Method method;
        private final Class<?>[] params;

        AnnotatedMethod(AnnotationProcessor processor, Method method) {
            this.processor = processor;
            this.method = method;
            this.params = method.getParameterTypes();
        }

        void invoke(final Annotation annotation, final Generator<?> generator, final InternalNode node) {
            try {
                if (params.length == 2) {
                    method.invoke(processor, annotation, generator);
                } else if (params.length == 3) {
                    method.invoke(processor, annotation, generator, node);
                } else {
                    throw Fail.withUsageError(annotationHandlerInvalidNumberOfParameters(processor.getClass(), method));
                }
            } catch (IllegalArgumentException ex) {
                final String msg = invalidAnnotationHandlerMethod(
                        processor.getClass(), method, annotation, generator, node);

                throw Fail.withUsageError(msg, ex);
            } catch (AssertionError | InstancioTerminatingException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new InstancioException("Failed invoking @AnnotationHandler method", ex);
            }
        }
    }


    private static boolean isObjectAssignableToNode(final InternalNode node, final Object obj) {
        if (obj == null) {
            return true;
        }
        Class<?> targetClass = node.getTargetClass();
        if (targetClass.isPrimitive()) {
            targetClass = PrimitiveWrapperBiLookup.getEquivalent(targetClass);
        }
        return targetClass.isAssignableFrom(obj.getClass());
    }

    @Nullable
    private Generator<?> getGenerator(
            final InternalNode node,
            final Annotation[] annotations,
            final AnnotationMap annotationMap) {

        for (AnnotationConsumer provider : annotationConsumers) {
            for (Annotation annotation : annotations) {
                if (provider.isPrimary(annotation.annotationType())) {
                    annotationMap.setPrimary(annotation);
                    return provider.resolveGenerator(annotation, generatorContext);
                }
            }
        }
        // If no primary annotation present or no generator defined
        // for the primary annotation, fallback to a built-in generator.
        // We ignore SPI generators (GeneratorProvider) here because
        // if there was one defined, it would have been used to generate
        // the value for this node (bypassing annotation processing).
        return generatorResolver.get(node);
    }
}
