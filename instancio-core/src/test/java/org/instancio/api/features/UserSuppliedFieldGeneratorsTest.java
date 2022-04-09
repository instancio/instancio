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
package org.instancio.api.features;

import org.instancio.Instancio;
import org.instancio.test.support.pojo.generics.foobarbaz.Foo;
import org.instancio.test.support.pojo.generics.foobarbaz.FooContainer;
import org.instancio.test.support.pojo.person.Address;
import org.instancio.test.support.pojo.person.Gender;
import org.instancio.test.support.pojo.person.Person;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.instancio.Bindings.allInts;
import static org.instancio.Bindings.field;

class UserSuppliedFieldGeneratorsTest {

    @Test
    @DisplayName("Field values should be created using user-supplied generators")
    void generatePersonWithCustomFieldGenerators() {
        final Integer[] ageOptions = {20, 30, 40, 50, 60, 70, 80};
        final Address customAddress = new Address();

        Person person = Instancio.of(Person.class)
                .generate(field("name"), gen -> gen.string().prefix("first-name-").minLength(10))
                .supply(field("gender"), () -> Gender.FEMALE)
                .supply(field("lastModified"), () -> LocalDateTime.now(ZoneOffset.UTC))
                .supply(field("address"), () -> customAddress)
                .generate(field("age"), gen -> gen.oneOf(ageOptions))
                .create();

        //noinspection ConfusingArgumentToVarargsMethod
        assertThat(person.getAge()).isIn(ageOptions);
        assertThat(person.getName()).matches("first-name-\\w{10}");
        assertThat(person.getGender()).isEqualTo(Gender.FEMALE);
        assertThat(person.getLastModified()).isCloseToUtcNow(within(3, ChronoUnit.SECONDS));
        assertThat(person.getAddress()).isSameAs(customAddress);
    }

    @Test
    void fooContainerWithUserSuppliedInstance() {
        final String expectedFooString = "expected-foo";
        final FooContainer result = Instancio.of(FooContainer.class)
                .supply(field("stringFoo"), () -> {
                    Foo<String> foo = new Foo<>();
                    foo.setFooValue(expectedFooString);
                    return foo;
                })
                .create();

        assertThat(result.getStringFoo()).isNotNull();
        assertThat(result.getStringFoo().getFooValue()).isEqualTo(expectedFooString);
        assertThat(result.getStringFoo().getOtherFooValue())
                .as("Value should not be set on user-supplied instance")
                .isNull();
    }

    @Test
    @DisplayName("Values provided via supply() should take precedence over generate()")
    void supplyShouldTakePrecedenceOverGenerate() {
        final String expectedName = "test name";
        final int expectedAge = 99;
        final Person result = Instancio.of(Person.class)
                .supply(field("name"), () -> expectedName)
                .supply(allInts(), () -> expectedAge)
                .generate(field("name"), gen -> gen.string().minLength(100))
                .generate(allInts(), gen -> gen.ints().min(expectedAge + 1))
                .create();

        assertThat(result.getName()).isEqualTo(expectedName);
        assertThat(result.getAge()).isEqualTo(expectedAge);
    }
}
