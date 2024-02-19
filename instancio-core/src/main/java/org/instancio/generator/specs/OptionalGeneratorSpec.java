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

import java.util.Optional;

/**
 * Generator spec for {@link Optional}.
 *
 * @param <T> the type of value
 * @since 2.14.0
 */
public interface OptionalGeneratorSpec<T> extends GeneratorSpec<Optional<T>> {

    /**
     * Specifies that an empty {@code Optional} can be generated.
     *
     * @return spec builder
     * @since 2.14.0
     */
    GeneratorSpec<Optional<T>> allowEmpty();
}
