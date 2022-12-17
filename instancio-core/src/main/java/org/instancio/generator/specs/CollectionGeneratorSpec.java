/*
 * Copyright 2022 the original author or authors.
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

import java.util.Collection;

/**
 * Generator spec for collections.
 *
 * @param <T> element type
 */
public interface CollectionGeneratorSpec<T> extends GeneratorSpec<Collection<T>> {

    /**
     * Size of collection to generate.
     *
     * @param size of collection
     * @return spec builder
     */
    CollectionGeneratorSpec<T> size(int size);

    /**
     * Minimum size of collection to generate.
     *
     * @param size minimum size (inclusive)
     * @return spec builder
     */
    CollectionGeneratorSpec<T> minSize(int size);

    /**
     * Maximum size of collection to generate.
     *
     * @param size maximum size (inclusive)
     * @return spec builder
     */
    CollectionGeneratorSpec<T> maxSize(int size);

    /**
     * Indicates that {@code null} value can be generated for the collection.
     *
     * @return spec builder
     */
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
    CollectionGeneratorSpec<T> subtype(Class<?> type);

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
    CollectionGeneratorSpec<T> with(T... elements);

}
