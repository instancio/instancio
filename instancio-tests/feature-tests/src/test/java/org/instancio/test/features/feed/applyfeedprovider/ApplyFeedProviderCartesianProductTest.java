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
package org.instancio.test.features.feed.applyfeedprovider;

import org.instancio.Instancio;
import org.instancio.TargetSelector;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.feed.FeedWithTag;
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
class ApplyFeedProviderCartesianProductTest {

    private static Stream<Arguments> args() {
        return Stream.of(
                Arguments.of(types().of(FeedWithTag.class)),
                Arguments.of(all(FeedWithTag.class)));
    }

    @MethodSource("args")
    @ParameterizedTest
    void cartesianProduct(final TargetSelector selector) {
        final List<FeedWithTag> result = Instancio.ofCartesianProduct(FeedWithTag.class)
                .with(field(FeedWithTag::getField1), "1A", "1B")
                .with(field(FeedWithTag::getField2), "2A", "2B")
                .applyFeed(selector, feed -> feed.ofResource(FeedWithTag.CSV_FILE))
                .create();

        // these field values should come from the Cartesian product inputs
        assertThat(result).extracting(FeedWithTag::getField1).containsExactly("1A", "1A", "1B", "1B");
        assertThat(result).extracting(FeedWithTag::getField2).containsExactly("2A", "2B", "2A", "2B");
        // the id values should come from the feed data
        assertThat(result).extracting(FeedWithTag::getId).containsExactly(101, 102, 103, 201);
    }
}
