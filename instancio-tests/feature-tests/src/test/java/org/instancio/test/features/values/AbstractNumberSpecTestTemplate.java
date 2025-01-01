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
package org.instancio.test.features.values;

import org.apache.commons.lang3.tuple.Pair;
import org.instancio.generator.specs.NumberSpec;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class AbstractNumberSpecTestTemplate<T extends Number>
        extends AbstractValueSpecTestTemplate<T> {

    protected abstract NumberSpec<T> spec();

    protected abstract Pair<T, T> getRange();

    @Override
    protected void assertDefaultSpecValue(final T actual) {
        assertThat(actual.longValue()).isPositive();
    }

    @Test
    void min() {
        final T bound = getRange().getLeft();
        final T actual = spec().min(bound).get();

        assertThat(new BigDecimal(actual.toString()))
                .isGreaterThanOrEqualTo(new BigDecimal(bound.toString()));
    }

    @Test
    void max() {
        final T bound = getRange().getLeft();
        final T actual = spec().max(bound).get();

        assertThat(new BigDecimal(actual.toString()))
                .isLessThanOrEqualTo(new BigDecimal(bound.toString()));
    }

    @Test
    void range() {
        final Pair<T, T> range = getRange();

        final T actual = spec()
                .range(range.getLeft(), range.getRight())
                .get();

        assertThat(new BigDecimal(actual.toString())).isBetween(
                new BigDecimal(range.getLeft().toString()),
                new BigDecimal(range.getRight().toString()));
    }
}
