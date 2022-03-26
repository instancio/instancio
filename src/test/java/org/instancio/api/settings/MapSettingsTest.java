package org.instancio.api.settings;

import org.instancio.Instancio;
import org.instancio.pojo.collections.maps.MapIntegerString;
import org.instancio.settings.Setting;
import org.instancio.settings.Settings;
import org.instancio.testsupport.tags.SettingsTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SettingsTag
class MapSettingsTest {

    private static final int SAMPLE_SIZE = 100;
    private static final int MIN_SIZE_OVERRIDE = 100;
    private static final int MAX_SIZE_OVERRIDE = 102;

    private static final Settings settings = Settings.create()
            .set(Setting.MAP_MIN_SIZE, MIN_SIZE_OVERRIDE)
            .set(Setting.MAP_MAX_SIZE, MAX_SIZE_OVERRIDE)
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
        final Settings overrides = settings.merge(Settings.create().set(Setting.MAP_NULLABLE, true));
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
        final Settings overrides = Settings.create().set(Setting.MAP_KEYS_NULLABLE, true);
        final MapIntegerString result = createMap(settings.merge(overrides));
        assertThat(result.getMap().keySet()).containsNull();
    }

    @Test
    @DisplayName("Allow null values in maps")
    void nullableValues() {
        final Settings overrides = Settings.create().set(Setting.MAP_VALUES_NULLABLE, true);
        final MapIntegerString result = createMap(settings.merge(overrides));
        assertThat(result.getMap().values()).containsNull();
    }

    private MapIntegerString createMap(final Settings settings) {
        return Instancio.of(MapIntegerString.class)
                .withSettings(settings)
                .create();
    }

}
