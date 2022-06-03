/*
 * Copyright 2022 the original author or authors.
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
package org.instancio.settings;

import org.instancio.exception.InstancioApiException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.settings.Keys.COLLECTION_NULLABLE;

class KeysTest {

    @Test
    void get() {
        assertThat(Keys.get(COLLECTION_NULLABLE.propertyKey())).isEqualTo(COLLECTION_NULLABLE);
    }

    @Test
    void getWithInvalidProperty() {
        assertThatThrownBy(() -> Keys.get("foo"))
                .isInstanceOf(InstancioApiException.class)
                .hasMessage("Invalid instancio property key: 'foo'");
    }
}
