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
import org.instancio.feed.Feed;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.FeedDataAccess;
import org.instancio.settings.FeedDataEndStrategy;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag({Feature.FEED, Feature.VALUE_SPEC})
@ExtendWith(InstancioExtension.class)
class FeedDataAccessTest {

    // Set data access to sequential via Settings
    // (this should be overridden by the @Feed.DataAccess annotation)
    @WithSettings
    private final Settings settings = Settings.create()
            .set(Keys.FEED_DATA_ACCESS, FeedDataAccess.SEQUENTIAL)
            .set(Keys.FEED_DATA_END_STRATEGY, FeedDataEndStrategy.FAIL);

    @Feed.DataAccess(FeedDataAccess.RANDOM)
    @Feed.Source(name = "data/FeedExample.csv")
    private interface SampleFeed extends Feed {}

    @Test
    void feedAccessAnnotationShouldTakePrecedenceOverSettings() {
        // The data file contains 3 records.
        // If records are picked in random order (repetition is allowed,
        // since the same record might get picked multiple times),
        // then we should have 3*3*3 = 27 permutations
        final int numRecordsInDataFile = 3;
        final int numExpectedResults = 27;
        final Set<String> results = new HashSet<>();
        final SampleFeed feed = Instancio.createFeed(SampleFeed.class);

        for (int i = 0; i < Constants.SAMPLE_SIZE_DDD; i++) {

            String permutation = "";
            for (int j = 0; j < numRecordsInDataFile; j++) {
                //noinspection StringConcatenationInLoop
                permutation += feed.stringSpec("id").get();
            }
            results.add(permutation);
        }
        assertThat(results).hasSize(numExpectedResults);
    }
}
