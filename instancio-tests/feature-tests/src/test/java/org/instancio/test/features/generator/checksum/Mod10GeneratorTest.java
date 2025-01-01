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
package org.instancio.test.features.generator.checksum;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.root;

@FeatureTag(Feature.GENERATOR)
@ExtendWith(InstancioExtension.class)
class Mod10GeneratorTest {

    @Test
    void createWithDefaults() {
        final String result = Instancio.of(String.class)
                .generate(root(), gen -> gen.checksum().mod10())
                .create();

        assertThat(result).as("default length").hasSize(16);
    }

    @Test
    void createCustomised() {
        final String result = Instancio.of(String.class)
                .generate(root(), gen -> gen.checksum().mod10()
                        .startIndex(3)
                        .endIndex(10)
                        .checkDigitIndex(20)
                        .weight(5)
                        .multiplier(3))
                .create();

        assertThat(result).hasSize(21);
    }

    @Test
    void createAsNumber() {
        final int length = 5;
        final Long result = Instancio.of(Long.class)
                .generate(root(), gen -> gen.checksum().mod10().length(length)
                        .as(Long::valueOf))
                .create();

        assertThat(result).isPositive();
        assertThat(result.toString()).hasSize(length);
    }
}