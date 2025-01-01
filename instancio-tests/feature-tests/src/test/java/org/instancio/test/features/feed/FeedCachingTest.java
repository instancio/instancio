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
package org.instancio.test.features.feed;

import org.instancio.Instancio;
import org.instancio.feed.Feed;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.FeedDataAccess;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.feed.FeedWithTag;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.root;

/**
 * Verifies that caching logic takes tag key into account when
 * different tag keys are used. This is done by creating feed
 * instances for the same resource, but with different tag keys.
 */
@FeatureTag({Feature.FEED, Feature.APPLY_FEED})
@ExtendWith(InstancioExtension.class)
class FeedCachingTest {

    private static final String RESOURCE = "data/FeedWithTag.csv";

    @WithSettings
    private final Settings settings = Settings.create()
            .set(Keys.FEED_DATA_ACCESS, FeedDataAccess.RANDOM)
            .set(Keys.FEED_TAG_KEY, "tag");

    @Feed.Source(resource = RESOURCE)
    private interface SampleFeed extends Feed {}

    @Test
    void dataSourceFromAnnotation() {
        final SampleFeed feed1 = Instancio.ofFeed(SampleFeed.class)
                .withTagValue("EN")
                .create();

        final SampleFeed feed2 = Instancio.ofFeed(SampleFeed.class)
                .withTagKey("id")
                .withTagValue("203")
                .create();

        assertThat(feed1.stringSpec("tag").get()).isEqualTo("EN");
        assertThat(feed2.stringSpec("id").get()).isEqualTo("203");
    }

    @Test
    void dataSourceFromBuilder() {
        final Feed feed1 = Instancio.ofFeed(Feed.class)
                .withDataSource(source -> source.ofResource(RESOURCE))
                .withTagValue("EN")
                .create();

        final Feed feed2 = Instancio.ofFeed(Feed.class)
                .withDataSource(source -> source.ofResource(RESOURCE))
                .withTagKey("id")
                .withTagValue("203")
                .create();

        assertThat(feed1.stringSpec("tag").get()).isEqualTo("EN");
        assertThat(feed2.stringSpec("id").get()).isEqualTo("203");
    }

    @Test
    void dataSourceFromBuilder_applyFeed_resource() {
        final FeedWithTag feed1 = Instancio.of(FeedWithTag.class)
                .applyFeed(root(), feed -> feed.ofResource(RESOURCE)
                        .withTagValue("EN"))
                .create();

        final FeedWithTag feed2 = Instancio.of(FeedWithTag.class)
                .applyFeed(root(), feed -> feed.ofResource(RESOURCE)
                        .withTagKey("id")
                        .withTagValue("203"))
                .create();

        assertThat(feed1.getTag()).isEqualTo("EN");
        assertThat(feed2.getId()).isEqualTo(203);
    }

    @Test
    void dataSourceFromBuilder_applyFeed_class() {
        final FeedWithTag feed1 = Instancio.of(FeedWithTag.class)
                .applyFeed(root(), feed -> feed.of(SampleFeed.class)
                        .withTagValue("EN"))
                .create();

        final FeedWithTag feed2 = Instancio.of(FeedWithTag.class)
                .applyFeed(root(), feed -> feed.of(SampleFeed.class)
                        .withTagKey("id")
                        .withTagValue("203"))
                .create();

        assertThat(feed1.getTag()).isEqualTo("EN");
        assertThat(feed2.getId()).isEqualTo(203);
    }
}
