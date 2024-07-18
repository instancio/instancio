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
package org.instancio.junit.given;

import org.instancio.feed.Feed;
import org.instancio.feed.FeedSpec;
import org.instancio.junit.Given;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.instancio.junit.WithSettings;
import org.instancio.settings.FeedDataEndAction;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

@Execution(ExecutionMode.CONCURRENT)
@ExtendWith(InstancioExtension.class)
class GivenFeedTest {

    @WithSettings
    private static final Settings settings = Settings.create()
            .set(Keys.FEED_DATA_END_ACTION, FeedDataEndAction.RECYCLE);

    @Feed.Source(string = "value\nA")
    private interface SampleFeed extends Feed {}

    private @Given SampleFeed feedField;

    @Test
    void feedAsSupplier(@Given final Supplier<SampleFeed> supplier) {
        final SampleFeed feed = supplier.get();

        assertThat(feed).isNotNull();
        assertThat(feed.stringSpec("value").get()).isEqualTo("A");
    }

    @Test
    void givenParams(@Given final String value, @Given final SampleFeed feed) {
        assertValues(value, feedField, feed);
    }

    @ValueSource(strings = "any")
    @ParameterizedTest
    void givenParamsWithValueSource(final String value, @Given final SampleFeed feed) {
        assertValues(value, feedField, feed);
    }

    @InstancioSource(samples = 5)
    @ParameterizedTest
    void givenParamsWithInstancioSource(@Given final SampleFeed feed, final String value) {
        assertValues(value, feedField, feed);
    }

    private static void assertValues(
            final String value,
            final SampleFeed feedField,
            final SampleFeed feedMethodArgument) {

        assertThat(value).isNotBlank();
        assertFeed(feedField);
        assertFeed(feedMethodArgument);
    }

    private static void assertFeed(final SampleFeed feed) {
        // assert twice to verify that the feed is using
        // the injected settings (i.e. FeedDataEndAction.RECYCLE)
        final FeedSpec<String> spec = feed.stringSpec("value");
        assertThat(spec.get()).isEqualTo("A");
        assertThat(spec.get()).isEqualTo("A");
    }
}
