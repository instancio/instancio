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

import org.instancio.documentation.NonDeterministic;

import java.time.Instant;
import java.time.temporal.TemporalUnit;

/**
 * Generator spec for {@link java.time.Instant}.
 *
 * @since 4.2.0
 */
public interface InstantGeneratorSpec extends
        TemporalGeneratorSpec<Instant>,
        TruncatableTemporalGeneratorSpec<Instant> {

    /**
     * {@inheritDoc}
     */
    @Override
    @NonDeterministic
    InstantGeneratorSpec past();

    /**
     * {@inheritDoc}
     */
    @Override
    @NonDeterministic
    InstantGeneratorSpec future();

    /**
     * {@inheritDoc}
     *
     * @since 4.6.0
     */
    @Override
    InstantGeneratorSpec min(Instant min);

    /**
     * {@inheritDoc}
     *
     * @since 4.6.0
     */
    @Override
    InstantGeneratorSpec max(Instant max);

    /**
     * {@inheritDoc}
     */
    @Override
    InstantGeneratorSpec range(Instant min, Instant max);

    /**
     * {@inheritDoc}
     */
    @Override
    InstantGeneratorSpec truncatedTo(TemporalUnit unit);

    /**
     * {@inheritDoc}
     */
    @Override
    InstantGeneratorSpec nullable();
}
