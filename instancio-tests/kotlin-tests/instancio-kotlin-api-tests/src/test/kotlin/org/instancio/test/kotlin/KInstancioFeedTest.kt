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
package org.instancio.test.kotlin

import org.assertj.core.api.Assertions.assertThat
import org.instancio.feed.Feed
import org.instancio.feed.FeedSpec
import org.instancio.junit.InstancioExtension
import org.instancio.junit.WithSettings
import org.instancio.kotlin.KInstancio
import org.instancio.settings.FeedDataAccess
import org.instancio.settings.Keys
import org.instancio.settings.Settings
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(InstancioExtension::class)
class KInstancioFeedTest {

    @Feed.Source(resource = "data/KFeedExample.csv")
    private interface SampleFeed : Feed {
        fun id(): FeedSpec<Int>
        fun value(): FeedSpec<String>
    }

    @Nested
    inner class CreateFeed {

        @WithSettings
        val settings: Settings = Settings.create()
            .set(Keys.FEED_DATA_ACCESS, FeedDataAccess.RANDOM)

        @Test
        fun createFeedReturnsPopulatedFeed() {
            val feed = KInstancio.createFeed<SampleFeed>()

            val id = feed.id().get()
            val value = feed.value().get()

            assertThat(id).isBetween(1, 3)
            assertThat(value).isIn("value1", "value2", "value3")
        }
    }

    @Nested
    inner class OfFeed {

        @WithSettings
        val settings: Settings = Settings.create()
            .set(Keys.FEED_DATA_ACCESS, FeedDataAccess.SEQUENTIAL)

        @Test
        fun ofFeedReturnsApiBuilder() {
            val feed = KInstancio.ofFeed<SampleFeed>().create()

            val results = generateSequence { feed.id().get().toString() + ":" + feed.value().get() }
                .take(3)
                .toList()

            assertThat(results).containsExactly("1:value1", "2:value2", "3:value3")
        }
    }
}
