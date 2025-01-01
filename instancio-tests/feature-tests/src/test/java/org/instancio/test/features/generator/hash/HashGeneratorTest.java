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
package org.instancio.test.features.generator.hash;

import org.instancio.GeneratorSpecProvider;
import org.instancio.Instancio;
import org.instancio.generators.Generators;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.root;

@FeatureTag(Feature.GENERATOR)
@ExtendWith(InstancioExtension.class)
class HashGeneratorTest {

    private static String create(final GeneratorSpecProvider<String> spec) {
        return Instancio.of(String.class)
                .generate(root(), spec)
                .create();
    }

    @Test
    void defaultIsMd5() {
        final String result = create(Generators::hash);

        assertResult(result, 32);
    }

    @Test
    void sha1() {
        final String result = create(gen -> gen.hash().sha1());

        assertResult(result, 40);
    }

    @Test
    void sha256() {
        final String result = create(gen -> gen.hash().sha256());

        assertResult(result, 64);
    }

    @Test
    void sha512() {
        final String result = create(gen -> gen.hash().sha512());

        assertResult(result, 128);
    }

    @Test
    void asByteArray() {
        final byte[] result = Instancio.of(byte[].class)
                .generate(root(), gen -> gen.hash().sha256().as(HashGeneratorTest::hexString))
                .create();

        assertThat(result).hasSize(32);
    }


    @Test
    void nullable() {
        final Stream<String> result = Stream.generate(() -> create(gen -> gen.hash().nullable()))
                .limit(Constants.SAMPLE_SIZE_DDD);

        assertThat(result).containsNull();
    }

    private static void assertResult(final String result, final int expected) {
        assertThat(result)
                .hasSize(expected)
                .isHexadecimal()
                .isUpperCase();
    }

    // Source: https://stackoverflow.com/a/19119453/45112
    private static byte[] hexString(final String s) {
        final int len = s.length();
        final byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
}
