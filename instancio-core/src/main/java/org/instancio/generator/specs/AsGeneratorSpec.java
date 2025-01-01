/*
 * Copyright 2022-2025 the original author or authors.
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

import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorSpec;

import java.util.function.Function;

/**
 * Allows mapping the result of a generator to another type.
 *
 * @param <T> generated type
 * @since 2.11.0
 */
public interface AsGeneratorSpec<T> extends GeneratorSpec<T> {

    /**
     * Converts the generated value to a {@code String}.
     *
     * @return generator spec with the result mapped to a string
     * using {@link Object#toString()}
     * @since 2.11.0
     */
    default GeneratorSpec<String> asString() {
        return as(Object::toString);
    }

    /**
     * Converts the generated value to another type
     * using the specified mapping function.
     *
     * <p>Example:
     * <pre>{@code
     *   record LogEntry(String msg, long timestamp) {}
     *
     *   LogEntry result = Instancio.of(LogEntry.class)
     *     .generate(field(Log::timestamp), gen -> gen.temporal().instant().past().as(Instant::toEpochMilli))
     *     .create();
     * }</pre>
     *
     * @param mappingFunction for mapping generated value to another type
     * @param <R>             resulting type after applying the function
     * @return generator spec with mapped result
     * @since 2.11.0
     */
    @SuppressWarnings("unchecked")
    default <R> GeneratorSpec<R> as(Function<T, R> mappingFunction) {
        return (Generator<R>) random -> {
            final Generator<T> generator = (Generator<T>) this;
            final T result = generator.generate(random);
            return mappingFunction.apply(result);
        };
    }
}
