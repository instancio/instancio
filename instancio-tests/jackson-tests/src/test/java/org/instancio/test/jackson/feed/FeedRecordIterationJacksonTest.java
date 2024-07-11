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
package org.instancio.test.jackson.feed;

import org.instancio.Instancio;
import org.instancio.Random;
import org.instancio.feed.Feed;
import org.instancio.feed.FeedSpec;
import org.instancio.feed.FunctionProvider;
import org.instancio.generator.Generator;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.FeedDataAccess;
import org.instancio.settings.FeedFormatType;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Note: this is a copy of {@code FeedRecordIterationTest}
 * (from {@code feature-tests}) which uses JSON instead of CSV.
 */
@FeatureTag(Feature.FEED)
@ExtendWith(InstancioExtension.class)
class FeedRecordIterationJacksonTest {

    @WithSettings
    private final Settings settings = Settings.create()
            .set(Keys.FEED_DATA_ACCESS, FeedDataAccess.RANDOM);

    @Feed.FormatType(FeedFormatType.JSON)
    @Feed.Source(resource = "data/FeedWithTag.json")
    private interface Sample extends Feed {

        FeedSpec<Integer> id();

        FeedSpec<String> field1();

        FeedSpec<String> field2();

        @TemplateSpec("${field1}:${field2}")
        FeedSpec<String> templateSpec();

        @FunctionSpec(params = {"id", "field1"}, provider = Concatenator.class)
        FeedSpec<String> functionSpec();

        @GeneratedSpec(FixedValueGenerator.class)
        FeedSpec<String> generatedSpec();
    }

    private static class Concatenator implements FunctionProvider {
        String concatenate(final Object x, final Object y) {
            return x + ":" + y;
        }
    }

    private static class FixedValueGenerator implements Generator<String> {
        @Override
        public String generate(final Random random) {
            return "fixed-value";
        }
    }

    @Test
    void shouldProduceValuesFromTheSameRecord_allSpecs() {
        final Sample feed = Instancio.ofFeed(Sample.class)
                .withTagKey("tag")
                .withTagValue("EN")
                .create();

        for (int i = 0; i < Constants.SAMPLE_SIZE_DDD; i++) {
            final Integer id = feed.id().get();

            assertThat(id).isIn(101, 102, 103);
            assertThat(feed.field1().get()).matches(String.format("D_%d", id));
            assertThat(feed.field2().get()).matches(String.format("F_%d", id));
            assertThat(feed.templateSpec().get()).matches(String.format("D_%d:F_%d", id, id));
            assertThat(feed.functionSpec().get()).matches(String.format("%d:D_%d", id, id));
            assertThat(feed.generatedSpec().get()).isEqualTo("fixed-value");
        }
    }

    @Test
    void shouldProduceValuesFromTheSameRecord_compositeSpecs() {
        final Sample feed = Instancio.ofFeed(Sample.class)
                .withTagKey("tag")
                .withTagValue("EN")
                .create();

        for (int i = 0; i < Constants.SAMPLE_SIZE_DDD; i++) {
            final Integer id = feed.id().get();

            assertThat(id).isIn(101, 102, 103);
            assertThat(feed.templateSpec().get()).matches(String.format("D_%d:F_%d", id, id));
            assertThat(feed.functionSpec().get()).matches(String.format("%d:D_%d", id, id));
        }
    }
}
