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
package org.instancio.test.features.schema;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.schema.Schema;
import org.instancio.schema.SchemaResource;
import org.instancio.schema.SchemaSpec;
import org.instancio.settings.Keys;
import org.instancio.settings.SchemaDataEndStrategy;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.person.Gender;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

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

import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag({Feature.SCHEMA, Feature.VALUE_SPEC})
@ExtendWith(InstancioExtension.class)
class SchemaSupportedTypesTest {

    @WithSettings
    private final Settings settings = Settings.create()
            .set(Keys.SCHEMA_DATA_END_STRATEGY, SchemaDataEndStrategy.RECYCLE);

    @SchemaResource(path = "data/DataSpecSupportedTypes.csv")
    private interface SchemaSupportedTypes extends Schema {
        //@formatter:off
        SchemaSpec<String> string();
        SchemaSpec<Boolean> _boolean();
        SchemaSpec<Character> character();
        SchemaSpec<Byte> _byte();
        SchemaSpec<Short> _short();
        SchemaSpec<Integer> integer();
        SchemaSpec<Long> _long();
        SchemaSpec<Double> _double();
        SchemaSpec<Float> _float();
        SchemaSpec<BigInteger> bigInteger();
        SchemaSpec<BigDecimal> bigDecimal();
        SchemaSpec<Instant> instant();
        SchemaSpec<LocalTime> localTime();
        SchemaSpec<LocalDate> localDate();
        SchemaSpec<LocalDateTime> localDateTime();
        SchemaSpec<OffsetTime> offsetTime();
        SchemaSpec<OffsetDateTime> offsetDateTime();
        SchemaSpec<ZonedDateTime> zonedDateTime();
        SchemaSpec<YearMonth> yearMonth();
        SchemaSpec<Year> year();
        SchemaSpec<UUID> uuid();
        SchemaSpec<Gender> gender();
        //@formatter:on
    }

    @Test
    void supportedTypes() {
        final SchemaSupportedTypes result = Instancio.createSchema(SchemaSupportedTypes.class);

        assertThat(result.string().get())
                .isEqualTo(result.stringSpec("string").get())
                .isEqualTo("foo bar");

        assertThat(result._boolean().get())
                .isEqualTo(result.booleanSpec("_boolean").get())
                .isTrue();

        assertThat(result.character().get())
                .isEqualTo(result.characterSpec("character").get())
                .isEqualTo('Z');

        assertThat(result._byte().get())
                .isEqualTo(result.byteSpec("_byte").get())
                .isEqualTo((byte) 1);

        assertThat(result._short().get())
                .isEqualTo(result.shortSpec("_short").get())
                .isEqualTo((short) 2);

        assertThat(result.integer().get())
                .isEqualTo(result.integerSpec("integer").get())
                .isEqualTo(3);

        assertThat(result._long().get())
                .isEqualTo(result.longSpec("_long").get())
                .isEqualTo(4);

        assertThat(result._double().get())
                .isEqualTo(result.doubleSpec("_double").get())
                .isEqualTo(5.1d);

        assertThat(result._float().get())
                .isEqualTo(result.floatSpec("_float").get())
                .isEqualTo(6.1f);

        assertThat(result.bigInteger().get())
                .isEqualTo(result.bigIntegerSpec("bigInteger").get())
                .isEqualTo(new BigInteger("12345"));

        assertThat(result.bigDecimal().get())
                .isEqualTo(result.bigDecimalSpec("bigDecimal").get())
                .isEqualTo(new BigDecimal("12345.6789"));

        assertThat(result.instant().get())
                .isEqualTo(result.instantSpec("instant").get())
                .isEqualTo(Instant.parse("2071-10-04T08:48:21.499609989Z"));

        assertThat(result.localTime().get())
                .isEqualTo(result.localTimeSpec("localTime").get())
                .isEqualTo(LocalTime.parse("06:50:07.871441943"));

        assertThat(result.localDate().get())
                .isEqualTo(result.localDateSpec("localDate").get())
                .isEqualTo(LocalDate.parse("2048-12-24"));

        assertThat(result.localDateTime().get())
                .isEqualTo(result.localDateTimeSpec("localDateTime").get())
                .isEqualTo(LocalDateTime.parse("2036-03-19T19:18:52.725994144"));

        assertThat(result.offsetTime().get())
                .isEqualTo(result.offsetTimeSpec("offsetTime").get())
                .isEqualTo(OffsetTime.parse("18:16:16.814320739Z"));

        assertThat(result.offsetDateTime().get())
                .isEqualTo(result.offsetDateTimeSpec("offsetDateTime").get())
                .isEqualTo(OffsetDateTime.parse("1975-12-04T07:34:43.807103492Z"));

        assertThat(result.zonedDateTime().get())
                .isEqualTo(result.zonedDateTimeSpec("zonedDateTime").get())
                .isEqualTo(ZonedDateTime.parse("2003-07-23T23:16:10.568513867Z"));

        assertThat(result.yearMonth().get())
                .isEqualTo(result.yearMonthSpec("yearMonth").get())
                .isEqualTo(YearMonth.parse("2045-05"));

        assertThat(result.year().get())
                .isEqualTo(result.yearSpec("year").get())
                .isEqualTo(Year.of(1991));

        assertThat(result.uuid().get())
                .isEqualTo(result.uuidSpec("uuid").get())
                .isEqualTo(UUID.fromString("5d418896-acf5-439e-902d-86a6c6fca4ae"));

        assertThat(result.gender().get())
                .isEqualTo(result.spec("gender", Gender::valueOf).get())
                .isEqualTo(Gender.FEMALE);
    }
}
