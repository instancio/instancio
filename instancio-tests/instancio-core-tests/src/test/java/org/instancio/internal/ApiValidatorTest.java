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
package org.instancio.internal;

import org.instancio.exception.InstancioApiException;
import org.instancio.internal.util.Sonar;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ApiValidatorTest {

    @Test
    void doesNotContainNull() {
        assertThatNoException()
                .isThrownBy(() -> {
                    final String[] input = {"a"};
                    final String[] output = ApiValidator.doesNotContainNull(input, () -> "a message");
                    assertThat(output).isSameAs(input);
                });

        assertThatThrownBy(() -> ApiValidator.doesNotContainNull(new String[]{null}, () -> "a message"))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("a message");
    }

    @Test
    void validateSubtype() {
        assertThatNoException().isThrownBy(() -> {
            ApiValidator.validateSubtype(Collection.class, Collection.class);
            ApiValidator.validateSubtype(Collection.class, List.class);
        });

        assertThatThrownBy(() -> ApiValidator.validateSubtype(List.class, Collection.class))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("invalid subtype mapping");
    }

    @Test
    @SuppressWarnings({"DataFlowIssue", Sonar.ONE_METHOD_WHEN_TESTING_EXCEPTIONS})
    void notEmptyCollection() {
        assertThatNoException().isThrownBy(() -> ApiValidator.notEmpty(List.of("foo"), "some error"));

        assertThatThrownBy(() -> ApiValidator.notEmpty(List.of(), "some %s", "error"))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("some error");

        assertThatThrownBy(() -> ApiValidator.notEmpty((Collection<Object>) null, "some %s", "error"))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("some error");
    }
}
