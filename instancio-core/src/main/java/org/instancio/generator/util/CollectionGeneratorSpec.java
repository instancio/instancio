/*
 *  Copyright 2022 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.instancio.generator.util;

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
     */
    CollectionGeneratorSpec<T> type(Class<?> type);

    /**
     * Adds given elements to the generated collection.
     * <p>
     * If the collection is a List, elements will be added at random positions;
     * otherwise elements will be added via {@link Collection#addAll(Collection)}
     * after the collection has been generated.
     *
     * @param elements to add
     * @return spec builder
     */
    CollectionGeneratorSpec<T> with(T... elements);

}
