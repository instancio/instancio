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

package org.instancio.test.features.generator.temporal;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.basic.SupportedTemporalTypes;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;

@FeatureTag({Feature.GENERATE, Feature.TEMPORAL_GENERATOR})
@ExtendWith(InstancioExtension.class)
class TemporalGeneratorsTest {

    @Test
    void generate() {
        final Instant instant = Instant.now();
        final LocalTime localTime = LocalTime.now();
        final LocalDate localDate = LocalDate.now();
        final LocalDateTime localDateTime = LocalDateTime.now();
        final OffsetTime offsetTime = OffsetTime.now();
        final OffsetDateTime offsetDateTime = OffsetDateTime.now();
        final ZonedDateTime zonedDateTime = ZonedDateTime.now();
        final YearMonth yearMonth = YearMonth.now();
        final Year year = Year.now();
        final Date date = new Date();
        final java.sql.Date sqlDate = java.sql.Date.valueOf(localDate);
        final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        final Calendar calendar = Calendar.getInstance();
        final int periodYears = 10;
        final int durationNanos = 200_000_000;

        final SupportedTemporalTypes result = Instancio.of(SupportedTemporalTypes.class)
                .generate(all(Instant.class), gen -> gen.temporal().instant().range(instant, instant))
                .generate(all(LocalTime.class), gen -> gen.temporal().localTime().range(localTime, localTime))
                .generate(all(LocalDate.class), gen -> gen.temporal().localDate().range(localDate, localDate))
                .generate(all(LocalDateTime.class), gen -> gen.temporal().localDateTime().range(localDateTime, localDateTime))
                .generate(all(OffsetTime.class), gen -> gen.temporal().offsetTime().range(offsetTime, offsetTime))
                .generate(all(OffsetDateTime.class), gen -> gen.temporal().offsetDateTime().range(offsetDateTime, offsetDateTime))
                .generate(all(ZonedDateTime.class), gen -> gen.temporal().zonedDateTime().range(zonedDateTime, zonedDateTime))
                .generate(all(YearMonth.class), gen -> gen.temporal().yearMonth().range(yearMonth, yearMonth))
                .generate(all(Year.class), gen -> gen.temporal().year().range(year, year))
                .generate(all(Duration.class), gen -> gen.temporal().duration().of(durationNanos, durationNanos, ChronoUnit.NANOS))
                .generate(all(Period.class), gen -> gen.temporal().period().years(periodYears, periodYears))
                .generate(all(Date.class), gen -> gen.temporal().date().range(date, date))
                .generate(all(java.sql.Date.class), gen -> gen.temporal().sqlDate().range(sqlDate, sqlDate))
                .generate(all(Timestamp.class), gen -> gen.temporal().timestamp().range(timestamp, timestamp))
                .generate(all(Calendar.class), gen -> gen.temporal().calendar().range(calendar, calendar))
                .create();

        assertThat(result.getInstant()).isEqualTo(instant);
        assertThat(result.getLocalTime()).isEqualTo(localTime);
        assertThat(result.getLocalDate()).isEqualTo(localDate);
        assertThat(result.getLocalDateTime()).isEqualTo(localDateTime);
        assertThat(result.getZonedDateTime()).isEqualTo(zonedDateTime);
        assertThat(result.getYearMonth()).isEqualTo(yearMonth);
        assertThat(result.getYear()).isEqualTo(year);
        assertThat(result.getDuration()).hasNanos(durationNanos);
        assertThat(result.getPeriod()).hasYears(periodYears);
        assertThat(result.getDate()).isEqualTo(date);
        assertThat(result.getSqlDate()).isEqualTo(sqlDate);
        assertThat(result.getTimestamp()).isEqualTo(timestamp);
        assertThat(result.getCalendar().getTimeInMillis()).isEqualTo(calendar.getTimeInMillis());
    }
}