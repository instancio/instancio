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
import org.instancio.TargetSelector;
import org.instancio.feed.Feed;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.misc.StringsGhi;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.field;
import static org.instancio.Select.types;

@FeatureTag({Feature.FEED, Feature.APPLY_FEED, Feature.CARTESIAN_PRODUCT})
@ExtendWith(InstancioExtension.class)
class ApplyFeedCartesianProductTest {

    private static Stream<Arguments> args() {
        return Stream.of(
                Arguments.of(types().of(StringsGhi.class)),
                Arguments.of(all(StringsGhi.class)));
    }

    @Feed.Source(string = "g, h, i\n" +
            " _G1_, _H1_, I1\n" +
            " _G2_, _H2_, I2\n" +
            " _G3_, _H3_, I3\n" +
            " _G4_, _H4_, I4")
    private interface SampleFeed extends Feed {}

    @MethodSource("args")
    @ParameterizedTest
    void cartesianProduct(final TargetSelector selector) {
        final Feed feed = Instancio.createFeed(SampleFeed.class);

        final List<StringsGhi> result = Instancio.ofCartesianProduct(StringsGhi.class)
                .with(field(StringsGhi::getG), "G1", "G2")
                .with(field(StringsGhi::getH), "H1", "H2")
                .applyFeed(selector, feed)
                .create();

        // G and H field values should come from the Cartesian product inputs
        assertThat(result).extracting(StringsGhi::getG).containsExactly("G1", "G1", "G2", "G2");
        assertThat(result).extracting(StringsGhi::getH).containsExactly("H1", "H2", "H1", "H2");
        // I field values should come from the feed data
        assertThat(result).extracting(StringsGhi::getI).containsExactly("I1", "I2", "I3", "I4");
    }
}
