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
package org.instancio.test.features.generator.net;

import org.instancio.Instancio;
import org.instancio.internal.util.CollectionUtils;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.root;

@FeatureTag(Feature.GENERATOR)
@ExtendWith(InstancioExtension.class)
class Ip4GeneratorTest {

    @Test
    void create() {
        final String result = Instancio.of(String.class)
                .generate(root(), gen -> gen.net().ip4())
                .create();

        final int[] octets = toIntOctets(result);
        assertThat(octets[0]).isBetween(1, 255);
        assertThat(octets[1]).isBetween(0, 255);
        assertThat(octets[2]).isBetween(0, 255);
        assertThat(octets[3]).isBetween(0, 255);
    }

    /**
     * Test cases were generated using Python's {@code ipaddress} module, e.g.
     *
     * <pre>{@code
     * >>> import ipaddress
     * >>> [str(ip) for ip in ipaddress.IPv4Network('192.0.2.0/30')]
     * ['192.0.2.0', '192.0.2.1', '192.0.2.2', '192.0.2.3']
     * }</pre>
     */
    @Nested
    class FromCidrTest {

        @Test
        void fromCidrMisc() {
            assertRange("192.168.1.1/32",
                    "192.168.1.1");

            assertRange("192.0.2.0/30",
                    "192.0.2.0", "192.0.2.1", "192.0.2.2", "192.0.2.3");

            // Note: ipaddress module produces: ValueError: 192.0.2.1/30 has host bits set
            assertRange("192.0.2.1/30",
                    "192.0.2.0", "192.0.2.1", "192.0.2.2", "192.0.2.3");
        }

        @Test
        void fromCidr8() {
            final String result = Instancio.of(String.class)
                    .generate(root(), gen -> gen.net().ip4().fromCidr("192.0.0.0/8"))
                    .create();

            final int[] octets = toIntOctets(result);
            assertThat(octets[0]).isEqualTo(192);
            assertThat(octets[1]).isBetween(0, 255);
            assertThat(octets[2]).isBetween(0, 255);
            assertThat(octets[3]).isBetween(0, 255);
        }

        @Test
        void fromCidr24() {
            final String result = Instancio.of(String.class)
                    .generate(root(), gen -> gen.net().ip4().fromCidr("1.1.1.0/24"))
                    .create();

            final int[] octets = toIntOctets(result);
            assertThat(octets[0]).isOne();
            assertThat(octets[1]).isOne();
            assertThat(octets[2]).isOne();
            assertThat(octets[3]).isBetween(0, 255);
        }

        private void assertRange(final String input, final String... expected) {
            final String result = Instancio.of(String.class)
                    .generate(root(), gen -> gen.net().ip4().fromCidr(input))
                    .create();

            assertThat(result).isIn(CollectionUtils.asSet(expected));
        }
    }

    private static int[] toIntOctets(final String result) {
        final String[] strOctets = result.split("\\.");
        assertThat(strOctets).hasSize(4);

        final int[] octets = new int[4];

        for (int i = 0; i < strOctets.length; i++) {
            octets[i] = Integer.parseInt(strOctets[i]);
        }
        return octets;
    }
}