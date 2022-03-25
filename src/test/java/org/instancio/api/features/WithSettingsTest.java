package org.instancio.api.features;

import org.instancio.Instancio;
import org.instancio.pojo.arrays.TwoArraysOfItemString;
import org.instancio.pojo.collections.maps.MapIntegerString;
import org.instancio.settings.Setting;
import org.instancio.settings.Settings;
import org.instancio.testsupport.tags.NonDeterministicTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for supplying {@link Settings} via @{@link org.instancio.InstancioApi#withSettings(Settings)}.
 */
@NonDeterministicTag
class WithSettingsTest {

    private static final int MIN_SIZE_OVERRIDE = 100;
    private static final int MAX_SIZE_OVERRIDE = 102;

    private static final Settings settings = Settings.create()
            .set(Setting.ARRAY_MIN_LENGTH, MIN_SIZE_OVERRIDE)
            .set(Setting.ARRAY_MAX_LENGTH, MAX_SIZE_OVERRIDE)
            .set(Setting.MAP_MIN_SIZE, MIN_SIZE_OVERRIDE)
            .set(Setting.MAP_MAX_SIZE, MAX_SIZE_OVERRIDE)
            .lock();

    @Test
    @DisplayName("Should override default map size range")
    void mapSize() {
        final MapIntegerString result = Instancio.of(MapIntegerString.class).withSettings(settings).create();
        assertThat(result.getMap()).hasSizeBetween(MIN_SIZE_OVERRIDE, MAX_SIZE_OVERRIDE);
    }

    @Test
    @DisplayName("Should override default array length range")
    void arrayLength() {
        final TwoArraysOfItemString result = Instancio.of(TwoArraysOfItemString.class).withSettings(settings).create();
        assertThat(result.getArray1()).hasSizeBetween(MIN_SIZE_OVERRIDE, MAX_SIZE_OVERRIDE);
        assertThat(result.getArray2()).hasSizeBetween(MIN_SIZE_OVERRIDE, MAX_SIZE_OVERRIDE);
    }
}
