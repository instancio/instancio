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
package org.instancio.generator.specs;

import org.instancio.generator.GeneratorSpec;

/**
 * Provides a method for specifying a specific subtype that should be generated.
 *
 * @param <T> generated type
 * @since 2.7.0
 */
public interface SubtypeGeneratorSpec<T> extends GeneratorSpec<T> {

    /**
     * Specifies the subtype that should be generated.
     *
     * @param type the subtype to generate
     * @return spec builder instance
     * @since 2.7.0
     */
    SubtypeGeneratorSpec<T> subtype(Class<?> type);
}
