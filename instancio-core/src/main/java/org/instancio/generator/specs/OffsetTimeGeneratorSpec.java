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

import java.time.OffsetTime;
import java.time.temporal.TemporalUnit;

/**
 * Generator spec for {@link OffsetTime}.
 *
 * @since 4.2.0
 */
public interface OffsetTimeGeneratorSpec extends
        TemporalGeneratorSpec<OffsetTime>,
        TruncatableTemporalGeneratorSpec<OffsetTime> {

    /**
     * {@inheritDoc}
     */
    @Override
    @NonDeterministic
    OffsetTimeGeneratorSpec past();

    /**
     * {@inheritDoc}
     */
    @Override
    @NonDeterministic
    OffsetTimeGeneratorSpec future();

    /**
     * {@inheritDoc}
     *
     * @since 4.6.0
     */
    @Override
    OffsetTimeGeneratorSpec min(OffsetTime min);

    /**
     * {@inheritDoc}
     *
     * @since 4.6.0
     */
    @Override
    OffsetTimeGeneratorSpec max(OffsetTime max);

    /**
     * {@inheritDoc}
     */
    @Override
    OffsetTimeGeneratorSpec range(OffsetTime min, OffsetTime max);

    /**
     * {@inheritDoc}
     */
    @Override
    OffsetTimeGeneratorSpec truncatedTo(TemporalUnit unit);

    /**
     * {@inheritDoc}
     */
    @Override
    OffsetTimeGeneratorSpec nullable();
}
