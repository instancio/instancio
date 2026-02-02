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
package org.instancio.test.features.feed;

import org.instancio.Instancio;
import org.instancio.exception.InstancioApiException;
import org.instancio.feed.Feed;
import org.instancio.feed.FeedSpec;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.Keys;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@FeatureTag(Feature.FEED)
@ExtendWith(InstancioExtension.class)
class FeedUnsupportedTagTest {

    @Feed.Source(resource = "data/FeedWithTag.csv")
    private interface SampleFeed extends Feed {
        FeedSpec<String> field1();
    }

    @ValueSource(strings = {"NONE", "INVALID_TAG"})
    @ParameterizedTest
    void unsupportedTag(final String tagValue) {
        final SampleFeed result = Instancio.ofFeed(SampleFeed.class)
                .withSetting(Keys.FEED_TAG_VALUE, tagValue)
                .create();

        final FeedSpec<String> spec = result.field1();

        assertThatThrownBy(spec::get)
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("no data found with tag value: '%s'", tagValue);
    }
}
