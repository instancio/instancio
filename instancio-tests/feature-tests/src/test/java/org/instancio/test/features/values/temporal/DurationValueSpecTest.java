/*
 * Copyright 2022-2023 the original author or authors.
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
package org.instancio.test.features.values.temporal;

import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Gen.temporal;

@FeatureTag(Feature.VALUE_SPEC)
class DurationValueSpecTest {

    @Test
    void get() {
        assertThat(temporal().duration().get()).isNotNull();
    }

    @Test
    void list() {
        final int size = 10;
        final List<Duration> results = temporal().duration().list(size);
        assertThat(results).hasSize(size);
    }

    @Test
    void map() {
        final Long result = temporal().duration().map(Duration::toDays);
        assertThat(result).isPositive();
    }

    @Test
    void of() {
        final Duration min = Duration.of(1, ChronoUnit.DAYS);
        final Duration max = Duration.of(9, ChronoUnit.DAYS);

        final List<Duration> actual = temporal().duration().of(1, 9, ChronoUnit.DAYS)
                .list(Constants.SAMPLE_SIZE_DDD);

        assertThat(actual)
                .hasSize(Constants.SAMPLE_SIZE_DDD)
                .allSatisfy(d -> assertThat(d).isBetween(min, max));
    }

    @Test
    void allowZero() {
        final List<Duration> actual = temporal().duration().allowZero().list(Constants.SAMPLE_SIZE_DDD);

        assertThat(actual)
                .hasSize(Constants.SAMPLE_SIZE_DDD)
                .contains(Duration.ZERO);
    }
}
