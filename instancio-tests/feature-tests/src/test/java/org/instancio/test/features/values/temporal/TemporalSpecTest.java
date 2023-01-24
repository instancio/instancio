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
package org.instancio.test.features.values.temporal;

import org.apache.commons.lang3.tuple.Pair;
import org.instancio.generator.specs.TemporalSpec;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.tags.NonDeterministicTag;
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
        TemporalSpec<Instant> getSpec() {
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
        TemporalSpec<LocalDate> getSpec() {
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
        TemporalSpec<LocalDateTime> getSpec() {
            return temporal().localDateTime();
        }

        @Override
        Pair<LocalDateTime, LocalDateTime> getRangeFromNow() {
            final LocalDateTime now = LocalDateTime.now();
            return Pair.of(now, now.plusDays(400));
        }
    }

    @Nested
    @NonDeterministicTag("This test will fail if run less than 5 nanos before midnight")
    class LocalTimeValueSpecTest extends AbstractTemporalSpecTestTemplate<LocalTime> {
        @Override
        TemporalSpec<LocalTime> getSpec() {
            return temporal().localTime();
        }

        @Override
        Pair<LocalTime, LocalTime> getRangeFromNow() {
            final LocalTime now = LocalTime.now();
            return Pair.of(now, now.plusNanos(5));
        }
    }

    @Nested
    class OffsetDateTimeValueSpecTest extends AbstractTemporalSpecTestTemplate<OffsetDateTime> {
        @Override
        TemporalSpec<OffsetDateTime> getSpec() {
            return temporal().offsetDateTime();
        }

        @Override
        Pair<OffsetDateTime, OffsetDateTime> getRangeFromNow() {
            final OffsetDateTime now = OffsetDateTime.now();
            return Pair.of(now, now.plusDays(400));
        }
    }

    @Nested
    @NonDeterministicTag("This test will fail if run less than 5 nanos before midnight")
    class OffsetTimeValueSpecTest extends AbstractTemporalSpecTestTemplate<OffsetTime> {
        @Override
        TemporalSpec<OffsetTime> getSpec() {
            return temporal().offsetTime();
        }

        @Override
        Pair<OffsetTime, OffsetTime> getRangeFromNow() {
            final OffsetTime now = OffsetTime.now(ZoneOffset.UTC);
            return Pair.of(now, now.plusNanos(5));
        }
    }

    @Nested
    class YearValueSpecTest extends AbstractTemporalSpecTestTemplate<Year> {
        @Override
        TemporalSpec<Year> getSpec() {
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
        TemporalSpec<YearMonth> getSpec() {
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
        TemporalSpec<ZonedDateTime> getSpec() {
            return temporal().zonedDateTime();
        }

        @Override
        Pair<ZonedDateTime, ZonedDateTime> getRangeFromNow() {
            final ZonedDateTime now = ZonedDateTime.now();
            return Pair.of(now, now.plusDays(400));
        }
    }
}
