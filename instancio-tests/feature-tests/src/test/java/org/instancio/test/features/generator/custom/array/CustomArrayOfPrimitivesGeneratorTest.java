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
package org.instancio.test.features.generator.custom.array;

import org.instancio.Instancio;
import org.instancio.Random;
import org.instancio.generator.AfterGenerate;
import org.instancio.generator.Generator;
import org.instancio.generator.Hints;
import org.instancio.generator.hints.ArrayHint;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.allInts;
import static org.instancio.Select.types;

@FeatureTag({
        Feature.AFTER_GENERATE,
        Feature.ARRAY_GENERATOR_LENGTH,
        Feature.GENERATE,
        Feature.GENERATOR,
        Feature.SUBTYPE
})
@ExtendWith(InstancioExtension.class)
class CustomArrayOfPrimitivesGeneratorTest {

    private static final int ARRAY_SIZE = 3;
    private static final int POPULATED_INDEX = 1;
    private static final int ORIGINAL_VALUE = -1;

    private static class ArrayGenerator implements Generator<int[]> {
        private final AfterGenerate afterGenerate;

        private ArrayGenerator(final AfterGenerate afterGenerate) {
            this.afterGenerate = afterGenerate;
        }

        @Override
        public int[] generate(final Random random) {
            final int[] array = new int[ARRAY_SIZE];
            array[POPULATED_INDEX] = ORIGINAL_VALUE;
            return array;
        }

        @Override
        public Hints hints() {
            return Hints.builder()
                    .afterGenerate(afterGenerate)
                    .with(ArrayHint.builder().build())
                    .build();
        }
    }

    @Test
    @DisplayName("Should populate all indices, including the one with an existing value")
    void populateAll() {
        final int[] result = Instancio.of(int[].class)
                .supply(types().of(int[].class), new ArrayGenerator(AfterGenerate.POPULATE_ALL))
                .create();

        assertThat(result)
                .hasSize(ARRAY_SIZE)
                .doesNotContain(0);

        assertThat(result[POPULATED_INDEX]).isNotEqualTo(ORIGINAL_VALUE);
    }

    @ParameterizedTest
    @EnumSource(value = AfterGenerate.class, mode = EnumSource.Mode.INCLUDE, names = "POPULATE_NULLS_AND_DEFAULT_PRIMITIVES")
    @DisplayName("Engine should only populate indices containing default value (0); occupied index should retain original value")
    void customArrayGeneratorWithSubtype(final AfterGenerate afterGenerate) {
        final int[] result = Instancio.of(int[].class)
                .supply(types().of(int[].class), new ArrayGenerator(afterGenerate))
                .create();

        assertThat(result)
                .hasSize(ARRAY_SIZE)
                .doesNotContain(0);

        assertThat(result[POPULATED_INDEX]).isEqualTo(ORIGINAL_VALUE);
    }

    @ParameterizedTest
    @EnumSource(value = AfterGenerate.class, mode = EnumSource.Mode.INCLUDE, names = {"DO_NOT_MODIFY", "POPULATE_NULLS", "APPLY_SELECTORS"})
    @DisplayName("Engine should not populate zeroes in the array; populated index should retain original value")
    void customArrayGeneratorWithApplySelectors(final AfterGenerate afterGenerate) {
        final int[] result = Instancio.of(int[].class)
                .supply(types().of(int[].class), new ArrayGenerator(afterGenerate))
                .create();

        assertThat(result[POPULATED_INDEX]).isEqualTo(ORIGINAL_VALUE);

        // other indices should have zeroes
        for (int i = 0; i < result.length; i++) {
            if (i != POPULATED_INDEX) {
                assertThat(result[i]).isZero();
            }
        }
    }

    @ParameterizedTest
    @EnumSource(value = AfterGenerate.class, mode = EnumSource.Mode.INCLUDE,
            names = {"APPLY_SELECTORS", "POPULATE_NULLS", "POPULATE_NULLS_AND_DEFAULT_PRIMITIVES"})
    @DisplayName("Engine should apply selector, overwriting existing values")
    void applySelectors(final AfterGenerate afterGenerate) {
        final int expected = 100;

        final int[] result = Instancio.of(int[].class)
                .supply(types().of(int[].class), new ArrayGenerator(afterGenerate))
                .set(allInts(), expected)
                .create();

        // should set new value for allInts() (overwriting existing values)
        for (final int element : result) {
            assertThat(element).isEqualTo(expected);
        }
    }
}
