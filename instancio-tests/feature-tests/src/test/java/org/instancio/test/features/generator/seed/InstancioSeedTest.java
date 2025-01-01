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
package org.instancio.test.features.generator.seed;

import org.instancio.Instancio;
import org.instancio.Result;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.Seed;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * These tests verify seed precedence based on the table below.
 * {@code instancio.properties} tests are not included since
 * the feature-tests module does not have a properties file.
 *
 * <pre>
 * +-------------------------------------------------------------------------------------------+
 * | random | instancio  | @Seed(long) | @WithSettings | .withSettings() | .withSeed() | actual|
 * | seed   | properties |             |  annotation   |   builder       |             |  seed |
 * |--------+------------+-------------+---------------+-----------------+-------------+-------|
 * |   R    |     5      |      4      |       3       |        2        |      1      |   1   |
 * |   R    |     5      |      4      |       3       |        2        |      -      |   2   |
 * |   R    |     5      |      4      |       3       |        -        |      -      |   3   |
 * |   R    |     5      |      4      |       -       |        -        |      -      |   4   |
 * |   R    |     5      |      -      |       -       |        -        |      -      |   5   |
 * |   R    |     -      |      -      |       -       |        -        |      -      |   R   |
 * +--------+------------+-------------+---------------+-----------------+-------------+-------+
 * </pre>
 */
@FeatureTag({Feature.SEED, Feature.SETTINGS, Feature.WITH_SEED, Feature.WITH_SEED_ANNOTATION,})
@ExtendWith(InstancioExtension.class)
class InstancioSeedTest {

    // Use negative seeds to differentiate them from random seeds which are positive
    private static final long SEED_WITH_SEED = -1; // .withSeed()
    private static final long SEED_WITH_SETTINGS_BUILDER = -2; // .withSettings()
    private static final long SEED_WITH_SETTINGS_ANNOTATION = -3; // @WithSettings
    private static final long SEED_ANNOTATION = -4; // @Seed

    private static final String RESULT_WITH_SEED = "VJFOW";
    private static final String RESULT_WITH_SETTINGS_ANNOTATION = "HEGKA";
    private static final String RESULT_ANNOTATION = "BUTNL";
    private static final String RESULT_WITH_SETTINGS_BUILDER = "PWRRG";

    /**
     * Does not have {@code @WithSettings} annotation.
     */
    @Nested
    class NoWithSettingsAnnotationTest {

        @Seed(SEED_ANNOTATION)
        @RepeatedTest(2)
        void withSeedWins() {
            final Result<String> result1 = Instancio.of(String.class)
                    .withSetting(Keys.SEED, SEED_WITH_SETTINGS_BUILDER)
                    .withSeed(SEED_WITH_SEED)
                    .asResult();

            final Result<String> result2 = Instancio.of(String.class)
                    .withSetting(Keys.SEED, SEED_WITH_SETTINGS_BUILDER)
                    .withSeed(SEED_WITH_SEED)
                    .asResult();

            assertThat(result1.getSeed()).isEqualTo(SEED_WITH_SEED);
            assertThat(result1.get()).isEqualTo(RESULT_WITH_SEED);

            assertThat(result2.getSeed()).isEqualTo(SEED_WITH_SEED);
            assertThat(result2.get()).isEqualTo(RESULT_WITH_SEED);
        }

        @Seed(SEED_ANNOTATION)
        @RepeatedTest(2)
        void withSettingBuilderWins() {
            final Result<String> result1 = Instancio.of(String.class)
                    .withSetting(Keys.SEED, SEED_WITH_SETTINGS_BUILDER)
                    .asResult();

            final Result<String> result2 = Instancio.of(String.class)
                    .withSetting(Keys.SEED, SEED_WITH_SETTINGS_BUILDER)
                    .asResult();

            assertThat(result1.getSeed()).isEqualTo(SEED_WITH_SETTINGS_BUILDER);
            assertThat(result1.get()).isEqualTo(RESULT_WITH_SETTINGS_BUILDER);

            assertThat(result2.getSeed()).isEqualTo(SEED_WITH_SETTINGS_BUILDER);
            assertThat(result2.get()).isEqualTo(RESULT_WITH_SETTINGS_BUILDER);
        }

        @Seed(SEED_ANNOTATION)
        @RepeatedTest(2)
        void seedAnnotationWins() {
            final Result<String> result1 = Instancio.of(String.class).asResult();
            final Result<String> result2 = Instancio.of(String.class).asResult();

            assertThat(result1.getSeed()).isEqualTo(SEED_ANNOTATION);
            assertThat(result1.get()).isEqualTo(RESULT_ANNOTATION);

            assertThat(result2.getSeed()).isEqualTo(SEED_ANNOTATION);
            assertThat(result2.get())
                    .as("Should generate a new value")
                    .isNotBlank()
                    .isNotEqualTo(result1.get());
        }

