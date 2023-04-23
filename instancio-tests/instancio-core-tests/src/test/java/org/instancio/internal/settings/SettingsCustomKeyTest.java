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
package org.instancio.internal.settings;

import org.instancio.exception.InstancioApiException;
import org.instancio.settings.Keys;
import org.instancio.settings.SettingKey;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SettingsCustomKeyTest {

    private static final SettingKey<StringHolder> KEY = Keys.ofType(StringHolder.class).create();

    @Test
    void customKey() {
        final StringHolder value = new StringHolder();

        final Settings settings = Settings.create().set(KEY, value);

        assertThat(settings.get(KEY)).isSameAs(value);
    }

    @Test
    void customKeyViaBuilder() {
        final SettingKey<String> key = Keys.ofType(String.class)
                .withPropertyKey("xyz")
                .create();

        assertThat(key.type()).isEqualTo(String.class);
        assertThat(key.propertyKey()).isEqualTo("xyz");
    }

    @Test
    void validation() {
        assertThatThrownBy(() -> Keys.ofType(null))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("type must not be null");

        final SettingKey.SettingKeyBuilder<String> builder = Keys.ofType(String.class);
        assertThatThrownBy(() -> builder.withPropertyKey(null))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("property key must not be null");
    }

    @Test
    void toStringWithCustomKey() {
        final StringHolder value = new StringHolder("foo");

        final Settings settings = Settings.create().set(KEY, value);

        assertThat(settings.toString())
                .contains(KEY.propertyKey(), ": StringHolder(value=foo)");
    }

}
