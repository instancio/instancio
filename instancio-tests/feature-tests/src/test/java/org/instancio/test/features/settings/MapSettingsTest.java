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
package org.instancio.test.features.settings;

import org.instancio.Instancio;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.collections.maps.MapIntegerString;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag(Feature.SETTINGS)
class MapSettingsTest {

    private static final int SAMPLE_SIZE = 100;
    private static final int MIN_SIZE_OVERRIDE = 100;
    private static final int MAX_SIZE_OVERRIDE = 102;

    private static final Settings settings = Settings.create()
            .set(Keys.INTEGER_MIN, Integer.MIN_VALUE)
            .set(Keys.INTEGER_MAX, Integer.MAX_VALUE)
            .set(Keys.MAP_MIN_SIZE, MIN_SIZE_OVERRIDE)
            .set(Keys.MAP_MAX_SIZE, MAX_SIZE_OVERRIDE)
            .lock();

    @Test
    @DisplayName("Should override default map size range")
    void size() {
        final MapIntegerString result = createMap(settings);
        assertThat(result.getMap()).hasSizeBetween(MIN_SIZE_OVERRIDE, MAX_SIZE_OVERRIDE);
    }

    @Test
    @DisplayName("Allow null to be generated for map")
    void nullable() {
        final Settings overrides = settings.merge(Settings.create()
                .set(Keys.MAP_NULLABLE, true));

        final Set<Map<?, ?>> results = new HashSet<>();
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            final MapIntegerString result = createMap(overrides);
            results.add(result.getMap());
        }
        assertThat(results).containsNull();
    }

    @Test
    @DisplayName("Allow null keys in maps")
    void nullableKeys() {
        final Settings overrides = Settings.from(settings)
                .set(Keys.MAP_KEYS_NULLABLE, true);

        final MapIntegerString result = createMap(settings.merge(overrides));
        assertThat(result.getMap().keySet()).containsNull();
    }

    @Test
    @DisplayName("Allow null values in maps")
    void nullableValues() {
        final Settings overrides = Settings.create().set(Keys.MAP_VALUES_NULLABLE, true);
        final MapIntegerString result = createMap(settings.merge(overrides));
        assertThat(result.getMap().values()).containsNull();
    }

    private MapIntegerString createMap(final Settings settings) {
        return Instancio.of(MapIntegerString.class)
                .withSettings(settings)
                .create();
    }

}
