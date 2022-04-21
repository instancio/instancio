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
package org.instancio.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class NumberUtils {

    private static final Map<Class<?>, Number> NUMERIC_MIN_VALUES = new HashMap<>();
    private static final Map<Class<?>, Number> NUMERIC_MAX_VALUES = new HashMap<>();
    private static final Map<Class<?>, Function<Long, Number>> LONG_CONVERTER_FUNCTIONS = new HashMap<>();

    static {
        NUMERIC_MIN_VALUES.put(Byte.class, Byte.MIN_VALUE);
        NUMERIC_MIN_VALUES.put(Short.class, Short.MIN_VALUE);
        NUMERIC_MIN_VALUES.put(Integer.class, Integer.MIN_VALUE);
        NUMERIC_MIN_VALUES.put(Long.class, Long.MIN_VALUE);
        NUMERIC_MIN_VALUES.put(Float.class, Float.MIN_VALUE);
        NUMERIC_MIN_VALUES.put(Double.class, Double.MIN_VALUE);

        NUMERIC_MAX_VALUES.put(Byte.class, Byte.MAX_VALUE);
        NUMERIC_MAX_VALUES.put(Short.class, Short.MAX_VALUE);
        NUMERIC_MAX_VALUES.put(Integer.class, Integer.MAX_VALUE);
        NUMERIC_MAX_VALUES.put(Long.class, Long.MAX_VALUE);
        NUMERIC_MAX_VALUES.put(Float.class, Float.MAX_VALUE);
        NUMERIC_MAX_VALUES.put(Double.class, Double.MAX_VALUE);

        LONG_CONVERTER_FUNCTIONS.put(Byte.class, Long::byteValue);
        LONG_CONVERTER_FUNCTIONS.put(Short.class, Long::shortValue);
        LONG_CONVERTER_FUNCTIONS.put(Integer.class, Long::intValue);
        LONG_CONVERTER_FUNCTIONS.put(Long.class, l -> l);
        LONG_CONVERTER_FUNCTIONS.put(Float.class, Long::floatValue);
        LONG_CONVERTER_FUNCTIONS.put(Double.class, Long::doubleValue);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Number & Comparable<T>> T getMinValue(final Class<?> klass) {
        return (T) NUMERIC_MIN_VALUES.get(klass);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Number & Comparable<T>> T getMaxValue(final Class<?> klass) {
        return (T) NUMERIC_MAX_VALUES.get(klass);
    }

    public static Function<Long, Number> getLongConverter(final Class<?> klass) {
        return LONG_CONVERTER_FUNCTIONS.get(klass);
    }

    private NumberUtils() {
        // non-instantiable
    }
}
