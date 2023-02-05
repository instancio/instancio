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

import org.instancio.documentation.InternalApi;
import org.instancio.generator.GeneratorSpec;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * Provides support for Bean Validation annotations.
 *
 * @since 2.7.0
 */
@InternalApi
interface BeanValidationProvider {

    /**
     * Checks if the specified annotation type is marked as
     * a primary annotation for this provider.
     *
     * @param annotationType to check
     * @return {@code true} if the type represents a primary annotation
     * @since 2.7.0
     */
    boolean isPrimary(Class<? extends Annotation> annotationType);

    /**
     * Returns a resolver for annotation handlers for this provider
     *
     * @return resolver for annotation handlers
     * @since 2.7.0
     */
    AnnotationHandlerResolver getAnnotationHandlerResolver();

    /**
     * Consumes all the annotations supported by this provider from the given map,
     * starting with the primary annotation. Consumed annotations are moved
     * from the map.
     *
     * @param map         from which annotations will be consumed
     * @param spec        generator spec for the given field
     * @param targetClass type being generated
     * @param field       the annotations are declared on
     * @since 2.7.0
     */
    void consumeAnnotations(AnnotationMap map,
                            GeneratorSpec<?> spec,
                            Class<?> targetClass,
                            Field field);
}
