/*
 * Copyright 2022-2024 the original author or authors.
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
import org.instancio.junit.Given;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.Seed;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Seed precedence when using {@code InstancioExtension}, from highest to lowest:
 *
 * <ul>
 *   <li>(a) withSeed()</li>
 *   <li>(b) withSettings()</li>
 *   <li>(c) @WithSettings</li>
 *   <li>(d) @Seed()</li>
 *   <li>(e) Global seed (from properties file)</li>
 *   <li>(f) random seed, if none of the above specified (default behaviour, not tested here)</li>
 * </ul>
 */
@FeatureTag({Feature.GLOBAL_SEED, Feature.WITH_SEED, Feature.WITH_SEED_ANNOTATION})
@ExtendWith(InstancioExtension.class)
class GlobalSeedWithInstancioExtensionTest {

    private static final long ANNOTATION_SEED = -123;

    @Test
    @Seed(ANNOTATION_SEED)
    @DisplayName("(a) withSeed() takes precedence over everything else")
    void withSeedTakesPrecedenceOverGlobalSeed() {
        final long seed = Instancio.create(long.class);
        final Result<String> s1 = Instancio.of(String.class)
                .withSeed(seed)
                .asResult();

        final Result<String> s2 = Instancio.of(String.class)
                .withSeed(seed)
                .withSettings(Settings.create().set(Keys.SEED, Instancio.create(long.class)))
                .asResult();

        final Result<String> s3 = Instancio.of(String.class).asResult();

        assertThat(s1.getSeed()).isEqualTo(seed);
        assertThat(s2.getSeed()).isEqualTo(seed);
        assertThat(s3.getSeed()).isEqualTo(ANNOTATION_SEED);

        assertThat(s1.get())
                .as("Same value should be generated using given seed")
                .isEqualTo(s2.get());

        assertThat(s1.get()).isNotEqualTo(s3.get());
    }

    @Test
    @Seed(ANNOTATION_SEED)
    @DisplayName("(b) withSettings() takes precedence over seed from properties and @Seed annotation")
    void settingsSeedTakesPrecedenceOverGlobalSeed() {
        final long seed = Instancio.create(long.class);
        final Settings settings = Settings.create().set(Keys.SEED, seed);
        final Result<String> s1 = Instancio.of(String.class).withSettings(settings).asResult();
        final Result<String> s2 = Instancio.of(String.class).withSettings(settings).asResult();
        final Result<String> s3 = Instancio.of(String.class).asResult();

        assertThat(s1.getSeed()).isEqualTo(seed);
        assertThat(s2.getSeed()).isEqualTo(seed);
        assertThat(s3.getSeed()).isEqualTo(ANNOTATION_SEED);

        assertThat(s1.get())
                .as("Same value should be generated using given seed")
                .isEqualTo(s2.get());

        assertThat(s1.get()).isNotEqualTo(s3.get());
    }

    @Nested
    @ExtendWith(InstancioExtension.class)
    class WithSettingsAnnotationTest {
        private final long WITH_SETTINGS_ANNOTATION_SEED = Instancio.create(long.class);

        @WithSettings
        private final Settings settings = Settings.create().set(Keys.SEED, WITH_SETTINGS_ANNOTATION_SEED);

        @Test
        @Seed(ANNOTATION_SEED)
        @DisplayName("(c) @WithSettings seed takes precedence over seed from properties and @Seed annotation")
        void withSettingsAnnotation() {
            final Result<String> result = Instancio.of(String.class).asResult();
            assertThat(result.getSeed()).isEqualTo(WITH_SETTINGS_ANNOTATION_SEED);
        }
    }

    @Test
    @Seed(ANNOTATION_SEED)
    @DisplayName("(d) @Seed() takes precedence over seed from properties file")
    void seedAnnotation() {
        final Result<String> result = Instancio.of(String.class).asResult();
        assertThat(result.getSeed()).isEqualTo(ANNOTATION_SEED);
    }

    @Test
    @DisplayName("(e) Global seed from properties file")
    void seedFromProperties() {
        final Result<String> s1 = Instancio.of(String.class).asResult();
        final Result<String> s2 = Instancio.of(String.class).asResult();

        assertThat(s1.get())
                .as("Distinct values should be generated")
                .isNotEqualTo(s2.get());

        assertThat(s1.getSeed())
                .isEqualTo(s2.getSeed())
                .isEqualTo(TestConstants.GLOBAL_SEED);
    }

    @Test
    @DisplayName("(f) using @Given")
    void generate(@Given String s1, @Given String s2) {
        assertThat(s1)
                .as("Distinct values should be generated")
                .isNotEqualTo(s2);
    }
}
