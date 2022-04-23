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

    @SuppressWarnings("unchecked")
    public static <T extends Number> Function<Long, T> getLongConverter(final Class<?> klass) {
        return (Function<Long, T>) LONG_CONVERTER_FUNCTIONS.get(klass);
    }

    /**
     * Calculate a new minimum given the new maximum.
     * <p>
     * If the new maximum is less than or equal to the current minimum,
     * returns a new minimum value that is lower than the nex maximum
     * by the specified percentage. Otherwise, returns the current minimum.
     *
     * @param curMin     current minimum
     * @param newMax     new maximum
     * @param percentage to adjust by
     * @param <T>        number type
     * @return new minimum if current is greater than or equal to current maximum
     */
    public static <T extends Number & Comparable<T>> T calculateNewMin(
            final T curMin, final T newMax, final int percentage) {

        final long newMin;
        if (curMin == null || curMin.compareTo(newMax) >= 0) {
            final long delta = (long) Math.abs((newMax.longValue() * (percentage / 100d)));
            final T absoluteMin = NumberUtils.getMinValue(newMax.getClass());
            newMin = absoluteMin.longValue() + delta <= newMax.longValue()
                    ? newMax.longValue() - delta
                    : absoluteMin.longValue();
        } else {
            newMin = curMin.longValue();
        }

        final Function<Long, T> fn = getLongConverter(newMax.getClass());
        return fn.apply(newMin);
    }

    /**
     * Calculate a new maximum given the new minimum.
     * <p>
     * If the new minimum is greater than or equal to the current maximum,
     * returns a new maximum value that is higher than the new minimum
     * by the specified percentage. Otherwise, returns the current maximum.
     *
     * @param curMax     current maximum
     * @param newMin     new minimum
     * @param percentage to adjust by
     * @param <T>        number type
     * @return new maximum if current is lower than or equal to current minimum
     */
    public static <T extends Number & Comparable<T>> T calculateNewMax(
            final T curMax, final T newMin, final int percentage) {

        final long newMax;
        if (curMax == null || curMax.compareTo(newMin) <= 0) {
            final long delta = (long) Math.abs((newMin.longValue() * (percentage / 100d)));
            final T absoluteMax = NumberUtils.getMaxValue(newMin.getClass());
            newMax = absoluteMax.longValue() - delta >= newMin.longValue()
                    ? newMin.longValue() + delta
                    : absoluteMax.longValue();
        } else {
            newMax = curMax.longValue();
        }

        final Function<Long, T> fn = getLongConverter(newMin.getClass());
        return fn.apply(newMax);
    }

    private NumberUtils() {
        // non-instantiable
    }
}