        @RepeatedTest(2)
        void randomSeedWins() {
            final Result<String> result1 = Instancio.of(String.class).asResult();
            final Result<String> result2 = Instancio.of(String.class).asResult();

            assertThat(result1.getSeed()).isPositive();
            assertThat(result1.get()).isNotBlank();

            assertThat(result2.getSeed()).isPositive();
            assertThat(result2.get())
                    .as("Should generate a new value")
                    .isNotBlank()
                    .isNotEqualTo(result1.get());
        }
    }

    /**
     * Has {@code @WithSettings} annotation.
     */
    @Nested
    class WithSettingsAnnotationTest {

        @WithSettings
        private final Settings settings = Settings.create()
                .set(Keys.SEED, SEED_WITH_SETTINGS_ANNOTATION);

        @Seed(SEED_ANNOTATION)
        @RepeatedTest(2)
        void withSeedWins() {
            final Result<String> result1 = Instancio.of(String.class)
                    .withSetting(Keys.SEED, SEED_WITH_SETTINGS_BUILDER)
                    .withSeed(SEED_WITH_SEED)
                    .asResult();

            final Result<String> result2 = Instancio.of(String.class)
                    .withSetting(Keys.SEED, SEED_WITH_SETTINGS_BUILDER)
                    .withSeed(SEED_WITH_SEED)
                    .asResult();

            assertThat(result1.getSeed()).isEqualTo(SEED_WITH_SEED);
            assertThat(result1.get()).isEqualTo(RESULT_WITH_SEED);

            assertThat(result2.getSeed()).isEqualTo(SEED_WITH_SEED);
            assertThat(result2.get()).isEqualTo(RESULT_WITH_SEED);
        }

        @Seed(SEED_ANNOTATION)
        @RepeatedTest(2)
        void withSettingBuilderWins() {
            final Result<String> result1 = Instancio.of(String.class)
                    .withSetting(Keys.SEED, SEED_WITH_SETTINGS_BUILDER)
                    .asResult();

            final Result<String> result2 = Instancio.of(String.class)
                    .withSetting(Keys.SEED, SEED_WITH_SETTINGS_BUILDER)
                    .asResult();

            assertThat(result1.getSeed()).isEqualTo(SEED_WITH_SETTINGS_BUILDER);
            assertThat(result1.get()).isEqualTo(RESULT_WITH_SETTINGS_BUILDER);

            assertThat(result2.getSeed()).isEqualTo(SEED_WITH_SETTINGS_BUILDER);
            assertThat(result2.get()).isEqualTo(RESULT_WITH_SETTINGS_BUILDER);
        }

        // Has @Seed
        @Seed(SEED_ANNOTATION)
        @RepeatedTest(2)
        void withSettingsAnnotationWins1() {
            final Result<String> result1 = Instancio.of(String.class).asResult();
            final Result<String> result2 = Instancio.of(String.class).asResult();

            assertThat(result1.getSeed()).isEqualTo(SEED_WITH_SETTINGS_ANNOTATION);
            assertThat(result1.get()).isEqualTo(RESULT_WITH_SETTINGS_ANNOTATION);

            // Same as result1 because @WithSettings is effectively the same as .withSettings()
            assertThat(result2.getSeed()).isEqualTo(SEED_WITH_SETTINGS_ANNOTATION);
            assertThat(result2.get()).isEqualTo(RESULT_WITH_SETTINGS_ANNOTATION);
        }

        // Does not have @Seed
        @RepeatedTest(2)
        void withSettingsAnnotationWins2() {
            final Result<String> result1 = Instancio.of(String.class).asResult();
            final Result<String> result2 = Instancio.of(String.class).asResult();

            assertThat(result1.getSeed()).isEqualTo(SEED_WITH_SETTINGS_ANNOTATION);
            assertThat(result1.get()).isEqualTo(RESULT_WITH_SETTINGS_ANNOTATION);

            // Same as result1 because @WithSettings is effectively the same as .withSettings()
            assertThat(result2.getSeed()).isEqualTo(SEED_WITH_SETTINGS_ANNOTATION);
            assertThat(result2.get()).isEqualTo(RESULT_WITH_SETTINGS_ANNOTATION);
        }
    }

    @Nested
    class WithSeedMethodOrderTest {

        @Test
        void methodOrderShouldNotMatter() {
            final Result<String> result1 = Instancio.of(String.class)
                    .withSeed(SEED_WITH_SEED) // withSeed() first
                    .withSetting(Keys.SEED, SEED_WITH_SETTINGS_BUILDER)
                    .asResult();

            final Result<String> result2 = Instancio.of(String.class)
                    .withSetting(Keys.SEED, SEED_WITH_SETTINGS_BUILDER)
                    .withSeed(SEED_WITH_SEED) // withSeed() last
                    .asResult();

            assertThat(result1.getSeed()).isEqualTo(SEED_WITH_SEED);
            assertThat(result1.get()).isEqualTo(RESULT_WITH_SEED);

            assertThat(result2.getSeed()).isEqualTo(SEED_WITH_SEED);
            assertThat(result2.get()).isEqualTo(RESULT_WITH_SEED);
        }
    }

}
