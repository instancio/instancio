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

import org.assertj.core.api.Assertions;
import org.instancio.GetMethodSelector;
import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.TargetSelector;
import org.instancio.exception.UnusedSelectorException;
import org.instancio.generators.Generators;
import org.instancio.test.support.pojo.basic.IntegerHolder;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.pojo.generics.foobarbaz.Bar;
import org.instancio.test.support.pojo.generics.foobarbaz.Baz;
import org.instancio.test.support.pojo.generics.foobarbaz.Foo;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.pojo.person.PersonName;
import org.instancio.test.support.pojo.person.Pojo;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.all;
import static org.instancio.Select.field;
import static org.instancio.Select.fields;
import static org.instancio.Select.types;
import static org.junit.jupiter.api.Assertions.fail;

@FeatureTag({Feature.MODE, Feature.SELECTOR, Feature.VALIDATION})
class UnusedSelectorFullErrorMessageTest {

    /**
     * Complete error message for reference.
     */
    @Test
    void verifyFullErrorMessage() {
        final GetMethodSelector<Foo<String>, String> getFooValueMethod = Foo::getFooValue;
        final TargetSelector timestampSelector = types().of(Timestamp.class);

        final InstancioApi<Person> api = Instancio.of(Person.class)
                .ignore(all(YearMonth.class))
                .ignore(field(Bar.class, "barValue"))
                .withNullable(all(BigDecimal.class))
                .withNullable(getFooValueMethod)
                .supply(all(Year.class), this::failIfCalled)
                .supply(field(Baz.class, "bazValue"), this::failIfCalled)
                .generate(field(StringHolder.class, "value"), Generators::string)
                .onComplete(field(IntegerHolder::getPrimitive), value -> failIfCalled())
                .onComplete(all(ZonedDateTime.class), value -> failIfCalled())
                .set(timestampSelector, null)
                .ignore(types().annotated(Pojo.class).annotated(PersonName.class))
                .supply(fields().named("foo"), () -> Assertions.fail("not called"))
                .ignore(types(klass -> false))
                .supply(fields(field -> false), () -> Assertions.fail("not called"));

        assertThatThrownBy(api::create)
                .isExactlyInstanceOf(UnusedSelectorException.class)
                .hasMessage(String.format("%n" +
                        "Found unused selectors referenced in the following methods:%n" +
                        "%n" +
                        " -> Unused selectors in ignore():%n" +
                        " 1: all(YearMonth)%n" +
                        "    at org.other.test.features.mode.UnusedSelectorFullErrorMessageTest.verifyFullErrorMessage(UnusedSelectorFullErrorMessageTest.java:62)%n" +
                        " 2: field(Bar, \"barValue\")%n" +
                        "    at org.other.test.features.mode.UnusedSelectorFullErrorMessageTest.verifyFullErrorMessage(UnusedSelectorFullErrorMessageTest.java:63)%n" +
                        " 3: types().annotated(Pojo).annotated(PersonName)%n" +
                        "    at org.other.test.features.mode.UnusedSelectorFullErrorMessageTest.verifyFullErrorMessage(UnusedSelectorFullErrorMessageTest.java:72)%n" +
                        " 4: types(Predicate<Class>)%n" +
                        "    at org.other.test.features.mode.UnusedSelectorFullErrorMessageTest.verifyFullErrorMessage(UnusedSelectorFullErrorMessageTest.java:74)%n" +
                        "%n" +
                        " -> Unused selectors in withNullable():%n" +
                        " 1: all(BigDecimal)%n" +
                        "    at org.other.test.features.mode.UnusedSelectorFullErrorMessageTest.verifyFullErrorMessage(UnusedSelectorFullErrorMessageTest.java:64)%n" +
                        " 2: field(Foo, \"fooValue\")%n" +
                        "    at org.other.test.features.mode.UnusedSelectorFullErrorMessageTest.verifyFullErrorMessage(UnusedSelectorFullErrorMessageTest.java:65)%n" +
                        "%n" +
                        " -> Unused selectors in generate(), set(), or supply():%n" +
                        " 1: all(Year)%n" +
                        "    at org.other.test.features.mode.UnusedSelectorFullErrorMessageTest.verifyFullErrorMessage(UnusedSelectorFullErrorMessageTest.java:66)%n" +
                        " 2: field(Baz, \"bazValue\")%n" +
                        "    at org.other.test.features.mode.UnusedSelectorFullErrorMessageTest.verifyFullErrorMessage(UnusedSelectorFullErrorMessageTest.java:67)%n" +
                        " 3: field(StringHolder, \"value\")%n" +
                        "    at org.other.test.features.mode.UnusedSelectorFullErrorMessageTest.verifyFullErrorMessage(UnusedSelectorFullErrorMessageTest.java:68)%n" +
                        " 4: fields().named(\"foo\")%n" +
                        "    at org.other.test.features.mode.UnusedSelectorFullErrorMessageTest.verifyFullErrorMessage(UnusedSelectorFullErrorMessageTest.java:73)%n" +
                        " 5: fields(Predicate<Field>)%n" +
                        "    at org.other.test.features.mode.UnusedSelectorFullErrorMessageTest.verifyFullErrorMessage(UnusedSelectorFullErrorMessageTest.java:75)%n" +
                        " 6: types().of(Timestamp)%n" +
                        "    at org.other.test.features.mode.UnusedSelectorFullErrorMessageTest.verifyFullErrorMessage(UnusedSelectorFullErrorMessageTest.java:71)%n" +
                        "%n" +
                        " -> Unused selectors in onComplete():%n" +
                        " 1: all(ZonedDateTime)%n" +
                        "    at org.other.test.features.mode.UnusedSelectorFullErrorMessageTest.verifyFullErrorMessage(UnusedSelectorFullErrorMessageTest.java:70)%n" +
                        " 2: field(IntegerHolder, \"primitive\")%n" +
                        "    at org.other.test.features.mode.UnusedSelectorFullErrorMessageTest.verifyFullErrorMessage(UnusedSelectorFullErrorMessageTest.java:69)%n" +
                        "%n" +
                        "This error aims to highlight potential problems and help maintain clean test code.%n" +
                        "%n" +
                        "Possible causes:%n" +
                        "%n" +
                        " -> Selector did not match any field or class within this object.%n" +
                        " -> Selector target is beyond the current maximum depth setting: 8%n" +
                        " -> Selector matches an ignored target, for example:%n" +
                        "%n" +
                        "    Person person = Instancio.of(Person.class)%n" +
                        "        .ignore(all(Phone.class))%n" +
                        "        .set(field(Phone::getNumber), \"555-66-77\") // unused!%n" +
                        "        .create();%n" +
                        "%n" +
                        " -> Selector targets a field or class in an object that was provided by:%n" +
                        "    -> set(TargetSelector, Object)%n" +
                        "    -> supply(TargetSelector, Supplier)%n" +
                        "%n" +
                        "    // Example%n" +
                        "    Supplier<Address> addressSupplier = () -> new Address(...);%n" +
                        "    Person person = Instancio.of(Person.class)%n" +
                        "        .supply(all(Address.class), () -> addressSupplier)%n" +
                        "        .set(field(Address::getCity), \"London\") // unused!%n" +
                        "        .create();%n" +
                        "%n" +
                        "    Instancio does not modify instances provided by a Supplier,%n" +
                        "    therefore, field(Address::getCity) will trigger unused selector error.%n" +
                        "%n" +
                        "To resolve this error:%n" +
                        "%n" +
                        " -> Remove the selector(s) causing the error, if applicable.%n" +
                        " -> Suppress the error by enabling 'lenient()' mode:%n" +
                        "%n" +
                        "    Example example = Instancio.of(Example.class)%n" +
                        "        .lenient()%n" +
                        "        .create();%n" +
                        "%n" +
                        "    // Or via Settings%n" +
                        "    Settings settings = Settings.create()%n" +
                        "        .set(Keys.MODE, Mode.LENIENT);%n" +
                        "%n" +
                        "    Example example = Instancio.of(Example.class)%n" +
                        "        .withSettings(settings)%n" +
                        "        .create();%n" +
                        "%n" +
                        "For more information see: https://www.instancio.org/user-guide/#selector-strictness"));
    }

    private <V> V failIfCalled() {
        return fail("Should not be called");
    }
}
