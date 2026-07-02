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
package org.instancio.test.features.selector;

import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.Model;
import org.instancio.exception.InstancioApiException;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.conditions.Conditions;
import org.instancio.test.support.pojo.misc.AbcListHolder;
import org.instancio.test.support.pojo.misc.AbcListHolder.Nested;
import org.instancio.test.support.pojo.misc.StringsAbc;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.all;
import static org.instancio.Select.elementOf;
import static org.instancio.Select.field;
import static org.instancio.Select.root;
import static org.instancio.Select.scope;

@FeatureTag({Feature.ELEMENT_OF_SELECTOR, Feature.SET_MODEL, Feature.MODEL, Feature.EMIT_GENERATOR})
@ExtendWith(InstancioExtension.class)
class ElementOfSelectorEmitTest {

    private static final int SIZE = 3;

    @WithSettings
    private static final Settings SETTINGS = Settings.create()
            .set(Keys.COLLECTION_MIN_SIZE, SIZE)
            .set(Keys.COLLECTION_MAX_SIZE, SIZE);

    @org.junit.jupiter.api.Nested
    class EmitFromInnerModel {

        @Test
        void emitOnElementField_acrossAllElements() {
            final Model<List<StringsAbc>> innerModel = Instancio.ofList(StringsAbc.class)
                    .generate(elementOf(root()).field(StringsAbc::getA),
                            gen -> gen.emit().items("x", "y", "z"))
                    .toModel();

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setModel(field(AbcListHolder::getAbcElements1), innerModel)
                    .create();

            final List<StringsAbc> elements = result.getAbcElements1();
            assertThat(elements).hasSize(SIZE);
            assertThat(elements.get(0).getA()).isEqualTo("x");
            assertThat(elements.get(1).getA()).isEqualTo("y");
            assertThat(elements.get(2).getA()).isEqualTo("z");
        }

        @Test
        void emitWithRecycleAcrossLargerList_isNotConsumedPerElement() {
            final Model<List<StringsAbc>> innerModel = Instancio.ofList(StringsAbc.class)
                    .generate(elementOf(root()).field(StringsAbc::getA),
                            gen -> gen.emit().items("x", "y", "z").whenEmptyRecycle())
                    .toModel();

            final InstancioApi<AbcListHolder> api = Instancio.of(AbcListHolder.class)
                    .size(field(AbcListHolder::getAbcElements1), 7)
                    .setModel(field(AbcListHolder::getAbcElements1), innerModel);

            assertThatThrownBy(api::create)
                    .isInstanceOf(InstancioApiException.class)
                    .hasMessageContaining("not all the items provided via the 'emit()' method have been consumed");
        }

        @Test
        void emitWithIgnoreUnusedAcrossSmallerList() {
            // more items than elements; ignoreUnused() avoids the leftover-items error
            final Model<List<StringsAbc>> innerModel = Instancio.ofList(StringsAbc.class)
                    .generate(elementOf(root()).field(StringsAbc::getA),
                            gen -> gen.emit().items("x", "y", "z", "p", "q").ignoreUnused())
                    .toModel();

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setModel(field(AbcListHolder::getAbcElements1), innerModel)
                    .create();

            final List<String> as = result.getAbcElements1().stream().map(StringsAbc::getA).toList();
            assertThat(as).containsExactly("x", "y", "z");
        }

        @Test
        void emitWhenEmptyEmitNull() {
            final int size = 5;

            final Model<List<StringsAbc>> innerModel = Instancio.ofList(StringsAbc.class)
                    .generate(elementOf(root()).field(StringsAbc::getA),
                            gen -> gen.emit().items("x", "y", "z").whenEmptyEmitNull())
                    .toModel();

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .size(field(AbcListHolder::getAbcElements1), size)
                    .setModel(field(AbcListHolder::getAbcElements1), innerModel)
                    .create();

            final List<String> as = result.getAbcElements1().stream().map(StringsAbc::getA).toList();
            assertThat(as).containsExactly("x", "y", "z", null, null);
        }

        @Test
        void emitWhenEmptyEmitRandom() {
            final int size = 5;

            final Model<List<StringsAbc>> innerModel = Instancio.ofList(StringsAbc.class)
                    .generate(elementOf(root()).field(StringsAbc::getA),
                            gen -> gen.emit().items("x", "y", "z").whenEmptyEmitRandom())
                    .toModel();

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .size(field(AbcListHolder::getAbcElements1), size)
                    .setModel(field(AbcListHolder::getAbcElements1), innerModel)
                    .create();

            final List<String> as = result.getAbcElements1().stream().map(StringsAbc::getA).toList();
            assertThat(as).startsWith("x", "y", "z");
            // remaining values are random (not from the emit sequence)
            assertThat(as.get(3)).is(Conditions.RANDOM_STRING);
            assertThat(as.get(4)).is(Conditions.RANDOM_STRING);
        }

        @Test
        void emitAtSpecificIndices() {
            final Model<List<StringsAbc>> innerModel = Instancio.ofList(StringsAbc.class)
                    .generate(elementOf(root()).at(0, 2).field(StringsAbc::getA),
                            gen -> gen.emit().items("x", "y"))
                    .toModel();

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setModel(field(AbcListHolder::getAbcElements1), innerModel)
                    .create();

            final List<StringsAbc> elements = result.getAbcElements1();
            assertThat(elements.get(0).getA()).isEqualTo("x");
            assertThat(elements.get(1).getA()).is(Conditions.RANDOM_STRING);
            assertThat(elements.get(2).getA()).isEqualTo("y");
        }

