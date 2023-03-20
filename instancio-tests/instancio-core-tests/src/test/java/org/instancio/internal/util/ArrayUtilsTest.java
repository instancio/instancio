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
package org.instancio.internal.util;

import org.instancio.Instancio;
import org.instancio.Random;
import org.instancio.support.DefaultRandom;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.all;

class ArrayUtilsTest {

    private static final Random RANDOM = new DefaultRandom();

    @Test
    void shuffleNonArray() {
        final String arg = "bad-input";
        assertThatThrownBy(() -> ArrayUtils.shuffle(arg, RANDOM))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("Not an array: %s", arg);
    }

    @Test
    void shuffleCharArray() {
        final char[] array = createArray(char[].class);
        Arrays.sort(array);
        final char[] sorted = Arrays.copyOf(array, array.length);

        ArrayUtils.shuffle(array, RANDOM);

        assertThat(array).isNotEqualTo(sorted);
    }

    @Test
    void shuffleByteArray() {
        final byte[] array = createArray(byte[].class);
        Arrays.sort(array);
        final byte[] sorted = Arrays.copyOf(array, array.length);

        ArrayUtils.shuffle(array, RANDOM);

        assertThat(array).isNotEqualTo(sorted);
    }

    @Test
    void shuffleShortArray() {
        final short[] array = createArray(short[].class);
        Arrays.sort(array);
        final short[] sorted = Arrays.copyOf(array, array.length);

        ArrayUtils.shuffle(array, RANDOM);

        assertThat(array).isNotEqualTo(sorted);
    }

    @Test
    void shuffleIntArray() {
        final int[] array = createArray(int[].class);
        Arrays.sort(array);
        final int[] sorted = Arrays.copyOf(array, array.length);

        ArrayUtils.shuffle(array, RANDOM);

        assertThat(array).isNotEqualTo(sorted);
    }

    @Test
    void shuffleLongArray() {
        final long[] array = createArray(long[].class);
        Arrays.sort(array);
        final long[] sorted = Arrays.copyOf(array, array.length);

        ArrayUtils.shuffle(array, RANDOM);

        assertThat(array).isNotEqualTo(sorted);
    }

    @Test
    void shuffleFloatArray() {
        final float[] array = createArray(float[].class);
        Arrays.sort(array);
        final float[] sorted = Arrays.copyOf(array, array.length);

        ArrayUtils.shuffle(array, RANDOM);

        assertThat(array).isNotEqualTo(sorted);
    }

    @Test
    void shuffleDoubleArray() {
        final double[] array = createArray(double[].class);
        Arrays.sort(array);
        final double[] sorted = Arrays.copyOf(array, array.length);

        ArrayUtils.shuffle(array, RANDOM);

        assertThat(array).isNotEqualTo(sorted);
    }

    @Test
    void shuffleStringArray() {
        final String[] array = createArray(String[].class);
        Arrays.sort(array);
        final String[] sorted = Arrays.copyOf(array, array.length);

        ArrayUtils.shuffle(array, RANDOM);

        assertThat(array).isNotEqualTo(sorted);
    }

    private static <T> T createArray(final Class<T> arrayClass) {
        return Instancio.of(arrayClass)
                .generate(all(arrayClass), gen -> gen.array().length(100))
                .create();
    }
}
