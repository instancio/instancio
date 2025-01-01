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
package org.instancio.test.features.feed.applyfeed;

import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.TargetSelector;
import org.instancio.feed.Feed;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.OnFeedPropertyUnmatched;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.root;
import static org.instancio.Select.types;

@FeatureTag({Feature.FEED, Feature.APPLY_FEED, Feature.MODEL})
@ExtendWith(InstancioExtension.class)
class ApplyFeedModelTest {

    @WithSettings
    private static final Settings settings = Settings.create()
            .set(Keys.FEED_TAG_KEY, "tag")
            .set(Keys.ON_FEED_PROPERTY_UNMATCHED, OnFeedPropertyUnmatched.IGNORE);

    @Feed.Source(string = "tag,value\n1,foo\n2,bar\n3,baz")
    private interface SampleFeed extends Feed {}

    private static Stream<Arguments> args() {
        return Stream.of(
                Arguments.of(root()),
                Arguments.of(types().of(StringHolder.class)),
                Arguments.of(all(StringHolder.class)));
    }

    @MethodSource("args")
    @ParameterizedTest
    void setModel(final TargetSelector selector) {
        final Feed feed = Instancio.ofFeed(SampleFeed.class)
                .withTagValue("2")
                .create();

        final Model<StringHolder> model = Instancio.of(StringHolder.class)
                .applyFeed(selector, feed)
                .toModel();

        final StringHolder result = Instancio.create(model);

        assertThat(result.getValue()).isEqualTo("bar");
    }
}
