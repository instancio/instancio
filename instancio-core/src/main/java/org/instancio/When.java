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
package org.instancio;

import org.instancio.documentation.ExperimentalApi;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * Provides convenience methods for creating {@link Predicate Predicates}.
 *
 * @since 3.0.0
 */
@ExperimentalApi
public final class When {

    /**
     * A predicate that returns {@code true} if values are equal
     * using {@link Objects#equals(Object, Object)}.
     *
     * @param value the value to compare against
     * @param <T>   value type
     * @return predicate that checks that values are equal
     * @since 3.0.0
     */
    @ExperimentalApi
    public static <T extends @Nullable Object> Predicate<T> is(final T value) {
        return v -> Objects.equals(v, value);
    }

    /**
     * A predicate that returns {@code true} if values are <b>not</b> equal
     * using {@link Objects#equals(Object, Object)}.
     *
     * @param value the value to compare against
     * @param <T>   value type
     * @return predicate that checks that values are not equal
     * @since 3.0.0
     */
    @ExperimentalApi
    public static <T extends @Nullable Object> Predicate<T> isNot(final T value) {
        return v -> !Objects.equals(v, value);
    }

    /**
     * A predicate that checks if a value is {@code null}.
     *
     * @param <T> value type
     * @return predicate that checks if a value is {@code null}
     * @since 3.0.0
     */
    @ExperimentalApi
    public static <T extends @Nullable Object> Predicate<T> isNull() {
        return Objects::isNull;
    }

    /**
     * A predicate that checks if a value is not {@code null}.
     *
     * @param <T> value type
     * @return predicate that checks if a value is not {@code null}
     * @since 3.0.0
     */
    @ExperimentalApi
    public static <T extends @Nullable Object> Predicate<T> isNotNull() {
        return Objects::nonNull;
    }

    /**
     * A predicate that checks whether a value is equal to any element in
     * the {@code values} array using {@link Objects#equals(Object, Object)}.
     *
     * @param values the value to compare against
     * @param <T>    value type
     * @return predicate that checks if a value is equal to
     * any element of the input array
     * @since 3.0.0
     */
    @SafeVarargs
    @ExperimentalApi
    public static <T extends @Nullable Object> Predicate<T> isIn(final T... values) {
        return value -> {
            for (T v : values) {
                if (Objects.equals(v, value)) {
                    return true;
                }
            }
            return false;
        };
    }

    private When() {
        // non-instantiable
    }
}