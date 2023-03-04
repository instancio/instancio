/*
 * Copyright 2022-2023 the original author or authors.
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
package org.instancio.internal;

import org.instancio.Mode;
import org.instancio.exception.InstancioApiException;
import org.instancio.settings.Keys;
import org.instancio.settings.SettingKey;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ApiValidatorTest {

    @Nested
    class ValidateKeyValueTest {
        @Test
        void nullKeyNotAllowed() {
            assertThatThrownBy(() -> ApiValidator.validateKeyValue(null, "some value"))
                    .isExactlyInstanceOf(InstancioApiException.class)
                    .hasMessage("Setting key must not be null");
        }

        @Test
        void nullAllowedForNullableValue() {
            final SettingKey<Long> key = Keys.SEED;
            assertThat(key.allowsNullValue()).isTrue();
            ApiValidator.validateKeyValue(key, null); // no error
        }

        @Test
        void nullNotAllowedForNonNullableValue() {
            final SettingKey<Mode> key = Keys.MODE;
            assertThat(key.allowsNullValue()).isFalse();
            assertThatThrownBy(() -> ApiValidator.validateKeyValue(key, null))
                    .isExactlyInstanceOf(InstancioApiException.class)
                    .hasMessage("Setting value for key 'mode' must not be null");
        }

        @Test
        void errorThrownIfValueHasInvalidType() {
            assertThatThrownBy(() -> ApiValidator.validateKeyValue(Keys.INTEGER_MIN, "bad"))
                    .isExactlyInstanceOf(InstancioApiException.class)
                    .hasMessage("The value 'bad' is of unexpected type (String) for key 'integer.min' (expected: Integer)");
        }
    }
}
