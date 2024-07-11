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
package org.instancio.test.features.feed.applyfeedprovider;

import org.instancio.Instancio;
import org.instancio.feed.Feed;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.feed.FeedWithTag;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.ByteArrayInputStream;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.root;

@FeatureTag({Feature.FEED, Feature.APPLY_FEED})
@ExtendWith(InstancioExtension.class)
class ApplyFeedProviderDataSourceTest {

    private static final String CSV = "tag, id, field1, field2\nEN, 101, D_101, F_101";

    @WithSettings
    private static final Settings settings = Settings.create()
            .set(Keys.FEED_TAG_KEY, "tag");

    @Feed.Source(string = CSV)
    private interface SampleFeed extends Feed {}

    @Test
    void feedClass() {
        final FeedWithTag result = Instancio.of(FeedWithTag.class)
                .applyFeed(root(), feed -> feed.of(SampleFeed.class))
                .create();

        assertResult(result);
    }

    @Test
    void inputStream() {
        final FeedWithTag result = Instancio.of(FeedWithTag.class)
                .applyFeed(root(), feed -> feed.ofInputStream(new ByteArrayInputStream(CSV.getBytes())))
                .create();

        assertResult(result);
    }

    @Test
    void file() {
        final Path path = Instancio.gen().nio().path().tmp()
                .createFile(new ByteArrayInputStream(CSV.getBytes()))
                .get();

        final FeedWithTag result = Instancio.of(FeedWithTag.class)
                .applyFeed(root(), feed -> feed.ofFile(path))
                .create();

        assertResult(result);
    }

    @RepeatedTest(5)
    void resource() {
        final FeedWithTag result = Instancio.of(FeedWithTag.class)
                .applyFeed(root(), feed -> feed.ofResource(FeedWithTag.CSV_FILE))
                .create();

        assertResult(result);
    }

    @Test
    void string() {
        final FeedWithTag result = Instancio.of(FeedWithTag.class)
                .applyFeed(root(), feed -> feed.ofString(CSV))
                .create();

        assertResult(result);
    }

    private static void assertResult(final FeedWithTag result) {
        assertThat(result.getTag()).isEqualTo("EN");
        assertThat(result.getId()).isEqualTo(101);
        assertThat(result.getField1()).isEqualTo("D_101");
        assertThat(result.getField2()).isEqualTo("F_101");
    }
}
