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
package org.instancio.test.jackson.feed.applyfeed;

import org.instancio.Instancio;
import org.instancio.feed.Feed;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.FeedFormatType;
import org.instancio.test.support.pojo.feed.FeedWithTag;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.root;

@FeatureTag({Feature.FEED, Feature.APPLY_FEED})
@ExtendWith(InstancioExtension.class)
class ApplyFeedDataSourceJacksonTest {

    private static final String DATA = """
            [{
               "tag": "EN",
               "id": "101",
               "field1": "D_101",
               "field2": "F_101"
            }]
            """;

    @Feed.FormatType(FeedFormatType.JSON)
    @Feed.Source(string = DATA)
    private interface SampleFeed extends Feed {}

    @Test
    void feedClass() {
        final SampleFeed feed = Instancio.createFeed(SampleFeed.class);

        final FeedWithTag result = Instancio.of(FeedWithTag.class)
                .applyFeed(root(), feed)
                .create();

        assertThat(result.getTag()).isEqualTo("EN");
        assertThat(result.getId()).isEqualTo(101);
        assertThat(result.getField1()).isEqualTo("D_101");
        assertThat(result.getField2()).isEqualTo("F_101");
    }
}
