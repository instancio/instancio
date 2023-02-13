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

import java.time.Period;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Gen.temporal;

@FeatureTag(Feature.VALUE_SPEC)
class PeriodValueSpecTest {

    private static final int MIN = 1;
    private static final int MAX = 9;

    @Test
    void get() {
        assertThat(temporal().period().get()).isNotNull();
    }

    @Test
    void list() {
        final int size = 10;
        final List<Period> results = temporal().period().list(size);
        assertThat(results).hasSize(size);
    }

    @Test
    void map() {
        final Integer result = temporal().period().map(Period::getDays);
        assertThat(result).isPositive();
    }

    @Test
    void days() {
        final List<Period> actual = temporal().period().days(MIN, MAX).list(Constants.SAMPLE_SIZE_DDD);

        assertThat(actual)
                .hasSize(Constants.SAMPLE_SIZE_DDD)
                .allSatisfy(p -> assertThat(p.getDays()).isBetween(MIN, MAX));
    }

    @Test
    void months() {
        final List<Period> actual = temporal().period().months(MIN, MAX).list(Constants.SAMPLE_SIZE_DDD);

        assertThat(actual)
                .hasSize(Constants.SAMPLE_SIZE_DDD)
                .allSatisfy(p -> assertThat(p.getMonths()).isBetween(MIN, MAX));
    }

    @Test
    void years() {
        final List<Period> actual = temporal().period().years(MIN, MAX).list(Constants.SAMPLE_SIZE_DDD);

        assertThat(actual)
                .hasSize(Constants.SAMPLE_SIZE_DDD)
                .allSatisfy(p -> assertThat(p.getYears()).isBetween(MIN, MAX));
    }
}
