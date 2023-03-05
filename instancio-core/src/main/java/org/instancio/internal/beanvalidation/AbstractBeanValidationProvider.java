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
package org.instancio.internal.beanvalidation;

import org.instancio.exception.InstancioException;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.GeneratorSpec;
import org.instancio.internal.generator.AbstractGenerator;
import org.instancio.internal.generator.lang.StringGenerator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.function.BiFunction;

abstract class AbstractBeanValidationProvider implements BeanValidationProvider {

    private final Map<Class<? extends Annotation>, BiFunction<Annotation, GeneratorContext, Generator<?>>> map;

    protected AbstractBeanValidationProvider(
            Map<Class<? extends Annotation>, BiFunction<Annotation, GeneratorContext, Generator<?>>> map) {
        this.map = Collections.unmodifiableMap(map);
    }

    @Override
    public final boolean isPrimary(Class<? extends Annotation> annotationType) {
        return this.map.containsKey(annotationType);
    }


    private Generator<?> resolveGenerator(Annotation annotation, GeneratorContext context) {
        Class<? extends Annotation> annotationType = annotation.annotationType();
        BiFunction<Annotation, GeneratorContext, Generator<?>> generatorBuilder = this.map.get(annotationType);
        if (generatorBuilder != null) {
            return generatorBuilder.apply(annotation, context);
        } else {
            // should not be reachable if caller checked for supported annotations
            throw new InstancioException("Unmapped primary annotation:  " + annotationType.getName());
        }
    }


    /**
     * Returns a resolver for annotation handlers for this provider
     *
     * @return resolver for annotation handlers
     */
    protected abstract AnnotationHandlerResolver getAnnotationHandlerResolver();

    @Override
    public final void consumeAnnotations(final AnnotationMap map,
                                         final GeneratorSpec<?> spec,
                                         final Class<?> targetClass,
                                         final Field field) {

        final AnnotationHandlerResolver resolver = getAnnotationHandlerResolver();
        final Annotation primary = map.removePrimary();

        if (primary != null) {
            final AbstractGenerator<?> suppliedGenerator = (AbstractGenerator<?>) spec;
            final GeneratorContext context = suppliedGenerator.getContext();
            final Generator<?> actualGenerator = resolveGenerator(primary, context);

            // Only string generator supports delegate generators.
            // An example would be delegating to the URLGenerator to handle
            // a @URL annotation on a string field
            ((StringGenerator) spec).setDelegate(actualGenerator);

            final FieldAnnotationHandler handler = resolver.resolveHandler(primary);
            if (handler != null) {
                handler.process(primary, actualGenerator, field, targetClass);
            }
        }

        final Collection<Annotation> annotations = map.getAnnotations();
        for (Annotation annotation : annotations) {
            final FieldAnnotationHandler handler = resolver.resolveHandler(annotation);
            if (handler != null) {
                handler.process(annotation, spec, field, targetClass);
                map.remove(annotation.annotationType());
            }
        }
    }
}
