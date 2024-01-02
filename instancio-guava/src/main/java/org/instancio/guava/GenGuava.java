/*
 * Copyright 2022-2024 the original author or authors.
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
package org.instancio.guava;

import org.instancio.documentation.ExperimentalApi;
import org.instancio.guava.generator.specs.MultimapGeneratorSpec;
import org.instancio.guava.generator.specs.TableGeneratorSpec;
import org.instancio.guava.internal.generator.GuavaArrayListMultimapGenerator;
import org.instancio.guava.internal.generator.GuavaHashBasedTableGenerator;

/**
 * Provides access to generators for Guava classes.
 *
 * @since 2.13.0
 */
@ExperimentalApi
public final class GenGuava {

    /**
     * Generator spec for multimaps.
     *
     * @param <K> the type of the key
     * @param <V> the type of the mapped values
     * @return generator spec
     * @since 2.13.0
     */
    public static <K, V> MultimapGeneratorSpec<K, V> multimap() {
        return new GuavaArrayListMultimapGenerator<>();
    }

    /**
     * Generator spec for tables.
     *
     * @param <R> the type of the table row keys
     * @param <C> the type of the table column keys
     * @param <V> the type of the mapped values
     * @return generator spec
     * @since 2.13.0
     */
    public static <R, C, V> TableGeneratorSpec<R, C, V> table() {
        return new GuavaHashBasedTableGenerator<>();
    }

    private GenGuava() {
        // non-instantiable
    }
}
