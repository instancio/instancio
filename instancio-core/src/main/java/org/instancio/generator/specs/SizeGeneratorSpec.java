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

/**
 * Generator spec for specifying the size of an object.
 *
 * @param <T> the object type
 * @since 2.13.0
 */
public interface SizeGeneratorSpec<T> extends GeneratorSpec<T> {

    /**
     * The exact size of the object to generate.
     *
     * @param size of the object
     * @return spec builder
     * @since 2.13.0
     */
    SizeGeneratorSpec<T> size(int size);

    /**
     * The minimum size of the object to generate.
     *
     * @param size minimum size (inclusive)
     * @return spec builder
     * @since 2.13.0
     */
    SizeGeneratorSpec<T> minSize(int size);

    /**
     * The maximum size of the object to generate.
     *
     * @param size maximum size (inclusive)
     * @return spec builder
     * @since 2.13.0
     */
    SizeGeneratorSpec<T> maxSize(int size);
}
