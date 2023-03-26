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
package org.instancio.internal.generator.net;

import org.instancio.Random;
import org.instancio.generator.GeneratorContext;
import org.instancio.settings.Settings;
import org.instancio.support.DefaultRandom;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.net.Inet4Address;
import java.net.UnknownHostException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;

class InetAddressGeneratorTest {
    private static final Settings settings = Settings.defaults();
    private static final Random random = new DefaultRandom();
    private static final GeneratorContext context = new GeneratorContext(settings, random);
    private final InetAddressGenerator generator = new InetAddressGenerator(context);

    @Test
    void apiMethod() {
        assertThat(generator.apiMethod()).isNull();
    }

    @Test
    void generate() {
        assertThat(generator.generate(random)).isExactlyInstanceOf(Inet4Address.class);
    }

    @Test
    void generateReturnsNullOnError() throws UnknownHostException {
        final InetAddressGenerator gen = Mockito.spy(generator);

        doThrow(new UnknownHostException()).when(gen).getLocalHost();

        assertThat(gen.generate(random)).isNull();
    }
}
