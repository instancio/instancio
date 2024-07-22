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
package org.instancio;

import org.instancio.documentation.ExperimentalApi;

import java.util.function.Supplier;

/**
 * A supplier of {@code start} and {@code end} interval values.
 *
 * @param <T> the type of value underlying the interval
 * @see InstancioGenApi#intervalStarting(Object)
 * @since 5.0.0
 */
@ExperimentalApi
public interface IntervalSupplier<T> {

    /**
     * Returns a supplier that produces interval start value.
     *
     * @return a supplier for interval start values
     * @since 5.0.0
     */
    @ExperimentalApi
    Supplier<T> start();

    /**
     * Returns a supplier that produces interval end value.
     *
     * @return a supplier for interval end values
     * @since 5.0.0
     */
    @ExperimentalApi
    Supplier<T> end();
}
