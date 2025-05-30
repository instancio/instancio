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
package org.instancio.test.features.create;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.Keys;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(InstancioExtension.class)
class CreateMapTest {

    @Test
    void createMap() {
        final Map<String, Long> result = Instancio.createMap(String.class, Long.class);

        assertThat(result).hasSizeBetween(
                Keys.MAP_MIN_SIZE.defaultValue(),
                Keys.MAP_MAX_SIZE.defaultValue());
    }
}
