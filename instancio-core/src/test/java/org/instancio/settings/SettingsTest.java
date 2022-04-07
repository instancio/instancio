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

import org.instancio.exception.InstancioApiException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SettingsTest {
    private static final String TYPE_MAPPING_PREFIX = "type.mapping.";

    @Test
    void defaults() {
        final Settings defaults = Settings.defaults();

        for (SettingKey settingKey : Setting.values()) {
            final Object actual = defaults.get(settingKey);
            final Object expected = settingKey.defaultValue();
            assertThat(actual).isEqualTo(expected);
        }
    }

    @Test
    void from() {
        final Map<Object, Object> map = new HashMap<>();
        map.put(Setting.FLOAT_MAX.key(), 9f);
        map.put(Setting.LONG_NULLABLE.key(), true);
        map.put(TYPE_MAPPING_PREFIX + "java.util.List", "java.util.ArrayList");
        map.put(TYPE_MAPPING_PREFIX + "java.util.Set", "java.util.HashSet"); // TODO validation

        final Settings settings = Settings.from(map);

        assertThat((Float) settings.get(Setting.FLOAT_MAX)).isEqualTo(9f);
        assertThat((Boolean) settings.get(Setting.LONG_NULLABLE)).isTrue();
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
        final Long originalLongMax = defaults.get(Setting.LONG_MAX);

        final Settings overrides = Settings.create()
                .set(Setting.BYTE_MIN, (byte) 99)
                .set(Setting.ARRAY_NULLABLE, true)
                .lock();

        final Settings result = defaults.merge(overrides);

        assertThat((Byte) result.get(Setting.BYTE_MIN)).isEqualTo((byte) 99);
        assertThat((Boolean) result.get(Setting.ARRAY_NULLABLE)).isTrue();
        assertThat((Long) result.get(Setting.LONG_MAX))
                .as("Properties that were not overridden should retain their value")
                .isEqualTo(originalLongMax);

        assertThat(result).as("Expecting a new instance of settings to be created").isNotSameAs(defaults);
    }

    @Test
    void getReturnsNullIfKeyHasNoValue() {
        assertThat((Byte) Settings.create().get(Setting.BYTE_MIN)).isNull();
    }

    @Test
    void setThrowsErrorIfGivenInvalidType() {
        assertThatThrownBy(() -> Settings.create().set(Setting.LONG_MAX, false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The value 'false' is of unexpected type (Boolean) for key 'LONG_MAX'");
    }

    @Test
    void mapTypeThrowsErrorIfGivenInvalidSubtype() {
        assertThatThrownBy(() -> Settings.create().mapType(List.class, HashSet.class))
                .isInstanceOf(InstancioApiException.class)
                .hasMessage("Class 'java.util.HashSet' is not a subtype of 'java.util.List'");
    }

    @Test
    void lockSettings() {
        final Settings locked = Settings.create().lock();

        assertThatThrownBy(() -> locked.set(Setting.LONG_MAX, 1L))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("Settings are read-only");

        assertThatThrownBy(() -> locked.mapType(List.class, ArrayList.class))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("Settings are read-only");
    }

    @Test
    void verifyToStringEmptySettings() {
        final String expected = "Settings[\n" +
                "isLockedForModifications: false\n" +
                "settingsMap: {}\n" +
                "subtypeMap: {}";

        assertThat(Settings.create()).hasToString(expected);
    }

    @Test
    void verifyToString() {
        final String expected = "Settings[\n" +
                "isLockedForModifications: true\n" +
                "settingsMap:\n" +
                "\tDOUBLE_MIN: 345.9\n" +
                "\tLONG_MIN: 123\n" +
                "subtypeMap:\n" +
                "\tinterface java.util.List: class java.util.ArrayList";

        assertThat(Settings.create()
                .set(Setting.LONG_MIN, 123L)
                .set(Setting.DOUBLE_MIN, 345.9)
                .mapType(List.class, ArrayList.class)
                .lock()
        ).hasToString(expected);
    }
}
