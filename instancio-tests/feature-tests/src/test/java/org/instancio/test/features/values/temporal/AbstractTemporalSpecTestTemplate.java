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

import org.apache.commons.lang3.tuple.Pair;
import org.instancio.generator.specs.TemporalSpec;
import org.junit.jupiter.api.Test;

import java.time.temporal.Temporal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

abstract class AbstractTemporalSpecTestTemplate<T extends Temporal & Comparable<? super T>> {

    abstract TemporalSpec<T> getSpec();

    abstract Pair<T, T> getRangeFromNow();

    @Test
    void get() {
        final T actual = getSpec().get();
        assertThat(actual).isNotNull();
    }

    @Test
    void list() {
        final int size = 10;
        final List<T> results = getSpec().list(size);
        assertThat(results).hasSize(size);
    }

    @Test
    void map() {
        final String result = getSpec().map(Object::toString);
        assertThat(result).isNotBlank();
    }

    @Test
    void past() {
        final T actual = getSpec().past().get();
        assertThat(actual).isLessThan(getNow());
    }

    @Test
    void future() {
        final T actual = getSpec().future().get();
        assertThat(actual).isGreaterThan(getNow());
    }

    @Test
    void range() {
        final Pair<T, T> range = getRangeFromNow();

        final T actual = getSpec()
                .range(range.getLeft(), range.getRight())
                .get();

        assertThat(actual).isBetween(range.getLeft(), range.getRight());
    }

    @Test
    void nullable() {
        final int size = 500;
        final List<T> actual = getSpec().nullable().list(size);
        assertThat(actual).containsNull();
    }

    private T getNow() {
        return getRangeFromNow().getLeft();
    }
}
