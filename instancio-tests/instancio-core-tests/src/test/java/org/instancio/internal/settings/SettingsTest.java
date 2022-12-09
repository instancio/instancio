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
package org.instancio.internal.settings;

import org.instancio.Instancio;
import org.instancio.Mode;
import org.instancio.TypeToken;
import org.instancio.exception.InstancioApiException;
import org.instancio.generator.PopulateAction;
import org.instancio.internal.util.Constants;
import org.instancio.settings.Keys;
import org.instancio.settings.SettingKey;
import org.instancio.settings.Settings;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentSkipListMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SettingsTest {
    private static final String TYPE_MAPPING_PREFIX = "subtype.";
    private static final boolean AUTO_ADJUST_DISABLED = false;

    private static final Settings DEFAULTS = Settings.defaults().lock();

    @Test
    void defaults() {
        for (SettingKey settingKey : Keys.all()) {
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

        assertThat((Float) settings.get(Keys.FLOAT_MAX)).isEqualTo(9f);
        assertThat((Boolean) settings.get(Keys.LONG_NULLABLE)).isTrue();
        assertThat(settings.getSubtypeMap())
                .containsEntry(List.class, ArrayList.class)
                .containsEntry(Set.class, HashSet.class);
    }

    @Test
    void merge() {
        verifyMergeSuccessful(Settings.defaults());
    }

    @Test
    void mergeWithLockedSettings() {
        verifyMergeSuccessful(Settings.defaults().lock());
    }

    private void verifyMergeSuccessful(final Settings defaults) {
        final Long originalLongMax = defaults.get(Keys.LONG_MAX);

        final Settings overrides = Settings.create()
                .set(Keys.BYTE_MIN, (byte) 99)
                .set(Keys.ARRAY_NULLABLE, true)
                .lock();

        final Settings result = defaults.merge(overrides);

        assertThat((Byte) result.get(Keys.BYTE_MIN)).isEqualTo((byte) 99);
        assertThat((Boolean) result.get(Keys.ARRAY_NULLABLE)).isTrue();
        assertThat((Long) result.get(Keys.LONG_MAX))
                .as("Properties that were not overridden should retain their value")
                .isEqualTo(originalLongMax);

        assertThat(result).as("Expecting a new instance of settings to be created").isNotSameAs(defaults);
    }

    @Test
    void getReturnsNullIfKeyHasNoValue() {
        assertThat((Byte) Settings.create().get(Keys.BYTE_MIN)).isNull();
    }

    @Test
    void strictModeIsEnabledByDefault() {
        assertThat((Mode) DEFAULTS.get(Keys.MODE)).isEqualTo(Mode.STRICT);
    }

    @Test
    void name() {
        assertThat((PopulateAction) DEFAULTS.get(Keys.GENERATOR_HINT_POPULATE_ACTION))
                .isEqualTo(PopulateAction.NULLS_AND_DEFAULT_PRIMITIVES);
    }

    @Test
    void setNullOnNonNullableValue() {
        final Settings settings = Settings.create();
        assertThatThrownBy(() -> settings.set(Keys.INTEGER_MIN, null))
                .isInstanceOf(InstancioApiException.class)
                .hasMessage("Setting value for key 'integer.min' must not be null");
    }

    @Test
    void getSetEnum() {
        final Settings settings = Settings.create().set(Keys.MODE, Mode.LENIENT);
        assertThat((Mode) settings.get(Keys.MODE)).isEqualTo(Mode.LENIENT);
    }

    @Test
    void setThrowsErrorIfGivenInvalidType() {
        final Settings settings = Settings.create();
        assertThatThrownBy(() -> settings.set(Keys.LONG_MAX, AUTO_ADJUST_DISABLED))
                .isInstanceOf(InstancioApiException.class)
                .hasMessage("The value 'false' is of unexpected type (Boolean) for key '%s' (expected: Long)",
                        Keys.LONG_MAX.propertyKey());
    }

    @Test
    void mapTypeThrowsErrorIfGivenInvalidSubtype() {
        final Settings settings = Settings.create();
        assertThatThrownBy(() -> settings.mapType(List.class, HashSet.class))
                .isInstanceOf(InstancioApiException.class)
                .hasMessage("Class 'java.util.HashSet' is not a subtype of 'java.util.List'");
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
                "subtypeMap: {}");
    }

    @Test
    void verifyToString() {
        assertThat(Settings.create()
                .set(Keys.LONG_MIN, 123L)
                .set(Keys.DOUBLE_MIN, 345.9)
                .mapType(List.class, ArrayList.class)
                .lock()
                .toString()
        ).containsSubsequence(
                "Settings[",
                "isLockedForModifications: true",
                "settingsMap:",
                "\t'double.min': 345.9",
                "\t'long.min': 123",
                "subtypeMap:",
                "\t'interface java.util.List': class java.util.ArrayList"
        );
    }

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

        assertThat((int) settings.get(Keys.COLLECTION_MIN_SIZE))
                .isEqualTo(settings.get(Keys.COLLECTION_MIN_SIZE))
                .isEqualTo(2);

        assertThat((float) settings.get(Keys.FLOAT_MIN))
                .isEqualTo(settings.get(Keys.FLOAT_MAX))
                .isEqualTo(3f);
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

    @Test
    @DisplayName("Should use subtypes specified in instancio.properties")
    void subtypeMappingFromInstancioProperties() {
        assertThat(Instancio.create(new TypeToken<SortedMap<String, Integer>>() {}))
                .isExactlyInstanceOf(ConcurrentSkipListMap.class);
    }
}
