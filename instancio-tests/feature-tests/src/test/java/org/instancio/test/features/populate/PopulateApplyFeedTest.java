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
package org.instancio.test.features.populate;

import org.instancio.Instancio;
import org.instancio.feed.Feed;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.PopulationStrategy;
import org.instancio.test.support.conditions.Conditions;
import org.instancio.test.support.pojo.misc.StringFields;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;

@FeatureTag({Feature.APPLY_FEED, Feature.FEED, Feature.POPULATE})
@ExtendWith(InstancioExtension.class)
class PopulateApplyFeedTest {

    @Feed.Source(string = "one,two,three\n1,2,3")
    private interface SampleFeed extends Feed {}

    @DisplayName("applyFeed() should overwrite initialised fields")
    @EnumSource(value = PopulationStrategy.class, names = {"POPULATE_NULLS", "POPULATE_NULLS_AND_DEFAULT_PRIMITIVES"})
    @ParameterizedTest
    void applyFeed(final PopulationStrategy populationStrategy) {
        final StringFields object = StringFields.builder()
                .one("foo")
                .two("bar")
                .three(null)
                .four(null)
                .build();

        Instancio.ofObject(object)
                .withPopulationStrategy(populationStrategy)
                .applyFeed(all(StringFields.class), feed -> feed.of(SampleFeed.class))
                .populate();

        assertThat(object.getOne()).isEqualTo("1");
        assertThat(object.getTwo()).isEqualTo("2");
        assertThat(object.getThree()).isEqualTo("3");
        assertThat(object.getFour()).is(Conditions.RANDOM_STRING);
    }

    @Test
    void applyFeed_applySelectors() {
        final StringFields object = StringFields.builder()
                .one("foo")
                .two("bar")
                .three(null)
                .four(null)
                .build();

        Instancio.ofObject(object)
                .withPopulationStrategy(PopulationStrategy.APPLY_SELECTORS)
                .applyFeed(all(StringFields.class), feed -> feed.of(SampleFeed.class))
                .populate();

        assertThat(object.getOne()).isEqualTo("1");
        assertThat(object.getTwo()).isEqualTo("2");
        assertThat(object.getThree()).isEqualTo("3");
        assertThat(object.getFour())
                .as("Since the feed has no value for this property, it remains null")
                .isNull();
    }
}
