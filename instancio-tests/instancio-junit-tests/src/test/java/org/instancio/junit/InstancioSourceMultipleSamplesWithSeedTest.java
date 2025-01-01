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
package org.instancio.junit;

import org.instancio.feed.Feed;
import org.instancio.feed.FeedSpec;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(InstancioExtension.class)
class InstancioSourceMultipleSamplesWithSeedTest {

    private static final long SEED = -1234;
    private static final int NUM_SAMPLES = 5;

    private final Set<String> results = new HashSet<>();

    @Feed.Source(string = "value\n1\n2\n3\n4\n5")
    private interface SampleFeed extends Feed {
        FeedSpec<Integer> value();
    }

    @Order(1)
    @Seed(SEED)
    @InstancioSource(samples = NUM_SAMPLES)
    @ParameterizedTest
    void first(final int number, final String string, final SampleFeed feed) {
        String result = number + ":" + string + ":" + feed.value().get();

        results.add(result);
    }

    @Order(2)
    @Seed(SEED)
    @InstancioSource(samples = NUM_SAMPLES)
    @ParameterizedTest
    void second(final int number, final String string, final SampleFeed feed) {
        String result = number + ":" + string + ":" + feed.value().get();

        results.add(result);

        assertThat(results).hasSize(NUM_SAMPLES);
    }
}
