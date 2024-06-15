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
package org.instancio.internal.generators;

import org.instancio.generator.GeneratorContext;
import org.instancio.generator.specs.DurationSpec;
import org.instancio.generator.specs.InstantSpec;
import org.instancio.generator.specs.LocalDateSpec;
import org.instancio.generator.specs.LocalDateTimeSpec;
import org.instancio.generator.specs.LocalTimeSpec;
import org.instancio.generator.specs.MonthDaySpec;
import org.instancio.generator.specs.OffsetDateTimeSpec;
import org.instancio.generator.specs.OffsetTimeSpec;
import org.instancio.generator.specs.PeriodSpec;
import org.instancio.generator.specs.TemporalSpec;
import org.instancio.generator.specs.YearMonthSpec;
import org.instancio.generator.specs.YearSpec;
import org.instancio.generator.specs.ZonedDateTimeSpec;
import org.instancio.generators.TemporalSpecs;
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
import java.util.Calendar;
import java.util.Date;

@SuppressWarnings("PMD.ExcessiveImports")
final class TemporalSpecsImpl implements TemporalSpecs {

    private final GeneratorContext context;

    TemporalSpecsImpl(final GeneratorContext context) {
        this.context = context;
    }

    @Override
    public DurationSpec duration() {
        return new DurationGenerator(context);
    }

    @Override
    public InstantSpec instant() {
        return new InstantGenerator(context);
    }

    @Override
    public LocalDateSpec localDate() {
        return new LocalDateGenerator(context);
    }

    @Override
    public LocalDateTimeSpec localDateTime() {
        return new LocalDateTimeGenerator(context);
    }

    @Override
    public LocalTimeSpec localTime() {
        return new LocalTimeGenerator(context);
    }

    @Override
    public MonthDaySpec monthDay() {
        return new MonthDayGenerator(context);
    }

    @Override
    public OffsetDateTimeSpec offsetDateTime() {
        return new OffsetDateTimeGenerator(context);
    }

    @Override
    public OffsetTimeSpec offsetTime() {
        return new OffsetTimeGenerator(context);
    }

    @Override
    public PeriodSpec period() {
        return new PeriodGenerator(context);
    }

    @Override
    public YearSpec year() {
        return new YearGenerator(context);
    }

    @Override
    public YearMonthSpec yearMonth() {
        return new YearMonthGenerator(context);
    }

    @Override
    public ZonedDateTimeSpec zonedDateTime() {
        return new ZonedDateTimeGenerator(context);
    }

    @Override
    public TemporalSpec<Date> date() {
        return new DateGenerator(context);
    }

    @Override
    public TemporalSpec<java.sql.Date> sqlDate() {
        return new SqlDateGenerator(context);
    }

    @Override
    public TemporalSpec<Timestamp> timestamp() {
        return new TimestampGenerator(context);
    }

    @Override
    public TemporalSpec<Calendar> calendar() {
        return new CalendarGenerator(context);
    }
}
