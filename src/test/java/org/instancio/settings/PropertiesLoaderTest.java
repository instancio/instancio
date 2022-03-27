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

import org.instancio.exception.InstancioException;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PropertiesLoaderTest {

    @Test
    void load() {
        final Properties props = new PropertiesLoader().load("instancio-test.properties");
        assertThat(props).isNotNull();
        assertThat(props.get(Setting.LONG_MAX.key())).isNotNull();
    }

    @Test
    void loadFileNotFound() {
        final String file = "non-existent.properties";
        assertThatThrownBy(() -> new PropertiesLoader().load(file))
                .isInstanceOf(InstancioException.class)
                .hasMessage("Unable to load properties from '%s'", file);
    }
}
