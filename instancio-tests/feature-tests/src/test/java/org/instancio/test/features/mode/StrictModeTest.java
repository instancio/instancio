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
package org.instancio.test.features.mode;

import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.generators.Generators;
import org.instancio.internal.util.Sonar;
import org.instancio.test.support.pojo.generics.foobarbaz.Bar;
import org.instancio.test.support.pojo.generics.foobarbaz.Baz;
import org.instancio.test.support.pojo.generics.foobarbaz.Foo;
import org.instancio.test.support.pojo.person.Address;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.pojo.person.Person_;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.LinkedList;
import java.util.SortedSet;

import static org.instancio.Select.all;
import static org.instancio.Select.allInts;
import static org.instancio.Select.allStrings;
import static org.instancio.Select.field;
import static org.instancio.test.support.UnusedSelectorsAssert.assertThrowsUnusedSelectorException;
import static org.junit.jupiter.api.Assertions.fail;

@FeatureTag({
        Feature.MODE,
        Feature.GENERATE,
        Feature.SET,
        Feature.ON_COMPLETE,
        Feature.SUBTYPE
})
class StrictModeTest {

    /**
     * Verify unused selectors with individual methods:
     * ignore(), withNullable(), set(), generate().
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

            assertThrowsUnusedSelectorException(api)
                    .hasUnusedSelectorCount(4)
                    .unusedWithNullableSelector(field(Foo.class, "fooValue"))
                    .unusedWithNullableSelector(field(Bar.class, "barValue"))
                    .unusedWithNullableSelector(field(Baz.class, "bazValue"))
                    .unusedWithNullableSelector(all(SortedSet.class));
        }

        @Test
        void unusedWithIgnore() {
            final InstancioApi<Person> api = Instancio.of(Person.class)
                    .ignore(field(Foo.class, "fooValue"))
                    .ignore(all(
                            field(Bar.class, "barValue"),
                            field(Baz.class, "bazValue")))
                    .ignore(all(SortedSet.class));

            assertThrowsUnusedSelectorException(api)
                    .hasUnusedSelectorCount(4)
                    .unusedIgnoreSelector(field(Foo.class, "fooValue"))
                    .unusedIgnoreSelector(field(Bar.class, "barValue"))
                    .unusedIgnoreSelector(field(Baz.class, "bazValue"))
                    .unusedIgnoreSelector(all(SortedSet.class));
        }

        @Test
        void unusedWithSet() {
            final InstancioApi<Person> api = Instancio.of(Person.class)
                    .set(field(Foo.class, "fooValue"), "gaz")
                    .set(all(
                            field(Bar.class, "barValue"),
                            field(Baz.class, "bazValue")), "jaz")
                    .set(all(SortedSet.class), Collections.emptySet());

            assertThrowsUnusedSelectorException(api)
                    .hasUnusedSelectorCount(4)
                    .unusedGeneratorSelector(field(Foo.class, "fooValue"))
                    .unusedGeneratorSelector(field(Bar.class, "barValue"))
                    .unusedGeneratorSelector(field(Baz.class, "bazValue"))
                    .unusedGeneratorSelector(all(SortedSet.class));
        }

        @Test
        void unusedWithGenerate() {
            final InstancioApi<Person> api = Instancio.of(Person.class)
                    .generate(field(Foo.class, "fooValue"), Generators::string)
                    .generate(all(
                            field(Bar.class, "barValue"),
                            field(Baz.class, "bazValue")), Generators::string)
                    .generate(all(SortedSet.class), Generators::collection);

            assertThrowsUnusedSelectorException(api)
                    .hasUnusedSelectorCount(4)
                    .unusedGeneratorSelector(field(Foo.class, "fooValue"))
                    .unusedGeneratorSelector(field(Bar.class, "barValue"))
                    .unusedGeneratorSelector(field(Baz.class, "bazValue"))
                    .unusedGeneratorSelector(all(SortedSet.class));
        }

        @Test
        void unusedWithOnComplete() {
            final InstancioApi<Person> api = Instancio.of(Person.class)
                    .onComplete(field(Foo.class, "fooValue"), obj -> failIfCalled())
                    .onComplete(all(
                            field(Bar.class, "barValue"),
                            field(Baz.class, "bazValue")), obj -> failIfCalled())
                    .onComplete(all(SortedSet.class), obj -> failIfCalled());

            assertThrowsUnusedSelectorException(api)
                    .hasUnusedSelectorCount(4)
                    .unusedOnCompleteSelector(field(Foo.class, "fooValue"))
                    .unusedOnCompleteSelector(field(Bar.class, "barValue"))
                    .unusedOnCompleteSelector(field(Baz.class, "bazValue"))
                    .unusedOnCompleteSelector(all(SortedSet.class));
        }

        @Test
        void unusedWithSubtype() {
            final InstancioApi<Person> api = Instancio.of(Person.class)
                    .subtype(field(Foo.class, "fooValue"), Object.class)
                    .subtype(all(
                            field(Bar.class, "barValue"),
                            field(Baz.class, "bazValue")), Object.class)
                    .subtype(all(SortedSet.class), Object.class);

            assertThrowsUnusedSelectorException(api)
                    .hasUnusedSelectorCount(4)
                    .unusedSubtypeSelector(field(Foo.class, "fooValue"))
                    .unusedSubtypeSelector(field(Bar.class, "barValue"))
                    .unusedSubtypeSelector(field(Baz.class, "bazValue"))
                    .unusedSubtypeSelector(all(SortedSet.class));
        }
    }

    private static <V> V failIfCalled() {
        return fail("Should not be called");
    }
}
