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
import org.instancio.junit.WithSettings;
import org.instancio.settings.FeedDataAccess;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag(Feature.FEED)
@ExtendWith(InstancioExtension.class)
class FeedDataSourceTest {

    @WithSettings
    private final Settings settings = Settings.create()
            .set(Keys.FEED_DATA_ACCESS, FeedDataAccess.RANDOM);

    private interface SampleFeedWithoutAnnotation extends Feed {
        FeedSpec<Integer> id();

        FeedSpec<String> value();
    }

    @Feed.Source(resource = "data/FeedExample.csv")
    private interface FeedExample extends SampleFeedWithoutAnnotation {
        @Override
        FeedSpec<Integer> id();

        @Override
        FeedSpec<String> value();
    }

    @Test
    void original() {
        final FeedExample feed = Instancio.createFeed(FeedExample.class);

        final Set<String> results = Stream.generate(() -> feed.id().get() + ":" + feed.value().get())
                .limit(100)
                .collect(Collectors.toSet());

        assertThat(results).containsOnly("1:value1", "2:value2", "3:value3");
    }

    @ValueSource(classes = {SampleFeedWithoutAnnotation.class, FeedExample.class})
    @ParameterizedTest
    void override(final Class<? extends SampleFeedWithoutAnnotation> klass) {
        final SampleFeedWithoutAnnotation feed = Instancio.ofFeed(klass)
                .withDataSource(source -> source.ofResource("data/FeedExampleOverride.csv"))
                .create();

        final Set<String> results = Stream.generate(() -> feed.id().get() + ":" + feed.value().get())
                .limit(100)
                .collect(Collectors.toSet());

        assertThat(results).containsOnly("1:override1", "2:override2", "3:override3");
    }
}
