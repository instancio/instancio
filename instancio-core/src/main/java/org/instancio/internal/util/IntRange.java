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
package org.instancio.internal.util;

public final class IntRange {
    private final int min;
    private final int max;

    private IntRange(final int min, final int max) {
        this.min = min;
        this.max = max;
    }

    /**
     * Range with the specified lower and upper bound.
     *
     * @param min lower bound
     * @param max upper bound
     * @return range with given bounds
     */
    public static IntRange range(final int min, final int max) {
        Verify.isTrue(min <= max,
                "Min must be less than or equal to max: (%s, %s)", min, max);
        return new IntRange(min, max);
    }

    public int min() {
        return min;
    }

    public int max() {
        return max;
    }

    @Override
    public String toString() {
        return String.format("Range[%s, %s]", min, max);
    }
}
