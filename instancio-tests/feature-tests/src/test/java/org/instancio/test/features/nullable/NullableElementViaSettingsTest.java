/*
 * Copyright 2022-2025 the original author or authors.
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
package org.instancio.test.features.nullable;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;

@FeatureTag({Feature.NULLABILITY, Feature.SETTINGS})
@ExtendWith(InstancioExtension.class)
class NullableElementViaSettingsTest {

    private static final int SAMPLE_SIZE = 1000;

    @WithSettings
    private final Settings settings = Settings.create()
            .set(Keys.ARRAY_ELEMENTS_NULLABLE, true)
            .set(Keys.COLLECTION_ELEMENTS_NULLABLE, true)
            .set(Keys.MAP_KEYS_NULLABLE, true);

    @Test
    void arrayElements() {
        final String[] results = Instancio.of(String[].class)
                .generate(all(String[].class), gen -> gen.array().length(SAMPLE_SIZE))
                .create();

        assertThat(results)
                .hasSize(SAMPLE_SIZE)
                .containsNull();
    }

    @Test
    void collectionElements() {
        final Set<String> results = Instancio.ofSet(String.class)
                .size(SAMPLE_SIZE)
                .create();

        assertThat(results)
                .hasSize(SAMPLE_SIZE)
                .containsNull();
    }

    @Test
    void mapKeys() {
        final Map<String, UUID> results = Instancio.ofMap(String.class, UUID.class)
                .size(SAMPLE_SIZE)
                .create();

        assertThat(results.keySet())
                .hasSize(SAMPLE_SIZE)
                .containsNull();
    }
}
