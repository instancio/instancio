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
package org.instancio.generator.specs;

import org.instancio.documentation.ExperimentalApi;

import java.util.function.UnaryOperator;

/**
 * A spec for generating numeric sequences.
 *
 * @param <T> the number type
 * @since 2.13.0
 */
@ExperimentalApi
public interface NumericSequenceGeneratorSpec<T extends Number & Comparable<T>>
        extends AsGeneratorSpec<T>, NullableGeneratorSpec<T> {

    /**
     * Specifies the starting value of the sequence.
     * The default is {@code 1}.
     *
     * @param start the initial value of the sequence
     * @return spec builder
     * @since 2.13.0
     */
    NumericSequenceGeneratorSpec<T> start(T start);

    /**
     * Specifies the function for calculating the next sequence value.
     * If unspecified, the default is to increment the sequence by {@code 1}.
     *
     * @param next a function for calculating the next value
     * @return spec builder
     * @since 2.13.0
     */
    NumericSequenceGeneratorSpec<T> next(UnaryOperator<T> next);

    /**
     * {@inheritDoc}
     *
     * @since 2.13.0
     */
    @Override
    NumericSequenceGeneratorSpec<T> nullable();
}
