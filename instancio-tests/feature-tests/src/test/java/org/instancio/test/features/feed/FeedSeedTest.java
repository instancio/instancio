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
import org.instancio.settings.Keys;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag({
        Feature.FEED,
        Feature.SEED,
        Feature.SETTINGS
})
@ExtendWith(InstancioExtension.class)
class FeedSeedTest {

    @Feed.Source(string = "number\n1\n2\n3\n4\n5")
    private interface SampleFeed extends Feed {
        FeedSpec<Integer> number();
    }

    @Test
    void seed() {
        final Set<Integer> results = Stream.generate(() -> {
                    final SampleFeed spec = Instancio.ofFeed(SampleFeed.class)
                            .withSetting(Keys.SEED, -1L)
                            .create();

                    return spec.number().get();
                })
                .limit(Constants.SAMPLE_SIZE_D)
                .collect(Collectors.toSet());

        assertThat(results).hasSize(1);
    }
}
