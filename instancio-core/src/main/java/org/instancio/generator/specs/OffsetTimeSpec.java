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

import java.time.OffsetTime;
import java.time.temporal.TemporalUnit;

/**
 * Spec for generating {@link OffsetTime} values.
 *
 * @since 2.6.0
 */
public interface OffsetTimeSpec extends
        TemporalSpec<OffsetTime>,
        OffsetTimeGeneratorSpec,
        TruncatableTemporalGeneratorSpec<OffsetTime> {

    /**
     * {@inheritDoc}
     */
    @Override
    OffsetTimeSpec past();

    /**
     * {@inheritDoc}
     */
    @Override
    OffsetTimeSpec future();

    /**
     * {@inheritDoc}
     *
     * @since 4.6.0
     */
    @Override
    OffsetTimeSpec min(OffsetTime min);

    /**
     * {@inheritDoc}
     *
     * @since 4.6.0
     */
    @Override
    OffsetTimeSpec max(OffsetTime max);

    /**
     * {@inheritDoc}
     */
    @Override
    OffsetTimeSpec range(OffsetTime min, OffsetTime max);

    /**
     * {@inheritDoc}
     *
     * @since 4.2.0
     */
    @Override
    OffsetTimeSpec truncatedTo(TemporalUnit unit);

    /**
     * {@inheritDoc}
     *
     * @since 2.7.0
     */
    @Override
    OffsetTimeSpec nullable();
}
