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
package org.instancio.test.beanvalidation;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.pojo.beanvalidation.TemporalPastFutureBV;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag(Feature.BEAN_VALIDATION)
@ExtendWith(InstancioExtension.class)
class TemporalPastFutureBVTest {

    @Test
    void past() {
        final TemporalPastFutureBV.WithPast result = Instancio.create(TemporalPastFutureBV.WithPast.class);

        assertThat(result.getInstant()).isBefore(Instant.now());
        assertThat(result.getLocalTime()).isBefore(LocalTime.now());
        assertThat(result.getLocalDate()).isBefore(LocalDate.now());
        assertThat(result.getLocalDateTime()).isBefore(LocalDateTime.now());
        ///assertThat(result.getMonthDay()).isLessThan(MonthDay.now()); // TODO
        assertThat(result.getOffsetTime()).isBefore(OffsetTime.now(ZoneOffset.UTC));
        assertThat(result.getOffsetDateTime()).isBefore(OffsetDateTime.now());
        assertThat(result.getZonedDateTime()).isBefore(ZonedDateTime.now());
        assertThat(result.getYearMonth()).isLessThan(YearMonth.now());
        assertThat(result.getYear()).isLessThan(Year.now());
        assertThat(result.getDate()).isBefore(new Date(System.currentTimeMillis()));
        assertThat(result.getSqlDate()).isBefore(new java.sql.Date(System.currentTimeMillis()));
        assertThat(result.getTimestamp()).isBefore(new Timestamp(System.currentTimeMillis()));
        assertThat(result.getCalendar()).isLessThan(Calendar.getInstance());
    }

    @Test
    void pastOrPresent() {
        final TemporalPastFutureBV.WithPastOrPresent result = Instancio.create(TemporalPastFutureBV.WithPastOrPresent.class);

        assertThat(result.getInstant()).isBefore(Instant.now());
        assertThat(result.getLocalTime()).isBefore(LocalTime.now());
        assertThat(result.getLocalDate()).isBefore(LocalDate.now());
        assertThat(result.getLocalDateTime()).isBefore(LocalDateTime.now());
        ///assertThat(result.getMonthDay()).isLessThan(MonthDay.now()); // TODO
        assertThat(result.getOffsetTime()).isBefore(OffsetTime.now(ZoneOffset.UTC));
        assertThat(result.getOffsetDateTime()).isBefore(OffsetDateTime.now());
        assertThat(result.getZonedDateTime()).isBefore(ZonedDateTime.now());
        assertThat(result.getYearMonth()).isLessThan(YearMonth.now());
        assertThat(result.getYear()).isLessThan(Year.now());
        assertThat(result.getDate()).isBefore(new Date(System.currentTimeMillis()));
        assertThat(result.getSqlDate()).isBefore(new java.sql.Date(System.currentTimeMillis()));
        assertThat(result.getTimestamp()).isBefore(new Timestamp(System.currentTimeMillis()));
        assertThat(result.getCalendar()).isLessThan(Calendar.getInstance());
    }

    @Test
    void future() {
        final TemporalPastFutureBV.WithFuture result = Instancio.create(TemporalPastFutureBV.WithFuture.class);

        assertThat(result.getInstant()).isAfter(Instant.now());
        assertThat(result.getLocalTime()).isAfter(LocalTime.now());
        assertThat(result.getLocalDate()).isAfter(LocalDate.now());
        assertThat(result.getLocalDateTime()).isAfter(LocalDateTime.now());
        //assertThat(result.getMonthDay()).isGreaterThan(MonthDay.now()); // TODO not supported for MonthDay
        assertThat(result.getOffsetTime()).isAfter(OffsetTime.now(ZoneOffset.UTC));
        assertThat(result.getOffsetDateTime()).isAfter(OffsetDateTime.now());
        assertThat(result.getZonedDateTime()).isAfter(ZonedDateTime.now());
        assertThat(result.getYearMonth()).isGreaterThan(YearMonth.now());
        assertThat(result.getYear()).isGreaterThan(Year.now());
        assertThat(result.getDate()).isAfter(new Date(System.currentTimeMillis()));
        assertThat(result.getSqlDate()).isAfter(new java.sql.Date(System.currentTimeMillis()));
        assertThat(result.getTimestamp()).isAfter(new Timestamp(System.currentTimeMillis()));
        assertThat(result.getCalendar()).isGreaterThan(Calendar.getInstance());
    }

    @Test
    void futureOrPresent() {
        final TemporalPastFutureBV.WithFutureOrPresent result = Instancio.create(TemporalPastFutureBV.WithFutureOrPresent.class);

        assertThat(result.getInstant()).isAfter(Instant.now());
        assertThat(result.getLocalTime()).isAfter(LocalTime.now());
        assertThat(result.getLocalDate()).isAfter(LocalDate.now());
        assertThat(result.getLocalDateTime()).isAfter(LocalDateTime.now());
        //assertThat(result.getMonthDay()).isGreaterThan(MonthDay.now()); // TODO not supported for MonthDay
        assertThat(result.getOffsetTime()).isAfter(OffsetTime.now(ZoneOffset.UTC));
        assertThat(result.getOffsetDateTime()).isAfter(OffsetDateTime.now());
        assertThat(result.getZonedDateTime()).isAfter(ZonedDateTime.now());
        assertThat(result.getYearMonth()).isGreaterThan(YearMonth.now());
        assertThat(result.getYear()).isGreaterThan(Year.now());
        assertThat(result.getDate()).isAfter(new Date(System.currentTimeMillis()));
        assertThat(result.getSqlDate()).isAfter(new java.sql.Date(System.currentTimeMillis()));
        assertThat(result.getTimestamp()).isAfter(new Timestamp(System.currentTimeMillis()));
        assertThat(result.getCalendar()).isGreaterThan(Calendar.getInstance());
    }
}
