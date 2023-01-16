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
package org.other.test.features.mode.usecases;

import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.generators.Generators;
import org.instancio.test.support.pojo.generics.foobarbaz.Bar;
import org.instancio.test.support.pojo.generics.foobarbaz.Baz;
import org.instancio.test.support.pojo.generics.foobarbaz.Foo;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.pojo.person.Person_;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.SortedSet;

import static org.instancio.Select.all;
import static org.instancio.Select.allStrings;
import static org.instancio.Select.field;
import static org.instancio.test.support.UnusedSelectorsAssert.assertThrowsUnusedSelectorException;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Verify unused selectors by using various combinations
 * of ignore(), withNullable(), set(), generate().
 */
@FeatureTag(Feature.MODE)
class UnusedSelectorsWithMixedApiMethodsTest {
    @Test
    void unusedWithAllMethods() {
        final InstancioApi<Person> api = Instancio.of(Person.class)
                .ignore(field(Foo.class, "fooValue"))
                .set(field(Bar.class, "barValue"), "barrr")
                .generate(field(Baz.class, "bazValue"), Generators::string)
                .supply(allStrings().within(Person_.age.toScope()), UnusedSelectorsWithMixedApiMethodsTest::failIfCalled)
                .onComplete(all(LinkedList.class), list -> failIfCalled())
                .withNullable(all(SortedSet.class))
                .subtype(all(CharSequence.class), String.class);

        int l = 48;
        assertThrowsUnusedSelectorException(api)
                .hasUnusedSelectorCount(7)
                .unusedIgnoreSelectorAt(field(Foo.class, "fooValue"), line(l++))
                .unusedGeneratorSelectorAt(field(Bar.class, "barValue"), line(l++))
                .unusedGeneratorSelectorAt(field(Baz.class, "bazValue"), line(l++))
                .unusedGeneratorSelectorAt(allStrings().within(Person_.age.toScope()), line(l++))
                .unusedOnCompleteSelectorAt(all(LinkedList.class), line(l++))
                .unusedWithNullableSelectorAt(all(SortedSet.class), line(l++))
                .unusedSubtypeSelectorAt(all(CharSequence.class), line(l));
    }

    private static String line(final int line) {
        return UnusedSelectorsWithMixedApiMethodsTest.class.getSimpleName() + ".java:" + line;
    }

    private static <V> V failIfCalled() {
        return fail("Should not be called");
    }
}
