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
package org.instancio.test.features.generator.array;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.Seed;
import org.instancio.test.support.pojo.arrays.ArrayLong;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;

// Casts added to suppress "non-varargs call of varargs" during the build
@SuppressWarnings("RedundantCast")
@FeatureTag({Feature.GENERATE, Feature.ARRAY_GENERATOR_WITH})
@ExtendWith(InstancioExtension.class)
class ArrayGeneratorWithElementOrderTest {
    private static final Long[] EXPECTED_LONGS = {1L, 2L, 3L};

    private static final Set<List<Long>> PRIMITIVE_SEQUENCES = new LinkedHashSet<>();
    private static final Set<List<Long>> WRAPPER_SEQUENCES = new LinkedHashSet<>();

    @Seed(-12345)
    @RepeatedTest(5)
    void shuffledElementShouldBeInTheSameOrderForAGivenSeed() {
        final ArrayLong result = Instancio.of(ArrayLong.class)
                .generate(all(long[].class), gen -> gen.array().maxLength(5).with((Object[]) EXPECTED_LONGS))
                .generate(all(Long[].class), gen -> gen.array().maxLength(5).with((Object[]) EXPECTED_LONGS))
                .create();

        assertThat(result.getPrimitive()).contains(EXPECTED_LONGS);
        assertThat(result.getWrapper()).contains(EXPECTED_LONGS);

        PRIMITIVE_SEQUENCES.add(Arrays.stream(result.getPrimitive()).boxed().collect(toList()));
        WRAPPER_SEQUENCES.add(Arrays.stream(result.getWrapper()).collect(toList()));

        assertThat(PRIMITIVE_SEQUENCES)
                .as("Same shuffled sequence should be generated each time")
                .hasSameSizeAs(WRAPPER_SEQUENCES)
                .hasSize(1);
    }
}