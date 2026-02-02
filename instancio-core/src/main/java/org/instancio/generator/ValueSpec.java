/*
 * Copyright 2022-2026 the original author or authors.
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

import org.instancio.Instancio;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * A spec for generating simple value types, such as strings, numbers,
 * dates, and so on. Note that this interface is not intended to be
 * implemented by users. Instead, instances of this class can be
 * created using the {@link Instancio#gen()} method, which provides
 * a shorthand API for generating values:
 *
 * <p>Example:
 * <pre>{@code
 * ValueSpec<String> spec = Instancio.gen().string().digits().length(5).prefix("FOO-");
 *
 * // Each call to get() will generate a random value, e.g.
 * String s1 = spec.get(); // Sample output: FOO-55025
 * String s2 = spec.get(); // Sample output: FOO-72941
 * }</pre>
 *
 * <p>In addition, value specs can generate lists of values:
 *
 * <pre>{@code
 * List<BigDecimal> list = Instancio.gen().math().bigDecimal().scale(3).list(4);
 *
 * // Sample output: [5233.423, 8510.780, 3306.888, 172.187]
 * }</pre>
 *
 * @param <T> the type of generated values
 * @since 2.6.0
 */
public interface ValueSpec<T> extends GeneratorSpec<T>, Supplier<T> {

    /**
     * Generates a single value.
     *
     * @return generated value
     * @since 2.6.0
     */
    @Override
    T get();

    /**
     * Generates a list of values of specified size.
     *
     * @param size of the list to generate
     * @return a list of random values
     * @since 2.6.0
     */
    default List<T> list(final int size) {
        final List<T> results = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            results.add(get());
        }
        return results;
    }

    /**
     * Returns an infinite {@link Stream} of values.
     *
     * <p>Note that {@link Stream#limit(long)}
     * must be called to avoid an infinite loop.
     *
     * @return an infinite stream of values
     * @since 2.6.0
     */
    default Stream<T> stream() {
        return Stream.generate(this);
    }

    /**
     * Maps the generated value using the specified function.
     *
     * @param fn  mapping function
     * @param <R> result type
     * @return the result of the mapping function
     * @since 2.6.0
     */
    default <R> R map(final Function<T, R> fn) {
        return fn.apply(get());
    }

    /**
     * Specifies that a {@code null} value can be generated
     *
     * @return spec builder reference
     * @since 2.11.0
     */
    ValueSpec<T> nullable();
}
