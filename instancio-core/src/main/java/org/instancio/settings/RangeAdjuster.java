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

import org.instancio.internal.util.NumberUtils;

/**
 * Provides support for auto-adjusting 'range' settings that have a min and a max value.
 *
 * @since 1.1.10
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
    <T extends Number & Comparable<T>> void adjustRange(Settings settings, SettingKey key, T otherValue);

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
                final Settings settings, final SettingKey minSetting, final T newMax) {

            final T curMin = settings.get(minSetting);
            final Number newMin = NumberUtils.calculateNewMin(curMin, newMax, percentage);
            settings.set(minSetting, newMin, false);
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
                final Settings settings, final SettingKey maxSetting, final T newMin) {

            final T curMax = settings.get(maxSetting);
            final Number newMax = NumberUtils.calculateNewMax(curMax, newMin, percentage);
            settings.set(maxSetting, newMax, false);
        }
    }
}
