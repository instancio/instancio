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
package org.instancio.test.features.assign;

import org.instancio.Gen;
import org.instancio.Instancio;
import org.instancio.generator.Generator;
import org.instancio.generator.ValueSpec;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.misc.StringsGhi;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Assign.given;
import static org.instancio.Select.field;

@FeatureTag(Feature.ASSIGN)
@ExtendWith(InstancioExtension.class)
class AssignGivenOriginTest {

    @Test
    void set() {
        final StringsGhi result = Instancio.of(StringsGhi.class)
                .assign(given(StringsGhi::getG).satisfies(s -> true).set(field(StringsGhi::getI), "I"))
                .create();

        assertThat(result.i).isEqualTo("I");
    }

    @Test
    void supplyWithSupplier() {
        final Supplier<String> supplier = () -> "I";
        final StringsGhi result = Instancio.of(StringsGhi.class)
                .assign(given(StringsGhi::getG).satisfies(s -> true).supply(field(StringsGhi::getI), supplier))
                .create();

        assertThat(result.i).isEqualTo("I");
    }

    @Test
    void supplyWithGenerator() {
        final Generator<String> generator = random -> "I";
        final StringsGhi result = Instancio.of(StringsGhi.class)
                .assign(given(StringsGhi::getG).satisfies(s -> true).supply(field(StringsGhi::getI), generator))
                .create();

        assertThat(result.i).isEqualTo("I");
    }

    @Test
    void generateWithValueSpec() {
        final ValueSpec<String> spec = Gen.text().pattern("I");

        final StringsGhi result = Instancio.of(StringsGhi.class)
                .assign(given(StringsGhi::getG).satisfies(s -> true)
                        .generate(field(StringsGhi::getI), spec))
                .create();

        assertThat(result.i).isEqualTo("I");
    }

    @Test
    void generateWithGeneratorSpec() {
        final StringsGhi result = Instancio.of(StringsGhi.class)
                .assign(given(StringsGhi::getG).satisfies(s -> true)
                        .generate(field(StringsGhi::getI), gen -> gen.text().pattern("I")))
                .create();

        assertThat(result.i).isEqualTo("I");
    }

    @Test
    @DisplayName("Predicate evaluates to false")
    void predicateEvaluatesToFalse() {
        final StringsGhi result = Instancio.of(StringsGhi.class)
                .assign(given(StringsGhi::getG).satisfies(s -> false).set(field(StringsGhi::getI), "I"))
                .create();

        assertThat(result.i).isNotEqualTo("I");
    }

    @Test
    @DisplayName("is() evaluates to false")
    void isEvaluatesToFalse() {
        final StringsGhi result = Instancio.of(StringsGhi.class)
                .assign(given(StringsGhi::getG).is("foo").set(field(StringsGhi::getI), "I"))
                .create();

        assertThat(result.i).isNotEqualTo("I");
    }

    @Test
    @DisplayName("isNot() evaluates to false")
    void isNotEvaluatesToFalse() {
        final StringsGhi result = Instancio.of(StringsGhi.class)
                .set(field(StringsGhi::getG), "foo")
                .assign(given(StringsGhi::getG).isNot("foo").set(field(StringsGhi::getI), "I"))
                .create();

        assertThat(result.i).isNotEqualTo("I");
    }

    @Test
    @DisplayName("isIn() evaluates to false")
    void isInEvaluatesToFalse() {
        final StringsGhi result = Instancio.of(StringsGhi.class)
                .assign(given(StringsGhi::getG).isIn("foo").set(field(StringsGhi::getI), "I"))
                .create();

        assertThat(result.i).isNotEqualTo("I");
    }
}
