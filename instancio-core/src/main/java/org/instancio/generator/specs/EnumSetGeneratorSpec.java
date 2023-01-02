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
package org.instancio.generator.specs;

import org.instancio.generator.GeneratorSpec;

import java.util.EnumSet;

/**
 * Generator spec for {@link EnumSet}.
 *
 * @since 2.0.0
 */
public interface EnumSetGeneratorSpec<E extends Enum<E>> extends GeneratorSpec<EnumSet<E>> {

    /**
     * Size of {@code EnumSet} to generate.
     *
     * @param size of {@code EnumSet}
     * @return spec builder
     * @since 2.0.0
     */
    EnumSetGeneratorSpec<E> size(int size);

    /**
     * Minimum size of {@code EnumSet} to generate.
     *
     * @param size minimum size (inclusive)
     * @return spec builder
     * @since 2.0.0
     */
    EnumSetGeneratorSpec<E> minSize(int size);

    /**
     * Maximum size of {@code EnumSet} to generate.
     *
     * @param size maximum size (inclusive)
     * @return spec builder
     * @since 2.0.0
     */
    EnumSetGeneratorSpec<E> maxSize(int size);

    /**
     * Specifies choices from which the {@code EnumSet} will be generated.
     *
     * @param elements from which the enum set will be generated
     * @return spec builder
     * @since 2.0.0
     */
    EnumSetGeneratorSpec<E> of(E... elements);

    /**
     * Specifies enum values to exclude from the generated {@code EnumSet}.
     *
     * @param elements to excluding from the generated enum set
     * @return spec builder
     * @since 2.0.0
     */
    EnumSetGeneratorSpec<E> excluding(E... elements);
}