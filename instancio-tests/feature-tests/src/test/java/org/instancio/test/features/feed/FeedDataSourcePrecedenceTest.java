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
import org.instancio.feed.FeedSpec;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag({Feature.FEED, Feature.VALUE_SPEC})
@ExtendWith(InstancioExtension.class)
class FeedDataSourcePrecedenceTest {

    @Feed.Source(name = "data/FeedExample.csv", data = "value\ninline")
    private interface FeedWithFileAndData extends Feed {
        FeedSpec<String> value();
    }

    @Feed.Source(name = "data/FeedExample.csv")
    private interface FeedWithFile extends Feed {
        FeedSpec<String> value();
    }

    @Test
    @DisplayName("Given classpath resource, inline, and a custom DataSource: inline should win")
    void givenAllThreeSources_inlineDataShouldWin() {
        final FeedWithFileAndData result = Instancio.ofFeed(FeedWithFileAndData.class)
                .withDataSource(source -> source.ofString("value\nstring"))
                .create();

        assertThat(result.value().get()).isEqualTo("inline");
    }

    @Test
    @DisplayName("Given classpath resource and DataSource: DataSource should win")
    void givenClasspathResourceAndDataSource_dataSourceShouldWin() {
        final FeedWithFile result = Instancio.ofFeed(FeedWithFile.class)
                .withDataSource(source -> source.ofString("value\nstring"))
                .create();

        assertThat(result.value().get()).isEqualTo("string");
    }

}
