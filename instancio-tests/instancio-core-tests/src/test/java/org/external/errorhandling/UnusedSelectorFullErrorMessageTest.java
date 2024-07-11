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
package org.external.errorhandling;

import org.assertj.core.api.Assertions;
import org.instancio.Assign;
import org.instancio.GetMethodSelector;
import org.instancio.Instancio;
import org.instancio.Select;
import org.instancio.TargetSelector;
import org.instancio.exception.UnusedSelectorException;
import org.instancio.feed.Feed;
import org.instancio.generators.Generators;
import org.instancio.settings.AssignmentType;
import org.instancio.settings.Keys;
import org.instancio.test.support.pojo.basic.IntegerHolder;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.pojo.generics.foobarbaz.Bar;
import org.instancio.test.support.pojo.generics.foobarbaz.Baz;
import org.instancio.test.support.pojo.generics.foobarbaz.Foo;
import org.instancio.test.support.pojo.misc.StringsAbc;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.pojo.person.PersonName;
import org.instancio.test.support.pojo.person.Pojo;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZonedDateTime;

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
        final TargetSelector timestampSelector = Select.types().of(Timestamp.class);

        Instancio.of(String.class)
                .ignore(Select.all(YearMonth.class))
                .ignore(Select.field(Bar.class, "barValue"))
                .withNullable(Select.all(BigDecimal.class))
                .withNullable(getFooValueMethod)
                .withNullable(Select.setter(Person::setName))
                .supply(Select.all(Year.class).within(Select.types().of(Year.class).toScope()), this::failIfCalled)
                .supply(Select.field(Baz.class, "bazValue"), this::failIfCalled)
                .generate(Select.field(StringHolder.class, "value"), Generators::string)
                .onComplete(Select.field(IntegerHolder::getPrimitive), value -> failIfCalled())
                .onComplete(Select.all(ZonedDateTime.class), value -> failIfCalled())
                .set(timestampSelector, null)
                .set(Select.setter(Person.class, "setAge", int.class), 99)
                .ignore(Select.types().annotated(Pojo.class).annotated(PersonName.class))
                .supply(Select.fields().named("foo"), () -> Assertions.fail("not called"))
                .ignore(Select.types(klass -> false).within(Select.scope(Person.class)))
                .supply(Select.fields(field -> false), () -> Assertions.fail("not called"))
                .assign(Assign.valueOf(StringsAbc::getA)
                        .to(StringsAbc::getC))
                // lenient selectors should not be reported as unused
                .set(Select.fields().named("bad-field-name").lenient(), 0)
                .setModel(Select.types(t -> false), Instancio.of(String.class).toModel())
                .filter(Select.all(Bar.class), bar -> false)
                // setBlank() selectors are marked as lenient and should not appear in unused selector error message
                .setBlank(Select.all(Bar.class))
                // withUnique() is implemented using filter()
                .withUnique(Select.all(YearMonth.class))
                .applyFeed(Select.all(String.class), Instancio.ofFeed(Feed.class)
                        .withDataSource(source -> source.ofString("value\nfoo"))
                        .create())
                .withSetting(Keys.ASSIGNMENT_TYPE, AssignmentType.METHOD)
                .create();
    }

    @Override
    String expectedMessage() {
        return """

                Found unused selectors referenced in the following methods:

                 -> Unused selector in: ignore()
                 1: all(YearMonth)
                    at org.external.errorhandling.UnusedSelectorFullErrorMessageTest.methodUnderTest(UnusedSelectorFullErrorMessageTest.java:63)
                 2: field(Bar, "barValue")
                    at org.external.errorhandling.UnusedSelectorFullErrorMessageTest.methodUnderTest(UnusedSelectorFullErrorMessageTest.java:64)
                 3: types().annotated(Pojo).annotated(PersonName)
                    at org.external.errorhandling.UnusedSelectorFullErrorMessageTest.methodUnderTest(UnusedSelectorFullErrorMessageTest.java:75)
                 4: types(Predicate<Class>).within(scope(Person))
                    at org.external.errorhandling.UnusedSelectorFullErrorMessageTest.methodUnderTest(UnusedSelectorFullErrorMessageTest.java:77)

                 -> Unused selector in: withNullable()
                 1: all(BigDecimal)
                    at org.external.errorhandling.UnusedSelectorFullErrorMessageTest.methodUnderTest(UnusedSelectorFullErrorMessageTest.java:65)
                 2: field(Foo, "fooValue")
                    at org.external.errorhandling.UnusedSelectorFullErrorMessageTest.methodUnderTest(UnusedSelectorFullErrorMessageTest.java:66)
                 3: setter(Person, "setName(String)")
                    at org.external.errorhandling.UnusedSelectorFullErrorMessageTest.methodUnderTest(UnusedSelectorFullErrorMessageTest.java:67)

                 -> Unused selector in: generate(), set(), or supply()
                 1: all(Year).within(scope(types().of(Year)))
                    at org.external.errorhandling.UnusedSelectorFullErrorMessageTest.methodUnderTest(UnusedSelectorFullErrorMessageTest.java:68)
                 2: field(Baz, "bazValue")
                    at org.external.errorhandling.UnusedSelectorFullErrorMessageTest.methodUnderTest(UnusedSelectorFullErrorMessageTest.java:69)
                 3: field(StringHolder, "value")
                    at org.external.errorhandling.UnusedSelectorFullErrorMessageTest.methodUnderTest(UnusedSelectorFullErrorMessageTest.java:70)
                 4: fields().named("foo")
                    at org.external.errorhandling.UnusedSelectorFullErrorMessageTest.methodUnderTest(UnusedSelectorFullErrorMessageTest.java:76)
                 5: fields(Predicate<Field>)
                    at org.external.errorhandling.UnusedSelectorFullErrorMessageTest.methodUnderTest(UnusedSelectorFullErrorMessageTest.java:78)
                 6: setter(Person, "setAge(int)")
                    at org.external.errorhandling.UnusedSelectorFullErrorMessageTest.methodUnderTest(UnusedSelectorFullErrorMessageTest.java:74)
                 7: types().of(Timestamp)
                    at org.external.errorhandling.UnusedSelectorFullErrorMessageTest.methodUnderTest(UnusedSelectorFullErrorMessageTest.java:73)

                 -> Unused selector in: onComplete()
                 1: all(ZonedDateTime)
                    at org.external.errorhandling.UnusedSelectorFullErrorMessageTest.methodUnderTest(UnusedSelectorFullErrorMessageTest.java:72)
                 2: field(IntegerHolder, "primitive")
                    at org.external.errorhandling.UnusedSelectorFullErrorMessageTest.methodUnderTest(UnusedSelectorFullErrorMessageTest.java:71)

                 -> Unused selector in: assign() origin
                 1: field(StringsAbc, "a")
                    at org.external.errorhandling.UnusedSelectorFullErrorMessageTest.methodUnderTest(UnusedSelectorFullErrorMessageTest.java:79)

                 -> Unused selector in: assign() destination
                 1: field(StringsAbc, "c")
                    at org.external.errorhandling.UnusedSelectorFullErrorMessageTest.methodUnderTest(UnusedSelectorFullErrorMessageTest.java:80)

                 -> Unused selector in: setModel()
                 1: types(Predicate<Class>)
                    at org.external.errorhandling.UnusedSelectorFullErrorMessageTest.methodUnderTest(UnusedSelectorFullErrorMessageTest.java:83)

                 -> Unused selector in: filter() or withUnique()
                 1: all(Bar)
                    at org.external.errorhandling.UnusedSelectorFullErrorMessageTest.methodUnderTest(UnusedSelectorFullErrorMessageTest.java:84)
                 2: all(YearMonth)
                    at org.external.errorhandling.UnusedSelectorFullErrorMessageTest.methodUnderTest(UnusedSelectorFullErrorMessageTest.java:88)

                 -> Unused selector in: applyFeed()
                 1: all(String)
                    at org.external.errorhandling.UnusedSelectorFullErrorMessageTest.methodUnderTest(UnusedSelectorFullErrorMessageTest.java:89)

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
