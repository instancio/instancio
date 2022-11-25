/*
 *  Copyright 2022 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.instancio.test.support.pojo.basic;

import lombok.Getter;
import lombok.ToString;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import java.util.Calendar;
import java.util.Date;

@Getter
@ToString
public class SupportedTemporalTypes {

    private Temporal temporal;
    private Instant instant;
    private LocalTime localTime;
    private LocalDate localDate;
    private LocalDateTime localDateTime;
    private ZonedDateTime zonedDateTime;
    private YearMonth yearMonth;
    private Duration duration;
    private Period period;
    private Year year;
    private Date date;
    private java.sql.Date sqlDate;
    private Timestamp timestamp;
    private Calendar calendar;
}
