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
package org.instancio.internal.util;

import java.util.Objects;

public final class Range<T> {

    private final T min;
    private final T max;

    private Range(final T min, final T max) {
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
    public static <T> Range<T> of(final T min, final T max) {
        return new Range<>(min, max);
    }

    public T min() {
        return min;
    }

    public T max() {
        return max;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Range<?> range)) return false;
        return Objects.equals(min, range.min) && Objects.equals(max, range.max);
    }

    @Override
    public int hashCode() {
        return Objects.hash(min, max);
    }

    @Override
    public String toString() {
        return String.format("Range[%s, %s]", min, max);
    }
}
