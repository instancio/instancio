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
package org.instancio.internal.annotation;

import org.instancio.generator.GeneratorSpec;
import org.instancio.internal.generator.AbstractGenerator;
import org.instancio.internal.util.Constants;
import org.instancio.internal.util.Fail;
import org.instancio.internal.util.Range;

final class AnnotationUtils {

    private AnnotationUtils() {
        // non-instantiable
    }

    static void setSpecNullableToFalse(final GeneratorSpec<?> spec) {
        if (spec instanceof AbstractGenerator) {
            ((AbstractGenerator<?>) spec).nullable(false);
        }
    }

    /**
     * Attempts to calculate a reasonable range for the given min/max values.
     * For example, the {@code Size} annotation has a default max value
     * of {@link Integer#MAX_VALUE}.
     * <p>
     * Note: if min is 0, will return min 1, unless max is also zero.
     *
     * @param min      value specified by an annotation
     * @param max      value specified by an annotation
     * @param maxLimit when min is zero, limit max to this value
     * @return a range containing the same values as provided, or
     * an updated range with new max, smaller than the original.
     */
    static Range<Integer> calculateRange(final int min, final int max, final int maxLimit) {
        if (min > max) {
            throw Fail.withUsageError("invalid bean validation annotation: " +
                    "min must be less than or equal to max: min=%s, max=%s", min, max);
        }

        final int minSize;
        final int maxSize;

        if (max == 0) {
            minSize = 0;
            maxSize = 0;
        } else if (min <= 1) {
            minSize = 1; // by default, don't generate empty values!
            maxSize = Math.min(max, maxLimit);
        } else if (isMoreThanDouble(min, max)) {
            final int tmpMax = (min * (100 + Constants.RANGE_ADJUSTMENT_PERCENTAGE)) / 100;
            minSize = min;
            maxSize = Math.min(max, tmpMax);
        } else {
            minSize = min;
            maxSize = max;
        }
        return Range.of(minSize, maxSize);
    }

    private static boolean isMoreThanDouble(final int min, final int max) {
        return max - min - min > 0;
    }
}
