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

import java.time.Instant;
import java.time.temporal.TemporalUnit;

/**
 * Spec for generating {@link Instant} values.
 *
 * @since 2.6.0
 */
public interface InstantSpec extends
        TemporalSpec<Instant>,
        InstantGeneratorSpec,
        TruncatableTemporalGeneratorSpec<Instant> {

    /**
     * {@inheritDoc}
     */
    @Override
    @NonDeterministic
    InstantSpec past();

    /**
     * {@inheritDoc}
     */
    @Override
    @NonDeterministic
    InstantSpec future();

    /**
     * {@inheritDoc}
     *
     * @since 4.6.0
     */
    @Override
    InstantSpec min(Instant min);

    /**
     * {@inheritDoc}
     *
     * @since 4.6.0
     */
    @Override
    InstantSpec max(Instant max);

    /**
     * {@inheritDoc}
     */
    @Override
    InstantSpec range(Instant min, Instant max);

    /**
     * {@inheritDoc}
     *
     * @since 4.2.0
     */
    @Override
    InstantSpec truncatedTo(TemporalUnit unit);

    /**
     * {@inheritDoc}
     *
     * @since 2.7.0
     */
    @Override
    InstantSpec nullable();
}
