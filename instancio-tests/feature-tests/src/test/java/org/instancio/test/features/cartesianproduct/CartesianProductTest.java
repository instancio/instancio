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
package org.instancio.test.features.cartesianproduct;

import lombok.Data;
import org.instancio.InstancioCartesianProductApi;
import org.instancio.Instancio;
import org.instancio.Selector;
import org.instancio.TypeToken;
import org.instancio.exception.InstancioApiException;
import org.instancio.exception.UnusedSelectorException;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.basic.IntegerHolder;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.pojo.generics.basic.Pair;
import org.instancio.test.support.pojo.generics.basic.Triplet;
import org.instancio.test.support.pojo.misc.StringsAbc;
import org.instancio.test.support.pojo.misc.StringsDef;
import org.instancio.test.support.pojo.misc.StringsGhi;
import org.instancio.test.support.pojo.person.Gender;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.pojo.person.PhoneType;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.all;
import static org.instancio.Select.allBooleans;
import static org.instancio.Select.allInts;
import static org.instancio.Select.field;

@FeatureTag(Feature.CARTESIAN_PRODUCT)
@ExtendWith(InstancioExtension.class)
class CartesianProductTest {

    @Test
    @DisplayName("When no values are specified, should return a single object")
    void withNoValues() {
        final List<StringHolder> results = Instancio.ofCartesianProduct(StringHolder.class)
                .create();

        assertThat(results).singleElement().hasNoNullFieldsOrProperties();
    }

    @Test
    void withOneSetOfValuesContainingOneElement() {
        final Integer[] expectedInts = {1};

        final List<Triplet<PhoneType, Integer, Boolean>> results = Instancio.ofCartesianProduct(
                        new TypeToken<Triplet<PhoneType, Integer, Boolean>>() {})
                .with(allInts(), expectedInts)
                .create();

        assertThat(results)
                .allSatisfy(e -> assertThat(e).hasNoNullFieldsOrProperties())
                .extracting(Triplet::getMid)
                .containsExactly(expectedInts);
    }

    @Test
    void withOneSetOfValues() {
        final Integer[] expectedInts = {1, 2, 3, 4, 5};

        final List<Triplet<PhoneType, Integer, Boolean>> results = Instancio.ofCartesianProduct(
                        new TypeToken<Triplet<PhoneType, Integer, Boolean>>() {})
                .with(all(Integer.class), expectedInts)
                .create();

        assertThat(results)
                .allSatisfy(e -> assertThat(e).hasNoNullFieldsOrProperties())
                .extracting(Triplet::getMid)
                .containsExactly(expectedInts);
    }

    @Test
    void withThreeSetsOfValues() {
        final List<Triplet<PhoneType, Integer, Boolean>> results = Instancio.ofCartesianProduct(
                        new TypeToken<Triplet<PhoneType, Integer, Boolean>>() {})
                .with(all(PhoneType.class), PhoneType.CELL, PhoneType.HOME, PhoneType.WORK)
                .with(allInts(), 1, 2)
                .with(allBooleans(), true, false)
                .create();

        assertThat(results)
                .extracting(Triplet::getLeft)
                .containsExactly(
                        PhoneType.CELL, PhoneType.CELL, PhoneType.CELL, PhoneType.CELL,
                        PhoneType.HOME, PhoneType.HOME, PhoneType.HOME, PhoneType.HOME,
                        PhoneType.WORK, PhoneType.WORK, PhoneType.WORK, PhoneType.WORK);

        assertThat(results)
                .extracting(Triplet::getMid)
                .containsExactly(1, 1, 2, 2, 1, 1, 2, 2, 1, 1, 2, 2);

        assertThat(results)
                .extracting(Triplet::getRight)
                .containsExactly(true, false, true, false, true, false, true, false, true, false, true, false);
    }

    @Test
    void withValuesAcrossDifferentClasses() {
        final List<StringsAbc> results = Instancio.ofCartesianProduct(StringsAbc.class)
                .with(field(StringsAbc::getA), "a1", "a2")
                .with(field(StringsDef::getE), "d1", "d2", "d3")
                .with(field(StringsGhi::getI), "i1", "i2")
                .create();

        assertThat(results)
                .hasSize(2 * 3 * 2)
                .allSatisfy(obj -> assertThat(obj).hasNoNullFieldsOrProperties())
                .extracting(obj -> obj.getA() + "-" + obj.getDef().getE() + "-" + obj.getDef().getGhi().getI())
                .containsExactly(
                        "a1-d1-i1",
                        "a1-d1-i2",
                        "a1-d2-i1",
                        "a1-d2-i2",
                        "a1-d3-i1",
                        "a1-d3-i2",
                        "a2-d1-i1",
                        "a2-d1-i2",
                        "a2-d2-i1",
                        "a2-d2-i2",
                        "a2-d3-i1",
                        "a2-d3-i2");
    }

