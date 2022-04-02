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
import org.instancio.pojo.collections.sets.SetLong;
import org.instancio.settings.Setting;
import org.instancio.settings.Settings;
import org.instancio.testsupport.tags.NonDeterministicTag;
import org.instancio.testsupport.tags.SettingsTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SettingsTag
@NonDeterministicTag
class CollectionSettingsTest {

    private static final int SAMPLE_SIZE = 100;
    private static final int MIN_SIZE_OVERRIDE = 100;
    private static final int MAX_SIZE_OVERRIDE = 102;

    private static final Settings settings = Settings.create()
            .set(Setting.COLLECTION_MIN_SIZE, MIN_SIZE_OVERRIDE)
            .set(Setting.COLLECTION_MAX_SIZE, MAX_SIZE_OVERRIDE)
            .lock();

    @Test
    @DisplayName("Override default collection size range")
    void size() {
        final SetLong result = createCollection(settings);
        assertThat(result.getSet()).hasSizeBetween(MIN_SIZE_OVERRIDE, MAX_SIZE_OVERRIDE);
    }

    @Test
    @DisplayName("Allow null to be generated for collection")
    void nullable() {
        final Settings overrides = settings.merge(Settings.create().set(Setting.COLLECTION_NULLABLE, true));
        final Set<Set<?>> results = new HashSet<>();
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            final SetLong result = createCollection(overrides);
            results.add(result.getSet());
        }
        assertThat(results).containsNull();
    }

    @Test
    @DisplayName("Allow null elements in collections")
    void nullableElements() {
        final Settings overrides = Settings.create().set(Setting.COLLECTION_ELEMENTS_NULLABLE, true);
        final SetLong result = createCollection(settings.merge(overrides));
        assertThat(result.getSet()).containsNull();
    }

    private SetLong createCollection(final Settings settings) {
        return Instancio.of(SetLong.class).withSettings(settings).create();
    }

}
