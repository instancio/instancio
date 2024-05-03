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

import java.time.LocalTime;
import java.time.temporal.TemporalUnit;

/**
 * Generator spec for {@link LocalTime}.
 *
 * @since 4.2.0
 */
public interface LocalTimeGeneratorAsSpec extends
        TemporalAsGeneratorSpec<LocalTime>,
        TruncatableTemporalSpec<LocalTime> {

    /**
     * {@inheritDoc}
     */
    @Override
    LocalTimeGeneratorAsSpec past();

    /**
     * {@inheritDoc}
     */
    @Override
    LocalTimeGeneratorAsSpec future();

    /**
     * {@inheritDoc}
     */
    @Override
    LocalTimeGeneratorAsSpec range(LocalTime min, LocalTime max);

    /**
     * {@inheritDoc}
     */
    @Override
    LocalTimeGeneratorAsSpec truncatedTo(TemporalUnit unit);

    /**
     * {@inheritDoc}
     */
    @Override
    LocalTimeGeneratorAsSpec nullable();
}
