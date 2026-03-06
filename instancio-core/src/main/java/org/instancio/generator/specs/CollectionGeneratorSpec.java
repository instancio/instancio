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
package org.instancio.generator.specs;

import org.jspecify.annotations.Nullable;

import java.util.Collection;

/**
 * Generator spec for collections.
 *
 * @param <T> element type
 */
public interface CollectionGeneratorSpec<T extends @Nullable Object>
        extends SizeGeneratorSpec<Collection<T>>,
        NullableGeneratorSpec<Collection<T>>,
        SubtypeGeneratorSpec<Collection<T>> {

    /**
     * Size of collection to generate.
     *
     * @param size of collection
     * @return spec builder
     */
    @Override
    CollectionGeneratorSpec<T> size(int size);

    /**
     * Minimum size of collection to generate.
     *
     * @param size minimum size (inclusive)
     * @return spec builder
     */
    @Override
    CollectionGeneratorSpec<T> minSize(int size);

    /**
     * Maximum size of collection to generate.
     *
     * @param size maximum size (inclusive)
     * @return spec builder
     */
    @Override
    CollectionGeneratorSpec<T> maxSize(int size);

    /**
     * Indicates that {@code null} value can be generated for the collection.
     *
     * @return spec builder
     */
    @Override
    CollectionGeneratorSpec<T> nullable();

    /**
     * Indicates that {@code null} values can be generated for collection elements.
     *
     * @return spec builder
     */
    CollectionGeneratorSpec<T> nullableElements();

    /**
     * Specifies the type of collection that should be generated.
     *
     * @param type of collection to generate
     * @return spec builder
     * @since 1.4.0
     */
    @Override
    CollectionGeneratorSpec<T> subtype(Class<?> type);


    /**
     * Specifies that a collection containing unique elements should be generated.
     *
     * <p>Special care must be taken when using this method and
     * {@link #size(int)}. Consider the following example where
     * the favourite numbers field is of type {@code Set<Integer>}:
     *
     * <pre>{@code
     *   Person person = Instancio.of(Person.class)
     *       .generate(allInts(), gen -> gen.ints().range(1, 5))
     *       .generate(field(Person::getFavouriteNumbers), gen -> gen.collection().unique().size(10))
     *       .create();
     * }</pre>
     * <p>
     * Since the integer range is restricted to {@code 1..5}, it is impossible
     * to generate a collection of 10 unique elements. In this case,
     * a collection of size 5 will be generated.
     *
     * @return spec builder
     * @since 2.8.0
     */
    CollectionGeneratorSpec<T> unique();

    /**
     * Adds given elements to the generated collection at random positions.
     * Note that the elements are added after the collection has been generated.
     * <p>
     * Example:
     * <pre>{@code
     *  // will generate a collection of size 5
     *  generate(field("someList"), gen -> gen.collection().size(3).with("element1", "element2")
     * }</pre>
     *
     * @param elements to add
     * @return spec builder
     */
    @SuppressWarnings("unchecked")
    CollectionGeneratorSpec<T> with(@Nullable T... elements);

}
