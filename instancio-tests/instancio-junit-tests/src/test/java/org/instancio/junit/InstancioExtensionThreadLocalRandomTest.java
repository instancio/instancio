/*
 * Copyright 2022-2026 the original author or authors.
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
package org.instancio.junit;

import org.instancio.Instancio;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.support.DefaultRandom;
import org.instancio.support.Seeds;
import org.instancio.support.ThreadLocalRandom;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.platform.testkit.engine.EngineTestKit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

@SuppressWarnings({"JUnitMalformedDeclaration", "NewClassNamingConvention"})
class InstancioExtensionThreadLocalRandomTest {

    private static final long SEED_WITH_SEED = -1; // .withSeed()
    private static final long SEED_WITH_SETTINGS_BUILDER = -2; // .withSettings()
    private static final long SEED_WITH_SETTINGS_ANNOTATION = -3; // @WithSettings
    private static final long SEED_ANNOTATION = -4; // @Seed

    @ValueSource(classes = {
            RandomSeed.class,
            SeedAnnotation.class,
            SettingsAnnotation.class,
            SettingsAnnotationAndWithSettingsBuilder.class,
            SettingsAnnotationAndSeedFromWithSettingsAnnotation.class
    })
    @ParameterizedTest
    void seedFromWithSettingsAnnotation(final Class<?> testClass) {
        verifyTestClass(testClass);
    }

    private static void verifyTestClass(final Class<?> testClass) {
        EngineTestKit.engine("junit-jupiter")
                .selectors(selectClass(testClass))
                .execute()
                .testEvents()
                .assertStatistics(stats -> stats.failed(1));
    }

    @ExtendWith(InstancioExtension.class)
    static class SettingsAnnotation {
        @WithSettings
        private final Settings settings = Settings.create()
                .set(Keys.SEED, SEED_WITH_SETTINGS_ANNOTATION);

        @AfterEach
        void tearDown() {
            assertSeed(SEED_WITH_SETTINGS_ANNOTATION, Seeds.Source.WITH_SETTINGS_ANNOTATION);
        }

        @Test
        void intentionallyFail() {
            doFail();
        }
    }

    @ExtendWith(InstancioExtension.class)
    static class SettingsAnnotationAndWithSettingsBuilder {
        @WithSettings
        private final Settings settings = Settings.create()
                .set(Keys.SEED, SEED_WITH_SETTINGS_ANNOTATION);

        @AfterEach
        void tearDown() {
            assertSeed(SEED_WITH_SETTINGS_ANNOTATION, Seeds.Source.WITH_SETTINGS_ANNOTATION);
        }

        @Test
        void intentionallyFail() {
            Instancio.of(String.class)
                    // Seeds specified directly via the Instancio builder API
                    // should never be reported by the extension
                    .withSetting(Keys.SEED, SEED_WITH_SETTINGS_BUILDER)
                    .withSeed(SEED_WITH_SEED)
                    .create();

            doFail();
        }
    }

    @ExtendWith(InstancioExtension.class)
    static class SettingsAnnotationAndSeedFromWithSettingsAnnotation {
        @WithSettings
        private final Settings settings = Settings.create()
                .set(Keys.SEED, SEED_WITH_SETTINGS_ANNOTATION);

        @AfterEach
        void tearDown() {
            assertSeed(SEED_WITH_SETTINGS_ANNOTATION, Seeds.Source.WITH_SETTINGS_ANNOTATION);
        }

        @Seed(SEED_ANNOTATION)
        @Test
        void intentionallyFail() {
            doFail();
        }
    }

    @ExtendWith(InstancioExtension.class)
    static class SeedAnnotation {

        @AfterEach
        void tearDown() {
            assertSeed(SEED_ANNOTATION, Seeds.Source.SEED_ANNOTATION);
        }

        @Seed(SEED_ANNOTATION)
        @Test
        void intentionallyFail() {
            doFail();
        }
    }

    @ExtendWith(InstancioExtension.class)
    static class RandomSeed {

        @AfterEach
        void tearDown() {
            assertRandomSeed();
        }

        @Test
        void intentionallyFail() {
            doFail();
        }
    }

    private static void doFail() {
        fail("Intentional failure");
    }

    private static void assertSeed(long expectedSeed, Seeds.Source expectedSource) {
        final DefaultRandom random = (DefaultRandom) ThreadLocalRandom.getInstance().get();

        try {
            assertThat(random.getSeed()).as("seed").isEqualTo(expectedSeed);
            assertThat(random.getSource()).as("seed source").isEqualTo(expectedSource);
        } catch (AssertionError e) {
            printStackTraceAndExit(e);
        }
    }

    private static void assertRandomSeed() {
        final DefaultRandom random = (DefaultRandom) ThreadLocalRandom.getInstance().get();

        try {
            assertThat(random.getSeed()).as("expected a positive random seed").isPositive();
            assertThat(random.getSource()).as("seed source").isEqualTo(Seeds.Source.RANDOM);
        } catch (AssertionError e) {
            printStackTraceAndExit(e);
        }
    }

    /**
     * AssertionError in {@link AfterEach} does not propagate up,
     * therefore we print the stack trace and exit the VM.
     * There must be a better way to do this!
     */
    @SuppressWarnings("CallToPrintStackTrace")
    private static void printStackTraceAndExit(final AssertionError error) {
        System.err.print("""
                #############################################

                 InstancioExtension test failure!
                 Intentionally exiting the VM.

                #############################################
                """);

        error.printStackTrace();
        System.exit(-1);
    }
}
