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
package org.instancio.junit.given;

import org.instancio.junit.Given;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(InstancioExtension.class)
class GivenWithSettingsAnnotationTest {

    @WithSettings
    private final Settings settings = Settings.create()
            .set(Keys.INTEGER_MIN, -1)
            .set(Keys.INTEGER_MAX, -1);

    private @Given int intField;

    @Test
    void givenParamShouldUseSettingsField1(@Given final int intParam) {
        assertThat(intParam).isEqualTo(-1);
        assertThat(intField).isEqualTo(-1);
    }

    @ParameterizedTest
    @ValueSource(strings = "foo")
    void givenParamShouldUseSettingsField2(final String string, @Given final int intParam) {
        assertThat(string).isEqualTo("foo");
        assertThat(intParam).isEqualTo(-1);
    }
}
