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
import org.instancio.settings.FeedDataEndAction;
import org.instancio.settings.Keys;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag(Feature.FEED)
@ExtendWith(InstancioExtension.class)
class FeedDataEndTest {

    @Feed.Source(string = "value\nfoo")
    private interface SampleFeed extends Feed {}

    @Test
    void recycle() {
        final SampleFeed feed = Instancio.ofFeed(SampleFeed.class)
                .withSetting(Keys.FEED_DATA_END_ACTION, FeedDataEndAction.FAIL)
                // should take precedence over Settings
                .onDataEnd(FeedDataEndAction.RECYCLE)
                .create();

        // repeated invocations should produce the same value
        for (int i = 0; i < 10; i++) {
            assertThat(feed.stringSpec("value").get()).isEqualTo("foo");
        }
    }
}
