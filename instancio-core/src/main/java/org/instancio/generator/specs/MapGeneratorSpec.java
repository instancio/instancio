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

import java.util.Map;

/**
 * Generator spec for maps.
 *
 * @param <K> key type
 * @param <V> value type
 */
public interface MapGeneratorSpec<K, V> extends GeneratorSpec<Map<K, V>> {

    /**
     * Size of map to generate.
     *
     * @param size of map
     * @return spec builder
     */
    MapGeneratorSpec<K, V> size(int size);


    /**
     * Minimum size of map to generate.
     *
     * @param size minimum size (inclusive)
     * @return spec builder
     */
    MapGeneratorSpec<K, V> minSize(int size);

    /**
     * Maximum size of map to generate.
     *
     * @param size maximum size (inclusive)
     * @return spec builder
     */
    MapGeneratorSpec<K, V> maxSize(int size);

    /**
     * Indicates that {@code null} value can be generated for the map.
     *
     * @return spec builder
     */
    MapGeneratorSpec<K, V> nullable();

    /**
     * Indicates that {@code null} values can be generated for map keys.
     *
     * @return spec builder
     */
    MapGeneratorSpec<K, V> nullableKeys();

    /**
     * Indicates that {@code null} values can be generated for map values.
     *
     * @return spec builder
     */
    MapGeneratorSpec<K, V> nullableValues();

    /**
     * Specifies the type of map that should be generated.
     *
     * @param type of collection to generate
     * @return spec builder
     * @since 1.4.0
     */
    MapGeneratorSpec<K, V> subtype(Class<?> type);

    /**
     * Adds given key/value pair to the generated map.
     * Note that the entry is added after the map has been generated.
     * <p>
     * Example:
     *
     * <pre>{@code
     *  // will generate a map of size 5
     *  generate(field("someMap"), gen -> gen.map()
     *          .size(3)
     *          .with("key1", "value1")
     *          .with("key2", "value2")
     * }</pre>
     *
     * @param key   to add
     * @param value to add
     * @return spec builder
     * @see #withKeys(Object[])
     * @since 2.0.0
     */
    MapGeneratorSpec<K, V> with(K key, V value);

    /**
     * Adds given keys to the map  in the order they are provided.
     * <p>
     * If the resulting map size is equal to the number of specified keys, then
     * the map will contain only the specified keys and no randomly generated keys.
     * <p>
     * Examples:
     *
     * <pre>{@code
     *  // will generate a map of size 1 containing only "key1"
     *  generate(field("someMap"), gen -> gen.map().size(1).withKeys("key1", "key2")
     *
     *  // will generate a map of size 2 containing "key1" and "key2"
     *  generate(field("someMap"), gen -> gen.map().size(2).withKeys("key1", "key2")
     *
     *  // will generate a map of size 5 containing "key1", "key2", and 3 randomly generated keys
     *  generate(field("someMap"), gen -> gen.map().size(5).withKeys("key1", "key2")
     * }</pre>
     *
     * @param keys to add
     * @return spec builder
     * @see #with(Object, Object)
     * @since 2.0.0
     */
    @SuppressWarnings("unchecked")
    MapGeneratorSpec<K, V> withKeys(K... keys);
}
