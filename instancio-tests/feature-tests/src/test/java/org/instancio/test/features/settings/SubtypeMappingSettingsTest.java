/*
 * Copyright 2022-2023 the original author or authors.
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
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.collections.sets.SetLong;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArraySet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;

@FeatureTag({
        Feature.COLLECTION_GENERATOR_SUBTYPE,
        Feature.SUBTYPE,
        Feature.SETTINGS,
        Feature.WITH_SETTINGS_ANNOTATION
})
@ExtendWith(InstancioExtension.class)
class SubtypeMappingSettingsTest {

    @WithSettings
    private static final Settings settings = Settings.create()
            .mapType(Set.class, TreeSet.class);

    @Test
    @DisplayName("Subtype from Settings overrides the default implementation for Set (HashSet)")
    void shouldUseSubtypeMappingFromSettings() {
        final SetLong result = Instancio.create(SetLong.class);
        assertThat(result.getSet()).isExactlyInstanceOf(TreeSet.class);
    }

    @Test
    @DisplayName("Subtype specified by InstancioApi.subtype() takes precedence over Settings")
    void subtypeFromBuilderTakesPrecedenceOverSettings() {
        final SetLong result = Instancio.of(SetLong.class)
                .subtype(all(Set.class), LinkedHashSet.class)
                .create();

        assertThat(result.getSet()).isExactlyInstanceOf(LinkedHashSet.class);
    }

    @Test
    @DisplayName("Subtype specified via generator takes precedence over InstancioApi.subtype() and Settings")
    void subtypeSpecifiedThroughGeneratorTakesPrecedence() {
        final SetLong result = Instancio.of(SetLong.class)
                .subtype(all(Set.class), LinkedHashSet.class)
                .generate(all(Set.class), gen -> gen.collection().subtype(CopyOnWriteArraySet.class))
                .create();

        assertThat(result.getSet()).isExactlyInstanceOf(CopyOnWriteArraySet.class);
    }

    @Test
    @DisplayName("Subtypes specified via Settings should not trigger 'unused selectors' error")
    void subtypesFromSettingsShouldNotCauseUnusedSelectorsError() {
        final SetLong result = Instancio.of(SetLong.class)
                // SetLong does not contain a Map, but this should not trigger an error
                .withSettings(Settings.create().mapType(Map.class, TreeMap.class))
                .create();

        assertThat(result).isNotNull();
    }
}
