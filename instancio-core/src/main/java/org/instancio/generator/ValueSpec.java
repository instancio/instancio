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
package org.instancio.generator;

import org.instancio.Gen;
import org.instancio.Random;
import org.instancio.internal.RandomHelper;
import org.instancio.internal.util.ObjectUtils;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.support.Global;
import org.instancio.support.ThreadLocalSettings;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * A spec for generating simple value types, such as strings, numbers,
 * dates, and so on.
 *
 * <p>Provides support for generating values using the shorthand API:
 *
 * <pre>{@code
 *   String str = Gen.string().length(10).prefix("foo").get();
 *   List<BigDecimal> list = Gen.math().bigDecimal().list(10);
 * }</pre>
 *
 * <p>where {@link Gen} is the entry point for generating various types of values.
 *
 * @param <T> type of value
 * @since 2.6.0
 */
public interface ValueSpec<T> extends GeneratorSpec<T> {

    /**
     * Generates a single value.
     *
     * @return a random value
     * @since 2.6.0
     */
    @SuppressWarnings("unchecked")
    default T get() {
        final Settings settings = ObjectUtils.defaultIfNull(
                ThreadLocalSettings.getInstance().get(),
                Global::getPropertiesFileSettings);

        // Shorthand API does not support withSeed() method
        final Random random = RandomHelper.resolveRandom(
                settings.get(Keys.SEED), /* withSeed = */ null);

        return ((Generator<T>) this).generate(random);
    }

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
     * <p>
     * Since the stream is infinite, {@link Stream#limit(long)} must be called
     * to avoid an infinite loop.
     *
     * @return an infinite stream of values
     * @since 2.6.0
     */
    default Stream<T> stream() {
        return Stream.generate(this::get);
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
