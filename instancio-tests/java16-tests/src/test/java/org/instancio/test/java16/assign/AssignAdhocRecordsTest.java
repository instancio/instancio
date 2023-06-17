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
package org.instancio.test.java16.assign;

import lombok.Data;
import org.assertj.core.api.ListAssert;
import org.assertj.core.api.ObjectArrayAssert;
import org.instancio.Assign;
import org.instancio.Instancio;
import org.instancio.TypeToken;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.basic.PrimitiveFields;
import org.instancio.test.support.pojo.basic.SupportedNumericTypes;
import org.instancio.test.support.pojo.basic.SupportedTemporalTypes;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.allInts;
import static org.instancio.Select.field;

@FeatureTag(Feature.ASSIGN)
@ExtendWith(InstancioExtension.class)
class AssignAdhocRecordsTest {

    @Test
    void listOfPairsWhereLeftDependsOnRight() {
        enum E {ONE, TWO, THREE, FOUR}
        record PairRecord(E left, int right) {}

        final List<PairRecord> results = Instancio.ofList(PairRecord.class)
                .size(100)
                .assign(Assign.given(all(E.class)).is(E.ONE).set(allInts(), -1))
                .assign(Assign.given(all(E.class)).is(E.TWO).set(allInts(), -2))
                .assign(Assign.given(all(E.class)).is(E.THREE).set(allInts(), -3))
                .create();

        assertThat(results).hasSize(100).allSatisfy(result -> {
            final E left = result.left;
            assertThat(left).isNotNull();

            switch (left) {
                case ONE -> assertThat(result.right).isEqualTo(-1);
                case TWO -> assertThat(result.right).isEqualTo(-2);
                case THREE -> assertThat(result.right).isEqualTo(-3);
                case FOUR -> assertThat(result.right).as("random value").isPositive();
            }
        });
    }

    @Test
    void mapValueDependsOnMapKey() {
        record R(int val) {}
        enum E {ONE, TWO, THREE}

        final Map<E, R> result = Instancio.ofMap(E.class, R.class)
                .size(3)
                .assign(Assign.given(all(E.class)).is(E.ONE).set(allInts(), 1))
                .assign(Assign.given(all(E.class)).is(E.TWO).set(allInts(), 2))
                .assign(Assign.given(all(E.class)).is(E.THREE).set(allInts(), 3))
                .create();

        assertThat(result)
                .hasSize(3)
                .hasEntrySatisfying(E.ONE, v -> assertThat(v.val).isEqualTo(1))
                .hasEntrySatisfying(E.TWO, v -> assertThat(v.val).isEqualTo(2))
                .hasEntrySatisfying(E.THREE, v -> assertThat(v.val).isEqualTo(3));
    }

    @Test
    void withRecordAsArrayElementAndRecordGenerator() {
        record B(int val) {}
        record A(B[] array, int val) {}

        final List<A> results = Instancio.ofList(A.class)
                .size(100)
                .generate(field(A::array), gen -> gen.array().length(2))
                .generate(field(A::val), gen -> gen.ints().range(1, 3))
                .assign(Assign.given(A::val).is(1).supply(all(B.class), r -> new B(r.intRange(-199, -100))))
                .assign(Assign.given(A::val).is(2).supply(all(B.class), r -> new B(r.intRange(-299, -200))))
                .create();

        assertThat(results).isNotEmpty().allSatisfy(a -> {
            final ObjectArrayAssert<B> arrayAssert = assertThat(a.array)
                    .as("result: " + a);

            assertThat(a.val).isBetween(1, 3);

            if (a.val == 1) {
                arrayAssert.allSatisfy(b -> assertThat(b.val).isBetween(-199, -100));
            } else if (a.val == 2) {
                arrayAssert.allSatisfy(b -> assertThat(b.val).isBetween(-299, -200));
            } else {
                arrayAssert.allSatisfy(b -> assertThat(b.val).isPositive());
            }
        });
    }

    @Test
    void withRecordAsListElementAndRecordGenerator() {
        record A(String string) {}
        record B(List<A> list, int val) {}

        List<B> results = Instancio.ofList(B.class)
                .size(100)
                .generate(field(B::val), gen -> gen.ints().range(1, 3))
                .generate(field(B::list), gen -> gen.collection().size(2))
                .assign(Assign.given(B::val).is(1).supply(all(A.class), r -> new A("bar_" + r.intRange(100, 199))))
                .assign(Assign.given(B::val).is(2).supply(all(A.class), r -> new A("bar_" + r.intRange(200, 299))))
                .create();

        assertThat(results).isNotEmpty().allSatisfy(result -> {
            final ListAssert<A> listAssert = assertThat(result.list)
                    .as("result: " + result);

            if (result.val == 1) {
                listAssert.allSatisfy(a -> assertThat(a.string).startsWith("bar_1"));
            } else if (result.val == 2) {
                listAssert.allSatisfy(a -> assertThat(a.string).startsWith("bar_2"));
            } else {
                listAssert.allSatisfy(a -> assertThat(a.string).doesNotStartWith("bar_"));
            }
        });
    }

