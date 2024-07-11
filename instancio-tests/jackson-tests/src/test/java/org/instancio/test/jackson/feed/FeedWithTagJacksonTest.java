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
package org.instancio.test.jackson.feed;

import org.instancio.Instancio;
import org.instancio.feed.Feed;
import org.instancio.feed.FeedSpec;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.FeedDataAccess;
import org.instancio.settings.FeedDataEndAction;
import org.instancio.settings.FeedFormatType;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag(Feature.FEED)
@ExtendWith(InstancioExtension.class)
class FeedWithTagJacksonTest {

    /**
     * Verify that incomplete records, where tag key is missing,
     * do not result in an error.
     */
    @Test
    void tagKeyMissingForSomeRecords() {
        final String data = """
                [
                    { "a" : 10, "b": 11, "c": 12 },
                    { "a" : 20 }
                ]
                """;

        final Feed result = Instancio.ofFeed(Feed.class)
                .withDataSource(source -> source.ofString(data))
                .formatType(FeedFormatType.JSON)
                .dataAccess(FeedDataAccess.SEQUENTIAL)
                .onDataEnd(FeedDataEndAction.RECYCLE)
                .withTagKey("c")
                .create();

        final FeedSpec<Integer> spec = result.intSpec("c");

        for (int i = 0; i < 5; i++) {
            assertThat(spec.get()).isEqualTo(12);
            assertThat(spec.get()).isNull(); // should return null for missing column
        }
    }
}
