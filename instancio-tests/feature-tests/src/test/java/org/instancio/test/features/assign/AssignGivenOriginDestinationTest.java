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
package org.instancio.test.features.assign;

import org.instancio.Assign;
import org.instancio.Assignment;
import org.instancio.GivenOriginDestinationAction;
import org.instancio.Instancio;
import org.instancio.generator.GeneratorSpec;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.misc.StringsGhi;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.FieldSource;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

@FeatureTag(Feature.ASSIGN)
@ExtendWith(InstancioExtension.class)
class AssignGivenOriginDestinationTest {

    private static final String ODD = "odd";
    private static final String EVEN = "even";

    private static GivenOriginDestinationAction assignGiven() {
        return Assign.given(field(StringsGhi::getG), field(StringsGhi::getI))
                .set((String s) -> s.length() % 2 == 0, EVEN);
    }

    private static final List<Assignment> args = Arrays.asList(
            assignGiven().elseSet(ODD),
            assignGiven().elseSupply(() -> ODD),
            assignGiven().elseSupply(random -> random.oneOf(ODD)),
            assignGiven().elseGenerate(Instancio.gen().text().pattern(ODD)),
            assignGiven().elseGenerate(gen -> gen.text().pattern(ODD)));

    @FieldSource("args")
    @ParameterizedTest
    void verifyAssignment(final Assignment assignment) {
        final StringsGhi result = Instancio.of(StringsGhi.class)
                .assign(assignment)
                .create();

        final String expected = result.g.length() % 2 == 0 ? EVEN : ODD;
        assertThat(result.i).isEqualTo(expected);
    }

    @Test
    void set() {
        final StringsGhi result = Instancio.of(StringsGhi.class)
                .assign(Assign.given(field(StringsGhi::getG), field(StringsGhi::getI))
                        .set(s -> true, "I"))
                .create();

        assertThat(result.i).isEqualTo("I");
    }

    @Test
    void supplyWithSupplier() {
        final StringsGhi result = Instancio.of(StringsGhi.class)
                .assign(Assign.given(field(StringsGhi::getG), field(StringsGhi::getI))
                        .supply(s -> true, () -> "I"))
                .create();

        assertThat(result.i).isEqualTo("I");
    }

    @Test
    void supplyWithGenerator() {
        final StringsGhi result = Instancio.of(StringsGhi.class)
                .assign(Assign.given(field(StringsGhi::getG), field(StringsGhi::getI))
                        .supply(s -> true, random -> "I"))
                .create();

        assertThat(result.i).isEqualTo("I");
    }

    @Test
    void generateWithGeneratorSpec() {
        final GeneratorSpec<String> spec = Instancio.gen().text().pattern("I");

        final StringsGhi result = Instancio.of(StringsGhi.class)
                .assign(Assign.given(field(StringsGhi::getG), field(StringsGhi::getI))
                        .generate(s -> true, spec))
                .create();

        assertThat(result.i).isEqualTo("I");
    }

    @Test
    void generateWithGenerators() {
        final StringsGhi result = Instancio.of(StringsGhi.class)
                .assign(Assign.given(field(StringsGhi::getG), field(StringsGhi::getI))
                        .generate(s -> true, gen -> gen.text().pattern("I")))
                .create();

        assertThat(result.i).isEqualTo("I");
    }

    @Test
    @DisplayName("Predicate evaluates to false")
    void predicateEvaluatesToFalse() {
        final StringsGhi result = Instancio.of(StringsGhi.class)
                .assign(Assign.given(field(StringsGhi::getG), field(StringsGhi::getI))
                        .set(s -> false, "I"))
                .create();

        assertThat(result.i).isNotEqualTo("I");
    }
}
