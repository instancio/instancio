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
package org.instancio.test.features.generator.id;

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
class EanGeneratorTest {

    private static String createEan(final GeneratorSpecProvider<String> spec) {
        return Instancio.of(String.class)
                .generate(root(), spec)
                .create();
    }

    @Test
    void defaultEanIsType13() {
        final String result = createEan(gen -> gen.id().ean());

        assertEan(result, 13);
    }

    @Test
    void ean13() {
        final String result = createEan(gen -> gen.id().ean().type13());

        assertEan(result, 13);
    }

    @Test
    void ean8() {
        final String result = createEan(gen -> gen.id().ean().type8());

        assertEan(result, 8);
    }

    @Test
    void nullable() {
        final Stream<String> result = Stream.generate(() -> createEan(gen -> gen.id().ean().nullable()))
                .limit(Constants.SAMPLE_SIZE_DDD);

        assertThat(result).containsNull();
    }

    private static void assertEan(final String result, final int expected) {
        assertThat(result)
                .hasSize(expected)
                .containsOnlyDigits();
    }
}
