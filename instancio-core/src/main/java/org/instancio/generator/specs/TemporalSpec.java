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

import org.instancio.documentation.NonDeterministic;
import org.instancio.generator.ValueSpec;

import java.time.temporal.Temporal;

/**
 * Spec for generating {@link Temporal} types.
 *
 * @param <T> temporal type
 * @since 2.6.0
 */
public interface TemporalSpec<T> extends ValueSpec<T>, TemporalGeneratorSpec<T> {

    /**
     * {@inheritDoc}
     */
    @Override
    @NonDeterministic
    TemporalSpec<T> past();

    /**
     * {@inheritDoc}
     */
    @Override
    @NonDeterministic
    TemporalSpec<T> future();

    /**
     * {@inheritDoc}
     *
     * @since 4.6.0
     */
    @Override
    TemporalSpec<T> min(T min);

    /**
     * {@inheritDoc}
     *
     * @since 4.6.0
     */
    @Override
    TemporalSpec<T> max(T max);

    /**
     * {@inheritDoc}
     */
    @Override
    TemporalSpec<T> range(T min, T max);

    /**
     * {@inheritDoc}
     *
     * @since 2.7.0
     */
    @Override
    TemporalSpec<T> nullable();
}
