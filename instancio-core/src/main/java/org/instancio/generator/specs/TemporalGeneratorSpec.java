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
package org.instancio.generator.specs;

/**
 * Generator spec for {@link java.time.temporal.Temporal} types.
 *
 * @param <T> temporal type
 * @since 1.1.2
 */
public interface TemporalGeneratorSpec<T> extends NullableGeneratorSpec<T> {

    /**
     * Generate a value in the past.
     *
     * @return spec builder
     */
    TemporalGeneratorSpec<T> past();

    /**
     * Generate a value in the future.
     *
     * @return spec builder
     */
    TemporalGeneratorSpec<T> future();

    /**
     * Generate a value within the given range.
     *
     * @param start lower bound (inclusive)
     * @param end   upper bound (inclusive)
     * @return spec builder
     */
    TemporalGeneratorSpec<T> range(T start, T end);

    /**
     * {@inheritDoc}
     *
     * @since 2.7.0
     */
    @Override
    NullableGeneratorSpec<T> nullable();
}
