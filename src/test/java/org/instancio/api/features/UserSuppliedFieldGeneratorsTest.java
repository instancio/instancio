package org.instancio.api.features;

import org.apache.commons.lang3.RandomStringUtils;
import org.instancio.Instancio;
import org.instancio.pojo.generics.foobarbaz.Foo;
import org.instancio.pojo.generics.foobarbaz.FooContainer;
import org.instancio.pojo.person.Address;
import org.instancio.pojo.person.Gender;
import org.instancio.pojo.person.Person;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.instancio.Bindings.field;
import static org.instancio.Generators.oneOf;

class UserSuppliedFieldGeneratorsTest {

    @Test
    @DisplayName("Field values should be created using user-supplied generators")
    void generatePersonWithCustomFieldGenerators() {
        final Integer[] ageOptions = {20, 30, 40, 50, 60, 70, 80};
        final Address customAddress = new Address();

        Person person = Instancio.of(Person.class)
                .with(field("name"), () -> "first-name-" + RandomStringUtils.randomAlphabetic(5))
                .with(field("gender"), () -> Gender.FEMALE)
                .with(field("age"), oneOf(ageOptions))
                .with(field("lastModified"), () -> LocalDateTime.now(ZoneOffset.UTC))
                .with(field("address"), () -> customAddress)
                .create();

        //noinspection ConfusingArgumentToVarargsMethod
        assertThat(person.getAge()).isIn(ageOptions);
        assertThat(person.getName()).matches("first-name-\\d{3}");
        assertThat(person.getGender()).isEqualTo(Gender.FEMALE);
        assertThat(person.getLastModified()).isCloseToUtcNow(within(3, ChronoUnit.SECONDS));
        assertThat(person.getAddress()).isSameAs(customAddress);
    }

    @Test
    void fooContainerWithUserSuppliedInstance() {
        final String expectedFooString = "expected-foo";
        final FooContainer result = Instancio.of(FooContainer.class)
                .with(field("stringFoo"), () -> {
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

}
