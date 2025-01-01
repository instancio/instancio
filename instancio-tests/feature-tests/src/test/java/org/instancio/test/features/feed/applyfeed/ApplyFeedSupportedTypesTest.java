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
package org.instancio.test.features.feed.applyfeed;

import lombok.Data;
import org.instancio.Instancio;
import org.instancio.feed.Feed;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.Keys;
import org.instancio.settings.OnFeedPropertyUnmatched;
import org.instancio.test.support.pojo.feed.FeedSupportedTypes;
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
import static org.instancio.Select.root;

@FeatureTag({Feature.FEED, Feature.APPLY_FEED})
@ExtendWith(InstancioExtension.class)
class ApplyFeedSupportedTypesTest {

    @Test
    void shouldMapAllSupportedTypesBasedOnFieldType() {
        final Feed feed = Instancio.ofFeed(Feed.class)
                .withDataSource(source -> source.ofResource(FeedSupportedTypes.CSV_FILE))
                .create();

        final FeedSupportedTypes result = Instancio.of(FeedSupportedTypes.class)
                .applyFeed(root(), feed)
                .create();

        assertThat(result.getString()).isEqualTo("foo bar");
        assertThat(result.get_boolean()).isTrue();
        assertThat(result.getCharacter()).isEqualTo('Z');
        assertThat(result.get_byte()).isEqualTo((byte) 1);
        assertThat(result.get_short()).isEqualTo((short) 2);
        assertThat(result.getInteger()).isEqualTo(3);
        assertThat(result.get_long()).isEqualTo(4);
        assertThat(result.get_double()).isEqualTo(5.1d);
        assertThat(result.get_float()).isEqualTo(6.1f);
        assertThat(result.getBigInteger()).isEqualTo(new BigInteger("12345"));
        assertThat(result.getBigDecimal()).isEqualTo(new BigDecimal("12345.6789"));
        assertThat(result.getInstant()).isEqualTo(Instant.parse("2071-10-04T08:48:21.499609989Z"));
        assertThat(result.getLocalTime()).isEqualTo(LocalTime.parse("06:50:07.871441943"));
        assertThat(result.getLocalDate()).isEqualTo(LocalDate.parse("2048-12-24"));
        assertThat(result.getLocalDateTime()).isEqualTo(LocalDateTime.parse("2036-03-19T19:18:52.725994144"));
        assertThat(result.getOffsetTime()).isEqualTo(OffsetTime.parse("18:16:16.814320739Z"));
        assertThat(result.getOffsetDateTime()).isEqualTo(OffsetDateTime.parse("1975-12-04T07:34:43.807103492Z"));
        assertThat(result.getZonedDateTime()).isEqualTo(ZonedDateTime.parse("2003-07-23T23:16:10.568513867Z"));
        assertThat(result.getYearMonth()).isEqualTo(YearMonth.parse("2045-05"));
        assertThat(result.getYear()).isEqualTo(Year.of(1991));
        assertThat(result.getUuid()).isEqualTo(UUID.fromString("5d418896-acf5-439e-902d-86a6c6fca4ae"));
        assertThat(result.getGender()).isEqualTo(Gender.FEMALE);
    }

    @Test
    void applyToPojoWithPrimitiveFields() {
        final PrimitiveTypes result = Instancio.of(PrimitiveTypes.class)
                .applyFeed(root(), feed -> feed.ofResource(FeedSupportedTypes.CSV_FILE))
                .withSetting(Keys.ON_FEED_PROPERTY_UNMATCHED, OnFeedPropertyUnmatched.IGNORE)
                .create();

        assertThat(result.is_boolean()).isTrue();
        assertThat(result.getCharacter()).isEqualTo('Z');
        assertThat(result.get_byte()).isEqualTo((byte) 1);
        assertThat(result.get_short()).isEqualTo((short) 2);
        assertThat(result.getInteger()).isEqualTo(3);
        assertThat(result.get_long()).isEqualTo(4);
        assertThat(result.get_double()).isEqualTo(5.1d);
        assertThat(result.get_float()).isEqualTo(6.1f);
    }

    @Data
    private static class PrimitiveTypes {
        private boolean _boolean;
        private char character;
        private byte _byte;
        private short _short;
        private int integer;
        private long _long;
        private double _double;
        private float _float;
    }
}