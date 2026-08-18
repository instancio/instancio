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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies that a non-static {@code @WithSettings} field is supported even when
 * the test class contains a {@code @ParameterizedTest} method. Prior to version
 * {@code 6.0.0}, this combination was rejected because of {@code @InstancioSource},
 * which required the settings field to be static.
 */
@ExtendWith(InstancioExtension.class)
class InstancioExtensionWithSettingsOnNonStaticFieldTest {

    @WithSettings
    private final Settings settings = Settings.create()
            .set(Keys.INTEGER_MIN, -1)
            .set(Keys.INTEGER_MAX, -1);

    @Test
    void nonStaticSettingsFieldWithRegularTest() {
        assertThat(Instancio.create(Integer.class)).isEqualTo(-1);
    }

    @ParameterizedTest
    @ValueSource(strings = {"foo", "bar"})
    void nonStaticSettingsFieldWithParameterizedTest(final String value) {
        assertThat(Instancio.create(Integer.class)).isEqualTo(-1);
    }
}
