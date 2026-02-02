/*
 * Copyright 2022-2026 the original author or authors.
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
package org.instancio.creation.generics;

import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.tags.GenericsTag;
import org.instancio.test.support.tags.NonDeterministicTag;
import org.instancio.test.support.util.Constants;
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
                    assertThat(address.getStreet()).isNotBlank();
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
