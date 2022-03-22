package org.instancio.creation.generics;

import org.instancio.pojo.person.Person;
import org.instancio.testsupport.Constants;
import org.instancio.testsupport.tags.GenericsTag;
import org.instancio.testsupport.tags.NonDeterministicTag;
import org.instancio.testsupport.templates.CreationTestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@GenericsTag
public class PersonCreationTest extends CreationTestTemplate<Person> {

    @Override
    @NonDeterministicTag("Person.age (int) could be zero")
    protected void verify(Person result) {
        assertThat(result.getAge()).isNotZero();
        assertThat(result.getDate()).isNotNull();
        assertThat(result.getGender()).isNotNull();
        assertThat(result.getLastModified()).isNotNull();
        assertThat(result.getName()).isNotBlank();
        assertThat(result.getPets()).hasSizeBetween(Constants.MIN_SIZE, Constants.MAX_SIZE);
        assertThat(result.getUuid()).isNotNull();

        assertThat(result.getAddress()).isNotNull()
                .satisfies(address -> {
                    assertThat(address.getAddress()).isNotBlank();
                    assertThat(address.getCity()).isNotBlank();
                    assertThat(address.getCountry()).isNotBlank();
                    assertThat(address.getPhoneNumbers())
                            .isNotEmpty()
                            .allSatisfy(phone -> {
                                assertThat(phone.getCountryCode()).isNotBlank();
                                assertThat(phone.getNumber()).isNotBlank();
                            });
                });
    }
}
