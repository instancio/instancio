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
package org.instancio.settings;

import org.instancio.util.NumberUtils;

import java.util.function.Function;

/**
 * Provides support for auto-adjusting 'range' settings that have a min and a max value.
 */
public interface RangeAdjuster {

    /**
     * Adjust given {@code key} based on {@code otherValue}.
     * <p>
     * For example, if min is set to a value higher than the max, will auto-adjust max
     * by a specified amount (and vice versa).
     *
     * @param settings   to update
     * @param key        to update
     * @param otherValue based on which to update given setting key
     * @param <T>        numeric type
     */
    <T extends Number & Comparable<T>> void adjustRange(Settings settings, SettingKey key, Object otherValue);

    /**
     * Adjusts the lower bound of a range given a new upper bound.
     */
    class ForMin implements RangeAdjuster {
        private final double percentageDelta;

        ForMin(final int percentage) {
            percentageDelta = (percentage / 100d);
        }

        @Override
        public <T extends Number & Comparable<T>> void adjustRange(
                final Settings settings, final SettingKey minSetting, final Object otherValue) {

            final T curMin = settings.get(minSetting);
            if (curMin == null) {
                return;
            }

            final T newMax = (T) otherValue;

            if (curMin.compareTo(newMax) >= 0) {
                final long delta = (long) Math.abs((newMax.longValue() * percentageDelta));
                final T absoluteMin = NumberUtils.getMinValue(newMax.getClass());
                final long newMin = absoluteMin.longValue() + delta <= newMax.longValue()
                        ? newMax.longValue() - delta
                        : absoluteMin.longValue();

                final Function<Long, Number> fn = NumberUtils.getLongConverter(newMax.getClass());
                settings.set(minSetting, fn.apply(newMin), false);
            }
        }
    }

    /**
     * Adjusts the upper bound of a range given a new lower bound.
     */
    class ForMax implements RangeAdjuster {
        private final double percentageDelta;

        ForMax(final int percentage) {
            percentageDelta = (percentage / 100d);
        }

        @Override
        public <T extends Number & Comparable<T>> void adjustRange(
                final Settings settings, final SettingKey maxSetting, final Object otherValue) {

            final T curMax = settings.get(maxSetting);
            if (curMax == null) {
                return;
            }

            final T newMin = (T) otherValue;

            if (curMax.compareTo(newMin) <= 0) {
                final long delta = (long) Math.abs((newMin.longValue() * percentageDelta));
                final T absoluteMax = NumberUtils.getMaxValue(newMin.getClass());
                final long newMax = absoluteMax.longValue() - delta >= newMin.longValue()
                        ? newMin.longValue() + delta
                        : absoluteMax.longValue();

                final Function<Long, Number> fn = NumberUtils.getLongConverter(newMin.getClass());
                settings.set(maxSetting, fn.apply(newMax), false);
            }
        }
    }
}
