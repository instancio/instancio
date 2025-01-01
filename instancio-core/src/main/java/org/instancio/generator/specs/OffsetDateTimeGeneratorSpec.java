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

import java.time.OffsetDateTime;
import java.time.temporal.TemporalUnit;

/**
 * Generator spec for {@link OffsetDateTime}.
 *
 * @since 4.2.0
 */
public interface OffsetDateTimeGeneratorSpec extends
        TemporalGeneratorSpec<OffsetDateTime>,
        TruncatableTemporalGeneratorSpec<OffsetDateTime> {

    /**
     * {@inheritDoc}
     */
    @Override
    @NonDeterministic
    OffsetDateTimeGeneratorSpec past();

    /**
     * {@inheritDoc}
     */
    @Override
    @NonDeterministic
    OffsetDateTimeGeneratorSpec future();

    /**
     * {@inheritDoc}
     *
     * @since 4.6.0
     */
    @Override
    OffsetDateTimeGeneratorSpec min(OffsetDateTime min);

    /**
     * {@inheritDoc}
     *
     * @since 4.6.0
     */
    @Override
    OffsetDateTimeGeneratorSpec max(OffsetDateTime max);

    /**
     * {@inheritDoc}
     */
    @Override
    OffsetDateTimeGeneratorSpec range(OffsetDateTime min, OffsetDateTime max);

    /**
     * {@inheritDoc}
     */
    @Override
    OffsetDateTimeGeneratorSpec truncatedTo(TemporalUnit unit);

    /**
     * {@inheritDoc}
     */
    @Override
    OffsetDateTimeGeneratorSpec nullable();
}
