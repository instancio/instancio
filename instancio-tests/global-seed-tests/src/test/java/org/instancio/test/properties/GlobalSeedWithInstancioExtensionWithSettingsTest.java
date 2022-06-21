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
package org.instancio.test.properties;

import org.instancio.Instancio;
import org.instancio.Result;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.instancio.junit.Seed;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Using {@code @WithSettings} annotation.
 */
@ExtendWith(InstancioExtension.class)
class GlobalSeedWithInstancioExtensionWithSettingsTest {

    private static final int ANNOTATION_SEED = -123;
    private static final int SETTINGS_SEED = -5678;

    @WithSettings
    private static final Settings settings = Settings.create()
            .set(Keys.SEED, SETTINGS_SEED)
            .lock();

    @Test
    void settingsSeed() {
        final Result<String> result = Instancio.of(String.class).asResult();
        assertThat(result.getSeed()).isEqualTo(SETTINGS_SEED);
    }

    @Test
    @DisplayName("withSettings() takes precedence over @WithSettings")
    void withSettingsTakesPrecedenceOverWithSettingsAnnotation() {
        final int seed = Instancio.create(int.class);

        final Result<String> s1 = Instancio.of(String.class)
                .withSettings(Settings.create().set(Keys.SEED, seed))
                .asResult();

        final Result<String> s2 = Instancio.of(String.class).asResult();

        assertThat(s1.getSeed()).isEqualTo(seed);
        assertThat(s2.getSeed()).isEqualTo(SETTINGS_SEED);
    }

    @Test
    @Seed(ANNOTATION_SEED)
    void withSettingsHasHigherPrecedenceThanSeedAnnotation() {
        final Result<String> result = Instancio.of(String.class).asResult();
        assertThat(result.getSeed()).isEqualTo(SETTINGS_SEED);
    }

    @Test
    @Seed(ANNOTATION_SEED)
    void seedAnnotation() {
        final int seed = Instancio.create(int.class);
        final Result<String> s1 = Instancio.of(String.class).withSeed(seed).asResult();
        final Result<String> s2 = Instancio.of(String.class).asResult();

        assertThat(s1.getSeed()).isEqualTo(seed);
        assertThat(s2.getSeed()).isEqualTo(SETTINGS_SEED);
        assertThat(s1.get()).isNotEqualTo(s2.get());
    }

    @ParameterizedTest
    @InstancioSource(value = {String.class, String.class})
    void parameterized(final String s1, final String s2) {
        assertThat(s1)
                .as("Distinct values should be generated")
                .isNotEqualTo(s2);

        final Result<String> s3 = Instancio.of(String.class).asResult();
        final Result<String> s4 = Instancio.of(String.class).asResult();

        assertThat(s3.getSeed()).isEqualTo(SETTINGS_SEED);
        assertThat(s4.getSeed()).isEqualTo(SETTINGS_SEED);

        assertThat(s3.get())
                .as("Same value should be generated using given seed")
                .isEqualTo(s4.get());
    }
}
