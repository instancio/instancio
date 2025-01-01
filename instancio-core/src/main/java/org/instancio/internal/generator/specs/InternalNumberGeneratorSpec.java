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
package org.instancio.internal.generator.specs;

import org.instancio.documentation.InternalApi;
import org.instancio.generator.specs.NumberGeneratorSpec;

import java.math.BigDecimal;

/**
 * Internal API for generating numbers.
 *
 * @since 5.2.1
 */
@InternalApi
public interface InternalNumberGeneratorSpec<T extends Number> extends NumberGeneratorSpec<T> {

    /**
     * Ensures that the current minimum value is greater than or equal to the
     * specified {@code otherMin} value. If the specified {@code otherMin}
     * value is greater than the current minimum, the minimum value is updated.
     *
     * @param otherMin the reference value to compare with the current minimum
     */
    void ensureMinIsGreaterThanOrEqualTo(BigDecimal otherMin);

    /**
     * Ensures that the current maximum value is less than or equal to the
     * specified {@code otherMax} value. If the specified {@code otherMax}
     * value is less than the current maximum, the maximum value is updated.
     *
     * @param otherMax the reference value to compare with the current maximum
     */
    void ensureMaxIsLessThanOrEqualTo(BigDecimal otherMax);

    /**
     * Updates the minimum and maximum bounds of the numeric range for the generator
     * based on the integer value provided. This ensures the bounds are adjusted
     * within the valid range of the target numeric type.
     *
     * <p>For example, given {@code integerMax} value of 2,
     * the maximum bounds are {@code [-99, 99]}.
     *
     * @param integerMax the number of digits to define the new maximum bounds for
     *                   the numeric range. This value will influence the range's
     *                   upper and lower limits.
     */
    void integerMax(int integerMax);
}
