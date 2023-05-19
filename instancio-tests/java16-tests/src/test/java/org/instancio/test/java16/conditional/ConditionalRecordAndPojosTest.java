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
package org.instancio.test.java16.conditional;

import lombok.Data;
import org.instancio.ConditionalAction;
import org.instancio.Gen;
import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.assignment.OnSetFieldError;
import org.instancio.exception.InstancioApiException;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.all;
import static org.instancio.Select.allInts;
import static org.instancio.Select.allStrings;
import static org.instancio.Select.field;
import static org.instancio.Select.scope;
import static org.instancio.Select.valueOf;

/**
 * Adhoc tests using the following data structure,
 * (primitive and String fields not shown):
 *
 * <pre>
 *                  Root
 *               /   |   \
 *    ________Rec  List  PojoA________________
 *   /       /               \       \     \  \
 *  List  PojoB           Optional Array  Map List
 * </pre>
 */
@FeatureTag(Feature.CONDITIONAL)
@ExtendWith(InstancioExtension.class)
class ConditionalRecordAndPojosTest {

    @Test
    void whenRootString_thenRecListSubtype() {
        final Root result = Instancio.of(Root.class)
                .set(field(Root::getString1), "foo")
                .when(valueOf(Root::getString1).is("foo")
                        .generate(field(Rec::list), gen -> gen.collection().subtype(LinkedList.class)))
                .create();

        assertThat(result.rec.list).isExactlyInstanceOf(LinkedList.class);
    }

    @Test
    void whenRootString_thenRecString() {
        final Root result = Instancio.of(Root.class)
                .when(valueOf(Root::getString1).satisfies(s -> true)
                        .generate(field(Rec::string), gen -> gen.string().length(10)))
                .create();

        assertThat(result.rec.string).hasSize(10);
    }

    @ValueSource(strings = {"foo", "bar"})
    @ParameterizedTest
    void whenRootString_thenOneOf(final String initial) {
        final Root result = Instancio.of(Root.class)
                .set(field(Root::getString2), initial)
                .when(valueOf(Root::getString2).is("foo")
                        .generate(all(
                                field(Root::getString1),
                                field(Rec::string)), gen -> gen.oneOf("foo1", "foo2"))
                )
                .when(valueOf(Root::getString2).is("bar")
                        .generate(all(
                                field(Root::getString1),
                                field(Rec::string)), gen -> gen.oneOf("bar1", "bar2"))
                )
                .create();

        if ("foo".equals(result.string2)) {
            assertThat(result.string1).isIn("foo1", "foo2");
            assertThat(result.rec.string).isIn("foo1", "foo2");
        } else if ("bar".equals(result.string2)) {
            assertThat(result.string1).isIn("bar1", "bar2");
            assertThat(result.rec.string).isIn("bar1", "bar2");
        }
    }

    @Test
    void whenPojoAString_thenEmitRecList() {
        final Root result = Instancio.of(Root.class)
                .set(field(PojoA::getString), "A")
                .when(valueOf(PojoA::getString).is("A")
                        .generate(allStrings().within(field(Rec::list).toScope()),
                                gen -> gen.emit().items("foo", "bar")))
                .create();

        assertThat(result.rec.list).contains("foo", "bar");
    }

    @ValueSource(strings = {"foo", "bar", "baz"})
    @ParameterizedTest
    void whenPojoAListContains_thenMapKey(final String listValue) {
        final ConditionalAction conditional1 = valueOf(PojoA::getList)
                .satisfies((List<String> list) -> list.contains("foo"))
                .generate(field(PojoA::getMap), gen -> gen.map().withKeys("foo"));

        final ConditionalAction conditional2 = valueOf(PojoA::getList)
                .satisfies((List<String> list) -> list.contains("bar"))
                .generate(field(PojoA::getMap), gen -> gen.map().withKeys("bar"));

        final Root result = Instancio.of(Root.class)
                .withSettings(Settings.create().set(Keys.ON_SET_FIELD_ERROR, OnSetFieldError.FAIL))
                .generate(field(PojoA::getList), gen -> gen.collection().with(listValue))
                .when(conditional1)
                .when(conditional2)
                .create();

        if (result.pojoA.list.contains("foo")) {
            assertThat(result.pojoA.map).containsKey("foo").doesNotContainKeys("bar", "baz");
        }
        if (result.pojoA.list.contains("bar")) {
            assertThat(result.pojoA.map).containsKey("bar").doesNotContainKeys("foo", "baz");
        }
        if (result.pojoA.list.contains("baz")) {
            assertThat(result.pojoA.map).doesNotContainKeys("foo", "bar");
        }
    }

