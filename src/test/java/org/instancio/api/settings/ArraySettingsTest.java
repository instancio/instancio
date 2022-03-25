package org.instancio.api.settings;

import org.instancio.Instancio;
import org.instancio.pojo.arrays.ArrayLong;
import org.instancio.settings.Setting;
import org.instancio.settings.Settings;
import org.instancio.testsupport.tags.SettingsTag;
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
    @DisplayName("Should override default array size range")
    void size() {
        final ArrayLong result = Instancio.of(ArrayLong.class).withSettings(settings).create();
        assertThat(result.getPrimitive()).hasSizeBetween(MIN_SIZE_OVERRIDE, MAX_SIZE_OVERRIDE);
        assertThat(result.getWrapper()).hasSizeBetween(MIN_SIZE_OVERRIDE, MAX_SIZE_OVERRIDE);
    }

    @Test
    @DisplayName("Allow null to be generated for array")
    void nullable() {
        final Settings overrides = settings.merge(Settings.create().set(Setting.ARRAY_NULLABLE, true));
        final Set<long[]> results = new HashSet<>();
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            final ArrayLong result = Instancio.of(ArrayLong.class).withSettings(overrides).create();
            results.add(result.getPrimitive());
        }
        assertThat(results).containsNull();
    }

    @Test
    @DisplayName("Allow null elements in arrays")
    void nullableElements() {
        final Settings overrides = Settings.create().set(Setting.ARRAY_ELEMENTS_NULLABLE, true);
        final ArrayLong result = Instancio.of(ArrayLong.class)
                .withSettings(settings.merge(overrides))
                .create();
        assertThat(result.getWrapper()).containsNull();
    }

}
