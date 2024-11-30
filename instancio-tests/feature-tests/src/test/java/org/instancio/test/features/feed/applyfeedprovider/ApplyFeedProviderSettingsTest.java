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
package org.instancio.test.features.feed.applyfeedprovider;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.FeedDataAccess;
import org.instancio.settings.Keys;
import org.instancio.settings.OnFeedPropertyUnmatched;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.root;

@FeatureTag({Feature.FEED, Feature.APPLY_FEED, Feature.SETTINGS})
@ExtendWith(InstancioExtension.class)
class ApplyFeedProviderSettingsTest {

    @Test
    void shouldInheritSettingsFromRootObject() {
        final Set<StringHolder> results = Stream.generate(() -> Instancio.of(StringHolder.class)
                        .applyFeed(root(), source -> source.ofResource("data/FeedExample.csv"))
                        .withSetting(Keys.FEED_DATA_ACCESS, FeedDataAccess.RANDOM)
                        .withSetting(Keys.ON_FEED_PROPERTY_UNMATCHED, OnFeedPropertyUnmatched.IGNORE)
                        .create())
                .limit(Constants.SAMPLE_SIZE_DD)
                .collect(Collectors.toSet());

        assertThat(results)
                .hasSize(3)
                .extracting(StringHolder::getValue)
                .containsExactlyInAnyOrder("value1", "value2", "value3");
    }

    @Test
    void shouldInheritSettingsSeedFromRootObject() {
        final Set<StringHolder> results = Stream.generate(() -> Instancio.of(StringHolder.class)
                        .applyFeed(root(), source -> source.ofResource("data/FeedExample.csv"))
                        .withSetting(Keys.FEED_DATA_ACCESS, FeedDataAccess.RANDOM)
                        .withSetting(Keys.ON_FEED_PROPERTY_UNMATCHED, OnFeedPropertyUnmatched.IGNORE)
                        .withSetting(Keys.SEED, -1L)
                        .create())
                .limit(Constants.SAMPLE_SIZE_DD)
                .collect(Collectors.toSet());

        assertThat(results).hasSize(1);
    }
}
