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
import org.instancio.feed.FeedSpec;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag(Feature.FEED)
@ExtendWith(InstancioExtension.class)
class FeedDataSourcePrecedenceTest {

    @Feed.Source(resource = "data/FeedExample.csv", string = "value\ninline")
    private interface FeedWithResourceAndData extends Feed {
        FeedSpec<String> value();
    }

    @Test
    @DisplayName("Given a resource, inline data, and a builder data source: builder should win")
    void givenAllThreeSources_builderDataSourceShouldWin() {
        final FeedWithResourceAndData result = Instancio.ofFeed(FeedWithResourceAndData.class)
                .withDataSource(source -> source.ofString("value\nbuilder"))
                .create();

        assertThat(result.value().get()).isEqualTo("builder");
    }

    @Test
    @DisplayName("Given a resource and inline data: inline should win")
    void givenResourceAndInlineData_inlineShouldWin() {
        final FeedWithResourceAndData result = Instancio.createFeed(FeedWithResourceAndData.class);

        assertThat(result.value().get()).isEqualTo("inline");
    }
}
