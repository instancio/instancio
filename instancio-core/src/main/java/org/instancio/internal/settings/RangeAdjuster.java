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
package org.instancio.internal.settings;

import org.instancio.documentation.InternalApi;
import org.instancio.internal.util.Constants;
import org.instancio.internal.util.NumberUtils;
import org.instancio.settings.SettingKey;
import org.instancio.settings.Settings;

/**
 * Provides support for auto-adjusting 'range' settings that have a min and a max value.
 *
 * @since 1.1.10
 */
@InternalApi
public interface RangeAdjuster {

    RangeAdjuster MIN_ADJUSTER = new RangeAdjuster.ForMin(Constants.RANGE_ADJUSTMENT_PERCENTAGE);
    RangeAdjuster MAX_ADJUSTER = new RangeAdjuster.ForMax(Constants.RANGE_ADJUSTMENT_PERCENTAGE);

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
    <T extends Number & Comparable<T>> void adjustRange(Settings settings, SettingKey<T> key, T otherValue);

    /**
     * Adjusts the lower bound of a range given a new upper bound.
     */
    class ForMin implements RangeAdjuster {
        private final int percentage;

        ForMin(final int percentage) {
            this.percentage = percentage;
        }

        @Override
        public <T extends Number & Comparable<T>> void adjustRange(
                final Settings settings, final SettingKey<T> minSetting, final T newMax) {

            final T curMin = settings.get(minSetting);
            final T newMin;

            if (((InternalKey<T>) minSetting).allowsNegative()) {
                newMin = NumberUtils.calculateNewMin(curMin, newMax, percentage);
            } else {
                newMin = (T) NumberUtils.calculateNewMinSize((Integer) curMin, (Integer) newMax);
            }
            ((InternalSettings) settings).set(minSetting, newMin, false);
        }
    }

    /**
     * Adjusts the upper bound of a range given a new lower bound.
     */
    class ForMax implements RangeAdjuster {
        private final int percentage;

        ForMax(final int percentage) {
            this.percentage = percentage;
        }

        @Override
        public <T extends Number & Comparable<T>> void adjustRange(
                final Settings settings, final SettingKey<T> maxSetting, final T newMin) {

            final T curMax = settings.get(maxSetting);
            final T newMax;
            if (((InternalKey<T>) maxSetting).allowsNegative()) {
                newMax = NumberUtils.calculateNewMax(curMax, newMin, percentage);
            } else {
                newMax = (T) NumberUtils.calculateNewMaxSize((Integer) curMax, (Integer) newMin);
            }
            ((InternalSettings) settings).set(maxSetting, newMax, false);
        }
    }
}
