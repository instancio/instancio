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
package org.instancio.test.jackson.feed;

import org.instancio.junit.InstancioExtension;
import org.instancio.feed.Feed;
import org.instancio.feed.FeedSpec;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Instancio.createFeed;
import static org.instancio.Instancio.ofFeed;
import static org.instancio.settings.FeedDataTrim.NONE;
import static org.instancio.settings.FeedDataTrim.UNQUOTED;
import static org.instancio.settings.Keys.FEED_DATA_TRIM;

@FeatureTag(Feature.FEED)
@ExtendWith(InstancioExtension.class)
class FeedFormatQuotedCsvParserTest {

    @Test
    void sampleQuotedFeed() {
        @Feed.Source(string = """
        col1, col2, col3
        "a,b", c, "d, e,  f"
        """)
        interface SampleFeed extends Feed {
            FeedSpec<String> col1();
            FeedSpec<String> col2();
            FeedSpec<String> col3();
        }

        var feed = createFeed(SampleFeed.class);
        assertThat(feed.col1().get()).isEqualTo("a,b");
        assertThat(feed.col2().get()).isEqualTo("c");
        assertThat(feed.col3().get()).isEqualTo("d, e,  f");
    }

    @Test
    void shouldTrimOnlyUnquotedValues() {
        @Feed.Source(string = """
            col1, col2, col3
            " a,b "  ,   c  ,   " d, e,  f "
            """)
        interface SampleFeed extends Feed {
            FeedSpec<String> col1();
            FeedSpec<String> col2();
            FeedSpec<String> col3();
        }

        var feed = ofFeed(SampleFeed.class)
                .withSetting(FEED_DATA_TRIM, UNQUOTED)
                .create();

        assertThat(feed.col1().get()).isEqualTo(" a,b "); // not trimmed
        assertThat(feed.col2().get()).isEqualTo("c"); // trimmed
        assertThat(feed.col3().get()).isEqualTo(" d, e,  f "); // not trimmed
    }

    @Test
    void shouldNotTrimAnyValues() {
        @Feed.Source(string = """
        col1,col2,col3
        " a "    , b ,   " c "
        """)
        interface SampleFeed extends Feed {
            FeedSpec<String> col1();
            FeedSpec<String> col2();
            FeedSpec<String> col3();
        }

        var feed = ofFeed(SampleFeed.class)
                .withSetting(FEED_DATA_TRIM, NONE)
                .create();

        assertThat(feed.col1().get()).isEqualTo(" a ");
        assertThat(feed.col2().get()).isEqualTo(" b ");
        assertThat(feed.col3().get()).isEqualTo(" c ");
    }
}
