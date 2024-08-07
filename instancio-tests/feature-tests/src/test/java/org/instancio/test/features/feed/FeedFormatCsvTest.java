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
package org.instancio.test.features.feed;

import org.instancio.Instancio;
import org.instancio.feed.Feed;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.FeedDataTrim;
import org.instancio.settings.Keys;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static java.lang.Character.UnicodeBlock.GENERAL_PUNCTUATION;
import static java.lang.Character.UnicodeBlock.SUPPLEMENTAL_PUNCTUATION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Fail.fail;
import static org.instancio.Instancio.gen;

@FeatureTag(Feature.FEED)
@ExtendWith(InstancioExtension.class)
class FeedFormatCsvTest {

    private static final Character[] SEPARATORS = "`~!@#$%^&*()-_=+[{]}\\|;:',<.>/?".chars()
            .mapToObj(i -> (char) i)
            .toArray(Character[]::new);

    @RepeatedTest(Constants.SAMPLE_SIZE_DDD)
    void csvWithTrimmedValue() {
        final FeedDataTrim dataTrim = gen().enumOf(FeedDataTrim.class).get();
        final char separator = gen().oneOf(SEPARATORS).get();
        final String commentPrefix = gen().string().length(1, 3)
                .unicode(GENERAL_PUNCTUATION, SUPPLEMENTAL_PUNCTUATION)
                .get();

        final String data = createCsv(commentPrefix, separator);

        final Feed result = getFeed(data, commentPrefix, separator, dataTrim);

        if (dataTrim == FeedDataTrim.NONE) {
            assertThat(result.stringSpec(" x ").get()).isEqualTo(" 1 ");
            assertThat(result.stringSpec(" y ").get()).isEqualTo(" 2 ");
        } else if (dataTrim == FeedDataTrim.UNQUOTED) {
            assertThat(result.stringSpec("x").get()).isEqualTo("1");
            assertThat(result.stringSpec("y").get()).isEqualTo("2");
        } else {
            fail("Unhandled data trim option: %s", dataTrim);
        }
    }

    private static String createCsv(String comment, char separator) {
        final String eol = gen().oneOf("\r\n\n\r\n", "\n\n\r\n").get();

        return comment + "-------------------" + eol
                + " x " + separator + " y " + eol
                + comment + "-------------------" + eol
                + " 1 " + separator + " 2 " + eol
                + comment + "-------------------" + eol;
    }

    @Test
    void markdownTable() {
        final String md = "\n" +
                "| id | code | desc            |\n" +
                "|----|------|-----------------|\n" +
                "| 1  | ABC  | Great widget    |\n" +
                "| 2  | FGH  | Awesome product |\n";

        final Feed result = Instancio.ofFeed(Feed.class)
                .withDataSource(source -> source.ofString(md))
                .formatOptions(format -> format.csv()
                        .commentPrefix("|-")
                        .delimiter('|'))
                .create();

        assertThat(result.stringSpec("id").get()).isEqualTo("1");
        assertThat(result.stringSpec("code").get()).isEqualTo("ABC");
        assertThat(result.stringSpec("desc").get()).isEqualTo("Great widget");

        assertThat(result.stringSpec("id").get()).isEqualTo("2");
        assertThat(result.stringSpec("code").get()).isEqualTo("FGH");
        assertThat(result.stringSpec("desc").get()).isEqualTo("Awesome product");
    }

    @Nested
    class FeedDataTrimSettingsTest {

        @Test
        void disableTrimViaSettings() {
            final Feed feed = Instancio.ofFeed(Feed.class)
                    .withDataSource(source -> source.ofString("value\n  foo  "))
                    .withSetting(Keys.FEED_DATA_TRIM, FeedDataTrim.NONE)
                    .create();

            assertThat(feed.stringSpec("value").get()).isEqualTo("  foo  ");
        }

        @Test
        void disableTrimViaSettings_withFormatOptionsWithoutTrimSpecified() {
            final Feed feed = Instancio.ofFeed(Feed.class)
                    .withDataSource(source -> source.ofString("value\n  foo  "))
                    .withSetting(Keys.FEED_DATA_TRIM, FeedDataTrim.NONE)
                    .formatOptions(format -> format.csv().delimiter(','))
                    .create();

            assertThat(feed.stringSpec("value").get()).isEqualTo("  foo  ");
        }

        @Test
        void overrideTrimSettingViaFormatOptions() {
            final Feed feed = Instancio.ofFeed(Feed.class)
                    .withDataSource(source -> source.ofString("value\n  foo  "))
                    .withSetting(Keys.FEED_DATA_TRIM, FeedDataTrim.NONE)
                    .formatOptions(format -> format.csv().delimiter(',').trim(FeedDataTrim.UNQUOTED))
                    .create();

            assertThat(feed.stringSpec("value").get()).isEqualTo("foo");
        }
    }

    @Test
    void csvWithInvalidDelimeter() {
        final FeedDataTrim dataTrim = gen().enumOf(FeedDataTrim.class).get();
        final char separator = '"';
        final String commentPrefix = gen().string().length(1, 3)
                .unicode(GENERAL_PUNCTUATION, SUPPLEMENTAL_PUNCTUATION)
                .get();

        final String data = createCsv(commentPrefix, separator);

        assertThatIllegalArgumentException()
                .isThrownBy(() -> getFeed(data, commentPrefix, separator, dataTrim))
                .withMessage("Invalid delimiter: \"");
    }

    private static Feed getFeed(String data, String commentPrefix, char separator, FeedDataTrim dataTrim) {
        return Instancio.ofFeed(Feed.class)
                .withDataSource(source -> source.ofString(data))
                .formatOptions(format -> format.csv()
                        .commentPrefix(commentPrefix)
                        .delimiter(separator)
                        .trim(dataTrim))
                .create();
    }
}
