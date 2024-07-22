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
package org.instancio.generator.specs;

import org.instancio.IntervalSupplier;
import org.instancio.RandomUnaryOperator;
import org.instancio.documentation.ExperimentalApi;
import org.instancio.generator.ValueSpec;

/**
 * Spec for generating intervals of various types,
 * such as numeric and temporal values.
 *
 * @param <T> the type of value that defines the interval
 * @since 5.0.0
 */
@ExperimentalApi
public interface IntervalSpec<T> extends ValueSpec<IntervalSupplier<T>> {

    /**
     * Specifies the function for calculating the {@code start}
     * value of the next interval based on the {@code end}
     * value of the current interval.
     *
     * @param nextStartFn function for calculating the starting
     *                    value of the next interval
     * @return API builder reference
     * @since 5.0.0
     */
    @ExperimentalApi
    IntervalSpec<T> nextStart(RandomUnaryOperator<T> nextStartFn);

    /**
     * Specifies the function for calculating the {@code end}
     * value of the interval based on the {@code start}
     * value of the current interval.
     *
     * @param nextEndFn function for calculating the {@code end}
     *                  value of the interval
     * @return API builder reference
     * @since 5.0.0
     */
    @ExperimentalApi
    IntervalSpec<T> nextEnd(RandomUnaryOperator<T> nextEndFn);

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    IntervalSpec<T> nullable();
}
