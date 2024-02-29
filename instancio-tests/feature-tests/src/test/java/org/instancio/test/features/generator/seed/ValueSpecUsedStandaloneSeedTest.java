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
package org.instancio.test.features.generator.seed;

import org.instancio.Gen;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.Seed;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * These tests use {@link Gen#string()} standalone.
 *
 * <p>These tests verify seed precedence based on the table below.
 * {@code instancio.properties} tests are not included since
 * the feature-tests module does not have a properties file.
 *
 * <pre>
 * +-----------------------------------------------------------+
 * | random | instancio  | @Seed(long) | @WithSettings | actual|
 * | seed   | properties |             |  annotation   |  seed |
 * |--------+------------+-------------+---------------+-------|
 * |   R    |     5      |      4      |       3       |   1   |
 * |   R    |     5      |      4      |       3       |   2   |
 * |   R    |     5      |      4      |       3       |   3   |
 * |   R    |     5      |      4      |       -       |   4   |
 * |   R    |     5      |      -      |       -       |   5   |
 * |   R    |     -      |      -      |       -       |   R   |
 * +--------+------------+-------------+---------------+-------+
 * </pre>
 *
 * @see ValueSpecUsedAsGeneratorSeedTest
 */
@FeatureTag({Feature.SEED, Feature.SETTINGS, Feature.WITH_SEED, Feature.WITH_SEED_ANNOTATION,})
@ExtendWith(InstancioExtension.class)
class ValueSpecUsedStandaloneSeedTest {

    // Use negative seeds to differentiate them from random seeds which are positive
    private static final long SEED_WITH_SETTINGS_ANNOTATION = -3; // @WithSettings
    private static final long SEED_ANNOTATION = -4; // @Seed

    private static final int STR_LENGTH = 7;
    private static final String RESULT_WITH_SETTINGS_ANNOTATION = "HEGKAJC";
    private static final String RESULT_ANNOTATION = "BUTNLYT";

    /**
     * Does not have {@code @WithSettings} annotation.
     */
    @Nested
    class NoWithSettingsAnnotationTest {

        @Seed(SEED_ANNOTATION)
        @RepeatedTest(2)
        void seedAnnotationWins() {
            final String result1 = Gen.string().length(STR_LENGTH).get();
            final String result2 = Gen.string().length(STR_LENGTH).get();

            assertThat(result1).isEqualTo(RESULT_ANNOTATION);

            assertThat(result2)
                    .as("Should generate a new value")
                    .hasSize(STR_LENGTH)
                    .isNotEqualTo(result1);
        }

        @RepeatedTest(2)
        void randomSeedWins() {
            final String result1 = Gen.string().length(STR_LENGTH).get();
            final String result2 = Gen.string().length(STR_LENGTH).get();

            assertThat(result1).isNotBlank();

            assertThat(result2)
                    .as("Should generate a new value")
                    .isNotBlank()
                    .isNotEqualTo(result1);
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

        // Has @Seed
        @Seed(SEED_ANNOTATION)
        @RepeatedTest(2)
        void withSettingsAnnotationWins1() {
            final String result1 = Gen.string().length(STR_LENGTH).get();
            final String result2 = Gen.string().length(STR_LENGTH).get();

            assertThat(result1).isEqualTo(RESULT_WITH_SETTINGS_ANNOTATION);
            assertThat(result2).isEqualTo(RESULT_WITH_SETTINGS_ANNOTATION);
        }

        // Does not have @Seed
        @RepeatedTest(2)
        void withSettingsAnnotationWins2() {
            final String result1 = Gen.string().length(STR_LENGTH).get();
            final String result2 = Gen.string().length(STR_LENGTH).get();

            assertThat(result1).isEqualTo(RESULT_WITH_SETTINGS_ANNOTATION);
            assertThat(result2).isEqualTo(RESULT_WITH_SETTINGS_ANNOTATION);
        }
    }
}
