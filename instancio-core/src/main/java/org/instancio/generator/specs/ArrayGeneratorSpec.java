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
package org.instancio.generator.specs;

/**
 * Generator spec for arrays.
 *
 * @param <T> array type
 * @since 1.0.1
 */
public interface ArrayGeneratorSpec<T>
        extends NullableGeneratorSpec<T>, SizeGeneratorSpec<T>, SubtypeGeneratorSpec<T> {

    /**
     * Size of array to generate.
     *
     * @param size of array
     * @return spec builder
     * @since 5.6.0
     */
    @Override
    ArrayGeneratorSpec<T> size(int size);

    /**
     * Minimum size of array to generate.
     *
     * @param size minimum size (inclusive)
     * @return spec builder
     * @since 5.6.0
     */
    @Override
    ArrayGeneratorSpec<T> minSize(int size);

    /**
     * Maximum size of array to generate.
     *
     * @param size maximum size (inclusive)
     * @return spec builder
     * @since 5.6.0
     */
    @Override
    ArrayGeneratorSpec<T> maxSize(int size);

    /**
     * length of array to generate.
     *
     * @param length of array
     * @return spec builder
     * @deprecated use {@link #size(int)} instead
     */
    @Deprecated
    ArrayGeneratorSpec<T> length(int length);

    /**
     * Minimum length of array to generate.
     *
     * @param length minimum length (inclusive)
     * @return spec builder
     * @deprecated use {@link #minSize(int)} instead
     */
    @Deprecated
    ArrayGeneratorSpec<T> minLength(int length);

    /**
     * Maximum length of array to generate.
     *
     * @param length maximum length (inclusive)
     * @return spec builder
     * @deprecated use {@link #maxSize(int)} instead
     */
    @Deprecated
    ArrayGeneratorSpec<T> maxLength(int length);

    /**
     * Indicates that {@code null} value can be generated for the array.
     *
     * @return spec builder
     */
    @Override
    ArrayGeneratorSpec<T> nullable();

    /**
     * Indicates that {@code null} values can be generated for array elements.
     *
     * @return spec builder
     */
    ArrayGeneratorSpec<T> nullableElements();

    /**
     * Specifies the type of array that should be generated.
     *
     * @param type of array to generate
     * @return spec builder
     * @since 1.4.0
     */
    @Override
    ArrayGeneratorSpec<T> subtype(Class<?> type);

    /**
     * Adds given elements to the generated array at random positions.
     *
     * @param elements to add
     * @return spec builder
     */
    ArrayGeneratorSpec<T> with(T... elements);
}
