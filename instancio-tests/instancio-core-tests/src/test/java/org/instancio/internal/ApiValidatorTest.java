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
package org.instancio.internal;

import org.instancio.exception.InstancioApiException;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ApiValidatorTest {

    @Test
    void doesNotContainNull() {
        assertThatNoException()
                .isThrownBy(() -> ApiValidator.doesNotContainNull(new String[]{"a"}, () -> "a message"));

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

}
