package org.instancio.api.features;

import org.instancio.Instancio;
import org.instancio.pojo.person.Person;
import org.instancio.pojo.person.Pet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Generators.withPrefix;

class UserSuppliedClassGeneratorsTest {

    @Test
    @DisplayName("All String fields should use custom prefix except person.name")
    void userSuppliedStringClassGenerator() {
        final String prefix = "custom-prefix-for-ALL-String-fields-";
        final String expectedName = "Jane Doe";

        Person person = Instancio.of(Person.class)
                .with("name", () -> expectedName)
                .with(String.class, withPrefix(prefix))
                .create();

        assertThat(person.getName()).isEqualTo(expectedName);
        assertThat(person.getAddress().getCity()).startsWith(prefix);
        assertThat(person.getAddress().getAddress()).startsWith(prefix);
        assertThat(person.getAddress().getCountry()).startsWith(prefix);

        person.getAddress().getPhoneNumbers().forEach(phone -> {
            assertThat(phone.getCountryCode()).startsWith(prefix);
            assertThat(phone.getNumber()).startsWith(prefix);
        });

        for (Pet pet : person.getPets()) {
            assertThat(pet.getName()).startsWith(prefix);
        }
    }

}
