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
import org.instancio.feed.FunctionProvider;
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

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag(Feature.FEED)
@ExtendWith(InstancioExtension.class)
class FeedFunctionSpecTest {

    @WithSettings
    private final Settings settings = Settings.create()
            .set(Keys.FEED_DATA_ACCESS, FeedDataAccess.RANDOM);

    @Feed.Source(string = "x,y\n1,2")
    private interface SampleFeed extends Feed {
        String X = "x";
        String Y = "y";

        @TemplateSpec("${x} + ${y}")
        FeedSpec<String> sumFormula();

        @FunctionSpec(params = {X, Y}, provider = SumFunction.class)
        FeedSpec<Integer> sum();

        @FunctionSpec(params = {X, Y}, provider = SumFunctionWithRandom.class)
        FeedSpec<Integer> sumWithRandom();

        @FunctionSpec(params = {"sumFormula", X, Y}, provider = SumFormulaFunction.class)
        FeedSpec<String> sumFormulaWithResult();

        class SumFunction implements FunctionProvider {
            @SuppressWarnings("unused")
            Integer addIntegers(final Integer x, final Integer y) {
                return x + y;
            }
        }

        class SumFormulaFunction implements FunctionProvider {
            @SuppressWarnings("unused")
            String formulaWithResult(final String formula, final Integer x, final Integer y) {
                final int result = x + y;
                return String.format("%s = %d", formula, result);
            }
        }

        class SumFunctionWithRandom implements FunctionProvider {
            @SuppressWarnings("unused")
            Integer addWithRandom(final Integer x, final Integer y, final Random random) {
                final int z = random.oneOf(10, 100);
                return x + y + z;
            }
        }
    }

    @Test
    void sum() {
        final SampleFeed spec = Instancio.createFeed(SampleFeed.class);

        assertThat(spec.sum().get()).isEqualTo(3);
    }

    @Test
    void sumFormulaWithResult() {
        final SampleFeed result = Instancio.createFeed(SampleFeed.class);

        assertThat(result.sumFormulaWithResult().get()).isEqualTo("1 + 2 = 3");
    }

    @Test
    void sumWithRandom() {
        final SampleFeed feed = Instancio.createFeed(SampleFeed.class);

        final Set<Integer> results = Stream.generate(() -> feed.sumWithRandom().get())
                .limit(Constants.SAMPLE_SIZE_DD)
                .collect(Collectors.toSet());

        assertThat(results).containsOnly(13, 103);
    }
}
