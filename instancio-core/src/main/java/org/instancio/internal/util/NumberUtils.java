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
package org.instancio.internal.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public final class NumberUtils {

    private static final int RADIX_DECIMAL = 10;

    private static final BigDecimal HUNDRED = new BigDecimal(100);

    private static final Map<Class<?>, Number> NUMERIC_MIN_VALUES = new HashMap<>(80);
    private static final Map<Class<?>, Number> NUMERIC_MAX_VALUES = new HashMap<>(80);
    private static final Map<Class<?>, Function<Long, Number>> CONVERT_FROM_LONG_FN_MAP = new HashMap<>();
    private static final Map<Class<?>, Function<BigDecimal, Number>> CONVERT_FROM_BD_FN_MAP = new HashMap<>();

    static {
        NUMERIC_MIN_VALUES.put(Byte.class, Byte.MIN_VALUE);
        NUMERIC_MIN_VALUES.put(Short.class, Short.MIN_VALUE);
        NUMERIC_MIN_VALUES.put(Integer.class, Integer.MIN_VALUE);
        NUMERIC_MIN_VALUES.put(Long.class, Long.MIN_VALUE);
        NUMERIC_MIN_VALUES.put(Float.class, Float.MIN_VALUE);
        NUMERIC_MIN_VALUES.put(Double.class, Double.MIN_VALUE);
        NUMERIC_MIN_VALUES.put(byte.class, Byte.MIN_VALUE);
        NUMERIC_MIN_VALUES.put(short.class, Short.MIN_VALUE);
        NUMERIC_MIN_VALUES.put(int.class, Integer.MIN_VALUE);
        NUMERIC_MIN_VALUES.put(long.class, Long.MIN_VALUE);
        NUMERIC_MIN_VALUES.put(float.class, Float.MIN_VALUE);
        NUMERIC_MIN_VALUES.put(double.class, Double.MIN_VALUE);
        NUMERIC_MIN_VALUES.put(BigInteger.class, BigInteger.valueOf(Long.MIN_VALUE));
        NUMERIC_MIN_VALUES.put(BigDecimal.class, new BigDecimal(Long.MIN_VALUE));

        NUMERIC_MAX_VALUES.put(Byte.class, Byte.MAX_VALUE);
        NUMERIC_MAX_VALUES.put(Short.class, Short.MAX_VALUE);
        NUMERIC_MAX_VALUES.put(Integer.class, Integer.MAX_VALUE);
        NUMERIC_MAX_VALUES.put(Long.class, Long.MAX_VALUE);
        NUMERIC_MAX_VALUES.put(Float.class, Float.MAX_VALUE);
        NUMERIC_MAX_VALUES.put(Double.class, Double.MAX_VALUE);
        NUMERIC_MAX_VALUES.put(byte.class, Byte.MAX_VALUE);
        NUMERIC_MAX_VALUES.put(short.class, Short.MAX_VALUE);
        NUMERIC_MAX_VALUES.put(int.class, Integer.MAX_VALUE);
        NUMERIC_MAX_VALUES.put(long.class, Long.MAX_VALUE);
        NUMERIC_MAX_VALUES.put(float.class, Float.MAX_VALUE);
        NUMERIC_MAX_VALUES.put(double.class, Double.MAX_VALUE);
        NUMERIC_MAX_VALUES.put(BigInteger.class, BigInteger.valueOf(Long.MAX_VALUE));
        NUMERIC_MAX_VALUES.put(BigDecimal.class, new BigDecimal(Long.MAX_VALUE));

        CONVERT_FROM_LONG_FN_MAP.put(byte.class, Long::byteValue);
        CONVERT_FROM_LONG_FN_MAP.put(short.class, Long::shortValue);
        CONVERT_FROM_LONG_FN_MAP.put(int.class, Long::intValue);
        CONVERT_FROM_LONG_FN_MAP.put(long.class, l -> l);
        CONVERT_FROM_LONG_FN_MAP.put(float.class, Long::floatValue);
        CONVERT_FROM_LONG_FN_MAP.put(double.class, Long::doubleValue);
        CONVERT_FROM_LONG_FN_MAP.put(Byte.class, Long::byteValue);
        CONVERT_FROM_LONG_FN_MAP.put(Short.class, Long::shortValue);
        CONVERT_FROM_LONG_FN_MAP.put(Integer.class, Long::intValue);
        CONVERT_FROM_LONG_FN_MAP.put(Long.class, l -> l);
        CONVERT_FROM_LONG_FN_MAP.put(Float.class, Long::floatValue);
        CONVERT_FROM_LONG_FN_MAP.put(Double.class, Long::doubleValue);
        CONVERT_FROM_LONG_FN_MAP.put(BigDecimal.class, BigDecimal::new);
        CONVERT_FROM_LONG_FN_MAP.put(BigInteger.class, BigInteger::valueOf);

        CONVERT_FROM_BD_FN_MAP.put(byte.class, b -> round(b).byteValue());
        CONVERT_FROM_BD_FN_MAP.put(short.class, b -> round(b).shortValue());
        CONVERT_FROM_BD_FN_MAP.put(int.class, b -> round(b).intValue());
        CONVERT_FROM_BD_FN_MAP.put(long.class, b -> round(b).longValue());
        CONVERT_FROM_BD_FN_MAP.put(float.class, BigDecimal::floatValue);
        CONVERT_FROM_BD_FN_MAP.put(double.class, BigDecimal::doubleValue);
        CONVERT_FROM_BD_FN_MAP.put(Byte.class, b -> round(b).byteValue());
        CONVERT_FROM_BD_FN_MAP.put(Short.class, b -> round(b).shortValue());
        CONVERT_FROM_BD_FN_MAP.put(Integer.class, b -> round(b).intValue());
        CONVERT_FROM_BD_FN_MAP.put(Long.class, b -> round(b).longValue());
        CONVERT_FROM_BD_FN_MAP.put(Float.class, BigDecimal::floatValue);
        CONVERT_FROM_BD_FN_MAP.put(Double.class, BigDecimal::doubleValue);
        CONVERT_FROM_BD_FN_MAP.put(BigDecimal.class, b -> b);
        CONVERT_FROM_BD_FN_MAP.put(BigInteger.class, b -> round(b).toBigInteger());
    }

    private static BigDecimal round(final BigDecimal b) {
        return b.setScale(0, RoundingMode.HALF_UP);
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
    public static <T extends Number> Function<Long, T> longConverter(final Class<?> klass) {
        return (Function<Long, T>) CONVERT_FROM_LONG_FN_MAP.get(klass);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Number> Function<BigDecimal, T> bigDecimalConverter(final Class<?> klass) {
        return (Function<BigDecimal, T>) CONVERT_FROM_BD_FN_MAP.get(klass);
    }

    /**
     * Calculate a new minimum given the new maximum.
     * <p>
     * If the new maximum is less than the current minimum,
     * returns a new minimum value that is less than the nex maximum
     * by the specified percentage. Otherwise, returns the current minimum.
     *
     * @param curMin     current minimum
     * @param newMax     new maximum
     * @param percentage to adjust by
     * @param <T>        number type
     * @return new minimum if current minimum is greater than the new maximum
     */
    public static <T extends Number & Comparable<T>> T calculateNewMin(
            @Nullable final T curMin, @NotNull final T newMax, final int percentage) {

        if (Objects.equals(newMax, curMin)) {
            return curMin;
        }

        BigDecimal newMaxBD = toBigDecimal(newMax);

        if (newMaxBD.compareTo(BigDecimal.ZERO) == 0) {
            newMaxBD = BigDecimal.ONE.negate();
        }

        final BigDecimal curMinBD = toBigDecimal(curMin);
        final BigDecimal newMinBD;

        if (curMinBD == null || curMinBD.compareTo(newMaxBD) > 0) {
            final BigDecimal bdPercentage = new BigDecimal(percentage);
            final BigDecimal absDelta = newMaxBD
                    .multiply(bdPercentage.divide(HUNDRED, 3, RoundingMode.HALF_UP))
                    .abs();

            final BigDecimal absoluteMin = toBigDecimal(NumberUtils.getMinValue(newMaxBD.getClass()));

            if (absoluteMin != null && absoluteMin.add(absDelta).compareTo(newMaxBD) <= 0) {
                newMinBD = newMaxBD.subtract(absDelta);
            } else {
                newMinBD = absoluteMin;
            }
        } else {
            newMinBD = curMinBD;
        }

        @SuppressWarnings("unchecked") final Class<T> numberClass = (Class<T>) newMax.getClass();
        final Function<BigDecimal, T> fn = bigDecimalConverter(numberClass);
        return fn.apply(newMinBD);
    }

    /**
     * Calculate a new maximum given the new minimum.
     * <p>
     * If the new minimum is greater than the current maximum,
     * returns a new maximum value that is higher than the new minimum
     * by the specified percentage. Otherwise, returns the current maximum.
     *
     * @param curMax     current maximum
     * @param newMin     new minimum
     * @param percentage to adjust by
     * @param <T>        number type
     * @return new maximum if current maximum is less than the new minimum
     */
    public static <T extends Number & Comparable<T>> T calculateNewMax(
            @Nullable final T curMax, @NotNull final T newMin, final int percentage) {

        if (Objects.equals(newMin, curMax)) {
            return curMax;
        }

        BigDecimal newMinBD = toBigDecimal(newMin);

        if (newMinBD.compareTo(BigDecimal.ZERO) == 0) {
            newMinBD = BigDecimal.ONE;
        }

        final BigDecimal curMaxBD = toBigDecimal(curMax);
        final BigDecimal newMaxBD;

        if (curMaxBD == null || curMaxBD.compareTo(newMinBD) < 0) {
            final BigDecimal bdPercentage = new BigDecimal(percentage);
            final BigDecimal absDelta = newMinBD
                    .multiply(bdPercentage.divide(HUNDRED, 3, RoundingMode.HALF_UP))
                    .abs();

            final BigDecimal absoluteMax = toBigDecimal(NumberUtils.getMaxValue(newMin.getClass()));

            if (absoluteMax != null && absoluteMax.subtract(absDelta).compareTo(newMinBD) >= 0) {
                newMaxBD = newMinBD.add(absDelta);
            } else {
                newMaxBD = absoluteMax;
            }
        } else {
            newMaxBD = curMaxBD;
        }

        @SuppressWarnings("unchecked") final Class<T> numberClass = (Class<T>) newMin.getClass();
        final Function<BigDecimal, T> fn = bigDecimalConverter(numberClass);
        return fn.apply(newMaxBD);
    }

    public static Integer calculateNewMinSize(@Nullable final Integer curMin, final Integer newMax) {
        return Math.max(0, calculateNewMin(curMin, newMax, Constants.RANGE_ADJUSTMENT_PERCENTAGE));
    }

    public static Integer calculateNewMaxSize(@Nullable final Integer curMax, final Integer newMin) {
        return calculateNewMax(curMax, newMin, Constants.RANGE_ADJUSTMENT_PERCENTAGE);
    }

    public static int sumDigits(final int num) {
        int sum = 0;
        int n = num;
        while (n > 0) {
            sum += n % 10;
            n /= 10;
        }
        return sum;
    }

    public static int toDigitInt(final char digit) {
        return Character.digit(digit, RADIX_DECIMAL);
    }

    public static char toDigitChar(final int digit) {
        return Character.forDigit(digit, RADIX_DECIMAL);
    }

    public static boolean isZero(final BigDecimal value) {
        return value.compareTo(BigDecimal.ZERO) == 0;
    }

    public static BigDecimal toBigDecimal(final Number n) {
        if (n instanceof BigDecimal bd) {
            return bd;
        }
        return n == null ? null : new BigDecimal(String.valueOf(n));
    }

    private NumberUtils() {
        // non-instantiable
    }
}