        @Test
        void emitOnWholeElementContainer() {
            final StringsAbc x = StringsAbc.builder().a("X").build();
            final StringsAbc y = StringsAbc.builder().a("Y").build();
            final StringsAbc z = StringsAbc.builder().a("Z").build();

            final Model<List<StringsAbc>> innerModel = Instancio.ofList(StringsAbc.class)
                    .generate(elementOf(root()), gen -> gen.emit().items(x, y, z))
                    .toModel();

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setModel(field(AbcListHolder::getAbcElements1), innerModel)
                    .create();

            final List<StringsAbc> elements = result.getAbcElements1();
            assertThat(elements.get(0).getA()).isEqualTo("X");
            assertThat(elements.get(1).getA()).isEqualTo("Y");
            assertThat(elements.get(2).getA()).isEqualTo("Z");
        }

        @RepeatedTest(5)
        void emitModelInsideListModelAtIndex() {
            final Model<StringsAbc> elementModel = Instancio.of(StringsAbc.class)
                    .generate(field(StringsAbc::getA), gen -> gen.emit().items("only"))
                    .toModel();

            final Model<List<StringsAbc>> listModel = Instancio.ofList(StringsAbc.class)
                    .setModel(elementOf(root()).at(1), elementModel)
                    .toModel();

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setModel(field(AbcListHolder::getAbcElements1), listModel)
                    .create();

            final List<StringsAbc> elements = result.getAbcElements1();
            assertThat(elements.get(0).getA()).is(Conditions.RANDOM_STRING);
            assertThat(elements.get(1).getA()).isEqualTo("only");
            assertThat(elements.get(2).getA()).is(Conditions.RANDOM_STRING);
        }
    }

    @org.junit.jupiter.api.Nested
    class EmitFromOuter {

        @Test
        void outerEmitOnElementFieldWithInnerElementModel() {
            final Model<StringsAbc> elementModel = Instancio.of(StringsAbc.class)
                    .set(field(StringsAbc::getB), "_fixed_")
                    .toModel();

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setModel(elementOf(AbcListHolder::getAbcElements1), elementModel)
                    .generate(elementOf(AbcListHolder::getAbcElements1).field(StringsAbc::getA),
                            gen -> gen.emit().items("x", "y", "z"))
                    .create();

            final List<StringsAbc> elements = result.getAbcElements1();
            assertThat(elements.get(0).getA()).isEqualTo("x");
            assertThat(elements.get(1).getA()).isEqualTo("y");
            assertThat(elements.get(2).getA()).isEqualTo("z");
            // inner model still applied to every element
            assertThat(elements).allSatisfy(e -> assertThat(e.getB()).isEqualTo("_fixed_"));
        }

        @Test
        void outerEmitWithScopedInnerModel() {
            final Model<StringsAbc> elementModel = Instancio.of(StringsAbc.class)
                    .set(field(StringsAbc::getC), "_c_")
                    .toModel();

            // 6 scoped elements (two lists of 3); emit feeds them in generation order
            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setModel(elementOf(all(List.class).within(scope(Nested.class))), elementModel)
                    .generate(elementOf(all(List.class).within(scope(Nested.class))).field(StringsAbc::getA),
                            gen -> gen.emit().items("p1", "p2", "p3", "p4", "p5", "p6"))
                    .create();

            final List<StringsAbc> nested1 = result.getNested().getAbcElements1();
            final List<StringsAbc> nested2 = result.getNested().getAbcElements2();

            assertThat(nested1).extracting(StringsAbc::getA).containsExactly("p1", "p2", "p3");
            assertThat(nested2).extracting(StringsAbc::getA).containsExactly("p4", "p5", "p6");

            // the inner model is applied to every scoped element
            assertThat(nested1).allSatisfy(e -> assertThat(e.getC()).isEqualTo("_c_"));
            assertThat(nested2).allSatisfy(e -> assertThat(e.getC()).isEqualTo("_c_"));

            // outer (non-scoped) lists are untouched by both
            assertThat(result.getAbcElements1()).allSatisfy(e ->
                    assertThat(e.getA()).is(Conditions.RANDOM_STRING));
        }
    }

    @org.junit.jupiter.api.Nested
    class StrictMode {

        @Test
        void innerEmitSelectorIndexExceedsExplicitSize() {
            // at(99) implies a min size of 100, which cannot override the explicit size of 3
            final Model<List<StringsAbc>> innerModel = Instancio.ofList(StringsAbc.class)
                    .generate(elementOf(root()).at(99).field(StringsAbc::getA),
                            gen -> gen.emit().items("x").ignoreUnused())
                    .toModel();

            final InstancioApi<AbcListHolder> api = Instancio.of(AbcListHolder.class)
                    .size(field(AbcListHolder::getAbcElements1), 3)
                    .setModel(field(AbcListHolder::getAbcElements1), innerModel);

            assertThatThrownBy(api::create)
                    .isInstanceOf(InstancioApiException.class)
                    .hasMessageContaining("requires at least 100 elements, but an explicit size of 3 was set");
        }
    }
}
