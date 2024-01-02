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
package org.instancio.test.features.values.temporal;

import org.apache.commons.lang3.tuple.Pair;
import org.instancio.generator.specs.TemporalSpec;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Nested;

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

import static org.instancio.Gen.temporal;

@FeatureTag(Feature.VALUE_SPEC)
class TemporalSpecTest {

    @Nested
    class InstantValueSpecTest extends AbstractTemporalSpecTestTemplate<Instant> {
        @Override
        protected TemporalSpec<Instant> spec() {
            return temporal().instant();
        }

        @Override
        Pair<Instant, Instant> getRangeFromNow() {
            final Instant now = Instant.now();
            return Pair.of(now, now.plusNanos(6000));
        }
    }

    @Nested
    class LocalDateValueSpecTest extends AbstractTemporalSpecTestTemplate<LocalDate> {
        @Override
        protected TemporalSpec<LocalDate> spec() {
            return temporal().localDate();
        }

        @Override
        Pair<LocalDate, LocalDate> getRangeFromNow() {
            final LocalDate now = LocalDate.now();
            return Pair.of(now, now.plusDays(400));
        }
    }

    @Nested
    class LocalDateTimeValueSpecTest extends AbstractTemporalSpecTestTemplate<LocalDateTime> {
        @Override
        protected TemporalSpec<LocalDateTime> spec() {
            return temporal().localDateTime();
        }

        @Override
        Pair<LocalDateTime, LocalDateTime> getRangeFromNow() {
            final LocalDateTime now = LocalDateTime.now();
            return Pair.of(now, now.plusDays(400));
        }
    }

    @Nested
    class LocalTimeValueSpecTest extends AbstractTemporalSpecTestTemplate<LocalTime> {
        @Override
        protected TemporalSpec<LocalTime> spec() {
            return temporal().localTime();
        }

        @Override
        Pair<LocalTime, LocalTime> getRangeFromNow() {
            final LocalTime now = LocalTime.now();
            return Pair.of(now, LocalTime.MAX);
        }
    }

    @Nested
    class OffsetDateTimeValueSpecTest extends AbstractTemporalSpecTestTemplate<OffsetDateTime> {
        @Override
        protected TemporalSpec<OffsetDateTime> spec() {
            return temporal().offsetDateTime();
        }

        @Override
        Pair<OffsetDateTime, OffsetDateTime> getRangeFromNow() {
            final OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
            return Pair.of(now, now.plusDays(400));
        }
    }

    @Nested
    class OffsetTimeValueSpecTest extends AbstractTemporalSpecTestTemplate<OffsetTime> {
        @Override
        protected TemporalSpec<OffsetTime> spec() {
            return temporal().offsetTime();
        }

        @Override
        Pair<OffsetTime, OffsetTime> getRangeFromNow() {
            final OffsetTime now = OffsetTime.now(ZoneOffset.UTC);
            return Pair.of(now, OffsetTime.MAX);
        }
    }

    @Nested
    class YearValueSpecTest extends AbstractTemporalSpecTestTemplate<Year> {
        @Override
        protected TemporalSpec<Year> spec() {
            return temporal().year();
        }

        @Override
        Pair<Year, Year> getRangeFromNow() {
            final Year now = Year.now();
            return Pair.of(now, now.plusYears(100));
        }
    }

    @Nested
    class YearMonthValueSpecTest extends AbstractTemporalSpecTestTemplate<YearMonth> {
        @Override
        protected TemporalSpec<YearMonth> spec() {
            return temporal().yearMonth();
        }

        @Override
        Pair<YearMonth, YearMonth> getRangeFromNow() {
            final YearMonth now = YearMonth.now();
            return Pair.of(now, now.plusMonths(100));
        }
    }

    @Nested
    class ZonedDateTimeValueSpecTest extends AbstractTemporalSpecTestTemplate<ZonedDateTime> {
        @Override
        protected TemporalSpec<ZonedDateTime> spec() {
            return temporal().zonedDateTime();
        }

        @Override
        Pair<ZonedDateTime, ZonedDateTime> getRangeFromNow() {
            final ZonedDateTime now = ZonedDateTime.now();
            return Pair.of(now, now.plusDays(400));
        }
    }
}
