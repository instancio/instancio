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
package org.instancio.test.features.values.temporal;

import org.instancio.Instancio;
import org.instancio.generator.specs.DurationSpec;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.features.values.AbstractValueSpecTestTemplate;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag(Feature.VALUE_SPEC)
@ExtendWith(InstancioExtension.class)
class DurationValueSpecTest extends AbstractValueSpecTestTemplate<Duration> {

    @Override
    protected DurationSpec spec() {
        return Instancio.gen().temporal().duration();
    }

    @Test
    void min() {
        final Duration min = Duration.of(1000, ChronoUnit.DAYS);

        final List<Duration> actual = spec().min(min.toDays(), ChronoUnit.DAYS)
                .list(Constants.SAMPLE_SIZE_DDD);

        assertThat(actual)
                .hasSize(Constants.SAMPLE_SIZE_DDD)
                .allSatisfy(d -> assertThat(d).isGreaterThanOrEqualTo(min));
    }

    @Test
    void max() {
        final Duration max = Duration.of(-1000, ChronoUnit.DAYS);

        final List<Duration> actual = spec().max(max.toDays(), ChronoUnit.DAYS)
                .list(Constants.SAMPLE_SIZE_DDD);

        assertThat(actual)
                .hasSize(Constants.SAMPLE_SIZE_DDD)
                .allSatisfy(d -> assertThat(d).isLessThanOrEqualTo(max));
    }

    @Test
    void minMax() {
        final Duration min = Duration.of(1, ChronoUnit.SECONDS);
        final Duration max = Duration.of(10, ChronoUnit.SECONDS);

        final List<Duration> actual = spec()
                .min(min.toNanos(), ChronoUnit.NANOS)
                .max(max.toNanos(), ChronoUnit.NANOS)
                .list(Constants.SAMPLE_SIZE_DDD);

        assertThat(actual)
                .hasSize(Constants.SAMPLE_SIZE_DDD)
                .allSatisfy(d -> assertThat(d).isBetween(min, max));
    }

    @Test
    void of() {
        final Duration min = Duration.of(1, ChronoUnit.DAYS);
        final Duration max = Duration.of(9, ChronoUnit.DAYS);

        final List<Duration> actual = spec().of(1, 9, ChronoUnit.DAYS)
                .list(Constants.SAMPLE_SIZE_DDD);

        assertThat(actual)
                .hasSize(Constants.SAMPLE_SIZE_DDD)
                .allSatisfy(d -> assertThat(d).isBetween(min, max));
    }

    @Test
    void allowZero() {
        final List<Duration> actual = spec().allowZero().list(Constants.SAMPLE_SIZE_DDD);

        assertThat(actual)
                .hasSize(Constants.SAMPLE_SIZE_DDD)
                .contains(Duration.ZERO);
    }
}
