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

import org.instancio.generator.AsStringGeneratorSpec;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

/**
 * Generator spec for {@link java.time.temporal.Temporal} types.
 *
 * @param <T> temporal type
 * @since 1.1.2
 */
public interface TemporalGeneratorSpec<T> extends AsStringGeneratorSpec<T> {

    ZoneOffset ZONE_OFFSET = OffsetDateTime.now().getOffset();
    Instant DEFAULT_MIN = LocalDateTime.of(1970, 1, 1, 0, 0).toInstant(ZONE_OFFSET);
    Instant DEFAULT_MAX = LocalDateTime.now().plusYears(50).truncatedTo(ChronoUnit.DAYS).toInstant(ZONE_OFFSET);

    /**
     * Generate a temporal value in the past.
     *
     * @return spec builder
     */
    TemporalGeneratorSpec<T> past();

    /**
     * Generate a temporal value in the future.
     *
     * @return spec builder
     */
    TemporalGeneratorSpec<T> future();

    /**
     * Generate a temporal value between the given range.
     *
     * @param start start date (inclusive)
     * @param end   end date (inclusive)
     * @return spec builder
     */
    TemporalGeneratorSpec<T> range(T start, T end);
}
