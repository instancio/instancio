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
package org.instancio.test.pojo.beanvalidation;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Past;
import javax.validation.constraints.PastOrPresent;
import lombok.Data;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import java.util.Calendar;
import java.util.Date;

public class TemporalPastFutureBV {

    @Data
    public static class WithPast {
        //@formatter:off
        @Past private Instant instant;
        @Past private LocalTime localTime;
        @Past private LocalDate localDate;
        @Past private LocalDateTime localDateTime;
        @Past private MonthDay monthDay;
        @Past private OffsetTime offsetTime;
        @Past private OffsetDateTime offsetDateTime;
        @Past private ZonedDateTime zonedDateTime;
        @Past private YearMonth yearMonth;
        @Past private Year year;
        @Past private Date date;
        @Past private java.sql.Date sqlDate;
        @Past private Timestamp timestamp;
        @Past private Calendar calendar;
        //@formatter:on
    }

    @Data
    public static class WithPastOrPresent {
        //@formatter:off
        @PastOrPresent private Instant instant;
        @PastOrPresent private LocalTime localTime;
        @PastOrPresent private LocalDate localDate;
        @PastOrPresent private LocalDateTime localDateTime;
        @PastOrPresent private MonthDay monthDay;
        @PastOrPresent private OffsetTime offsetTime;
        @PastOrPresent private OffsetDateTime offsetDateTime;
        @PastOrPresent private ZonedDateTime zonedDateTime;
        @PastOrPresent private YearMonth yearMonth;
        @PastOrPresent private Year year;
        @PastOrPresent private Date date;
        @PastOrPresent private java.sql.Date sqlDate;
        @PastOrPresent private Timestamp timestamp;
        @PastOrPresent private Calendar calendar;
        //@formatter:on
    }

    @Data
    public static class WithFuture {
        //@formatter:off
        @Future private Temporal temporal;
        @Future private Instant instant;
        @Future private LocalTime localTime;
        @Future private LocalDate localDate;
        @Future private LocalDateTime localDateTime;
        @Future private MonthDay monthDay;
        @Future private OffsetTime offsetTime;
        @Future private OffsetDateTime offsetDateTime;
        @Future private ZonedDateTime zonedDateTime;
        @Future private YearMonth yearMonth;
        @Future private Duration duration;
        @Future private Period period;
        @Future private ZoneId zoneId;
        @Future private ZoneOffset zoneOffset;
        @Future private Year year;
        @Future private Date date;
        @Future private java.sql.Date sqlDate;
        @Future private Timestamp timestamp;
        @Future private Calendar calendar;
        //@formatter:on
    }

    @Data
    public static class WithFutureOrPresent {
        //@formatter:off
        @FutureOrPresent private Temporal temporal;
        @FutureOrPresent private Instant instant;
        @FutureOrPresent private LocalTime localTime;
        @FutureOrPresent private LocalDate localDate;
        @FutureOrPresent private LocalDateTime localDateTime;
        @FutureOrPresent private MonthDay monthDay;
        @FutureOrPresent private OffsetTime offsetTime;
        @FutureOrPresent private OffsetDateTime offsetDateTime;
        @FutureOrPresent private ZonedDateTime zonedDateTime;
        @FutureOrPresent private YearMonth yearMonth;
        @FutureOrPresent private Duration duration;
        @FutureOrPresent private Period period;
        @FutureOrPresent private ZoneId zoneId;
        @FutureOrPresent private ZoneOffset zoneOffset;
        @FutureOrPresent private Year year;
        @FutureOrPresent private Date date;
        @FutureOrPresent private java.sql.Date sqlDate;
        @FutureOrPresent private Timestamp timestamp;
        @FutureOrPresent private Calendar calendar;
        //@formatter:on
    }
}
