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
package org.instancio.test.features.conditional;

import org.instancio.Gen;
import org.instancio.Instancio;
import org.instancio.generator.GeneratorSpec;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.misc.StringsGhi;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.instancio.When.valueOf;

@FeatureTag(Feature.CONDITIONAL)
@ExtendWith(InstancioExtension.class)
class ConditionalValueOfTest {

    @Test
    void set() {
        final StringsGhi result = Instancio.of(StringsGhi.class)
                .when(valueOf(StringsGhi::getG).satisfies(s -> true).set(field(StringsGhi::getI), "I"))
                .create();

        assertThat(result.i).isEqualTo("I");
    }

    @Test
    void supplyWithSupplier() {
        final StringsGhi result = Instancio.of(StringsGhi.class)
                .when(valueOf(StringsGhi::getG).satisfies(s -> true).supply(field(StringsGhi::getI), () -> "I"))
                .create();

        assertThat(result.i).isEqualTo("I");
    }

    @Test
    void supplyWithGenerator() {
        final StringsGhi result = Instancio.of(StringsGhi.class)
                .when(valueOf(StringsGhi::getG).satisfies(s -> true).supply(field(StringsGhi::getI), random -> "I"))
                .create();

        assertThat(result.i).isEqualTo("I");
    }

    @Test
    void generateWithGeneratorSpec() {
        final GeneratorSpec<String> spec = Gen.text().pattern("I");

        final StringsGhi result = Instancio.of(StringsGhi.class)
                .when(valueOf(StringsGhi::getG).satisfies(s -> true)
                        .generate(field(StringsGhi::getI), spec))
                .create();

        assertThat(result.i).isEqualTo("I");
    }

    @Test
    void generateWithGenerators() {
        final StringsGhi result = Instancio.of(StringsGhi.class)
                .when(valueOf(StringsGhi::getG).satisfies(s -> true)
                        .generate(field(StringsGhi::getI), gen -> gen.text().pattern("I")))
                .create();

        assertThat(result.i).isEqualTo("I");
    }

    @Test
    @DisplayName("satisfies() evaluates to false")
    void satisfiesEvaluatesToFalse() {
        final StringsGhi result = Instancio.of(StringsGhi.class)
                .when(valueOf(StringsGhi::getG).satisfies(s -> false).set(field(StringsGhi::getI), "I"))
                .create();

        assertThat(result.i).isNotEqualTo("I");
    }

    @Test
    @DisplayName("is() evaluates to false")
    void isEvaluatesToFalse() {
        final StringsGhi result = Instancio.of(StringsGhi.class)
                .when(valueOf(StringsGhi::getG).is("foo").set(field(StringsGhi::getI), "I"))
                .create();

        assertThat(result.i).isNotEqualTo("I");
    }

    @Test
    @DisplayName("isIn() evaluates to false")
    void isInEvaluatesToFalse() {
        final StringsGhi result = Instancio.of(StringsGhi.class)
                .when(valueOf(StringsGhi::getG).isIn("foo").set(field(StringsGhi::getI), "I"))
                .create();

        assertThat(result.i).isNotEqualTo("I");
    }
}
