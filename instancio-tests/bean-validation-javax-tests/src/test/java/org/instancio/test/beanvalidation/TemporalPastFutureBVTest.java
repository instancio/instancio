/*
 * Copyright 2022-2025 the original author or authors.
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

import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.Year;
import java.time.ZoneOffset;
import java.util.Calendar;

import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag(Feature.BEAN_VALIDATION)
@ExtendWith(InstancioExtension.class)
class TemporalPastFutureBVTest {

    @Test
    void past() {
        final TemporalPastFutureBV.WithPast result = Instancio.create(TemporalPastFutureBV.WithPast.class);

        assertThat(result.getInstant()).isInThePast();
        assertThat(result.getLocalTime()).isBefore(LocalTime.now());
        assertThat(result.getLocalDate()).isInThePast();
        assertThat(result.getLocalDateTime()).isInThePast();
        ///assertThat(result.getMonthDay()).isLessThan(MonthDay.now()); // TODO
        assertThat(result.getOffsetTime()).isBefore(OffsetTime.now(ZoneOffset.UTC));
        assertThat(result.getOffsetDateTime()).isInThePast();
        assertThat(result.getZonedDateTime()).isInThePast();
        assertThat(result.getYearMonth()).isInThePast();
        assertThat(result.getYear()).isLessThan(Year.now());
        assertThat(result.getDate()).isInThePast();
        assertThat(result.getSqlDate()).isInThePast();
        assertThat(result.getTimestamp()).isInThePast();
        assertThat(result.getCalendar()).isLessThan(Calendar.getInstance());
    }

    @Test
    void pastOrPresent() {
        final TemporalPastFutureBV.WithPastOrPresent result = Instancio.create(TemporalPastFutureBV.WithPastOrPresent.class);

        assertThat(result.getInstant()).isInThePast();
        assertThat(result.getLocalTime()).isBefore(LocalTime.now());
        assertThat(result.getLocalDate()).isInThePast();
        assertThat(result.getLocalDateTime()).isInThePast();
        ///assertThat(result.getMonthDay()).isLessThan(MonthDay.now()); // TODO
        assertThat(result.getOffsetTime()).isBefore(OffsetTime.now(ZoneOffset.UTC));
        assertThat(result.getOffsetDateTime()).isInThePast();
        assertThat(result.getZonedDateTime()).isInThePast();
        assertThat(result.getYearMonth()).isInThePast();
        assertThat(result.getYear()).isLessThan(Year.now());
        assertThat(result.getDate()).isInThePast();
        assertThat(result.getSqlDate()).isInThePast();
        assertThat(result.getTimestamp()).isInThePast();
        assertThat(result.getCalendar()).isLessThan(Calendar.getInstance());
    }

    @Test
    void future() {
        final TemporalPastFutureBV.WithFuture result = Instancio.create(TemporalPastFutureBV.WithFuture.class);

        assertThat(result.getInstant()).isInTheFuture();
        assertThat(result.getLocalTime()).isAfter(LocalTime.now());
        assertThat(result.getLocalDate()).isInTheFuture();
        assertThat(result.getLocalDateTime()).isInTheFuture();
        //assertThat(result.getMonthDay()).isGreaterThan(MonthDay.now()); // TODO not supported for MonthDay
        assertThat(result.getOffsetTime()).isAfter(OffsetTime.now(ZoneOffset.UTC));
        assertThat(result.getOffsetDateTime()).isInTheFuture();
        assertThat(result.getZonedDateTime()).isInTheFuture();
        assertThat(result.getYearMonth()).isInTheFuture();
        assertThat(result.getYear()).isGreaterThan(Year.now());
        assertThat(result.getDate()).isInTheFuture();
        assertThat(result.getSqlDate()).isInTheFuture();
        assertThat(result.getTimestamp()).isInTheFuture();
        assertThat(result.getCalendar()).isGreaterThan(Calendar.getInstance());
    }

    @Test
    void futureOrPresent() {
        final TemporalPastFutureBV.WithFutureOrPresent result = Instancio.create(TemporalPastFutureBV.WithFutureOrPresent.class);

        assertThat(result.getInstant()).isInTheFuture();
        assertThat(result.getLocalTime()).isAfter(LocalTime.now());
        assertThat(result.getLocalDate()).isInTheFuture();
        assertThat(result.getLocalDateTime()).isInTheFuture();
        //assertThat(result.getMonthDay()).isGreaterThan(MonthDay.now()); // TODO not supported for MonthDay
        assertThat(result.getOffsetTime()).isAfter(OffsetTime.now(ZoneOffset.UTC));
        assertThat(result.getOffsetDateTime()).isInTheFuture();
        assertThat(result.getZonedDateTime()).isInTheFuture();
        assertThat(result.getYearMonth()).isInTheFuture();
        assertThat(result.getYear()).isGreaterThan(Year.now());
        assertThat(result.getDate()).isInTheFuture();
        assertThat(result.getSqlDate()).isInTheFuture();
        assertThat(result.getTimestamp()).isInTheFuture();
        assertThat(result.getCalendar()).isGreaterThan(Calendar.getInstance());
    }
}
