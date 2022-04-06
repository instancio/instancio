/*
 *  Copyright 2022 the original author or authors.
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
import org.instancio.internal.ThreadLocalSettingsProvider;
import org.instancio.settings.Setting;
import org.instancio.settings.Settings;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(InstancioExtension.class)
class InstancioExtensionWithSettingsAnnotationTest {

    private static final long MIN = 9;

    @WithSettings
    private final Settings settings = Settings.create()
            .set(Setting.LONG_MIN, MIN)
            .set(Setting.LONG_MAX, MIN + 1);

    @AfterAll
    static void afterAll() {
        assertThat(ThreadLocalSettingsProvider.getInstance().get())
                .as("Expected thread local value to be removed after test is done")
                .isNull();
    }

    @Test
    void assertValueCreatedUsingAnnotatedSettings() {
        assertThat(Instancio.create(Long.class)).isEqualTo(MIN);
    }

    @Test
    @DisplayName("withSettings() should take precedence over the thread local settings")
    void modelSettingsShouldTakePrecedenceOverThreadLocalSettings() {
        final long minOverride = 100;

        final Long result = Instancio.of(Long.class)
                .withSettings(Settings.create()
                        .set(Setting.LONG_MIN, minOverride)
                        .set(Setting.LONG_MAX, minOverride + 1))
                .create();

        assertThat(result).isEqualTo(minOverride);
    }
}
