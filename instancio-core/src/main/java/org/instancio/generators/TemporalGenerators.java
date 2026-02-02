/*
 * Copyright 2022-2026 the original author or authors.
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

import org.instancio.generator.specs.DurationGeneratorSpec;
import org.instancio.generator.specs.InstantGeneratorSpec;
import org.instancio.generator.specs.LocalDateTimeGeneratorSpec;
import org.instancio.generator.specs.LocalTimeGeneratorSpec;
import org.instancio.generator.specs.MonthDayGeneratorSpec;
import org.instancio.generator.specs.OffsetDateTimeGeneratorSpec;
import org.instancio.generator.specs.OffsetTimeGeneratorSpec;
import org.instancio.generator.specs.PeriodGeneratorSpec;
import org.instancio.generator.specs.TemporalGeneratorSpec;
import org.instancio.generator.specs.ZonedDateTimeGeneratorSpec;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;

/**
 * Contains built-in temporal generators.
 *
 * @since 1.4.0
 */
public interface TemporalGenerators {

    /**
     * Customises generated {@link Instant} values.
     *
     * @return API builder reference
     */
    InstantGeneratorSpec instant();

    /**
     * Customises generated {@link LocalDate} values.
     *
     * @return API builder reference
     */
    TemporalGeneratorSpec<LocalDate> localDate();

    /**
     * Customises generated {@link LocalTime} values.
     *
     * @return API builder reference
     */
    LocalTimeGeneratorSpec localTime();

    /**
     * Customises generated {@link LocalDateTime} values.
     *
     * @return API builder reference
     */
    LocalDateTimeGeneratorSpec localDateTime();

    /**
     * Customises generated {@link MonthDay} values.
     *
     * @return API builder reference
     * @since 2.3.0
     */
    MonthDayGeneratorSpec monthDay();

    /**
     * Customises generated {@link OffsetTime} values.
     *
     * @return API builder reference
     * @since 2.4.0
     */
    OffsetTimeGeneratorSpec offsetTime();

    /**
     * Customises generated {@link OffsetDateTime} values.
     *
     * @return API builder reference
     * @since 2.4.0
     */
    OffsetDateTimeGeneratorSpec offsetDateTime();

    /**
     * Customises generated {@link ZonedDateTime} values.
     *
     * @return API builder reference
     */
    ZonedDateTimeGeneratorSpec zonedDateTime();

    /**
     * Customises generated {@link YearMonth} values.
     *
     * @return API builder reference
     */
    TemporalGeneratorSpec<YearMonth> yearMonth();

    /**
     * Customises generated {@link Year} values.
     *
     * @return API builder reference
     */
    TemporalGeneratorSpec<Year> year();

    /**
     * Customises returned {@link java.time.Duration} value.
     *
     * @return API builder reference
     */
    DurationGeneratorSpec duration();

    /**
     * Customises returned {@link java.time.Period} values.
     *
     * @return API builder reference
     */
    PeriodGeneratorSpec period();

    /**
     * Customises generated {@link Date} values.
     *
     * @return API builder reference
     */
    TemporalGeneratorSpec<Date> date();

    /**
     * Customises generated {@link java.sql.Date} values.
     *
     * @return API builder reference
     */
    TemporalGeneratorSpec<java.sql.Date> sqlDate();

    /**
     * Customises generated {@link Timestamp} values.
     *
     * @return API builder reference
     */
    TemporalGeneratorSpec<Timestamp> timestamp();

    /**
     * Customises generated {@link Calendar} values.
     *
     * @return API builder reference
     */
    TemporalGeneratorSpec<Calendar> calendar();
}
