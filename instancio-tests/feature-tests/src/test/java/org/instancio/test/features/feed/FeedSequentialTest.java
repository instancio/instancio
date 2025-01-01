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
import org.instancio.exception.InstancioApiException;
import org.instancio.feed.Feed;
import org.instancio.feed.FeedSpec;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.FeedDataAccess;
import org.instancio.settings.FeedDataEndAction;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@FeatureTag(Feature.FEED)
@ExtendWith(InstancioExtension.class)
class FeedSequentialTest {

    @Feed.Source(string = "id\n1\n2")
    private interface SampleFeed extends Feed {
        FeedSpec<Integer> id();
    }

    @WithSettings
    private final Settings settings = Settings.create()
            .set(Keys.FEED_DATA_ACCESS, FeedDataAccess.SEQUENTIAL);

    @RepeatedTest(5)
    void sequential() {
        final SampleFeed result = Instancio.createFeed(SampleFeed.class);

        assertThat(result.id().get()).isEqualTo(1);
        assertThat(result.id().get()).isEqualTo(2);
    }

    @Test
    void insufficientItems_shouldThrowErrorByDefault() {
        final SampleFeed feed = Instancio.createFeed(SampleFeed.class);

        assertThat(feed.id().get()).isEqualTo(1);
        assertThat(feed.id().get()).isEqualTo(2);

        final FeedSpec<Integer> idSpec = feed.id();

        assertThatThrownBy(idSpec::get)
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("reached end of data");
    }

    @Test
    void insufficientItems_recycle() {
        final SampleFeed result = Instancio.ofFeed(SampleFeed.class)
                .withSetting(Keys.FEED_DATA_END_ACTION, FeedDataEndAction.RECYCLE)
                .create();

        for (int i = 0; i < 10; i++) {
            assertThat(result.id().get()).isEqualTo(1);
            assertThat(result.id().get()).isEqualTo(2);
        }
    }
}
