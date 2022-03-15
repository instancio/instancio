package org.instancio.api.features;

import org.instancio.Instancio;
import org.instancio.pojo.person.Address;
import org.instancio.pojo.person.Gender;
import org.instancio.pojo.person.Person;
import org.instancio.util.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.instancio.Generators.oneOf;

class UserSuppliedFieldGeneratorsTest {

    @Test
    @DisplayName("Field values should be created using user-supplied generators")
    void generatePersonWithCustomFieldGenerators() {
        final Integer[] ageOptions = {20, 30, 40, 50, 60, 70, 80};
        final Address customAddress = new Address();

        Person person = Instancio.of(Person.class)
                .with("name", () -> "first-name-" + Random.intBetween(100, 999))
                .with("gender", () -> Gender.FEMALE)
                .with("age", oneOf(ageOptions))
                .with("lastModified", () -> LocalDateTime.now(ZoneOffset.UTC))
                .with("address", () -> customAddress)
                .create();

        //noinspection ConfusingArgumentToVarargsMethod
        assertThat(person.getAge()).isIn(ageOptions);
        assertThat(person.getName()).matches("first-name-\\d{3}");
        assertThat(person.getGender()).isEqualTo(Gender.FEMALE);
        assertThat(person.getLastModified()).isCloseToUtcNow(within(3, ChronoUnit.SECONDS));
        assertThat(person.getAddress()).isSameAs(customAddress);
    }

}
