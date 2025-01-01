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
package org.instancio.generator.specs;

import org.instancio.documentation.ExperimentalApi;
import org.instancio.generator.ValueSpec;

import java.util.function.UnaryOperator;

/**
 * Spec for generating numeric sequences.
 *
 * @param <T> the number type
 * @since 2.13.0
 */
@ExperimentalApi
public interface NumericSequenceSpec<T extends Number & Comparable<T>>
        extends ValueSpec<T>, NumericSequenceGeneratorSpec<T> {

    @Override
    NumericSequenceSpec<T> start(T start);

    @Override
    NumericSequenceSpec<T> next(UnaryOperator<T> next);

    @Override
    NumericSequenceSpec<T> nullable();
}
