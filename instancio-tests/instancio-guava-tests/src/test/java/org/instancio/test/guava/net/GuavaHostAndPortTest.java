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
package org.instancio.test.guava.net;

import com.google.common.net.HostAndPort;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(InstancioExtension.class)
class GuavaHostAndPortTest {

    private static final String IP4_REGEX = "\\d{1,3}\\.\\d{1,3}.\\d{1,3}.\\d{1,3}";

    @Test
    void create() {
        final HostAndPort result = Instancio.create(HostAndPort.class);

        assertThat(result.getHost()).matches(IP4_REGEX);
        assertThat(result.getPort()).isBetween(0, 65535);
    }
}