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
package org.instancio.creation.array.primitive;

import org.instancio.Instancio;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.util.ArrayUtils;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

class DirectArrayCreationTest {

    @Test
    void intArray() {
        int[] results = Instancio.create(int[].class);
        assertThat(new HashSet<>(ArrayUtils.toList(results))).hasSizeGreaterThanOrEqualTo(1);
    }

    @Test
    void integerArray() {
        Integer[] results = Instancio.create(Integer[].class);
        assertThat(new HashSet<>(ArrayUtils.toList(results))).hasSizeGreaterThanOrEqualTo(1);
    }

    @Test
    void twoDimensionalIntArray() {
        int[][] arr1 = Instancio.of(int[][].class)
                .withSettings(Settings.create().set(Keys.INTEGER_MIN, 1))
                .create();

        assertThat(arr1).isNotNull();

        for (int[] arr2 : arr1) {
            for (int n : arr2) {
                assertThat(n).isPositive();
            }
        }
    }

    @Test
    void threeDimensionalIntArray() {
        int[][][] arr1 = Instancio.of(int[][][].class)
                .withSettings(Settings.create().set(Keys.INTEGER_MIN, 1))
                .create();

        assertThat(arr1).isNotNull();

        for (int[][] arr2 : arr1) {
            for (int[] arr3 : arr2) {
                for (int n : arr3) {
                    assertThat(n).isPositive();
                }
            }
        }
    }
}
