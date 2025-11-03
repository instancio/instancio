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
package org.instancio.test.features.settings;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;

import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag(Feature.WITH_SETTINGS_ANNOTATION)
@ExtendWith(InstancioExtension.class)
class WithSettingsAnnotationTest {

    @WithSettings
    private static final Settings settings = Settings.create()
            .set(Keys.INTEGER_MIN, -1)
            .set(Keys.INTEGER_MAX, -1);

    @Test
    void withSettings() {
        assertThat(Instancio.create(int.class)).isEqualTo(-1);
    }

    @Test
    void withSettingsOverride() {
        assertThat(Instancio.of(int.class).withSettings(Settings.create()
                        .set(Keys.INTEGER_MIN, -2)
                        .set(Keys.INTEGER_MAX, -2))
                .create())
                .isEqualTo(-2);
    }

    @RepeatedTest(2)
    void withSettingsWithRepeatedTest() {
        assertThat(Instancio.create(int.class)).isEqualTo(-1);
    }

    @InstancioSource(samples = 5)
    @ParameterizedTest
    void withSettingsWithParameterizedTest(int value) {
        assertThat(value).isEqualTo(-1);
    }

    @Nested
    class FooTest {
        @Test
        @DisplayName("Settings are injected into nested test class")
        void verifyWithSettings() {
            assertThat(Instancio.create(int.class)).isEqualTo(-1);
        }
    }
}
