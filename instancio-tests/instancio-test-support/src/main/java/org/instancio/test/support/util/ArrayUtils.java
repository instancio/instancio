/*
 * Copyright 2022 the original author or authors.
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
package org.instancio.test.support.util;

import java.util.ArrayList;
import java.util.List;

public final class ArrayUtils {
    private ArrayUtils() {
        // non-instantiable
    }

    public static <T> List<T> toList(T... values) {
        List<T> result = new ArrayList<>();
        for (T v : values) result.add(v);
        return result;
    }

    public static List<Byte> toList(byte... values) {
        List<Byte> result = new ArrayList<>();
        for (byte v : values) result.add(v);
        return result;
    }

    public static List<Short> toList(short... values) {
        List<Short> result = new ArrayList<>();
        for (short v : values) result.add(v);
        return result;
    }

    public static List<Integer> toList(int... values) {
        List<Integer> result = new ArrayList<>();
        for (int v : values) result.add(v);
        return result;
    }

    public static List<Long> toList(long... values) {
        List<Long> result = new ArrayList<>();
        for (long v : values) result.add(v);
        return result;
    }

    public static List<Float> toList(float... values) {
        List<Float> result = new ArrayList<>();
        for (float v : values) result.add(v);
        return result;
    }

    public static List<Double> toList(double... values) {
        List<Double> result = new ArrayList<>();
        for (double v : values) result.add(v);
        return result;
    }

    public static List<Boolean> toList(boolean... values) {
        List<Boolean> result = new ArrayList<>();
        for (boolean v : values) result.add(v);
        return result;
    }

    public static List<Character> toList(char... values) {
        List<Character> result = new ArrayList<>();
        for (char v : values) result.add(v);
        return result;
    }

}
