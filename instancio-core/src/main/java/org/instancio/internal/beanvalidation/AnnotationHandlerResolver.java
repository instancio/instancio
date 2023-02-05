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
import org.instancio.exception.InstancioException;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorContext;
import org.instancio.internal.util.Sonar;

import java.lang.annotation.Annotation;

/**
 * Resolves handlers for annotations.
 *
 * @since 2.7.0
 */
@InternalApi
interface AnnotationHandlerResolver {

    /**
     * Returns a handler for the given annotation.
     *
     * @param annotation the annotation to resolve a handler for
     * @return resolved handler, or {@code null} if none found
     * @since 2.7.0
     */
    FieldAnnotationHandler resolveHandler(Annotation annotation);

    /**
     * Returns a generator for the given primary annotation.
     * The generator must be mapped, or an error will be thrown.
     *
     * @param annotation the annotation for which to resolve a generator
     * @param context    the context that will be used to initialise the generator
     * @return resolved generator
     * @throws InstancioException if the generator could not be resolved
     * @since 2.7.0
     */
    @SuppressWarnings(Sonar.GENERIC_WILDCARD_IN_RETURN)
    Generator<?> resolveGenerator(
            Annotation annotation,
            GeneratorContext context);
}
