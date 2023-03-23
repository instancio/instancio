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
 * Generator spec for {@link Enum Enums}.
 *
 * @param <E> enum type
 * @since 1.6.0
 */
public interface EnumGeneratorSpec<E extends Enum<E>> extends GeneratorSpec<E> {

    /**
     * Generate an enum while excluding the specified values.
     * The argument can be an empty array, but not {@code null}.
     *
     * @param values to exclude
     * @return spec builder
     * @since 1.6.0
     */
    @SuppressWarnings("unchecked")
    EnumGeneratorSpec<E> excluding(E... values);

    /**
     * Indicates that {@code null} value can be generated.
     *
     * @return spec builder
     * @since 1.6.0
     */
    EnumGeneratorSpec<E> nullable();
}
