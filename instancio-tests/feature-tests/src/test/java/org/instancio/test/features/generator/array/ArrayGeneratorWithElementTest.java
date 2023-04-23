/*
 *  Copyright 2022-2023 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.instancio.test.features.generator.array;

import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.exception.InstancioApiException;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.arrays.ArrayLong;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.all;

// Casts added to suppress "non-varargs call of varargs" during the build
@SuppressWarnings("RedundantCast")
@FeatureTag({Feature.GENERATE, Feature.ARRAY_GENERATOR_WITH})
@ExtendWith(InstancioExtension.class)
class ArrayGeneratorWithElementTest {
    private static final Long[] EXPECTED_LONGS = {1L, 2L, 3L};

    @Test
    void arrayWithElements() {
        final ArrayLong result = Instancio.of(ArrayLong.class)
                .generate(all(long[].class), gen -> gen.array().with((Object[]) EXPECTED_LONGS))
                .generate(all(Long[].class), gen -> gen.array().with((Object[]) EXPECTED_LONGS))
                .create();

        assertThat(result.getPrimitive()).contains(EXPECTED_LONGS);
        assertThat(result.getWrapper()).contains(EXPECTED_LONGS);
    }

    @Test
    void createArrayDirectly() {
        final long[] result = Instancio.of(long[].class)
                .generate(all(long[].class), gen -> gen.array().with((Object[]) EXPECTED_LONGS))
                .create();

        assertThat(result).contains(EXPECTED_LONGS);
    }

    @Test
    void withElementsAndMaxLengthOfZero() {
        final Long[] sorted = LongStream.range(1, 100).boxed().toArray(Long[]::new);

        final ArrayLong result = Instancio.of(ArrayLong.class)
                .generate(all(all(long[].class), all(Long[].class)), gen -> gen.array().maxLength(0).with(sorted))
                .create();

        assertThat(result.getPrimitive()).containsOnly(sorted);
        assertThat(result.getWrapper())
                .as("Result should be shuffled")
                .containsOnly(sorted)
                .isNotEqualTo(sorted);
    }

    @Test
    void withNullOrEmpty() {
        assertValidation((Object[]) null);
        assertValidation();
    }

    private void assertValidation(final Object... arg) {
        final InstancioApi<ArrayLong> api = Instancio.of(ArrayLong.class)
                .generate(all(long[].class), gen -> gen.array().with(arg));

        assertThatThrownBy(api::create)
                .isInstanceOf(InstancioApiException.class)
                .hasMessageContaining("'array().with(...)' must contain at least one element");
    }
}