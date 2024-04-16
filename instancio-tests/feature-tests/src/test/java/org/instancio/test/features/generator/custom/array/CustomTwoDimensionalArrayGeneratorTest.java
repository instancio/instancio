/*
 * Copyright 2022-2024 the original author or authors.
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
import org.instancio.generator.Generator;
import org.instancio.internal.util.Constants;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;

@FeatureTag(Feature.GENERATOR)
@ExtendWith(InstancioExtension.class)
class CustomTwoDimensionalArrayGeneratorTest {

    @Test
    void customTwoDimensionalArrayGenerator() {
        final int min = 0;
        final int max = 10;

        final Generator<int[][]> generator = r -> new int[][]{
                new int[]{r.intRange(min, max)},
                new int[]{r.intRange(min, max)},
        };

        final int[][] result = Instancio.of(int[][].class)
                .supply(all(int[][].class), generator)
                .create();

        assertThat(result.length).isBetween(Constants.MIN_SIZE, Constants.MAX_SIZE);

        for (int[] arr : result) {
            assertThat(arr).hasSize(1);
            assertThat(arr[0]).isBetween(min, max);
        }
    }
}
