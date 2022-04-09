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
package org.instancio.api.settings;

import org.instancio.Instancio;
import org.instancio.settings.Setting;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.arrays.ArrayLong;
import org.instancio.test.support.tags.SettingsTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SettingsTag
class ArraySettingsTest {

    private static final int SAMPLE_SIZE = 100;
    private static final int MIN_SIZE_OVERRIDE = 100;
    private static final int MAX_SIZE_OVERRIDE = 102;

    private static final Settings settings = Settings.create()
            .set(Setting.ARRAY_MIN_LENGTH, MIN_SIZE_OVERRIDE)
            .set(Setting.ARRAY_MAX_LENGTH, MAX_SIZE_OVERRIDE)
            .lock();

    @Test
    @DisplayName("Override default array length range")
    void length() {
        final ArrayLong result = createArray(settings);
        assertThat(result.getPrimitive()).hasSizeBetween(MIN_SIZE_OVERRIDE, MAX_SIZE_OVERRIDE);
        assertThat(result.getWrapper()).hasSizeBetween(MIN_SIZE_OVERRIDE, MAX_SIZE_OVERRIDE);
    }

    @Test
    @DisplayName("Allow null to be generated for array")
    void nullable() {
        final Settings overrides = settings.merge(Settings.create().set(Setting.ARRAY_NULLABLE, true));
        final Set<long[]> results = new HashSet<>();
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            final ArrayLong result = createArray(overrides);
            results.add(result.getPrimitive());
        }
        assertThat(results).containsNull();
    }

    @Test
    @DisplayName("Allow null elements in arrays")
    void nullableElements() {
        final Settings overrides = Settings.create().set(Setting.ARRAY_ELEMENTS_NULLABLE, true);
        final ArrayLong result = createArray(settings.merge(overrides));
        assertThat(result.getWrapper()).containsNull();
    }

    private ArrayLong createArray(final Settings settings) {
        return Instancio.of(ArrayLong.class)
                .withSettings(settings)
                .create();
    }

}
