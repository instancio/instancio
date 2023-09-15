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

/**
 * Generator spec for numeric types.
 *
 * @param <T> type of number
 * @since 1.0.1
 */
public interface NumberGeneratorSpec<T extends Number> extends NullableGeneratorSpec<T> {

    /**
     * Specifies the lower bound.
     *
     * @param min lower bound (inclusive)
     * @return spec builder
     * @since 1.0.1
     */
    NumberGeneratorSpec<T> min(T min);

    /**
     * Specifies the upper bound.
     *
     * @param max upper bound (inclusive)
     * @return spec builder
     * @since 1.0.1
     */
    NumberGeneratorSpec<T> max(T max);

    /**
     * Specifies the range for generated numbers.
     *
     * <p>The following examples use {@code int}, however the same
     * principle applies to all numeric types, including
     * {@code BigInteger} and {@code BigDecimal}.
     *
     * <p>This method can be invoked multiple times to specify several
     * ranges, for example, the following will generate a random value
     * within {@code [10-15]} or {@code [20-25]}:
     *
     * <pre>{@code
     *   ints().range(10, 15).range(20, 25)
     * }</pre>
     *
     * <b>Note:</b> this method has higher precedence than {@link #min(Number)}
     * and {@link #max(Number)}. For example, the following will generate
     * a number within {@code [1, 5]}:
     *
     * <pre>{@code
     *   ints().range(1, 5).min(95).max(99)
     * }</pre>
     *
     * @param min lower bound (inclusive)
     * @param max upper bound (inclusive)
     * @return spec builder
     * @since 1.1.2
     */
    NumberGeneratorSpec<T> range(T min, T max);

    /**
     * Specifies that a {@code null} can be generated.
     *
     * @return spec builder
     * @since 1.0.1
     */
    @Override
    NumberGeneratorSpec<T> nullable();
}
