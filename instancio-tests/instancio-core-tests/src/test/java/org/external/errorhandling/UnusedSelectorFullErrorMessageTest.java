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
package org.external.errorhandling;

import org.assertj.core.api.Assertions;
import org.instancio.GetMethodSelector;
import org.instancio.Instancio;
import org.instancio.TargetSelector;
import org.instancio.exception.UnusedSelectorException;
import org.instancio.generators.Generators;
import org.instancio.test.support.pojo.basic.IntegerHolder;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.pojo.generics.foobarbaz.Bar;
import org.instancio.test.support.pojo.generics.foobarbaz.Baz;
import org.instancio.test.support.pojo.generics.foobarbaz.Foo;
import org.instancio.test.support.pojo.misc.StringsAbc;
import org.instancio.test.support.pojo.person.PersonName;
import org.instancio.test.support.pojo.person.Pojo;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZonedDateTime;

import static org.instancio.Assign.valueOf;
import static org.instancio.Select.all;
import static org.instancio.Select.field;
import static org.instancio.Select.fields;
import static org.instancio.Select.types;
import static org.junit.jupiter.api.Assertions.fail;

@FeatureTag({Feature.MODE, Feature.SELECTOR, Feature.VALIDATION})
class UnusedSelectorFullErrorMessageTest extends AbstractErrorMessageTestTemplate {

    @Override
    protected Class<?> expectedException() {
        return UnusedSelectorException.class;
    }

    @Override
    void methodUnderTest() {
        final GetMethodSelector<Foo<String>, String> getFooValueMethod = Foo::getFooValue;
        final TargetSelector timestampSelector = types().of(Timestamp.class);

        Instancio.of(String.class)
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
                .supply(fields(field -> false), () -> Assertions.fail("not called"))
                .assign(valueOf(StringsAbc::getA)
                        .to(StringsAbc::getC))
                .create();
    }

    @Override
    String expectedMessage() {
        return """

                Found unused selectors referenced in the following methods:

                 -> Unused selectors in ignore():
                 1: all(YearMonth)
                    at org.external.errorhandling.UnusedSelectorFullErrorMessageTest.methodUnderTest(UnusedSelectorFullErrorMessageTest.java:62)
                 2: field(Bar, "barValue")
                    at org.external.errorhandling.UnusedSelectorFullErrorMessageTest.methodUnderTest(UnusedSelectorFullErrorMessageTest.java:63)
                 3: types().annotated(Pojo).annotated(PersonName)
                    at org.external.errorhandling.UnusedSelectorFullErrorMessageTest.methodUnderTest(UnusedSelectorFullErrorMessageTest.java:72)
                 4: types(Predicate<Class>)
                    at org.external.errorhandling.UnusedSelectorFullErrorMessageTest.methodUnderTest(UnusedSelectorFullErrorMessageTest.java:74)

                 -> Unused selectors in withNullable():
                 1: all(BigDecimal)
                    at org.external.errorhandling.UnusedSelectorFullErrorMessageTest.methodUnderTest(UnusedSelectorFullErrorMessageTest.java:64)
                 2: field(Foo, "fooValue")
                    at org.external.errorhandling.UnusedSelectorFullErrorMessageTest.methodUnderTest(UnusedSelectorFullErrorMessageTest.java:65)

                 -> Unused selectors in generate(), set(), or supply():
                 1: all(Year)
                    at org.external.errorhandling.UnusedSelectorFullErrorMessageTest.methodUnderTest(UnusedSelectorFullErrorMessageTest.java:66)
                 2: field(Baz, "bazValue")
                    at org.external.errorhandling.UnusedSelectorFullErrorMessageTest.methodUnderTest(UnusedSelectorFullErrorMessageTest.java:67)
                 3: field(StringHolder, "value")
                    at org.external.errorhandling.UnusedSelectorFullErrorMessageTest.methodUnderTest(UnusedSelectorFullErrorMessageTest.java:68)
                 4: fields().named("foo")
                    at org.external.errorhandling.UnusedSelectorFullErrorMessageTest.methodUnderTest(UnusedSelectorFullErrorMessageTest.java:73)
                 5: fields(Predicate<Field>)
                    at org.external.errorhandling.UnusedSelectorFullErrorMessageTest.methodUnderTest(UnusedSelectorFullErrorMessageTest.java:75)
                 6: types().of(Timestamp)
                    at org.external.errorhandling.UnusedSelectorFullErrorMessageTest.methodUnderTest(UnusedSelectorFullErrorMessageTest.java:71)

                 -> Unused selectors in onComplete():
                 1: all(ZonedDateTime)
                    at org.external.errorhandling.UnusedSelectorFullErrorMessageTest.methodUnderTest(UnusedSelectorFullErrorMessageTest.java:70)
                 2: field(IntegerHolder, "primitive")
                    at org.external.errorhandling.UnusedSelectorFullErrorMessageTest.methodUnderTest(UnusedSelectorFullErrorMessageTest.java:69)

                 -> Unused origin selectors in assign():
                 1: field(StringsAbc, "a")
                    at org.external.errorhandling.UnusedSelectorFullErrorMessageTest.methodUnderTest(UnusedSelectorFullErrorMessageTest.java:76)

                 -> Unused destination selectors in assign():
                 1: field(StringsAbc, "c")
                    at org.external.errorhandling.UnusedSelectorFullErrorMessageTest.methodUnderTest(UnusedSelectorFullErrorMessageTest.java:77)

                This error aims to highlight potential problems and help maintain clean test code.

                Possible causes:

                 -> Selector did not match any field or class within this object.
                 -> Selector target is beyond the current maximum depth setting: 8
                 -> Selector matches an ignored target, for example:

                    Person person = Instancio.of(Person.class)
                        .ignore(all(Phone.class))
                        .set(field(Phone::getNumber), "555-66-77") // unused!
                        .create();

                 -> Selector targets a field or class in an object that was provided by:
                    -> set(TargetSelector, Object)
                    -> supply(TargetSelector, Supplier)

                    // Example
                    Supplier<Address> addressSupplier = () -> new Address(...);
                    Person person = Instancio.of(Person.class)
                        .supply(all(Address.class), () -> addressSupplier)
                        .set(field(Address::getCity), "London") // unused!
                        .create();

                    Instancio does not modify instances provided by a Supplier,
                    therefore, field(Address::getCity) will trigger unused selector error.

                To resolve this error:

                 -> Remove the selector(s) causing the error, if applicable.
                 -> Suppress the error by enabling 'lenient()' mode:

                    Example example = Instancio.of(Example.class)
                        .lenient()
                        .create();

                    // Or via Settings
                    Settings settings = Settings.create()
                        .set(Keys.MODE, Mode.LENIENT);

                    Example example = Instancio.of(Example.class)
                        .withSettings(settings)
                        .create();

                For more information see: https://www.instancio.org/user-guide/#selector-strictness
                """;
    }

    private <V> V failIfCalled() {
        return fail("Should not be called");
    }
}