    @Test
    void withCustomisedValues() {
        final String expectedName = "foo";

        final List<Person> results = Instancio.ofCartesianProduct(Person.class)
                .with(field(Person::getGender), Gender.values())
                .with(field(Person::getAge), 30, 40)
                // custom values
                .set(field(Person::getName), expectedName)
                .generate(field(Person::getDate), gen -> gen.temporal().date().past())
                .create();

        assertThat(results)
                .hasSize(6) // 3 genders x 2 age values
                .allSatisfy(p -> {
                    assertThat(p.getName()).isEqualTo(expectedName);
                    assertThat(p.getDate()).isInThePast();
                });

        assertThat(results)
                .extracting(Person::getGender)
                .containsExactly(
                        Gender.MALE, Gender.MALE,
                        Gender.FEMALE, Gender.FEMALE,
                        Gender.OTHER, Gender.OTHER);

        assertThat(results)
                .extracting(Person::getAge)
                .containsExactly(30, 40, 30, 40, 30, 40);
    }

    @Test
    void overwriteCartesianValuesUsingSet() {
        final List<IntegerHolder> results = Instancio.ofCartesianProduct(IntegerHolder.class)
                .with(field(IntegerHolder::getPrimitive), 1, 2)
                .with(field(IntegerHolder::getWrapper), 3, 4)
                .set(field(IntegerHolder::getPrimitive), 8)
                .create();

        assertThat(results).extracting(IntegerHolder::getPrimitive)
                .as("Should overwrites [1, 1, 2, 2]")
                .containsExactly(8, 8, 8, 8);

        assertThat(results).extracting(IntegerHolder::getWrapper)
                .containsExactly(3, 4, 3, 4);
    }

    /**
     * Ideally, duplicate selectors would overwrite previous values
     * so that this test would produce:
     *
     * <pre>
     *   [5, 7]
     *   [5, 8]
     *   [6, 7]
     *   [6, 8]
     * </pre>
     *
     * <p>This is not possible since method references are never equal.
     * The Cartesian product implementation works with "unprocessed" selectors
     * (i.e. doesn't use {@link org.instancio.internal.selectors.SelectorProcessor}),
     * which means that the {@link java.lang.reflect.Field} has not been resolved yet.
     */
    @Test
    @FeatureTag(Feature.UNSUPPORTED)
    void withDuplicateSelectors() {
        final List<IntegerHolder> results = Instancio.ofCartesianProduct(IntegerHolder.class)
                .with(field(IntegerHolder::getPrimitive), 1, 2)
                .with(field(IntegerHolder::getWrapper), 3, 4)
                // duplicate selectors
                .with(field(IntegerHolder::getPrimitive), 5, 6)
                .with(field(IntegerHolder::getWrapper), 7, 8)
                .create();

        assertThat(results).hasSize(16);

        assertThat(results).extracting(IntegerHolder::getPrimitive).containsExactly(
                5, 5, 6, 6, 5, 5, 6, 6, 5, 5, 6, 6, 5, 5, 6, 6);

        assertThat(results).extracting(IntegerHolder::getWrapper).containsExactly(
                7, 8, 7, 8, 7, 8, 7, 8, 7, 8, 7, 8, 7, 8, 7, 8);
    }

    @Test
    void overflow() {
        final Integer[] range = IntStream.range(1, 50_000).boxed().toArray(Integer[]::new);

        final InstancioCartesianProductApi<IntegerHolder> api = Instancio.ofCartesianProduct(IntegerHolder.class)
                .with(field(IntegerHolder::getPrimitive), range)
                .with(field(IntegerHolder::getWrapper), range);

        assertThatThrownBy(api::create)
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("Cartesian product too large; must have size at most Integer.MAX_VALUE");
    }

    @Test
    void withEmit() {
        final List<IntegerHolder> results = Instancio.ofCartesianProduct(IntegerHolder.class)
                .with(field(IntegerHolder::getPrimitive), 1, 2, 3)
                .generate(field(IntegerHolder::getWrapper), gen -> gen.emit().items(5, 6, 7))
                .create();

        assertThat(results).extracting(IntegerHolder::getPrimitive).containsExactly(1, 2, 3);
        assertThat(results).extracting(IntegerHolder::getWrapper).containsExactly(5, 6, 7);
    }

