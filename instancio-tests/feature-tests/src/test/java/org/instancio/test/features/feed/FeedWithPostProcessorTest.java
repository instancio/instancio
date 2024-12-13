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
import org.instancio.Random;
import org.instancio.feed.Feed;
import org.instancio.feed.FeedSpec;
import org.instancio.feed.PostProcessor;
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

import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.allInts;

@FeatureTag(Feature.FEED)
@ExtendWith(InstancioExtension.class)
class FeedWithPostProcessorTest {

    @WithSettings
    private final Settings settings = Settings.create()
            .set(Keys.FEED_DATA_ACCESS, FeedDataAccess.RANDOM);

    @Feed.Source(string = "number\n100\n200\n")
    private interface SampleFeed extends Feed {

        @WithPostProcessor({MultiplyBy10.class, IncrementBy1.class})
        @AliasSpec("number")
        FeedSpec<Integer> number();
    }

    private static class MultiplyBy10 implements PostProcessor<Integer> {
        @Override
        public Integer process(final Integer input, final Random random) {
            // for simplicity use a fixed "random" value
            return input * random.intRange(10, 10);
        }
    }

    private static class IncrementBy1 implements PostProcessor<Integer> {
        @Override
        public Integer process(final Integer input, final Random random) {
            return input + random.intRange(1, 1);
        }
    }

    @Test
    void postProcessor() {
        final SampleFeed feed = Instancio.createFeed(SampleFeed.class);

        final List<Integer> results = Instancio.ofList(Integer.class)
                .size(Constants.SAMPLE_SIZE_DDD)
                .generate(allInts(), feed.number())
                .create();

        assertThat(new HashSet<>(results)).containsOnly(1001, 2001);
    }

}
