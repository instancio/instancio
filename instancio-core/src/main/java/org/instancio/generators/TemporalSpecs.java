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
package org.instancio.generators;

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

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

/**
 * Provides generators for {@code java.time} classes.
 *
 * @since 5.0.0
 */
public interface TemporalSpecs extends TemporalGenerators {

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    DurationSpec duration();

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    InstantSpec instant();

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    LocalDateSpec localDate();

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    LocalDateTimeSpec localDateTime();

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    LocalTimeSpec localTime();

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    OffsetDateTimeSpec offsetDateTime();

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    OffsetTimeSpec offsetTime();

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    PeriodSpec period();

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    YearSpec year();

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    YearMonthSpec yearMonth();

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    ZonedDateTimeSpec zonedDateTime();

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    MonthDaySpec monthDay();

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    TemporalSpec<Date> date();

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    TemporalSpec<java.sql.Date> sqlDate();

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    TemporalSpec<Timestamp> timestamp();

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    TemporalSpec<Calendar> calendar();
}
