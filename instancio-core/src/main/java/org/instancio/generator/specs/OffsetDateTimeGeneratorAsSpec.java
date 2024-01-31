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

import java.time.OffsetDateTime;
import java.time.temporal.TemporalUnit;

/**
 * Generator spec for {@link OffsetDateTime}.
 *
 * @since 4.2.0
 */
public interface OffsetDateTimeGeneratorAsSpec extends
        TemporalAsGeneratorSpec<OffsetDateTime>,
        TruncatableTemporalSpec<OffsetDateTime> {

    /**
     * {@inheritDoc}
     */
    @Override
    OffsetDateTimeGeneratorAsSpec past();

    /**
     * {@inheritDoc}
     */
    @Override
    OffsetDateTimeGeneratorAsSpec future();

    /**
     * {@inheritDoc}
     */
    @Override
    OffsetDateTimeGeneratorAsSpec range(OffsetDateTime start, OffsetDateTime end);

    /**
     * {@inheritDoc}
     */
    @Override
    OffsetDateTimeGeneratorAsSpec truncatedTo(TemporalUnit unit);

    /**
     * {@inheritDoc}
     */
    @Override
    OffsetDateTimeGeneratorAsSpec nullable();
}
