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
package org.instancio.support;

import org.instancio.settings.Settings;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ThreadLocalSettingsTest {

    @Test
    @Order(1)
    void get() {
        assertThat(ThreadLocalSettings.getInstance().get()).isNull();
    }

    @Test
    @Order(2)
    void set() {
        final Settings settings = Settings.create();
        ThreadLocalSettings.getInstance().set(settings);
        assertThat(ThreadLocalSettings.getInstance().get()).isSameAs(settings);
    }

    @Test
    @Order(3)
    void remove() {
        ThreadLocalSettings.getInstance().remove();
        assertThat(ThreadLocalSettings.getInstance().get()).isNull();
    }
}
