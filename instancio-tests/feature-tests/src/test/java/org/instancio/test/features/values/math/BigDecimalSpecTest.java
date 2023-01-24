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
package org.instancio.test.features.values.math;

import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Gen.math;

@FeatureTag(Feature.VALUE_SPEC)
class BigDecimalSpecTest {

    @Test
    void get() {
        assertThat(math().bigDecimal().get()).isNotNull();
    }

    @Test
    void list() {
        final int size = 10;
        final List<BigDecimal> results = math().bigDecimal().list(size);
        assertThat(results).hasSize(size);
    }

    @Test
    void map() {
        final String result = math().bigDecimal().map(BigDecimal::toString);
        assertThat(result).isNotBlank();
    }

    @Test
    void nullable() {
        final Stream<BigDecimal> result = IntStream.range(0, 500)
                .mapToObj(i -> math().bigDecimal().nullable().get());

        assertThat(result).containsNull();
    }

    @Test
    void scale() {
        final BigDecimal result = math().bigDecimal().scale(10).get();
        assertThat(result).hasScaleOf(10);
    }

    @Test
    void min() {
        final BigDecimal result = math().bigDecimal().min(new BigDecimal(Integer.MAX_VALUE)).get();
        assertThat(result).isGreaterThanOrEqualTo(new BigDecimal(Integer.MAX_VALUE));
    }

    @Test
    void max() {
        final BigDecimal result = math().bigDecimal().max(new BigDecimal(Integer.MIN_VALUE)).get();
        assertThat(result).isLessThanOrEqualTo(new BigDecimal(Integer.MIN_VALUE));
    }

    @Test
    void range() {
        final BigDecimal result = math().bigDecimal().range(BigDecimal.ZERO, BigDecimal.ONE).get();
        assertThat(result).isBetween(BigDecimal.ZERO, BigDecimal.ONE);
    }
}
