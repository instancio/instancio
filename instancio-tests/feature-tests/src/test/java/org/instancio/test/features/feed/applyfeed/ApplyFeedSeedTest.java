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
import org.instancio.feed.Feed;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.Seed;
import org.instancio.junit.WithSettings;
import org.instancio.settings.FeedDataAccess;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;

@FeatureTag({
        Feature.FEED,
        Feature.APPLY_FEED,
        Feature.SEED,
        Feature.WITH_SEED
})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(InstancioExtension.class)
class ApplyFeedSeedTest {

    @Feed.Source(string = "value\n1\n2\n3\n4\n5")
    private interface SampleFeed extends Feed {}

    private static final long SEED = -1;

    @WithSettings
    private static final Settings settings = Settings.create()
            .set(Keys.FEED_DATA_ACCESS, FeedDataAccess.RANDOM);

    private static final Set<StringHolder> results = new HashSet<>();

    @Order(1)
    @Seed(SEED)
    @RepeatedTest(10)
    void seedAnnotation() {
        final Feed feed = Instancio.createFeed(SampleFeed.class);

        final StringHolder result = Instancio.of(StringHolder.class)
                .applyFeed(all(StringHolder.class), feed)
                .create();

        results.add(result);

        assertThat(results).hasSize(1);
    }

    @Order(2)
    @RepeatedTest(10)
    void withSettingsSeed() {
        final Feed feed = Instancio.ofFeed(SampleFeed.class)
                .withSetting(Keys.SEED, SEED)
                .create();

        final StringHolder result = Instancio.of(StringHolder.class)
                .applyFeed(all(StringHolder.class), feed)
                .withSetting(Keys.SEED, SEED)
                .create();

        results.add(result);

        assertThat(results).hasSize(1);
    }
}
