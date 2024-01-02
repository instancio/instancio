/*
 * Copyright 2022-2024 the original author or authors.
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
import org.junit.jupiter.api.Nested;
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
        assertThat(Keys.get("an-invalid-property")).isNull();
    }

    @Test
    void create() {
        final SettingKey<Integer> key = Keys.ofType(Integer.class)
                .withPropertyKey("foo.bar")
                .create();

        assertThat(key.propertyKey()).isEqualTo("foo.bar");
        assertThat(key.type()).isEqualTo(Integer.class);
        assertThat(key.defaultValue()).isNull();
        assertThat(key.allowsNullValue()).isTrue();
    }

    @Test
    void createWithoutPropertyKey() {
        final SettingKey<String> key = Keys.ofType(String.class).create();

        assertThat(key.propertyKey()).matches("custom\\.key\\.[0-9a-f]{20}");
        assertThat(key.type()).isEqualTo(String.class);
        assertThat(key.defaultValue()).isNull();
        assertThat(key.allowsNullValue()).isTrue();
    }

    @Test
    void validationNullType() {
        assertThatThrownBy(() -> Keys.ofType(null))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("type must not be null");
    }

    @Test
    void validationNullPropertyKey() {
        final SettingKey.SettingKeyBuilder<String> builder = Keys.ofType(String.class);

        assertThatThrownBy(() -> builder.withPropertyKey(null))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("property key must not be null");
    }

    @Nested
    class EqualsHashCodeTest {
        @Test
        void withTypeAndPropertyKey() {
            final SettingKey<Integer> key1 = Keys.ofType(Integer.class)
                    .withPropertyKey("foo.bar")
                    .create();

            final SettingKey<Integer> key2 = Keys.ofType(Integer.class)
                    .withPropertyKey("foo.bar")
                    .create();

            assertThat(key1).isEqualTo(key2).hasSameHashCodeAs(key2);
        }

        @Test
        void withoutTypeAndPropertyKey() {
            final SettingKey<Integer> key1 = Keys.ofType(Integer.class).create();
            final SettingKey<Integer> key2 = Keys.ofType(Integer.class).create();

            assertThat(key1).isNotEqualTo(key2).doesNotHaveSameHashCodeAs(key2);
        }
    }
}