    @Test
    void withIntSeq() {
        final List<IntegerHolder> results = Instancio.ofCartesianProduct(IntegerHolder.class)
                .with(field(IntegerHolder::getPrimitive), 1, 2, 3)
                .generate(field(IntegerHolder::getWrapper), gen -> gen.intSeq().start(5))
                .create();

        assertThat(results).extracting(IntegerHolder::getPrimitive).containsExactly(1, 2, 3);
        assertThat(results).extracting(IntegerHolder::getWrapper).containsExactly(5, 6, 7);
    }

    /**
     * In lenient mode, the unused selector error should be suppressed.
     * Since the selector does not match anything, it does not assign values.
     * However, the resulting Cartesian product list size still takes
     * into account the input list for the invalid selector.
     * For this reason, this test returns 4 results, instead of 2.
     */
    @Test
    void lenient() {
        @Data
        class SingleValueHolder {
            int value;
        }

        final List<SingleValueHolder> results = Instancio.ofCartesianProduct(SingleValueHolder.class)
                .with(field(SingleValueHolder::getValue), 1, 2)
                .with(field(Person::getName), "foo", "bar") // invalid selector
                .lenient()
                .create();

        // Ideally, it would return [1, 2]
        assertThat(results)
                .extracting(SingleValueHolder::getValue)
                .containsExactly(1, 1, 2, 2);
    }

    /**
     * Although {@code ofCartesianProduct()} returns a {@code List},
     * therefore the root object (at depth 0) is the List:
     *
     * <pre>
     *   Depth 0: List
     *   Depth 1: IntegerHolder
     *   Depth 2: IntegerHolder::getPrimitive
     * </pre>
     */
    @Nested
    class MaxDepthTest {

        @Test
        void fieldsShouldBeAtDepth2() {
            final List<IntegerHolder> results = Instancio.ofCartesianProduct(IntegerHolder.class)
                    .withMaxDepth(2)
                    .with(field(IntegerHolder::getPrimitive), 1, 2)
                    .create();

            assertThat(results)
                    .extracting(IntegerHolder::getPrimitive)
                    .containsExactly(1, 2);
        }

        @Test
        void maxDepthCausingUnusedSelectorError() {
            final InstancioCartesianProductApi<IntegerHolder> api = Instancio.ofCartesianProduct(IntegerHolder.class)
                    .withMaxDepth(1)
                    .with(field(IntegerHolder::getPrimitive), 1, 2);

            assertThatThrownBy(api::create)
                    .isExactlyInstanceOf(UnusedSelectorException.class)
                    .hasMessageContaining("field(IntegerHolder, \"primitive\")");
        }
    }

    @Test
    void shouldThrowExceptionIfGenericTypeIsPassed() {
        assertThatThrownBy(() -> Instancio.ofCartesianProduct(Pair.class))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("invalid usage of ofCartesianProduct() method");
    }

    @Nested
    class NullTest {
        @Test
        void nullArray() {
            final InstancioCartesianProductApi<Pair<Integer, Boolean>> api = Instancio.ofCartesianProduct(new TypeToken<Pair<Integer, Boolean>>() {});
            final Selector selector = allInts();

            assertThatThrownBy(() -> api.with(selector, (Object[]) null))
                    .isExactlyInstanceOf(InstancioApiException.class)
                    .hasMessageContaining("with() requires a non-empty array, but got: null");
        }

        @Test
        void emptyArray() {
            final InstancioCartesianProductApi<Pair<Integer, Boolean>> api = Instancio.ofCartesianProduct(new TypeToken<Pair<Integer, Boolean>>() {});
            final Selector selector = allInts();

            assertThatThrownBy(() -> api.with(selector, new Object[0]))
                    .isExactlyInstanceOf(InstancioApiException.class)
                    .hasMessageContaining("with() requires a non-empty array, but got: []");
        }

        @Test
        void withSingleNullObject() {
            final List<Pair<Integer, Boolean>> results = Instancio.ofCartesianProduct(new TypeToken<Pair<Integer, Boolean>>() {})
                    .with(allInts(), (Object) null)
                    .create();

            assertThat(results)
                    .hasSize(1)
                    .allSatisfy(result -> {
                        assertThat(result.getLeft()).isNull();
                        assertThat(result.getRight()).isNotNull();
                    });
        }

        @Test
        void withMultipleNullObjects() {
            final List<Pair<Integer, Boolean>> results = Instancio.ofCartesianProduct(new TypeToken<Pair<Integer, Boolean>>() {})
                    .with(allInts(), null, null)
                    .with(allBooleans(), null, null)
                    .create();

            assertThat(results)
                    .hasSize(4)
                    .allSatisfy(result -> {
                        assertThat(result.getLeft()).isNull();
                        assertThat(result.getRight()).isNull();
                    });
        }
    }
}
