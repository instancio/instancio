/*
 * Copyright 2022-2023 the original author or authors.
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
import org.instancio.internal.generator.AbstractGenerator;
import org.instancio.internal.generator.lang.StringGenerator;
import org.instancio.internal.util.Verify;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

abstract class AbstractAnnotationConsumer implements AnnotationConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractAnnotationConsumer.class);

    /**
     * Maps primary annotation to a function that provides a generator for the given annotation
     */
    private final Map<Class<? extends Annotation>, AnnotationGeneratorFn> primaryAnnotations = new HashMap<>();

    protected abstract AnnotationHandlerMap getAnnotationHandlerMap();

    final <A extends Annotation> void register(
            final Supplier<Class<A>> annotationTypeSupplier,
            final AnnotationGeneratorFn annotationGeneratorFn) {

        try {
            primaryAnnotations.put(annotationTypeSupplier.get(), annotationGeneratorFn);
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
                                         final Class<?> targetClass) {

        final Annotation primaryAnnotation = annotationMap.removePrimary();
        final AnnotationHandlerMap annotationHandlerMap = getAnnotationHandlerMap();

        if (primaryAnnotation != null) {
            final AbstractGenerator<?> suppliedGenerator = (AbstractGenerator<?>) spec;
            final GeneratorContext context = suppliedGenerator.getContext();
            final Generator<?> actualGenerator = resolveGenerator(primaryAnnotation, context);

            // Only string generator supports delegate generators.
            // An example would be delegating to the URLGenerator to handle
            // a @URL annotation on a string field
            if (spec instanceof StringGenerator) {
                ((StringGenerator) spec).setDelegate(actualGenerator);
            } else {
                LOG.warn("Ignoring annotation {} applied to {}",
                        primaryAnnotation.annotationType().getName(), targetClass);
            }
        }

        final Collection<Annotation> annotations = annotationMap.getAnnotations();
        for (Annotation annotation : annotations) {
            final FieldAnnotationHandler handler = annotationHandlerMap.get(annotation);
            if (handler != null) {
                handler.process(annotation, spec, targetClass);
                annotationMap.remove(annotation.annotationType());
            }
        }
    }

    private Generator<?> resolveGenerator(Annotation annotation, GeneratorContext context) {
        AnnotationGeneratorFn generatorFn = primaryAnnotations.get(annotation.annotationType());

        // should not be reachable if caller checked for supported annotations
        Verify.notNull(generatorFn, "Unmapped primary annotation: %s", annotation.annotationType());
        return generatorFn.apply(annotation, context);
    }
}
