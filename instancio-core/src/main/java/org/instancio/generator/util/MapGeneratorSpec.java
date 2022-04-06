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
     */
    MapGeneratorSpec<K, V> type(Class<?> type);

}
