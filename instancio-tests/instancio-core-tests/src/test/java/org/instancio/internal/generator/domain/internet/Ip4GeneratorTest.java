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
package org.instancio.internal.generator.domain.internet;

import org.instancio.exception.InstancioApiException;
import org.instancio.internal.generator.AbstractGenerator;
import org.instancio.internal.generator.AbstractGeneratorTestTemplate;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class Ip4GeneratorTest extends AbstractGeneratorTestTemplate {

    private final Ip4Generator generator = new Ip4Generator(getGeneratorContext());

    @Override
    protected String getApiMethod() {
        return "ip4()";
    }

    @Override
    protected AbstractGenerator<?> generator() {
        return generator;
    }

    @Test
    void assertEntireCidrRange() {
        final String input = "192.168.1.0/24";
        final Set<Integer> lastOctets = new HashSet<>();

        generator.fromCidr(input);

        for (int i = 0; i < Constants.SAMPLE_SIZE_DDDD; i++) {
            final String result = generator.generate(random);
            assertThat(result).startsWith("192.168.1.");
            final String last = result.substring(result.lastIndexOf('.') + 1);
            lastOctets.add(Integer.parseInt(last));
        }

        assertThat(lastOctets)
                .contains(0)
                .contains(255)
                .hasSize(256);
    }

    @Test
    void fromCidrInvalidValue() {
        final String badInput = "foo";
        generator.fromCidr(badInput);

        assertThatThrownBy(() -> generator.generate(random))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessage("Error generating IPv4 address from: '%s'", badInput);
    }

    @Test
    void fromCidrNull() {
        assertThatThrownBy(() -> generator.fromCidr(null))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessage("CIDR must not be null");
    }
}
