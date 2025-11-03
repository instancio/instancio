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
package org.instancio.junit;

import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(InstancioExtension.class)
class InstancioExtensionWithNestedSettingsTest {

    @WithSettings
    private static final Settings settings = Settings.create()
            .set(Keys.INTEGER_MIN, -1)
            .set(Keys.INTEGER_MAX, -1);

    private @Given int intField;

    @Test
    void givenParamShouldUseSettingsField(@Given final int intParam) {
        assertThat(intParam).isEqualTo(-1);
        assertThat(intField).isEqualTo(-1);
    }

    @Nested
    class InheritSettingsNestedTestClass {
        private @Given int intField;

        @Test
        void givenParamShouldUseSettingsField(@Given final int intParam) {
            assertThat(intParam).isEqualTo(-1);
            assertThat(intField).isEqualTo(-1);
        }

        @Nested
        class OverrideSettingsNestedTestClass {

            @WithSettings
            private final Settings settings = Settings.create()
                    .set(Keys.INTEGER_MIN, -2)
                    .set(Keys.INTEGER_MAX, -2);

            private @Given int intField;

            @Test
            void givenParamShouldUseSettingsField(@Given final int intParam) {
                assertThat(intParam).isEqualTo(-2);
                assertThat(intField).isEqualTo(-2);
            }

            @Nested
            class MergeSettingsNestedTestClass {

                @WithSettings
                private final Settings settings = Settings.create()
                        .set(Keys.STRING_MIN_LENGTH, 15)
                        .set(Keys.STRING_MAX_LENGTH, 15);

                private @Given int intField;
                private @Given String stringField;

                @Test
                void givenParamShouldUseSettingsField(@Given final int intParam, @Given final String stringParam) {
                    assertThat(intParam).isEqualTo(-2);
                    assertThat(intField).isEqualTo(-2);
                    assertThat(stringParam).hasSize(15);
                    assertThat(stringField).hasSize(15);
                }
            }
        }
    }

    @Nested
    class InstancioSourceNestedTestClass {

        @WithSettings
        private static final Settings settings = Settings.create()
                .set(Keys.STRING_MIN_LENGTH, 15)
                .set(Keys.STRING_MAX_LENGTH, 15);

        @ParameterizedTest
        @InstancioSource
        void givenParamShouldUseSettingsField(final int intParam, final String stringParam) {
            assertThat(intParam).isEqualTo(-1);
            assertThat(stringParam).hasSize(15);
        }
    }
}
