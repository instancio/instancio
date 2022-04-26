/*
 *  Copyright 2022 the original author or authors.
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
package org.instancio.util;

import org.instancio.Random;

public class ArrayUtils {

    public static void shuffle(final Object arr, final Random random) {
        if (arr instanceof Object[]) {
            shuffleArray((Object[]) arr, random);
        } else if (arr instanceof byte[]) {
            shuffleArray((byte[]) arr, random);
        } else if (arr instanceof short[]) {
            shuffleArray((short[]) arr, random);
        } else if (arr instanceof int[]) {
            shuffleArray((int[]) arr, random);
        } else if (arr instanceof long[]) {
            shuffleArray((long[]) arr, random);
        } else if (arr instanceof float[]) {
            shuffleArray((float[]) arr, random);
        } else if (arr instanceof double[]) {
            shuffleArray((double[]) arr, random);
        } else if (arr instanceof char[]) {
            shuffleArray((char[]) arr, random);
        } else if (arr instanceof boolean[]) {
            shuffleArray((boolean[]) arr, random);
        } else {
            throw new IllegalArgumentException("Not an array: " + arr);
        }
    }

    private static void shuffleArray(Object[] arr, Random random) {
        for (int i = 0; i < arr.length; i++) {
            int r = random.intRange(0, i + 1);
            Object tmp = arr[i];
            arr[i] = arr[r];
            arr[r] = tmp;
        }
    }

    private static void shuffleArray(byte[] arr, Random random) {
        for (int i = 0; i < arr.length; i++) {
            int r = random.intRange(0, i + 1);
            byte tmp = arr[i];
            arr[i] = arr[r];
            arr[r] = tmp;
        }
    }

    private static void shuffleArray(short[] arr, Random random) {
        for (int i = 0; i < arr.length; i++) {
            int r = random.intRange(0, i + 1);
            short tmp = arr[i];
            arr[i] = arr[r];
            arr[r] = tmp;
        }
    }

    private static void shuffleArray(int[] arr, Random random) {
        for (int i = 0; i < arr.length; i++) {
            int r = random.intRange(0, i + 1);
            int tmp = arr[i];
            arr[i] = arr[r];
            arr[r] = tmp;
        }
    }

    private static void shuffleArray(long[] arr, Random random) {
        for (int i = 0; i < arr.length; i++) {
            int r = random.intRange(0, i + 1);
            long tmp = arr[i];
            arr[i] = arr[r];
            arr[r] = tmp;
        }
    }

    private static void shuffleArray(float[] arr, Random random) {
        for (int i = 0; i < arr.length; i++) {
            int r = random.intRange(0, i + 1);
            float tmp = arr[i];
            arr[i] = arr[r];
            arr[r] = tmp;
        }
    }

    private static void shuffleArray(double[] arr, Random random) {
        for (int i = 0; i < arr.length; i++) {
            int r = random.intRange(0, i + 1);
            double tmp = arr[i];
            arr[i] = arr[r];
            arr[r] = tmp;
        }
    }

    private static void shuffleArray(char[] arr, Random random) {
        for (int i = 0; i < arr.length; i++) {
            int r = random.intRange(0, i + 1);
            char tmp = arr[i];
            arr[i] = arr[r];
            arr[r] = tmp;
        }
    }

    private static void shuffleArray(boolean[] arr, Random random) {
        for (int i = 0; i < arr.length; i++) {
            int r = random.intRange(0, i + 1);
            boolean tmp = arr[i];
            arr[i] = arr[r];
            arr[r] = tmp;
        }
    }

    private ArrayUtils() {
        // non-instantiable
    }
}

