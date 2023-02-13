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
package org.instancio.generators;

import org.instancio.generator.specs.DurationSpec;
import org.instancio.generator.specs.InstantSpec;
import org.instancio.generator.specs.LocalDateSpec;
import org.instancio.generator.specs.LocalDateTimeSpec;
import org.instancio.generator.specs.LocalTimeSpec;
import org.instancio.generator.specs.OffsetDateTimeSpec;
import org.instancio.generator.specs.OffsetTimeSpec;
import org.instancio.generator.specs.YearMonthSpec;
import org.instancio.generator.specs.YearSpec;
import org.instancio.generator.specs.ZonedDateTimeSpec;
import org.instancio.internal.generator.time.DurationGenerator;
import org.instancio.internal.generator.time.InstantGenerator;
import org.instancio.internal.generator.time.LocalDateGenerator;
import org.instancio.internal.generator.time.LocalDateTimeGenerator;
import org.instancio.internal.generator.time.LocalTimeGenerator;
import org.instancio.internal.generator.time.OffsetDateTimeGenerator;
import org.instancio.internal.generator.time.OffsetTimeGenerator;
import org.instancio.internal.generator.time.YearGenerator;
import org.instancio.internal.generator.time.YearMonthGenerator;
import org.instancio.internal.generator.time.ZonedDateTimeGenerator;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZonedDateTime;

/**
 * Provides generators for {@code java.time} classes.
 *
 * @since 2.6.0
 */
@SuppressWarnings("PMD.ExcessiveImports")
public final class TemporalSpecs {

    /**
     * Generates {@link Duration} values.
     *
     * @return API builder reference
     * @since 2.9.0
     */
    public DurationSpec duration() {
        return new DurationGenerator();
    }

    /**
     * Generates {@link Instant} values.
     *
     * @return API builder reference
     * @since 2.6.0
     */
    public InstantSpec instant() {
        return new InstantGenerator();
    }

    /**
     * Generates {@link LocalDate} values.
     *
     * @return API builder reference
     * @since 2.6.0
     */
    public LocalDateSpec localDate() {
        return new LocalDateGenerator();
    }

    /**
     * Generates {@link LocalDateTime} values.
     *
     * @return API builder reference
     * @since 2.6.0
     */
    public LocalDateTimeSpec localDateTime() {
        return new LocalDateTimeGenerator();
    }

    /**
     * Generates {@link LocalTime} values.
     *
     * @return API builder reference
     * @since 2.6.0
     */
    public LocalTimeSpec localTime() {
        return new LocalTimeGenerator();
    }

    /**
     * Generates {@link OffsetDateTime} values.
     *
     * @return API builder reference
     * @since 2.6.0
     */
    public OffsetDateTimeSpec offsetDateTime() {
        return new OffsetDateTimeGenerator();
    }

    /**
     * Generates {@link OffsetTime} values.
     *
     * @return API builder reference
     * @since 2.6.0
     */
    public OffsetTimeSpec offsetTime() {
        return new OffsetTimeGenerator();
    }

    /**
     * Generates {@link Year} values.
     *
     * @return API builder reference
     * @since 2.6.0
     */
    public YearSpec year() {
        return new YearGenerator();
    }

    /**
     * Generates {@link YearMonth} values.
     *
     * @return API builder reference
     * @since 2.6.0
     */
    public YearMonthSpec yearMonth() {
        return new YearMonthGenerator();
    }

    /**
     * Generates {@link ZonedDateTime} values.
     *
     * @return API builder reference
     * @since 2.6.0
     */
    public ZonedDateTimeSpec zonedDateTime() {
        return new ZonedDateTimeGenerator();
    }
}
