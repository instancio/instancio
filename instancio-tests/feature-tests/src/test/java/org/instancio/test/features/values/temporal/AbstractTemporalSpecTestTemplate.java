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
package org.instancio.test.features.values.temporal;

import org.apache.commons.lang3.tuple.Pair;
import org.instancio.generator.specs.TemporalSpec;
import org.instancio.test.features.values.AbstractValueSpecTestTemplate;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.time.temporal.Temporal;

import static org.assertj.core.api.Assertions.assertThat;

abstract class AbstractTemporalSpecTestTemplate<T extends Temporal & Comparable<? super T>>
        extends AbstractValueSpecTestTemplate<T> {

    abstract Pair<T, T> getRangeFromNow();

    @Override
    protected abstract TemporalSpec<T> spec();

    @Test
    void past() {
        final T actual = spec().past().get();
        assertThat(actual).isLessThan(getNow());
    }

    @Test
    void future() {
        final T actual = spec().future().get();
        assertThat(actual).isGreaterThan(getNow());
    }

    @RepeatedTest(Constants.SAMPLE_SIZE_DD)
    void minMax() {
        final Pair<T, T> range = getRangeFromNow();

        final T actual = spec().min(range.getLeft()).max(range.getRight()).get();

        assertThat(actual).isBetween(range.getLeft(), range.getRight());
    }

    @RepeatedTest(Constants.SAMPLE_SIZE_DD)
    void range() {
        final Pair<T, T> range = getRangeFromNow();

        final T actual = spec()
                .range(range.getLeft(), range.getRight())
                .get();

        assertThat(actual).isBetween(range.getLeft(), range.getRight());
    }

    private T getNow() {
        return getRangeFromNow().getLeft();
    }
}
