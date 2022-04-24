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

import org.instancio.util.Constants;
import org.instancio.util.NumberUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Expectations based on 10% delta between min and max:
 *
 * <pre>
 *   newMin curMax MAX_VALUE | newMax
 *   910    900    1000      | 1000
 *   100    99     1000      | 110
 *   100    100    1000      | 110
 *   100    101    1000      | 101
 *   100    null   1000      | null
 * </pre>
 */
class SettingsAutoAdjustmentTest {

    private static final int PERCENTAGE = Constants.RANGE_ADJUSTMENT_PERCENTAGE;

    private final Settings settings = Settings.defaults();

    @Test
    @DisplayName("max > newMin: then max should not change")
    void maxGreaterThanNewMin() {
        final int newMin = 100;
        final int max = settings.get(Keys.INTEGER_MAX);
        settings.set(Keys.INTEGER_MIN, newMin, false);

        assertThat((Object) settings.get(Keys.INTEGER_MAX)).isEqualTo(max);
    }

    @ValueSource(ints = {100, 101})
    @ParameterizedTest
    @DisplayName("newMin >= max: should update max to: newMin + PERCENTAGE")
    void newMinGreaterThanOrEqualToMax(final int max) {
        final int newMin = 101;
        settings
                .set(Keys.ARRAY_MAX_LENGTH, max)
                .set(Keys.ARRAY_MIN_LENGTH, newMin);

        assertThat((Object) settings.get(Keys.ARRAY_MAX_LENGTH))
                .as("Expecting newMax value to be greater than newMin by %s%%", PERCENTAGE)
                .isEqualTo((newMin * (100 + PERCENTAGE)) / 100);
    }

    @ValueSource(ints = {Integer.MAX_VALUE - 1})
    @ParameterizedTest
    @DisplayName("newMin > max: should update max to: absolute max")
    void newMaxIsAbsoluteMax(final int newMin) {
        settings
                .set(Keys.INTEGER_MAX, 0)
                .set(Keys.INTEGER_MIN, newMin);

        assertThat((Object) settings.get(Keys.INTEGER_MAX)).isEqualTo(Integer.MAX_VALUE);
    }

    @ValueSource(longs = {Long.MIN_VALUE + 1})
    @ParameterizedTest
    @DisplayName("newMax < min: should update min to: absolute min")
    void newMinIsAbsoluteMin(final long newMax) {
        settings
                .set(Keys.LONG_MIN, Long.MIN_VALUE + 100)
                .set(Keys.LONG_MAX, newMax);

        assertThat((Object) settings.get(Keys.LONG_MIN)).isEqualTo(Long.MIN_VALUE);
    }

    @Test
    @DisplayName("newMax (negative) < min: should update min to: newMax - PERCENTAGE")
    void newMaxIsNegativeAndIsLessThanMin() {
        settings.set(Keys.INTEGER_MAX, -100);

        assertThat((Object) settings.get(Keys.INTEGER_MIN)).isEqualTo(-150);
    }

    @Test
    @DisplayName("newMin (negative) > max (negative): should update max to: newMin + PERCENTAGE")
    void newMinIsNegativeAndIsGreaterThanMax() {
        settings
                .set(Keys.INTEGER_MAX, -120)
                .set(Keys.INTEGER_MIN, -100);

        assertThat((Object) settings.get(Keys.INTEGER_MAX)).isEqualTo(-50);
    }

    @Test
    @DisplayName("max is null: should set max")
    void setMaxIfNull() {
        final Settings settings = Settings.create();
        assertThat((Object) settings.get(Keys.INTEGER_MAX)).isNull();
        settings.set(Keys.INTEGER_MIN, 100);
        assertThat((Object) settings.get(Keys.INTEGER_MAX)).isEqualTo(150);
    }

    @Test
    @DisplayName("min is null: should set min")
    void setMinIfNull() {
        final Settings settings = Settings.create();
        assertThat((Object) settings.get(Keys.INTEGER_MIN)).isNull();
        settings.set(Keys.INTEGER_MAX, -100);
        assertThat((Object) settings.get(Keys.INTEGER_MIN)).isEqualTo(-150);
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class AllKeysTest {

        @ParameterizedTest
        @MethodSource("minSettingKeys")
        @DisplayName("newMin > max: verify each settings updates its max to: newMin + PERCENTAGE")
        void newMinIsGreaterThanMax(SettingKey minSetting) {
            final Optional<SettingKey> maxSetting = Keys.getAutoAdjustable(minSetting);
            assertThat(maxSetting).isPresent();

            final Number max = NumberUtils.getLongConverter(minSetting.type()).apply(50L);
            final Number newMin = NumberUtils.getLongConverter(minSetting.type()).apply(60L);
            final Number expectedMax = NumberUtils.getLongConverter(minSetting.type()).apply(90L);

            settings
                    .set(maxSetting.get(), max)
                    .set(minSetting, newMin);

            assertThat((Object) settings.get(maxSetting.get())).isEqualTo(expectedMax);
        }

        @ParameterizedTest
        @MethodSource("maxSettingKeys")
        @DisplayName("newMax < min: verify each settings updates its min to: newMax - PERCENTAGE")
        void newMaxIsLessThanMin(SettingKey maxSetting) {
            final Optional<SettingKey> minSetting = Keys.getAutoAdjustable(maxSetting);
            assertThat(minSetting).isPresent();

            final Number min = NumberUtils.getLongConverter(maxSetting.type()).apply(120L);
            final Number newMax = NumberUtils.getLongConverter(maxSetting.type()).apply(100L);
            final Number expectedMin = NumberUtils.getLongConverter(maxSetting.type()).apply(50L);

            settings
                    .set(minSetting.get(), min)
                    .set(maxSetting, newMax);

            assertThat((Object) settings.get(minSetting.get())).isEqualTo(expectedMin);
        }

        private Stream<Arguments> minSettingKeys() {
            return Stream.of(
                    Arguments.of(Keys.BYTE_MIN),
                    Arguments.of(Keys.SHORT_MIN),
                    Arguments.of(Keys.INTEGER_MIN),
                    Arguments.of(Keys.LONG_MIN),
                    Arguments.of(Keys.FLOAT_MIN),
                    Arguments.of(Keys.DOUBLE_MIN),
                    Arguments.of(Keys.ARRAY_MIN_LENGTH),
                    Arguments.of(Keys.COLLECTION_MIN_SIZE),
                    Arguments.of(Keys.MAP_MIN_SIZE),
                    Arguments.of(Keys.STRING_MIN_LENGTH));
        }

        private Stream<Arguments> maxSettingKeys() {
            return Stream.of(
                    Arguments.of(Keys.BYTE_MAX),
                    Arguments.of(Keys.SHORT_MAX),
                    Arguments.of(Keys.INTEGER_MAX),
                    Arguments.of(Keys.LONG_MAX),
                    Arguments.of(Keys.FLOAT_MAX),
                    Arguments.of(Keys.DOUBLE_MAX),
                    Arguments.of(Keys.ARRAY_MAX_LENGTH),
                    Arguments.of(Keys.COLLECTION_MAX_SIZE),
                    Arguments.of(Keys.MAP_MAX_SIZE),
                    Arguments.of(Keys.STRING_MAX_LENGTH));
        }
    }
}
