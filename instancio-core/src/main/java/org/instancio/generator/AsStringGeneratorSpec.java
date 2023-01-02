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
package org.instancio.generator;

import java.util.function.Function;

/**
 * A spec for generators that can produce their values as strings.
 *
 * @param <T> generated type
 * @since 2.0.0
 */
public interface AsStringGeneratorSpec<T> extends GeneratorSpec<T> {

    /**
     * Returns the generated value as a {@code String}.
     *
     * @return result as a string using the {@link Object#toString()} method
     * @since 2.0.0
     */
    default Generator<String> asString() {
        return asString(Object::toString);
    }

    /**
     * Returns the generated value as a {@code String} using
     * the specified function.
     *
     * @param toStringFunction function for converting the result to a string
     * @return result as a string using the specified function for conversion
     * @since 2.0.0
     */
    @SuppressWarnings("unchecked")
    default Generator<String> asString(Function<T, String> toStringFunction) {
        return random -> {
            final T result = ((Generator<T>) this).generate(random);
            return toStringFunction.apply(result);
        };
    }
}
