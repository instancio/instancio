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
package org.instancio;

import org.instancio.generator.GeneratorSpec;
import org.instancio.generators.Generators;

/**
 * Provides access to built-in generators that support customisation
 * of generated values using the API.
 *
 * @param <V> generated object type
 * @since 2.2.0
 */
@FunctionalInterface
public interface GeneratorSpecProvider<V> {

    /**
     * Returns a customised generator spec.
     *
     * @param gen specs available out of the box
     * @return generator spec
     */
    GeneratorSpec<V> getSpec(Generators gen);
}
