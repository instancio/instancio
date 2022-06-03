/*
 * Copyright 2022 the original author or authors.
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
package org.instancio.test.features.mode;

import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.exception.UnusedSelectorException;
import org.instancio.generators.Generators;
import org.instancio.test.support.asserts.UnusedSelectorsAssert.ApiMethod;
import org.instancio.test.support.pojo.generics.foobarbaz.Bar;
import org.instancio.test.support.pojo.generics.foobarbaz.Baz;
import org.instancio.test.support.pojo.generics.foobarbaz.Foo;
import org.instancio.test.support.pojo.person.Address;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.pojo.person.Person_;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.util.Sonar;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.LinkedList;
import java.util.SortedSet;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.all;
import static org.instancio.Select.allInts;
import static org.instancio.Select.allStrings;
import static org.instancio.Select.field;
import static org.instancio.test.support.asserts.UnusedSelectorsAssert.assertUnusedSelectorMessage;
import static org.junit.jupiter.api.Assertions.fail;

@FeatureTag({
        Feature.MODE,
        Feature.GENERATE,
        Feature.SET,
        Feature.ON_COMPLETE,
        Feature.SUBTYPE
})
class StrictModeTest {

    @Nested
    class DuplicateSelectorsTest {

        @Test
        @DisplayName("Since field selector takes precedence over class selector, the latter remains unused")
        void sameTargetUsingFieldAndClassSelectors() {
            final InstancioApi<Person> api = Instancio.of(Person.class)
                    .supply(field("address"), () -> null)
                    .supply(all(Address.class), Address::new);

            assertThatThrownBy(api::create)
                    .isExactlyInstanceOf(UnusedSelectorException.class)
                    .satisfies(ex -> assertUnusedSelectorMessage(ex.getMessage())
                            .hasUnusedSelectorCount(1)
                            .containsOnly(ApiMethod.GENERATE_SET_SUPPLY)
                            .containsUnusedSelector(Address.class));
        }
    }

    @Nested
    class UnusedSelectorsPrimitiveAndWrappersTest {
        class IntHolder {
            @SuppressWarnings("unused")
            private int value;
        }

        @Test
        @DisplayName("No error with allInts() even though class has no Integer field")
        @SuppressWarnings(Sonar.ADD_ASSERTION)
        void allIntsWithClassContainingOnlyPrimitiveUsed() {
            Instancio.of(IntHolder.class).ignore(allInts()).create();
            Instancio.of(IntHolder.class).ignore(all(int.class)).create();
        }

        @Test
        @DisplayName("Error selecting Integer when only primitive int is present")
        void allIntegersWithClassContainingOnlyPrimitiveUsed() {
            final InstancioApi<IntHolder> api = Instancio.of(IntHolder.class)
                    .ignore(all(Integer.class));

            assertThatThrownBy(api::create)
                    .isInstanceOf(UnusedSelectorException.class)
                    .satisfies(ex -> assertUnusedSelectorMessage(ex.getMessage())
                            .hasUnusedSelectorCount(1)
                            .containsOnly(ApiMethod.IGNORE)
                            .containsUnusedSelector(Integer.class));
        }
    }

    /**
     * Verify unused selectors by using various combinations of ignore(), withNullable(), set(), generate().
     *
     * @see UnusedSelectorsWithIndividualApiMethodsTest
     */
    @Nested
    class UnusedSelectorsWithMixedApiMethodsTest {
        @Test
        void unusedWithAllMethods() {
            final InstancioApi<Person> api = Instancio.of(Person.class)
                    .ignore(field(Foo.class, "fooValue"))
                    .set(field(Bar.class, "barValue"), "barrr")
                    .generate(field(Baz.class, "bazValue"), Generators::string)
                    .supply(allStrings().within(Person_.age.toScope()), StrictModeTest::failIfCalled)
                    .onComplete(all(LinkedList.class), list -> failIfCalled())
                    .withNullable(all(SortedSet.class))
                    .subtype(all(CharSequence.class), String.class);

            assertThatThrownBy(api::create)
                    .isInstanceOf(UnusedSelectorException.class)
                    .satisfies(ex -> assertUnusedSelectorMessage(ex.getMessage())
                            .hasUnusedSelectorCount(7)
                            .containsOnly(
                                    ApiMethod.IGNORE,
                                    ApiMethod.GENERATE_SET_SUPPLY,
                                    ApiMethod.ON_COMPLETE,
                                    ApiMethod.WITH_NULLABLE,
                                    ApiMethod.SUBTYPE)
                            .containsUnusedSelector(LinkedList.class)
                            .containsUnusedSelector(SortedSet.class)
                            .containsUnusedSelector(String.class) // TODO assert scope in reported message
                            .containsUnusedSelector(Foo.class, "fooValue")
                            .containsUnusedSelector(Bar.class, "barValue")
                            .containsUnusedSelector(Baz.class, "bazValue")
                            .containsUnusedSelector(CharSequence.class));
        }
    }

    /**
     * Verify unused selectors with individual methods: ignore(), withNullable(), set(), generate().
     *
     * @see UnusedSelectorsWithMixedApiMethodsTest
     */
    @Nested
    class UnusedSelectorsWithIndividualApiMethodsTest {
        @Test
        void unusedWithNullable() {
            final InstancioApi<Person> api = Instancio.of(Person.class)
                    .withNullable(field(Foo.class, "fooValue"))
                    .withNullable(all(
                            field(Bar.class, "barValue"),
                            field(Baz.class, "bazValue")))
                    .withNullable(all(SortedSet.class));

            assertUnusedSelectorsForMethod(api, ApiMethod.WITH_NULLABLE);
        }

        @Test
        void unusedWithIgnore() {
            final InstancioApi<Person> api = Instancio.of(Person.class)
                    .ignore(field(Foo.class, "fooValue"))
                    .ignore(all(
                            field(Bar.class, "barValue"),
                            field(Baz.class, "bazValue")))
                    .ignore(all(SortedSet.class));

            assertUnusedSelectorsForMethod(api, ApiMethod.IGNORE);
        }

        @Test
        void unusedWithSet() {
            final InstancioApi<Person> api = Instancio.of(Person.class)
                    .set(field(Foo.class, "fooValue"), "gaz")
                    .set(all(
                            field(Bar.class, "barValue"),
                            field(Baz.class, "bazValue")), "jaz")
                    .set(all(SortedSet.class), Collections.emptySet());

            assertUnusedSelectorsForMethod(api, ApiMethod.GENERATE_SET_SUPPLY);
        }

        @Test
        void unusedWithGenerate() {
            final InstancioApi<Person> api = Instancio.of(Person.class)
                    .generate(field(Foo.class, "fooValue"), Generators::string)
                    .generate(all(
                            field(Bar.class, "barValue"),
                            field(Baz.class, "bazValue")), Generators::string)
                    .generate(all(SortedSet.class), Generators::collection);

            assertUnusedSelectorsForMethod(api, ApiMethod.GENERATE_SET_SUPPLY);
        }

        @Test
        void unusedWithOnComplete() {
            final InstancioApi<Person> api = Instancio.of(Person.class)
                    .onComplete(field(Foo.class, "fooValue"), obj -> failIfCalled())
                    .onComplete(all(
                            field(Bar.class, "barValue"),
                            field(Baz.class, "bazValue")), obj -> failIfCalled())
                    .onComplete(all(SortedSet.class), obj -> failIfCalled());

            assertUnusedSelectorsForMethod(api, ApiMethod.ON_COMPLETE);
        }

        @Test
        void unusedWithSubtype() {
            final InstancioApi<Person> api = Instancio.of(Person.class)
                    .subtype(field(Foo.class, "fooValue"), Object.class)
                    .subtype(all(
                            field(Bar.class, "barValue"),
                            field(Baz.class, "bazValue")), Object.class)
                    .subtype(all(SortedSet.class), Object.class);

            assertUnusedSelectorsForMethod(api, ApiMethod.SUBTYPE);
        }

        private void assertUnusedSelectorsForMethod(final InstancioApi<?> api, final ApiMethod apiMethod) {
            assertThatThrownBy(api::create)
                    .isInstanceOf(UnusedSelectorException.class)
                    .satisfies(ex -> assertUnusedSelectorMessage(ex.getMessage())
                            .hasUnusedSelectorCount(4)
                            .containsOnly(apiMethod)
                            .containsUnusedSelector(SortedSet.class)
                            .containsUnusedSelector(Foo.class, "fooValue")
                            .containsUnusedSelector(Bar.class, "barValue")
                            .containsUnusedSelector(Baz.class, "bazValue"));
        }
    }

    private static <V> V failIfCalled() {
        return fail("Should not be called");
    }
}
