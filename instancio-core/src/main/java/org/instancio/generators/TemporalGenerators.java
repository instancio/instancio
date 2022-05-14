/*
 * Copyright 2022 the original author or authors.
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
import org.instancio.generator.specs.TemporalGeneratorSpec;
import org.instancio.generator.sql.SqlDateGenerator;
import org.instancio.generator.sql.TimestampGenerator;
import org.instancio.generator.time.InstantGenerator;
import org.instancio.generator.time.LocalDateGenerator;
import org.instancio.generator.time.LocalDateTimeGenerator;
import org.instancio.generator.time.LocalTimeGenerator;
import org.instancio.generator.time.YearGenerator;
import org.instancio.generator.time.YearMonthGenerator;
import org.instancio.generator.time.ZonedDateTimeGenerator;
import org.instancio.generator.util.CalendarGenerator;
import org.instancio.generator.util.DateGenerator;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Contains built-in temporal generators.
 */
public class TemporalGenerators {

    private final GeneratorContext context;

    TemporalGenerators(final GeneratorContext context) {
        this.context = context;
    }

    static Map<Class<?>, String> getApiMethods() {
        Map<Class<?>, String> map = new HashMap<>();
        map.put(InstantGenerator.class, "instant()");
        map.put(LocalDateGenerator.class, "localDate()");
        map.put(LocalDateTimeGenerator.class, "localDateTime()");
        map.put(ZonedDateTimeGenerator.class, "zonedDateTime()");
        map.put(LocalTimeGenerator.class, "localTime()");
        map.put(YearGenerator.class, "year()");
        map.put(YearMonthGenerator.class, "yearMonth()");
        map.put(DateGenerator.class, "date()");
        map.put(SqlDateGenerator.class, "sqlDate()");
        map.put(TimestampGenerator.class, "timestamp()");
        map.put(CalendarGenerator.class, "calendar()");
        return map;
    }

    /**
     * Customises generated {@link Instant} values.
     *
     * @return customised generator
     */
    public TemporalGeneratorSpec<Instant> instant() {
        return new InstantGenerator(context);
    }

    /**
     * Customises generated {@link LocalDate} values.
     *
     * @return customised generator
     */
    public TemporalGeneratorSpec<LocalDate> localDate() {
        return new LocalDateGenerator(context);
    }

    /**
     * Customises generated {@link LocalTime} values.
     *
     * @return customised generator
     */
    public TemporalGeneratorSpec<LocalTime> localTime() {
        return new LocalTimeGenerator(context);
    }

    /**
     * Customises generated {@link LocalDateTime} values.
     *
     * @return customised generator
     */
    public TemporalGeneratorSpec<LocalDateTime> localDateTime() {
        return new LocalDateTimeGenerator(context);
    }

    /**
     * Customises generated {@link ZonedDateTimeGenerator} values.
     *
     * @return customised generator
     */
    public TemporalGeneratorSpec<ZonedDateTime> zonedDateTime() {
        return new ZonedDateTimeGenerator(context);
    }

    /**
     * Customises generated {@link YearMonth} values.
     *
     * @return customised generator
     */
    public TemporalGeneratorSpec<YearMonth> yearMonth() {
        return new YearMonthGenerator(context);
    }

    /**
     * Customises generated {@link Year} values.
     *
     * @return customised generator
     */
    public TemporalGeneratorSpec<Year> year() {
        return new YearGenerator(context);
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
