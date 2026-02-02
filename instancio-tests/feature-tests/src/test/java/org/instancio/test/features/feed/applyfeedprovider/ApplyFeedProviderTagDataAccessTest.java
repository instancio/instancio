/*
 * Copyright 2022-2026 the original author or authors.
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
package org.instancio.test.features.feed.applyfeedprovider;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.FeedDataAccess;
import org.instancio.settings.FeedDataEndAction;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.feed.FeedWithTag;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;

@FeatureTag({Feature.FEED, Feature.APPLY_FEED})
@ExtendWith(InstancioExtension.class)
class ApplyFeedProviderTagDataAccessTest {

    @Nested
    class NullTagValueTest {

        @WithSettings
        private final Settings settings = Settings.create()
                .set(Keys.FEED_TAG_KEY, "tag");

        /**
         * Should produce records in sequential order, starting from
         * the beginning of the feed. Once the end of the feed has been
         * reached, should restart from the beginning of the feed.
         */
        @Test
        void sequentialDataAccess() {
            final List<FeedWithTag> results = Instancio.ofList(FeedWithTag.class)
                    .size(12)
                    .applyFeed(all(FeedWithTag.class), feed -> feed
                            .ofResource(FeedWithTag.CSV_FILE)
                            .onDataEnd(FeedDataEndAction.RECYCLE))
                    .create();

            assertThat(results).extracting(FeedWithTag::getId)
                    .containsExactly(101, 102, 103, 201, 202, 203, 101, 102, 103, 201, 202, 203);
        }

        @Test
        void randomDataAccess() {
            final Set<List<FeedWithTag>> results = Stream.generate(() ->
                            Instancio.ofList(FeedWithTag.class)
                                    .size(12)
                                    .applyFeed(all(FeedWithTag.class), feed -> feed
                                            .ofResource(FeedWithTag.CSV_FILE)
                                            .dataAccess(FeedDataAccess.RANDOM))
                                    .create())
                    .limit(Constants.SAMPLE_SIZE_D)
                    .collect(Collectors.toSet());

            assertThat(results).hasSizeGreaterThan(1);
        }
    }

    @Nested
    class NonNullTagValueTest {

        @WithSettings
        private final Settings settings = Settings.create()
                .set(Keys.FEED_TAG_KEY, "tag");

        /**
         * Should produce records in sequential order, starting from
         * the beginning of tagged records. Once the end of the feed has been
         * reached, should restart from the beginning of the tagged records.
         */
        @Test
        void sequential_EN() {
            final List<FeedWithTag> results = Instancio.ofList(FeedWithTag.class)
                    .size(6)
                    .applyFeed(all(FeedWithTag.class), feed -> feed
                            .ofResource(FeedWithTag.CSV_FILE)
                            .withTagValue("EN")
                            .onDataEnd(FeedDataEndAction.RECYCLE))
                    .create();

            assertThat(results).extracting(FeedWithTag::getId)
                    .containsExactly(101, 102, 103, 101, 102, 103);
        }

        @Test
        void sequential_RU() {
            final List<FeedWithTag> results = Instancio.ofList(FeedWithTag.class)
                    .size(6)
                    .applyFeed(all(FeedWithTag.class), feed -> feed
                            .ofResource(FeedWithTag.CSV_FILE)
                            .withTagValue("RU")
                            .onDataEnd(FeedDataEndAction.RECYCLE))
                    .create();

            assertThat(results).extracting(FeedWithTag::getId)
                    .containsExactly(201, 202, 203, 201, 202, 203);
        }

        @ValueSource(strings = {"EN", "RU"})
        @ParameterizedTest
        void randomDataAccess(final String tag) {
            final Set<List<FeedWithTag>> results = Stream.generate(() ->
                            Instancio.ofList(FeedWithTag.class)
                                    .size(12)
                                    .applyFeed(all(FeedWithTag.class), feed -> feed
                                            .ofResource(FeedWithTag.CSV_FILE)
                                            .dataAccess(FeedDataAccess.RANDOM)
                                            .withTagValue(tag))
                                    .create())
                    .limit(Constants.SAMPLE_SIZE_D)
                    .collect(Collectors.toSet());

            assertThat(results)
                    .hasSizeGreaterThan(1)
                    .allSatisfy(list -> assertThat(list)
                            .hasSize(12)
                            .allMatch(o -> tag.equals(o.getTag())));
        }
    }
}
