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
package org.instancio.test.features.generator.misc;

import org.instancio.Instancio;
import org.instancio.IntervalSupplier;
import org.instancio.junit.Given;
import org.instancio.junit.GivenProvider;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;

import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag(Feature.GENERATOR)
@ExtendWith(InstancioExtension.class)
class IntervalGeneratorTest {

    private static class BetweenZeroAnd10 implements GivenProvider {
        @Override
        public Object provide(final ElementContext context) {
            return context.random().intRange(0, 10);
        }
    }

    @InstancioSource
    @ParameterizedTest
    void dateIntervals_callingStartFirst(
            @Given(BetweenZeroAnd10.class) final int startingValue,
            @Given(BetweenZeroAnd10.class) final int intervalLength,
            @Given(BetweenZeroAnd10.class) final int betweenIntervals) {

        final IntervalSupplier<Integer> results = getIntervals(
                startingValue, intervalLength, betweenIntervals);

        int start = startingValue;
        int end = startingValue + intervalLength;

        for (int i = 0; i < Constants.SAMPLE_SIZE_DD; i++) {
            // start() first
            assertThat(results.start().get()).isEqualTo(start);
            assertThat(results.end().get()).isEqualTo(end);

            start = end + betweenIntervals;
            end = start + intervalLength;
        }
    }

    @InstancioSource
    @ParameterizedTest
    void dateIntervals_callingEndFirst(
            @Given(BetweenZeroAnd10.class) final int startingValue,
            @Given(BetweenZeroAnd10.class) final int intervalLength,
            @Given(BetweenZeroAnd10.class) final int betweenIntervals) {

        final IntervalSupplier<Integer> results = getIntervals(
                startingValue, intervalLength, betweenIntervals);

        int start = startingValue;
        int end = startingValue + intervalLength;

        for (int i = 0; i < Constants.SAMPLE_SIZE_DD; i++) {
            // end() first
            assertThat(results.end().get()).isEqualTo(end);
            assertThat(results.start().get()).isEqualTo(start);

            start = end + betweenIntervals;
            end = start + intervalLength;
        }
    }

    @Test
    void consecutiveStartInvocations(
            @Given(BetweenZeroAnd10.class) final int startingValue,
            @Given(BetweenZeroAnd10.class) final int intervalLength,
            @Given(BetweenZeroAnd10.class) final int betweenIntervals) {

        final IntervalSupplier<Integer> results = getIntervals(
                startingValue, intervalLength, betweenIntervals);

        int prev = results.start().get();
        for (int i = 0; i < Constants.SAMPLE_SIZE_DD; i++) {
            final Integer cur = results.start().get();

            assertThat(cur - prev).isEqualTo(betweenIntervals + intervalLength);
            prev = cur;
        }
    }

    @Test
    void consecutiveEndInvocations(
            @Given(BetweenZeroAnd10.class) final int startingValue,
            @Given(BetweenZeroAnd10.class) final int intervalLength,
            @Given(BetweenZeroAnd10.class) final int betweenIntervals) {

        final IntervalSupplier<Integer> results = getIntervals(
                startingValue, intervalLength, betweenIntervals);

        int prev = results.end().get();
        for (int i = 0; i < Constants.SAMPLE_SIZE_DD; i++) {
            final Integer cur = results.end().get();

            assertThat(cur - prev).isEqualTo(betweenIntervals + intervalLength);
            prev = cur;
        }
    }

    private static IntervalSupplier<Integer> getIntervals(
            @Given(BetweenZeroAnd10.class) final int startingValue,
            @Given(BetweenZeroAnd10.class) final int intervalLength,
            @Given(BetweenZeroAnd10.class) final int betweenIntervals) {

        return Instancio.gen()
                .intervalStarting(startingValue)
                .nextEnd((start, random) -> start + intervalLength)
                .nextStart((end, random) -> end + betweenIntervals)
                .get();
    }
}
