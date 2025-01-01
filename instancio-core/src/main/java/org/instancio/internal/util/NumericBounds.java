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
package org.instancio.internal.util;

import java.math.BigDecimal;

public class NumericBounds {

    private static final BigDecimal INTEGER_ZERO_MIN = new BigDecimal("-0.9");
    private static final BigDecimal INTEGER_ZERO_MAX = new BigDecimal("0.9");

    private final BigDecimal min;
    private final BigDecimal max;

    public NumericBounds(BigDecimal min, BigDecimal max) {
        this.min = min;
        this.max = max;
    }

    public BigDecimal getMin() {
        return min;
    }

    public BigDecimal getMax() {
        return max;
    }

    public NumericBounds updateBounds(int integer) {
        if (integer == 0) {
            return new NumericBounds(INTEGER_ZERO_MIN, INTEGER_ZERO_MAX);
        }

        // Calculate the lower and upper bounds based on the number of digits
        final BigDecimal lowerBound = BigDecimal.TEN.pow(integer).negate().add(BigDecimal.ONE);
        final BigDecimal upperBound = BigDecimal.TEN.pow(integer).subtract(BigDecimal.ONE);

        // Ensure the new min and max are trimmed to fit within the range.
        final BigDecimal newMin = min.max(lowerBound);
        final BigDecimal newMax = max.max(lowerBound).min(upperBound);

        return new NumericBounds(newMin, newMax);
    }
}
