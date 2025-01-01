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

import org.instancio.documentation.InternalApi;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.GeneratorSpec;
import org.instancio.internal.util.Sonar;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;

/**
 * Provides support for customising generated values based on annotations.
 * Each annotation library has its own implementation of this class.
 *
 * @since 2.7.0
 */
@InternalApi
public interface AnnotationLibraryFacade {

    /**
     * Checks if the specified annotation type is marked as
     * a primary annotation for this provider.
     *
     * <p>When multiple annotations are declared, the processor first determines
     * and consumes the <i>primary</i> annotation. Primary annotations are those
     * that can only be applied to a certain type and have a certain meaning, e.g.
     * {@code  @Email, @URL, @UUID}. Currently, all supported primary annotations
     * target character sequences. Once a primary annotation has been consumed,
     * the processor will apply the remaining annotations.
     *
     * @param annotationType to check
     * @return {@code true} if the type represents a primary annotation
     * @since 2.7.0
     */
    boolean isPrimary(Class<? extends Annotation> annotationType);

    /**
     * Consumes all the annotations supported by this consumer from the given map,
     * starting with the primary annotation. Consumed annotations are removed
     * from the map.
     *
     * @param map         from which annotations will be consumed
     * @param spec        generator spec for the given field
     * @param targetClass type being generated
     * @since 2.7.0
     */
    void consumeAnnotations(AnnotationMap map,
                            GeneratorSpec<?> spec,
                            Class<?> targetClass);

    /**
     * Resolves a generator for the given primary annotation.
     *
     * @param annotation the primary annotation to resolve the generator for
     * @param context    generator context
     * @return resolved generator or {@code null}
     */
    @Nullable
    @SuppressWarnings(Sonar.GENERIC_WILDCARD_IN_RETURN)
    Generator<?> resolveGenerator(Annotation annotation, GeneratorContext context);
}
