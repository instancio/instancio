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
import org.instancio.exception.InstancioApiException;
import org.instancio.feed.Feed;
import org.instancio.feed.FeedSpec;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.FeedDataAccess;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@FeatureTag(Feature.FEED)
@ExtendWith(InstancioExtension.class)
class FeedWithTagKeyAnnotationTest {

    @Feed.TagKey("customTag")
    @Feed.Source(string = "customTag,id\nfoo,1\nbar,2")
    private interface SampleFeed extends Feed {
        FeedSpec<Integer> id();
    }

    @Feed.TagKey("")
    @Feed.Source(string = "customTag,id\nfoo,1\nbar,2")
    private interface SampleFeedWithBlankTag extends Feed {
        FeedSpec<Integer> id();
    }

    @Nested
    class SampleFeedWithBlankTagTest {
        @WithSettings
        private final Settings settings = Settings.create()
                .set(Keys.FEED_DATA_ACCESS, FeedDataAccess.RANDOM);

        @Test
        void withTagBar() {
            final String tag = "bar";
            final SampleFeedWithBlankTag feed = Instancio.ofFeed(SampleFeedWithBlankTag.class)
                    .withTagValue(tag)
                    .create();

            final FeedSpec<Integer> spec = feed.id();

            assertThatThrownBy(spec::get)
                    .isExactlyInstanceOf(InstancioApiException.class)
                    .hasMessageContaining("no data found with tag value: '%s'", tag);
        }

        @Test
        void withoutTag() {
            final List<Integer> results = Instancio.createFeed(SampleFeedWithBlankTag.class).id().list(10);

            assertThat(results).containsOnly(1, 2);
        }
    }

    @Nested
    class SampleFeedTest {
        @WithSettings
        private final Settings settings = Settings.create()
                .set(Keys.FEED_DATA_ACCESS, FeedDataAccess.RANDOM);

        @Test
        void withTagFoo() {
            final List<Integer> results = Instancio.ofFeed(SampleFeed.class)
                    .withTagValue("foo")
                    .create()
                    .id()
                    .list(10);

            assertThat(results).containsOnly(1);
        }

        @Test
        void withTagBar() {
            final List<Integer> results = Instancio.ofFeed(SampleFeed.class)
                    .withTagValue("bar")
                    .create()
                    .id()
                    .list(10);

            assertThat(results).containsOnly(2);
        }

        @Test
        void withoutTag() {
            final List<Integer> results = Instancio.createFeed(SampleFeed.class)
                    .id()
                    .list(100);

            assertThat(results).containsOnly(1, 2);
        }
    }
}
