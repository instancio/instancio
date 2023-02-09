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
package org.instancio.internal.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class LuhnUtilsTest {

    @ValueSource(strings = {
            "65", "362", "3431", "47013", "762194", "7733353", "20798201", "200426950", "1980716370",
            "73789867181", "247211861894", "4047046295092", "13630187298936", "752979255287461", "1767344869570547",
            "8611647366320010715949897824300630115887570463629462860129377888022595697408285412164779650720331913"
    })
    @ParameterizedTest
    void invalid(final String value) {
        assertThat(LuhnUtils.isLuhnValid(value))
                .as("Failed check for invalid Luhn '%s'", value)
                .isFalse();
    }

    @ValueSource(strings = {
            "67", "364", "3434", "47019", "762195", "7733355", "20798203", "200426955", "1980716375",
            "73789867188", "247211861890", "4047046295090", "13630187298939", "752979255287463", "1767344869570542",
            "8611647366320010715949897824300630115887570463629462860129377888022595697408285412164779650720331914"
    })
    @ParameterizedTest
    void valid(final String value) {
        assertThat(LuhnUtils.isLuhnValid(value))
                .as("Failed check for valid Luhn '%s'", value)
                .isTrue();
    }

    /**
     * Check digit is the last char of the payload.
     */
    @Test
    void validWithIndexCheckDigitWithinPayload() {
        assertThat(LuhnUtils.isLuhnValid(1, 2, 2, "X67X")).isTrue();
        assertThat(LuhnUtils.isLuhnValid(2, 4, 4, "XX364XXX")).isTrue();
    }

    @Test
    void validWithIndexCheckDigitOutsidePayload() {
        assertThat(LuhnUtils.isLuhnValid(1, 2, 3, "X677")).isTrue();
        assertThat(LuhnUtils.isLuhnValid(2, 4, 0, "4X364XXX")).isTrue();
    }
}
