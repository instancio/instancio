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
package org.instancio.test.features.values;

import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Gen.booleans;

@FeatureTag(Feature.VALUE_SPEC)
class BooleanSpecTest {

    @Test
    void get() {
        assertThat(booleans().get()).isNotNull();
    }

    @Test
    void list() {
        final int size = 10;
        final List<Boolean> results = booleans().list(size);
        assertThat(results).hasSize(size);
    }

    @Test
    void map() {
        final Integer result = booleans().map(b -> b ? 1 : 0);
        assertThat(result).isIn(0, 1);
    }

    @Test
    void nullable() {
        final Stream<Boolean> result = IntStream.range(0, 500)
                .mapToObj(i -> booleans().nullable().get());

        assertThat(result).containsNull();
    }
}
