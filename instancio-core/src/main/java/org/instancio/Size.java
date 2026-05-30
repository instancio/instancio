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
import org.instancio.internal.InternalSize;

/**
 * Represents the size (or size range) of a collection, array, or map.
 *
 * <p>Instances are created via the factory methods:
 * <ul>
 *   <li>{@link #of(int)} — exact size</li>
 *   <li>{@link #range(int, int)} — random size within the specified range</li>
 * </ul>
 *
 * @see InstancioApi#size(TargetSelector, int)
 * @see InstancioApi#size(TargetSelector, Size)
 * @since 6.0.0
 */
@ExperimentalApi
public sealed interface Size permits InternalSize {

    /**
     * Creates a {@code Size} with an exact value.
     *
     * @param size the exact size
     * @return a {@code Size} representing the given exact size
     * @since 6.0.0
     */
    @ExperimentalApi
    static Size of(final int size) {
        return new InternalSize(size, size);
    }

    /**
     * Creates a {@code Size} representing a random value within the specified range.
     *
     * @param min the minimum size (inclusive)
     * @param max the maximum size (inclusive)
     * @return a {@code Size} representing the given range
     * @since 6.0.0
     */
    @ExperimentalApi
    static Size range(final int min, final int max) {
        return new InternalSize(min, max);
    }
}
