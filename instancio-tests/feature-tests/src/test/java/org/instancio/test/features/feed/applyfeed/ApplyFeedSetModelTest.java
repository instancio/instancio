/*
 * Copyright 2022-2026 the original author or authors.
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
import static org.instancio.Select.types;

@FeatureTag({
        Feature.FEED,
        Feature.APPLY_FEED,
        Feature.MODEL,
        Feature.SET_MODEL
})
@ExtendWith(InstancioExtension.class)
class ApplyFeedSetModelTest {

    @Feed.Source(string = "value\nfoo\nbar\nbaz")
    private interface SampleFeed extends Feed {}

    private static Stream<Arguments> args() {
        return Stream.of(
                Arguments.of(types().of(StringHolder.class)),
                Arguments.of(types().atDepth(0)),
                Arguments.of(all(StringHolder.class))
        );
    }

    @MethodSource("args")
    @ParameterizedTest
    void setModel(final TargetSelector selector) {
        final Feed feed = Instancio.createFeed(SampleFeed.class);

        final Model<StringHolder> model = Instancio.of(StringHolder.class)
                .applyFeed(selector, feed)
                .toModel();

        final StringHolder result = Instancio.of(StringHolder.class)
                .setModel(selector, model)
                .create();

        assertThat(result.getValue()).isEqualTo("foo");
    }
}
