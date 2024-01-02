/*
 *  Copyright 2022-2024 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.instancio.junit;

import org.instancio.Instancio;
import org.instancio.internal.util.Sonar;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.support.ThreadLocalRandom;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.platform.testkit.engine.EngineTestKit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

class InstancioExtensionWithSettingsAnnotationTest {

    @Test
    void threadLocalSettingsCleanup() {
        EngineTestKit.engine("junit-jupiter")
                .selectors(selectClass(ThreadLocalSettingsCleanupTest.class))
                .execute()
                .testEvents()
                .assertStatistics(stats -> stats.succeeded(2));

        assertThat(ThreadLocalRandom.getInstance().get())
                .as("Expected thread local Settings to be removed after the test is done")
                .isNull();
    }

    @Nested
    @ExtendWith(InstancioExtension.class)
    class ThreadLocalSettingsCleanupTest {
        private final long EXPECTED_LONG_VALUE = 9;

        @WithSettings
        private final Settings settings = Settings.create()
                .set(Keys.LONG_MIN, EXPECTED_LONG_VALUE)
                .set(Keys.LONG_MAX, EXPECTED_LONG_VALUE);

        @Test
        void assertValueCreatedUsingAnnotatedSettings() {
            assertThat(Instancio.create(Long.class)).isEqualTo(EXPECTED_LONG_VALUE);
        }

        @Test
        @DisplayName("withSettings() should take precedence over the thread local settings")
        void modelSettingsShouldTakePrecedenceOverThreadLocalSettings() {
            final long minOverride = 100;

            final Long result = Instancio.of(Long.class)
                    .withSettings(Settings.create()
                            .set(Keys.LONG_MIN, minOverride)
                            .set(Keys.LONG_MAX, minOverride))
                    .create();

            assertThat(result).isEqualTo(minOverride);
        }
    }

    @Nested
    @ExtendWith(InstancioExtension.class)
    class ParameterizedWithNonStaticSettingsFieldTest {

        /**
         * {@code ParameterizedTest} can only access a static instance of settings.
         * If the settings instance is not static, an exception should be thrown.
         */
        @WithSettings
        private final Settings nonStaticSettings = Settings.create();

        @ParameterizedTest
        @InstancioSource
        @Disabled("Need to find a way to assert the thrown exception")
        @SuppressWarnings(Sonar.ADD_ASSERTION)
        void verify(final String any) {
            // Expected: InstancioApiException before the test method gets a chance to run.
            // notifying the user that the Settings field must be static
        }
    }
}