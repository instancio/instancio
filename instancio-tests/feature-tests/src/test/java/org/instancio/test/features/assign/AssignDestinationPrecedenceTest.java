/*
 * Copyright 2022-2026 the original author or authors.
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
import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.misc.StringsAbc;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Assign.given;
import static org.instancio.Select.elementOf;
import static org.instancio.Select.field;
import static org.instancio.Select.fields;

/**
 * When destination selectors of multiple assignments match the same target,
 * the usual selector precedence rules apply (as with {@code set()}),
 * regardless of the order in which the assignments are specified.
 * If the destination selectors have the same precedence, the last one wins.
 */
@FeatureTag({Feature.ASSIGN, Feature.SELECTOR_PRECEDENCE})
@ExtendWith(InstancioExtension.class)
class AssignDestinationPrecedenceTest {

    private record Item(String name) {}

    private record Order(List<Item> items) {}

    @Test
    void fieldSelectorFirst() {
        final StringsAbc result = Instancio.of(StringsAbc.class)
                .assign(Assign.valueOf(field(StringsAbc::getB)).set("field"))
                .assign(Assign.valueOf(fields().named("b")).set("fields"))
                .create();

        assertThat(result.getB()).isEqualTo("field");
    }

    @Test
    void fieldSelectorLast() {
        final StringsAbc result = Instancio.of(StringsAbc.class)
                .assign(Assign.valueOf(fields().named("b")).set("fields"))
                .assign(Assign.valueOf(field(StringsAbc::getB)).set("field"))
                .create();

        assertThat(result.getB()).isEqualTo("field");
    }

    @Test
    void valueOfToWithFieldSelectorFirst() {
        final StringsAbc result = Instancio.of(StringsAbc.class)
                .assign(Assign.valueOf(StringsAbc::getA).to(field(StringsAbc::getB)))
                .assign(Assign.valueOf(StringsAbc::getC).to(fields().named("b")))
                .create();

        assertThat(result.getB()).isEqualTo(result.getA());
    }

    @Test
    void elementOfSelectorFirst() {
        final Order result = Instancio.of(Order.class)
                .assign(Assign.valueOf(elementOf(Order::items).at(0).field(Item::name)).set("first"))
                .assign(Assign.valueOf(field(Item::name)).set("all"))
                .create();

        assertThat(result.items().get(0).name()).isEqualTo("first");
        assertThat(result.items().subList(1, result.items().size()))
                .extracting(Item::name)
                .containsOnly("all");
    }

    @Test
    void elementOfSelectorLast() {
        final Order result = Instancio.of(Order.class)
                .assign(Assign.valueOf(field(Item::name)).set("all"))
                .assign(Assign.valueOf(elementOf(Order::items).at(0).field(Item::name)).set("first"))
                .create();

        assertThat(result.items().get(0).name()).isEqualTo("first");
        assertThat(result.items().subList(1, result.items().size()))
                .extracting(Item::name)
                .containsOnly("all");
    }

    @Test
    void sameSelectorPrecedenceLastWins() {
        final StringsAbc result = Instancio.of(StringsAbc.class)
                .assign(Assign.valueOf(fields().named("b")).set("first"))
                .assign(Assign.valueOf(fields().named("b").declaredIn(StringsAbc.class)).set("last"))
                .create();

        assertThat(result.getB()).isEqualTo("last");
    }

    @Test
    void conditionalAssignmentWithFieldSelectorShouldOverrideDefault() {
        final List<StringsAbc> results = Instancio.ofList(StringsAbc.class)
                .size(50)
                .generate(field(StringsAbc::getA), gen -> gen.oneOf("x", "y"))
                .assign(given(field(StringsAbc::getA)).satisfies(a -> true).set(fields().named("b"), "default"))
                .assign(given(field(StringsAbc::getA)).is("x").set(field(StringsAbc::getB), "x-value"))
                .create();

        assertThat(results).extracting(StringsAbc::getA).contains("x", "y");
        assertThat(results).allSatisfy(result ->
                assertThat(result.getB()).isEqualTo("x".equals(result.getA()) ? "x-value" : "default"));
    }

    /**
     * If the condition of an assignment with a higher precedence destination
     * is not satisfied, an assignment with a lower precedence destination applies.
     */
    @Test
    void unsatisfiedConditionShouldFallThroughToLowerPrecedenceDestination() {
        final List<StringsAbc> results = Instancio.ofList(StringsAbc.class)
                .size(50)
                .generate(field(StringsAbc::getA), gen -> gen.oneOf("x", "y"))
                .assign(given(field(StringsAbc::getA)).is("x").set(field(StringsAbc::getB), "x-value"))
                .assign(given(field(StringsAbc::getA)).is("y").set(fields().named("b"), "y-value"))
                .create();

        assertThat(results).extracting(StringsAbc::getA).contains("x", "y");
        assertThat(results).allSatisfy(result ->
                assertThat(result.getB()).isEqualTo("x".equals(result.getA()) ? "x-value" : "y-value"));
    }

    @Test
    void overrideModelAssignmentUsingFieldSelector() {
        final Model<StringsAbc> model = Instancio.of(StringsAbc.class)
                .assign(Assign.valueOf(fields().named("b")).set("model"))
                .toModel();

        final StringsAbc result = Instancio.of(model)
                .assign(Assign.valueOf(field(StringsAbc::getB)).set("override"))
                .create();

        assertThat(result.getB()).isEqualTo("override");
    }

    @Test
    void lowerPrecedenceSelectorShouldNotOverrideModelAssignment() {
        final Model<StringsAbc> model = Instancio.of(StringsAbc.class)
                .assign(Assign.valueOf(field(StringsAbc::getB)).set("model"))
                .toModel();

        final StringsAbc result = Instancio.of(model)
                .assign(Assign.valueOf(fields().named("b")).set("override"))
                .create();

        assertThat(result.getB()).isEqualTo("model");
    }

    /**
     * Assignments take precedence over other API methods,
     * regardless of selector precedence.
     */
    @Test
    void assignmentShouldHaveHigherPrecedenceThanSetWithHigherPrecedenceSelector() {
        final StringsAbc result = Instancio.of(StringsAbc.class)
                .set(field(StringsAbc::getB), "set")
                .assign(Assign.valueOf(fields().named("b")).set("assign"))
                .create();

        assertThat(result.getB()).isEqualTo("assign");
    }

    @Test
    void assignmentShouldHaveHigherPrecedenceThanGenerateWithHigherPrecedenceSelector() {
        final StringsAbc result = Instancio.of(StringsAbc.class)
                .generate(field(StringsAbc::getB), gen -> gen.oneOf("generate"))
                .assign(Assign.valueOf(fields().named("b")).set("assign"))
                .create();

        assertThat(result.getB()).isEqualTo("assign");
    }
}
