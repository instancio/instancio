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

import org.instancio.generator.GeneratorContext;
import org.instancio.generator.specs.DurationGeneratorSpec;
import org.instancio.generator.specs.MonthDayGeneratorSpec;
import org.instancio.generator.specs.PeriodGeneratorSpec;
import org.instancio.generator.specs.TemporalAaGeneratorSpec;
import org.instancio.generator.specs.TemporalGeneratorSpec;
import org.instancio.internal.generator.sql.SqlDateGenerator;
import org.instancio.internal.generator.sql.TimestampGenerator;
import org.instancio.internal.generator.time.DurationGenerator;
import org.instancio.internal.generator.time.InstantGenerator;
import org.instancio.internal.generator.time.LocalDateGenerator;
import org.instancio.internal.generator.time.LocalDateTimeGenerator;
import org.instancio.internal.generator.time.LocalTimeGenerator;
import org.instancio.internal.generator.time.MonthDayGenerator;
import org.instancio.internal.generator.time.OffsetDateTimeGenerator;
import org.instancio.internal.generator.time.OffsetTimeGenerator;
import org.instancio.internal.generator.time.PeriodGenerator;
import org.instancio.internal.generator.time.YearGenerator;
import org.instancio.internal.generator.time.YearMonthGenerator;
import org.instancio.internal.generator.time.ZonedDateTimeGenerator;
import org.instancio.internal.generator.util.CalendarGenerator;
import org.instancio.internal.generator.util.DateGenerator;

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
@SuppressWarnings("PMD.ExcessiveImports")
public class TemporalGenerators {

    private final GeneratorContext context;

    public TemporalGenerators(final GeneratorContext context) {
        this.context = context;
    }

    /**
     * Customises generated {@link Instant} values.
     *
     * @return customised generator
     */
    public TemporalAaGeneratorSpec<Instant> instant() {
        return new InstantGenerator(context);
    }

    /**
     * Customises generated {@link LocalDate} values.
     *
     * @return customised generator
     */
    public TemporalAaGeneratorSpec<LocalDate> localDate() {
        return new LocalDateGenerator(context);
    }

    /**
     * Customises generated {@link LocalTime} values.
     *
     * @return customised generator
     */
    public TemporalAaGeneratorSpec<LocalTime> localTime() {
        return new LocalTimeGenerator(context);
    }

    /**
     * Customises generated {@link LocalDateTime} values.
     *
     * @return customised generator
     */
    public TemporalAaGeneratorSpec<LocalDateTime> localDateTime() {
        return new LocalDateTimeGenerator(context);
    }

    /**
     * Customises generated {@link MonthDay} values.
     *
     * @return customised generator
     * @since 2.3.0
     */
    public MonthDayGeneratorSpec monthDay() {
        return new MonthDayGenerator(context);
    }

    /**
     * Customises generated {@link OffsetTime} values.
     *
     * @return customised generator
     * @since 2.4.0
     */
    public TemporalAaGeneratorSpec<OffsetTime> offsetTime() {
        return new OffsetTimeGenerator(context);
    }

    /**
     * Customises generated {@link OffsetDateTime} values.
     *
     * @return customised generator
     * @since 2.4.0
     */
    public TemporalAaGeneratorSpec<OffsetDateTime> offsetDateTime() {
        return new OffsetDateTimeGenerator(context);
    }

    /**
     * Customises generated {@link ZonedDateTime} values.
     *
     * @return customised generator
     */
    public TemporalAaGeneratorSpec<ZonedDateTime> zonedDateTime() {
        return new ZonedDateTimeGenerator(context);
    }

    /**
     * Customises generated {@link YearMonth} values.
     *
     * @return customised generator
     */
    public TemporalAaGeneratorSpec<YearMonth> yearMonth() {
        return new YearMonthGenerator(context);
    }

    /**
     * Customises generated {@link Year} values.
     *
     * @return customised generator
     */
    public TemporalAaGeneratorSpec<Year> year() {
        return new YearGenerator(context);
    }

    /**
     * Customises returned {@link java.time.Duration} value.
     *
     * @return customised generator
     */
    public DurationGeneratorSpec duration() {
        return new DurationGenerator(context);
    }

    /**
     * Customises returned {@link java.time.Period} values.
     *
     * @return customised generator
     */
    public PeriodGeneratorSpec period() {
        return new PeriodGenerator(context);
    }

    /**
     * Customises generated {@link Date} values.
     *
     * @return customised generator
     */
    public TemporalGeneratorSpec<Date> date() {
        return new DateGenerator(context);
    }

    /**
     * Customises generated {@link java.sql.Date} values.
     *
     * @return customised generator
     */
    public TemporalGeneratorSpec<java.sql.Date> sqlDate() {
        return new SqlDateGenerator(context);
    }

    /**
     * Customises generated {@link Timestamp} values.
     *
     * @return customised generator
     */
    public TemporalGeneratorSpec<Timestamp> timestamp() {
        return new TimestampGenerator(context);
    }

    /**
     * Customises generated {@link Calendar} values.
     *
     * @return customised generator
     */
    public TemporalGeneratorSpec<Calendar> calendar() {
        return new CalendarGenerator(context);
    }
}
