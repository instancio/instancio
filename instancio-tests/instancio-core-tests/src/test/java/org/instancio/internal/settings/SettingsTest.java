/*
 * Copyright 2022-2024 the original author or authors.
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

import org.instancio.Instancio;
import org.instancio.exception.InstancioApiException;
import org.instancio.generator.AfterGenerate;
import org.instancio.internal.context.PropertiesLoader;
import org.instancio.internal.util.Constants;
import org.instancio.settings.Keys;
import org.instancio.settings.Mode;
import org.instancio.settings.SettingKey;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.interfaces.PropertiesInterface;
import org.instancio.test.support.pojo.interfaces.PropertiesInterfaceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SettingsTest {
    private static final String TYPE_MAPPING_PREFIX = "subtype.";
    private static final boolean AUTO_ADJUST_DISABLED = false;

    private static final Settings DEFAULTS = Settings.defaults().lock();

    @Test
    void defaults() {
        for (SettingKey<?> settingKey : Keys.all()) {
            final Object actual = DEFAULTS.get(settingKey);
            final Object expected = settingKey.defaultValue();
            assertThat(actual).isEqualTo(expected);
        }
    }

    @Test
    void from() {
        final Map<Object, Object> map = new HashMap<>();
        map.put(Keys.FLOAT_MAX.propertyKey(), 9f);
        map.put(Keys.LONG_NULLABLE.propertyKey(), true);
        map.put(TYPE_MAPPING_PREFIX + "java.util.List", "java.util.ArrayList");
        map.put(TYPE_MAPPING_PREFIX + "java.util.Set", "java.util.HashSet");

        final Settings settings = Settings.from(map);

        assertThat(settings.get(Keys.FLOAT_MAX)).isEqualTo(9f);
        assertThat(settings.get(Keys.LONG_NULLABLE)).isTrue();
        assertThat(settings.getSubtypeMap())
                .containsEntry(List.class, ArrayList.class)
                .containsEntry(Set.class, HashSet.class);
    }

    @Test
    void merge() {
        final long originalLongMax = -1;

        final Settings original = Settings.defaults()
                .set(Keys.LONG_MAX, originalLongMax)
                .lock();

        final Settings overrides = Settings.create()
                .set(Keys.BYTE_MIN, (byte) 99)
                .set(Keys.ARRAY_NULLABLE, true)
                .lock();

        final Settings merged = original.merge(overrides);

        assertThat(merged.get(Keys.BYTE_MIN)).isEqualTo((byte) 99);
        assertThat(merged.get(Keys.ARRAY_NULLABLE)).isTrue();
        assertThat(merged.get(Keys.LONG_MAX))
                .as("Properties that were not overridden should retain their value")
                .isEqualTo(originalLongMax);

        assertThat(merged).as("Expecting a new instance of settings to be created").isNotSameAs(original);
    }

    @Test
    void getReturnsNullIfKeyHasNoValue() {
        assertThat(Settings.create().get(Keys.BYTE_MIN)).isNull();
    }

    @Test
    void strictModeIsEnabledByDefault() {
        assertThat(DEFAULTS.get(Keys.MODE)).isEqualTo(Mode.STRICT);
    }

    @Test
    void afterGenerateHintDefault() {
        assertThat(DEFAULTS.get(Keys.AFTER_GENERATE_HINT))
                .isEqualTo(AfterGenerate.POPULATE_NULLS_AND_DEFAULT_PRIMITIVES);
    }

    @Test
    void getSetEnum() {
        final Settings settings = Settings.create().set(Keys.MODE, Mode.LENIENT);
        assertThat(settings.get(Keys.MODE)).isEqualTo(Mode.LENIENT);
    }

    @Test
    void mapTypeThrowsErrorIfGivenInvalidSubtype() {
        final Settings settings = Settings.create();
        assertThatThrownBy(() -> settings.mapType(List.class, HashSet.class))
                .isInstanceOf(InstancioApiException.class)
                .hasMessageContaining("class 'java.util.HashSet' is not a subtype of 'java.util.List'");
    }

    @Test
    void numericValueOutOfBounds() {
        final int value = 1000;
        final Map<Object, Object> map = new HashMap<>();
        map.put(Keys.BYTE_MAX.propertyKey(), value);

        assertThatThrownBy(() -> Settings.from(map))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("invalid value %s (of type Integer) for setting key %s", value, Keys.BYTE_MAX)
                .hasCauseExactlyInstanceOf(NumberFormatException.class);
    }

    @Test
    void lockSettings() {
        final Settings locked = Settings.create().lock();

        assertThatThrownBy(() -> locked.set(Keys.LONG_MAX, 1L))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("This instance of Settings has been locked and is read-only");

        assertThatThrownBy(() -> locked.mapType(List.class, ArrayList.class))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("This instance of Settings has been locked and is read-only");
    }

    @Test
    void verifyToStringEmptySettings() {
        assertThat(Settings.create().toString()).containsSubsequence(
                "Settings[",
                "isLockedForModifications: false",
                "settingsMap: {}",
                "Settings subtypeMap: {}");
    }

    @Test
    @DisplayName("Should produce settings sorted by SettingKey.propertyKey()")
    void verifyToString() {
        final SettingKey<String> customKey = Keys.ofType(String.class).create();

        assertThat(Settings.create()
                .set(Keys.LONG_MIN, 123L)
                .set(Keys.DOUBLE_MIN, 345.9)
                .set(Keys.STRING_NULLABLE, true)
                .set(customKey, "custom-key-value")
                .set(Keys.ARRAY_MIN_LENGTH, 1)
                .set(Keys.ARRAY_MAX_LENGTH, 5)
                .mapType(List.class, ArrayList.class)
                .lock()
                .toString()
        ).containsSubsequence(
                "Settings[",
                "isLockedForModifications: true",
                "settingsMap:",
                "\t'array.max.length': 5",
                "\t'array.min.length': 1",
                "\t'custom.key.", "': custom-key-value",
                "\t'double.min': 345.9",
                "\t'long.min': 123",
                "\t'string.nullable': true",
                "Settings subtypeMap:",
                "\t'interface java.util.List': class java.util.ArrayList"
        );
    }

    /**
     * Bounds auto-adjusted based on {@link Constants#RANGE_ADJUSTMENT_PERCENTAGE}.
     */
    @Nested
    class AutoAdjustmentTest {
        @Test
        void setAutoAdjustsRangeBounds() {
            final int minLength = 1000;
            final int newMaxLength = 100;
            final Settings settings = Settings.defaults()
                    .set(Keys.ARRAY_MIN_LENGTH, minLength)
                    .set(Keys.ARRAY_MAX_LENGTH, newMaxLength);

            final int expected = newMaxLength - newMaxLength * Constants.RANGE_ADJUSTMENT_PERCENTAGE / 100;
            final int newMin = settings.get(Keys.ARRAY_MIN_LENGTH);
            assertThat(newMin).isEqualTo(expected);
        }

        @Test
        void setAutoAdjustEqualBounds() {
            final Settings settings = Settings.defaults()
                    .set(Keys.COLLECTION_MIN_SIZE, 2)
                    .set(Keys.COLLECTION_MAX_SIZE, 2)
                    .set(Keys.FLOAT_MIN, 3f)
                    .set(Keys.FLOAT_MAX, 3f);

            assertThat(settings.get(Keys.COLLECTION_MIN_SIZE))
                    .isEqualTo(settings.get(Keys.COLLECTION_MIN_SIZE))
                    .isEqualTo(2);

            assertThat(settings.get(Keys.FLOAT_MIN))
                    .isEqualTo(settings.get(Keys.FLOAT_MAX))
                    .isEqualTo(3f);
        }

        @Test
        void autoAdjustWithMerge() {
            final Settings settings1 = Settings.create()
                    .set(Keys.LONG_MIN, 10L); // min/max = [10, 15]

            final Settings settings2 = Settings.create()
                    .set(Keys.LONG_MAX, 11L); // min/max = [6, 11]

            final Settings result = Settings.defaults()
                    .merge(settings1)
                    .merge(settings2);

            assertThat(result.get(Keys.LONG_MIN)).isEqualTo(6);
            assertThat(result.get(Keys.LONG_MAX)).isEqualTo(11);
        }

        /**
         * For size settings, if max is set to zero, min should auto-adjust to zero.
         */
        @Test
        void minSizeShouldAutoAdjustToZeroIfMaxIsSetToToZero() {
            final Map<SettingKey<Integer>, SettingKey<Integer>> sizeKeys = Map.of(
                    Keys.ARRAY_MIN_LENGTH, Keys.ARRAY_MAX_LENGTH,
                    Keys.COLLECTION_MIN_SIZE, Keys.COLLECTION_MAX_SIZE,
                    Keys.MAP_MIN_SIZE, Keys.MAP_MAX_SIZE,
                    Keys.STRING_MIN_LENGTH, Keys.STRING_MAX_LENGTH
            );

            sizeKeys.forEach((min, max) -> {
                final Settings settings = Settings.create().set(max, 0);
                final Settings merged = Settings.defaults().merge(settings);

                assertThat(settings.get(min))
                        .isEqualTo(settings.get(max))
                        .isEqualTo(merged.get(min))
                        .isEqualTo(merged.get(max))
                        .isZero();
            });
        }

        /**
         * For numeric ranges, if max is set to zero, min should auto-adjust to a negative value.
         */
        @Test
        void minSizeShouldAutoAdjustToNegativeIfMaxIsSetToToZero() {
            final Settings settings = Settings.create()
                    .set(Keys.BYTE_MAX, (byte) 0)
                    .set(Keys.SHORT_MAX, (short) 0)
                    .set(Keys.INTEGER_MAX, 0)
                    .set(Keys.LONG_MAX, 0L)
                    .set(Keys.FLOAT_MAX, 0f)
                    .set(Keys.DOUBLE_MAX, 0d);

            assertRange(settings, Keys.BYTE_MIN, Keys.BYTE_MAX, (byte) 0);
            assertRange(settings, Keys.SHORT_MIN, Keys.SHORT_MAX, (short) 0);
            assertRange(settings, Keys.INTEGER_MIN, Keys.INTEGER_MAX, 0);
            assertRange(settings, Keys.LONG_MIN, Keys.LONG_MAX, 0L);
            assertRange(settings, Keys.FLOAT_MIN, Keys.FLOAT_MAX, 0f);
            assertRange(settings, Keys.DOUBLE_MIN, Keys.DOUBLE_MAX, 0d);
        }

        private static <T extends Number & Comparable<T>> void assertRange(
                final Settings settings, final SettingKey<T> min, final SettingKey<T> max, final T zero) {

            assertThat(settings.get(min)).isLessThan(zero);
            assertThat(settings.get(max)).isEqualTo(zero);

            final Settings merged = Settings.defaults().merge(settings);
            assertThat(merged.get(min)).isLessThan(zero);
            assertThat(merged.get(max)).isEqualTo(zero);
        }

        @Test
        void setWithoutAutoAdjust() {
            final int minLength = 1000;
            final int newMaxLength = 100;
            final Settings settings = Settings.defaults()
                    .set(Keys.ARRAY_MIN_LENGTH, minLength);

            ((InternalSettings) settings).set(Keys.ARRAY_MAX_LENGTH, newMaxLength, AUTO_ADJUST_DISABLED);

            final int newMin = settings.get(Keys.ARRAY_MIN_LENGTH);
            assertThat(newMin).isEqualTo(minLength);
        }
    }

    @Test
    @DisplayName("Should use subtypes specified in instancio.properties")
    void subtypeMappingFromInstancioProperties() {
        final PropertiesInterface result = Instancio.create(PropertiesInterface.class);

        assertThat(result).isExactlyInstanceOf(PropertiesInterfaceImpl.class);
        assertThat(result.getValue()).isNotBlank();
    }

    @Test
    void userDefinedKeyFromProperties() {
        final SettingKey<Integer> key = Keys.ofType(Integer.class)
                .withPropertyKey("user.defined.key")
                .create();

        final Settings settings = Settings.from(PropertiesLoader.loadDefaultPropertiesFile());

        assertThat(settings.get(key)).isEqualTo(12345);
    }

    @Test
    @DisplayName("Settings should accept values that are subtypes of SettingKey.type()")
    void keyType() {
        final SettingKey<CharSequence> key = Keys.ofType(CharSequence.class)
                .withPropertyKey("custom.key")
                .create();

        final String expected = "foo";
        final Settings settings = Settings.create().set(key, expected);

        final CharSequence actual = settings.get(key);

        assertThat(actual).isEqualTo(expected);
    }

    @Nested
    class ValidationTest {
        private final Settings settings = Settings.create();

        @Test
        void setNullOnNonNullableValue() {
            assertThatThrownBy(() -> settings.set(Keys.INTEGER_MIN, null))
                    .isInstanceOf(InstancioApiException.class)
                    .hasMessageContaining("setting value for key 'integer.min' must not be null");
        }

        @Test
        void nullAllowedForNullableValue() {
            final SettingKey<Long> key = Keys.SEED;
            assertThat(key.allowsNullValue()).isTrue();

            assertThatNoException().isThrownBy(() -> settings.set(key, null));
        }

        @Test
        void nullKeyNotAllowed() {
            assertThatThrownBy(() -> settings.set(null, "some value"))
                    .isExactlyInstanceOf(InstancioApiException.class)
                    .hasMessageContaining("setting key must not be null");
        }
    }
}
