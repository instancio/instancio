/*
 * Copyright 2022-2026 the original author or authors.
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
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.spi.ProviderEntry;
import org.instancio.internal.util.Fail;
import org.instancio.internal.util.ReflectionUtils;
import org.instancio.spi.InstancioServiceProvider.AnnotationProcessor;
import org.instancio.spi.InstancioServiceProvider.AnnotationProcessor.AnnotationHandler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.instancio.internal.util.ErrorMessageUtils.annotationHandlerInvalidNumberOfParameters;
import static org.instancio.internal.util.ErrorMessageUtils.invalidAnnotationHandlerMethod;

class AnnotationProcessorHelper {

    /**
     * Contains user-defined {@link AnnotationHandler} methods from custom
     * {@link AnnotationProcessor} implementations with the annotation type as the key.
     */
    private final Map<Class<?>, List<AnnotatedMethod>> annotationProcessorMethods;

    AnnotationProcessorHelper(final List<ProviderEntry<AnnotationProcessor>> annotationProcessors) {
        this.annotationProcessorMethods = collectAnnotationProcessorMethods(annotationProcessors);
    }

    List<AnnotatedMethod> get(final Class<? extends Annotation> annotationType) {
        return annotationProcessorMethods.getOrDefault(annotationType, Collections.emptyList());
    }

    private static Map<Class<?>, List<AnnotatedMethod>> collectAnnotationProcessorMethods(
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

    static final class AnnotatedMethod {
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
}
