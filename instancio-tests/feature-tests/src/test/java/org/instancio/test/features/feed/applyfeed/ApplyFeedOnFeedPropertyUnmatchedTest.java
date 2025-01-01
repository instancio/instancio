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
import org.instancio.InstancioApi;
import org.instancio.Random;
import org.instancio.exception.InstancioApiException;
import org.instancio.feed.Feed;
import org.instancio.feed.FeedSpec;
import org.instancio.generator.Generator;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.root;

@FeatureTag({Feature.FEED, Feature.APPLY_FEED})
@ExtendWith(InstancioExtension.class)
class ApplyFeedOnFeedPropertyUnmatchedTest {

    @Feed.Source(string = "value,unmatchedProperty1\nfoo,bar")
    private interface SampleFeed extends Feed {

        // methods that don't return FeedSpec should NOT be
        // reported in the unmapped property error message
        Integer unrelated1();

        void unrelated2();

        @AliasSpec("value")
        FeedSpec<Integer> unmatchedProperty2();

        @GeneratedSpec(SomeIntegerGenerator.class)
        FeedSpec<Integer> unmatchedProperty3();

        class SomeIntegerGenerator implements Generator<Integer> {
            @Override
            public Integer generate(final Random random) {
                return 123;
            }
        }
    }

    @Test
    void shouldThrowExceptionOnUnmatchedFeedProperties() {
        final SampleFeed feed = Instancio.createFeed(SampleFeed.class);

        final InstancioApi<StringHolder> api = Instancio.of(StringHolder.class)
                .applyFeed(root(), feed);

        assertThatThrownBy(api::create)
                .isExactlyInstanceOf(InstancioApiException.class)
                // Error message should contain alphabetically sorted property names
                .hasMessageContainingAll("unmapped feed properties",
                        "unmatchedProperty1, unmatchedProperty2, unmatchedProperty3");
    }
}