    @Test
    void whenPojoAString_thenMapKeys() {
        final Root result = Instancio.of(Root.class)
                .when(valueOf(PojoA::getString).satisfies(s -> true)
                        .generate(allStrings().within(field(PojoA::getMap).toScope()),
                                gen -> gen.string().prefix("foo_")))
                .when(valueOf(PojoA::getString).satisfies(s -> true)
                        .generate(allInts().within(field(PojoA::getMap).toScope()),
                                gen -> gen.ints().range(-10, -1)))
                .create();

        assertThat(result.pojoA.map.keySet()).allMatch(k -> k.startsWith("foo_"));
        assertThat(result.pojoA.map.values()).allMatch(v -> v >= -10 && v <= -1);
    }

    @Test
    void whenPojoAString_thenArray() {
        final Root result = Instancio.of(Root.class)
                .when(valueOf(PojoA::getString).satisfies(s -> true)
                        .generate(allStrings().within(field(PojoA::getArray).toScope()),
                                gen -> gen.string().prefix("foo_")))
                .create();

        assertThat(result.pojoA.array).allMatch(e -> e.startsWith("foo_"));
    }

    @Test
    void whenPojoAString_thenNullRecord() {
        final Root result = Instancio.of(Root.class)
                .set(field(PojoA::getString), "A")
                .when(valueOf(PojoA::getString)
                        .is("A")
                        .set(all(Rec.class), null))
                .create();

        assertThat(result.rec).isNull();
    }

    @Test
    void whenPojoBString_thenNullRecord() {
        final InstancioApi<Root> api = Instancio.of(Root.class)
                .set(field(PojoB::getString), "B")
                .when(valueOf(PojoB::getString)
                        .is("B")
                        .set(all(Rec.class), null));

        assertThatThrownBy(api::create)
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("Circular conditional detected");
    }

    @Test
    void whenPojoBString_thenArray() {
        final Root result = Instancio.of(Root.class)
                .set(field(PojoB::getString), "B")
                .when(valueOf(PojoB::getString).is("B")
                        .generate(field(PojoA::getArray), gen -> gen.array().length(2)))
                .create();

        assertThat(result.pojoA.array)
                .hasSize(2)
                .doesNotContainNull();
    }

    @RepeatedTest(Constants.SAMPLE_SIZE_D)
    void whenOptionalIsEmpty_thenSetPojoBToNull() {
        final Root result = Instancio.of(Root.class)
                .generate(field(PojoA::getOptional), gen -> gen.optional().allowEmpty())
                .when(valueOf(PojoA::getOptional)
                        .satisfies((Optional<String> o) -> o.isEmpty())
                        .set(field(Rec::pojoB), null))
                .create();

        if (result.pojoA.optional.isPresent()) {
            assertThat(result.rec.pojoB).isNotNull();
        } else {
            assertThat(result.rec.pojoB).isNull();
        }

    }

    @RepeatedTest(Constants.SAMPLE_SIZE_D)
    void whenPojoAListContains_thenRecList() {
        final String element = Gen.oneOf("foo", "xyz").get();

        final Root result = Instancio.of(Root.class)
                .generate(field(PojoA::getList), gen -> gen.collection().with(element))
                .when(valueOf(PojoA::getList)
                        .satisfies((List<String> l) -> l.contains("foo"))
                        .set(allStrings().within(scope(Rec.class)), "bar"))
                .create();

        if (result.pojoA.list.contains("foo")) {
            assertThat(result.rec.list).allMatch(s -> s.equals("bar"));
        } else {
            assertThat(result.rec.list)
                    .as("Random values (are uppercase by default)")
                    .noneMatch(s -> s.equals("bar"));
        }
    }

    @Test
    void whenPojoAString_thenPojoBString() {
        final Root result = Instancio.of(Root.class)
                .set(field(PojoA::getString), "A")
                .when(valueOf(PojoA::getString).is("A").set(field(PojoB::getString), "B"))
                .create();

        assertThat(result.pojoA.string).isEqualTo("A");
        assertThat(result.rec.pojoB.string).isEqualTo("B");
    }

    @Test
    void whenPojoBString_thenRecString() {
        final Root result = Instancio.of(Root.class)
                .set(field(PojoB::getString), "B")
                .when(valueOf(PojoB::getString).is("B").set(field(Rec::string), "R"))
                .create();

        assertThat(result.rec.pojoB.string).isEqualTo("B");
        assertThat(result.rec.string).isEqualTo("R");
    }


    private static @Data class Root {
        private Rec rec;
        private String string1;
        private int i;
        private List<String> list;
        private PojoA pojoA;
        private String string2;
    }

    private static @Data class PojoA {
        private Optional<String> optional;
        private String[] array;
        private Map<String, Integer> map;
        private List<String> list;
        private String string;
    }

    private record Rec(List<String> list, String string, PojoB pojoB) {}

    private static @Data class PojoB {
        private String string;
    }
}
