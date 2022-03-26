package org.instancio.api.settings;

import org.instancio.Instancio;
import org.instancio.pojo.basic.StringHolder;
import org.instancio.pojo.collections.lists.ListString;
import org.instancio.settings.Setting;
import org.instancio.settings.Settings;
import org.instancio.testsupport.tags.SettingsTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SettingsTag
class StringSettingsTest {

    private static final int MIN_SIZE_OVERRIDE = 100;
    private static final int MAX_SIZE_OVERRIDE = 102;

    private static final Settings settings = Settings.create()
            .set(Setting.STRING_MIN_LENGTH, MIN_SIZE_OVERRIDE)
            .set(Setting.STRING_MAX_LENGTH, MAX_SIZE_OVERRIDE)
            // increase collection size for bigger sample
            .set(Setting.COLLECTION_MIN_SIZE, MIN_SIZE_OVERRIDE)
            .set(Setting.COLLECTION_MAX_SIZE, MAX_SIZE_OVERRIDE)
            .lock();

    @Test
    @DisplayName("Override default string length range")
    void length() {
        final StringHolder result = Instancio.of(StringHolder.class).withSettings(settings).create();
        assertThat(result.getValue()).hasSizeBetween(MIN_SIZE_OVERRIDE, MAX_SIZE_OVERRIDE);
    }

    @Test
    @DisplayName("Override allowEmpty to true")
    void allowEmpty() {
        final Settings overrides = settings.merge(Settings.create().set(Setting.STRING_ALLOW_EMPTY, true));
        final ListString result = Instancio.of(ListString.class).withSettings(overrides).create();
        assertThat(result.getList()).contains("");
    }

    @Test
    @DisplayName("Override nullable to true - generates null in String fields")
    void nullable() {
        final Settings overrides = settings.merge(Settings.create().set(Setting.STRING_NULLABLE, true));
        final Set<String> results = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            final StringHolder result = Instancio.of(StringHolder.class).withSettings(overrides).create();
            results.add(result.getValue());
        }
        assertThat(results).containsNull();
    }

    @Test
    @DisplayName("Override nullable to true - does not generate null in collection elements")
    void stringIsNotNullInCollections() {
        final Settings overrides = settings.merge(Settings.create().set(Setting.STRING_NULLABLE, true));
        final ListString result = Instancio.of(ListString.class).withSettings(overrides).create();
        assertThat(result.getList()).doesNotContainNull();
    }


}
