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
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.FeedFormatType;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag(Feature.FEED)
@ExtendWith(InstancioExtension.class)
class FeedDataFormatJacksonTest {

    @Feed.FormatType(FeedFormatType.JSON)
    @Feed.Source(string = """
            [
              { "id": "1", "value": "foo" },
              { "id":  2,  "value": "bar" }
            ]
            """)
    private interface SampleFeed extends Feed {}

    @Test
    void json() {
        final Feed result = Instancio.createFeed(SampleFeed.class);

        assertThat(result.stringSpec("id").get()).isEqualTo("1");
        assertThat(result.stringSpec("value").get()).isEqualTo("foo");

        assertThat(result.stringSpec("id").get()).isEqualTo("2");
        assertThat(result.stringSpec("value").get()).isEqualTo("bar");
    }

    @Test
    void idAsInteger() {
        final Feed result = Instancio.createFeed(SampleFeed.class);

        assertThat(result.intSpec("id").get()).isEqualTo(1);
        assertThat(result.intSpec("id").get()).isEqualTo(2);
    }
}
