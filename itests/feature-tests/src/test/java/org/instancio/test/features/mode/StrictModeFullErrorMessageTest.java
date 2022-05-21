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
import org.instancio.test.support.pojo.basic.IntegerHolder;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.pojo.generics.foobarbaz.Bar;
import org.instancio.test.support.pojo.generics.foobarbaz.Baz;
import org.instancio.test.support.pojo.generics.foobarbaz.Foo;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.all;
import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.fail;

@FeatureTag({Feature.MODE, Feature.VALIDATION})
class StrictModeFullErrorMessageTest {

    /**
     * Complete error message for reference.
     */
    @Test
    void verifyFullErrorMessage() {
        final InstancioApi<Person> api = Instancio.of(Person.class)
                .ignore(all(YearMonth.class))
                .ignore(field(Bar.class, "barValue"))
                .withNullable(all(BigDecimal.class))
                .withNullable(field(Foo.class, "fooValue"))
                .supply(all(Year.class), this::failIfCalled)
                .supply(field(Baz.class, "bazValue"), this::failIfCalled)
                .generate(field(StringHolder.class, "value"), Generators::string)
                .onComplete(field(IntegerHolder.class, "primitive"), value -> failIfCalled())
                .onComplete(all(ZonedDateTime.class), value -> failIfCalled());

        assertThatThrownBy(api::create)
                .isExactlyInstanceOf(UnusedSelectorException.class)
                .hasMessageContaining(String.format("Found unused selectors referenced in the following methods:%n" +
                        "%n" +
                        " -> Unused selectors in ignore():%n" +
                        " 1: all(YearMonth)%n" +
                        " 2: field(Bar, \"barValue\")%n" +
                        "%n" +
                        " -> Unused selectors in withNullable():%n" +
                        " 1: all(BigDecimal)%n" +
                        " 2: field(Foo, \"fooValue\")%n" +
                        "%n" +
                        " -> Unused selectors in generate(), set(), or supply():%n" +
                        " 1: field(StringHolder, \"value\")%n" +
                        " 2: all(Year)%n" +
                        " 3: field(Baz, \"bazValue\")%n" +
                        "%n" +
                        " -> Unused selectors in onComplete():%n" +
                        " 1: field(IntegerHolder, \"primitive\")%n" +
                        " 2: all(ZonedDateTime)"));
    }

    private <V> V failIfCalled() {
        return fail("Should not be called");
    }
}
