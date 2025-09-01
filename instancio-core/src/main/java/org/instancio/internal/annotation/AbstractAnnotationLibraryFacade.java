/*
 * Copyright 2022-2025 the original author or authors.
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
package org.instancio.internal.annotation;

import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.GeneratorSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

abstract class AbstractAnnotationLibraryFacade implements AnnotationLibraryFacade {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractAnnotationLibraryFacade.class);

    /**
     * Maps primary annotation to a function that provides a generator for the given annotation
     */
    private final Map<Class<? extends Annotation>, AnnotationGeneratorFn> primaryAnnotations = new HashMap<>();

    protected abstract AnnotationHandlerMap getAnnotationHandlerMap();

    final <A extends Annotation> void putPrimary(
            final Supplier<Class<A>> annotationTypeSupplier,
            final AnnotationGeneratorFn annotationGeneratorFn) {

        try {
            Class<A> annotationType = annotationTypeSupplier.get();
            if (annotationType != null) {
                primaryAnnotations.put(annotationTypeSupplier.get(), annotationGeneratorFn);
            }
        } catch (NoClassDefFoundError error) {
            LOG.trace("Annotation not available on classpath: {}", error.toString());
        }
    }

    @Override
    public final boolean isPrimary(Class<? extends Annotation> annotationType) {
        return primaryAnnotations.containsKey(annotationType);
    }

    @Override
    public final void consumeAnnotations(final AnnotationMap annotationMap,
                                         final GeneratorSpec<?> spec,
                                         final Class<?> targetClass,
                                         final GeneratorContext generatorContext) {

        final AnnotationHandlerMap annotationHandlerMap = getAnnotationHandlerMap();
        final Collection<Annotation> annotations = annotationMap.getAnnotations();

        for (Annotation annotation : annotations) {
            final FieldAnnotationHandler handler = annotationHandlerMap.get(annotation);
            if (handler != null) {
                handler.process(annotation, spec, targetClass, generatorContext);
                annotationMap.remove(annotation.annotationType());
            }
        }
    }

    @Override
    public Generator<?> resolveGenerator(Annotation annotation, GeneratorContext context) {
        AnnotationGeneratorFn fn = primaryAnnotations.get(annotation.annotationType());
        return fn == null ? null : fn.apply(annotation, context);
    }
}
