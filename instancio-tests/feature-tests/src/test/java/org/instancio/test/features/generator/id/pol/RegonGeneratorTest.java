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
package org.instancio.test.features.generator.id.pol;

import org.instancio.GeneratorSpecProvider;
import org.instancio.Instancio;
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
class RegonGeneratorTest {

    private static String createRegon(final GeneratorSpecProvider<String> spec) {
        return Instancio.of(String.class)
                .generate(root(), spec)
                .create();
    }

    @Test
    void defaultRegonIsType9() {
        final String result = createRegon(gen -> gen.id().pol().regon());

        assertRegon(result, 9);
    }

    @Test
    void regon9() {
        final String result = createRegon(gen -> gen.id().pol().regon().type9());

        assertRegon(result, 9);
    }

    @Test
    void regon14() {
        final String result = createRegon(gen -> gen.id().pol().regon().type14());

        assertRegon(result, 14);
    }

    @Test
    void nullable() {
        final Stream<String> result = Stream.generate(() -> createRegon(gen -> gen.id().pol().regon().nullable()))
                .limit(Constants.SAMPLE_SIZE_DDD);

        assertThat(result).containsNull();
    }

    private static void assertRegon(final String result, final int expectedSize) {
        assertThat(result)
                .hasSize(expectedSize)
                .containsOnlyDigits();
    }
}
