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
import org.instancio.junit.WithSettings;
import org.instancio.settings.FeedDataAccess;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag(Feature.FEED)
@ExtendWith(InstancioExtension.class)
class FeedWithTemplateSpecTest {

    @WithSettings
    private final Settings settings = Settings.create()
            .set(Keys.FEED_DATA_ACCESS, FeedDataAccess.RANDOM);

    @Feed.Source(resource = "data/FeedTemplateSpec.csv")
    private interface SampleFeed extends Feed {

        @TemplateSpec("${number} ${stringA}")
        FeedSpec<String> templateSpec();

        @TemplateSpec("${stringA} ${stringA} ${stringA}")
        FeedSpec<String> repeatingTemplateSpec();

        @TemplateSpec("[${templateSpec}] ${stringB}")
        FeedSpec<String> templateFromTemplateSpec();
    }

    @Test
    void templateSpec() {
        final SampleFeed result = Instancio.createFeed(SampleFeed.class);

        final Set<String> results = Stream.generate(() -> result.templateSpec().get())
                .limit(Constants.SAMPLE_SIZE_DD)
                .collect(Collectors.toSet());

        assertThat(results).containsOnly("1 a1", "2 a2", "3 a3");
    }

    @Test
    void repeatingTemplateSpec() {
        final SampleFeed result = Instancio.createFeed(SampleFeed.class);

        final Set<String> results = Stream.generate(() -> result.repeatingTemplateSpec().get())
                .limit(Constants.SAMPLE_SIZE_DD)
                .collect(Collectors.toSet());

        assertThat(results).containsOnly("a1 a1 a1", "a2 a2 a2", "a3 a3 a3");
    }

    @Test
    void templateFromTemplateSpec() {
        final SampleFeed result = Instancio.createFeed(SampleFeed.class);

        final Set<String> results = Stream.generate(() -> result.templateFromTemplateSpec().get())
                .limit(Constants.SAMPLE_SIZE_DD)
                .collect(Collectors.toSet());

        assertThat(results).containsOnly("[3 a3] b3", "[2 a2] b2", "[1 a1] b1");
    }

    @Test
    void nullableTemplateSpec() {
        final SampleFeed result = Instancio.createFeed(SampleFeed.class);

        final Set<String> results = Stream.generate(() -> result.templateFromTemplateSpec().nullable().get())
                .limit(Constants.SAMPLE_SIZE_DD)
                .collect(Collectors.toSet());

        assertThat(results).containsOnly(null, "[3 a3] b3", "[2 a2] b2", "[1 a1] b1");
    }

    @Feed.Source(string = "a,b,c\nfoo,,baz")
    interface TemplateSpecWithNullComponent extends Feed {
        @TemplateSpec("${a} ${b} ${c}")
        FeedSpec<String> abc();
    }

    @Test
    void templateWithNullComponent() {
        final TemplateSpecWithNullComponent feed = Instancio.createFeed(TemplateSpecWithNullComponent.class);

        assertThat(feed.abc().get()).isEqualTo("foo  baz");
    }
}