    @RepeatedTest(Constants.SAMPLE_SIZE_D)
    void multipleGenerateCallsLinkedViaConditionals() {
        // @formatter:off
        // Irrelevant filler classes to make the POJO more complicated:
        record F1 (String s, PrimitiveFields primitives) {}
        class F2 {F1 f1; String s; SupportedNumericTypes numericTypes;}
        class F3 {F2 f2; String s; SupportedTemporalTypes temporalTypes;}
        class F4 {String s;}

        // Classes under test ('String val' are the fields of interest):
        record A(String val) {}
        @Data class B {F1 f1; F2 f2; String val; F3 f3; F4 f4;}
        @Data class BHolder {F1 f1; F2 f2; B b; F3 f3; F4 f4;}
        @Data class C {F1 f1; F2 f2; A a; F3 f3; BHolder bHolder; F4 f4;}
        @Data class Root {F1 f1; String val; F2 f2; F3 f3; C c; F4 f4;}
        // @formatter:on

        final List<List<Root>> results = Instancio.of(new TypeToken<List<List<Root>>>() {})
                // 1
                .generate(field(B::getVal), gen -> gen.oneOf("B", "other"))
                // 3
                .assign(Assign.given(A::val).is("A").set(field(Root::getVal), "R"))
                // 2
                .assign(Assign.given(B::getVal).is("B").generate(field(A::val), gen -> gen.oneOf("A", "other")))
                .create();

        //noinspection CodeBlock2Expr
        assertThat(results).isNotEmpty().allSatisfy(innerList -> {

            assertThat(innerList).isNotEmpty().allSatisfy(result -> {
                final String rVal = result.val;
                final String aVal = result.c.a.val;
                final String bVal = result.c.bHolder.b.val;

                assertThat(bVal).isIn("B", "other");

                if (bVal.equals("B")) { // 1

                    assertThat(aVal).isIn("A", "other");

                    if (aVal.equals("A")) { // 2
                        assertThat(rVal).isEqualTo("R"); // 3
                    } else {
                        assertThat(rVal).isNotEqualTo("R");
                    }

                } else {
                    assertThat(aVal).isNotIn("A", "other");
                }
            });
        });
    }

    @RepeatedTest(Constants.SAMPLE_SIZE_D)
    void multipleGenerateCallsLinkedViaConditionals_NestedList() {
        // @formatter:off
        record RecA(String string) {}
        @Data class PojoB { String string; }
        @Data class PojoC { String string; RecA pojoA; PojoB pojoB; }
        @Data class Root { List<PojoC> list; }
        // @formatter:on

        final List<Root> results = Instancio.ofList(Root.class)
                .size(1)
                .generate(field(Root::getList), gen -> gen.collection().size(2))
                // 1
                .generate(field(PojoB::getString), gen -> gen.oneOf("B", "other-B"))
                // 3
                .assign(Assign.given(RecA::string).is("A").set(field(PojoC::getString), "C"))
                // 2
                .assign(Assign.given(PojoB::getString).is("B").generate(field(RecA::string), gen -> gen.oneOf("A", "other-A")))
                .create();

        assertThat(results).isNotEmpty();

        results.forEach(listOfD -> {

            final List<PojoC> list = listOfD.getList();

            assertThat(list).isNotEmpty();

            list.forEach((PojoC c) -> {
                final String cVal = c.string;
                final String aVal = c.pojoA.string;
                final String bVal = c.pojoB.string;

                assertThat(bVal).isIn("B", "other-B");

                if (bVal.equals("B")) { // 1

                    assertThat(aVal).isIn("A", "other-A");

                    if (aVal.equals("A")) { // 2
                        assertThat(cVal).isEqualTo("C"); // 3
                    } else {
                        assertThat(cVal).isNotEqualTo("C");
                    }

                } else {
                    assertThat(aVal).as("c = %s", c).isNotIn("A", "other-A");
                }
            });
        });
    }

    @RepeatedTest(Constants.SAMPLE_SIZE_D)
    void multipleGenerateCallsLinkedViaConditionals_NestedMap() {
        // @formatter:off
        record RecA(String string) {}
        @Data class PojoB { String string; }
        @Data class PojoC { String string; RecA pojoA; PojoB pojoB; }
        @Data class Root { Map<UUID, PojoC> map; }
        // @formatter:on

        final List<Root> results = Instancio.ofList(Root.class)
                .size(1)
                .generate(field(Root::getMap), gen -> gen.map().size(2))
                // 1
                .generate(field(PojoB::getString), gen -> gen.oneOf("B", "other-B"))
                // 3
                .assign(Assign.given(RecA::string).is("A").set(field(PojoC::getString), "C"))
                // 2
                .assign(Assign.given(PojoB::getString).is("B").generate(field(RecA::string), gen -> gen.oneOf("A", "other-A")))
                .create();

        assertThat(results).isNotEmpty();

        results.forEach(listOfD -> {

            final Collection<PojoC> collection = listOfD.getMap().values();

            assertThat(collection).isNotEmpty();

            collection.forEach((PojoC c) -> {
                final String cVal = c.string;
                final String aVal = c.pojoA.string;
                final String bVal = c.pojoB.string;

                assertThat(bVal).isIn("B", "other-B");

                if (bVal.equals("B")) { // 1

                    assertThat(aVal).isIn("A", "other-A");

                    if (aVal.equals("A")) { // 2
                        assertThat(cVal).isEqualTo("C"); // 3
                    } else {
                        assertThat(cVal).isNotEqualTo("C");
                    }

                } else {
                    assertThat(aVal).as("c = %s", c).isNotIn("A", "other-A");
                }
            });
        });
    }
}
