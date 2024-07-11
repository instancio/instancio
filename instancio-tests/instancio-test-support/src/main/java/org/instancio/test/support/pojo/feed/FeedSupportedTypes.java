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
package org.instancio.test.support.pojo.feed;

import lombok.Data;
import org.instancio.test.support.pojo.person.Gender;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.util.UUID;

@Data
public class FeedSupportedTypes {

    // Object representation of the following data file
    public static final String CSV_FILE = "data/FeedSupportedTypes.csv";
    public static final String JSON_FILE = "data/FeedSupportedTypes.json";

    private String string;
    private Boolean _boolean;
    private Character character;
    private Byte _byte;
    private Short _short;
    private Integer integer;
    private Long _long;
    private Double _double;
    private Float _float;
    private BigInteger bigInteger;
    private BigDecimal bigDecimal;
    private Instant instant;
    private LocalTime localTime;
    private LocalDate localDate;
    private LocalDateTime localDateTime;
    private OffsetTime offsetTime;
    private OffsetDateTime offsetDateTime;
    private ZonedDateTime zonedDateTime;
    private YearMonth yearMonth;
    private Year year;
    private UUID uuid;
    private Gender gender;
}
