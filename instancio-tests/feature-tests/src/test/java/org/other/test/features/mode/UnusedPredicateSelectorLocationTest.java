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
package org.other.test.features.mode;

import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.TargetSelector;
import org.instancio.exception.UnusedSelectorException;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.pojo.person.PersonName;
import org.instancio.test.support.pojo.person.Pojo;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.fail;
import static org.instancio.Select.fields;
import static org.instancio.Select.types;

@FeatureTag({Feature.MODE, Feature.PREDICATE_SELECTOR})
class UnusedPredicateSelectorLocationTest {

    @Test
    void unused() {
        final TargetSelector timestampSelector = types().of(Timestamp.class);
        final InstancioApi<Person> api = Instancio.of(Person.class)
                .set(timestampSelector, null)
                .ignore(types().annotated(Pojo.class).annotated(PersonName.class))
                .supply(fields().named("foo"), () -> fail("not called"))
                .ignore(types(klass -> false))
                .supply(fields(field -> false), () -> fail("not called"));

        assertThatThrownBy(api::create)
                .isInstanceOf(UnusedSelectorException.class)
                .satisfies(ex -> assertThat(ex.getMessage()).isEqualTo(String.format(
                        "%nFound unused selectors referenced in the following methods:%n" +
                                "%n" +
                                " -> Unused selectors in ignore():%n" +
                                " 1: types().annotated(Pojo).annotated(PersonName)%n" +
                                "    at org.other.test.features.mode.UnusedPredicateSelectorLocationTest.unused(UnusedPredicateSelectorLocationTest.java:45)%n" +
                                " 2: types(Predicate<Class>)%n" +
                                "    at org.other.test.features.mode.UnusedPredicateSelectorLocationTest.unused(UnusedPredicateSelectorLocationTest.java:47)%n" +
                                "%n" +
                                " -> Unused selectors in generate(), set(), or supply():%n" +
                                " 1: fields().named(\"foo\")%n" +
                                "    at org.other.test.features.mode.UnusedPredicateSelectorLocationTest.unused(UnusedPredicateSelectorLocationTest.java:46)%n" +
                                " 2: fields(Predicate<Field>)%n" +
                                "    at org.other.test.features.mode.UnusedPredicateSelectorLocationTest.unused(UnusedPredicateSelectorLocationTest.java:48)%n" +
                                " 3: types().of(Timestamp)%n" +
                                "    at org.other.test.features.mode.UnusedPredicateSelectorLocationTest.unused(UnusedPredicateSelectorLocationTest.java:44)%n" +
                                "%n" +
                                "This error aims to highlight potential problems and help maintain clean test code.%n" +
                                "You are most likely selecting a field or class that does not exist within this object.%n" +
                                "%n" +
                                "This error can be suppressed by switching to lenient mode, for example:%n" +
                                "%n" +
                                "      Example example = Instancio.of(Example.class)%n" +
                                "          // snip...%n" +
                                "          .lenient().create();%n" +
                                "%n" +
                                "For more information see: https://www.instancio.org/user-guide/%n"
                )));
    }
}
