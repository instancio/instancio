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
package org.instancio.test.features.assignmenttype.method;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.AssignmentType;
import org.instancio.settings.Keys;
import org.instancio.settings.OnSetMethodError;
import org.instancio.settings.OnSetMethodNotFound;
import org.instancio.settings.OnSetMethodUnmatched;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.dynamic.DynAddress;
import org.instancio.test.support.pojo.dynamic.DynPerson;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.tags.RunWith;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith.MethodAssignmentOnly
@FeatureTag({Feature.ASSIGNMENT_TYPE, Feature.ASSIGNMENT_TYPE_METHOD})
@ExtendWith(InstancioExtension.class)
class MethodAssignmentDynPersonTest {

    @WithSettings
    private final Settings settings = Settings.create()
            .set(Keys.ASSIGNMENT_TYPE, AssignmentType.METHOD)
            .set(Keys.ON_SET_METHOD_UNMATCHED, OnSetMethodUnmatched.INVOKE)
            .set(Keys.ON_SET_METHOD_NOT_FOUND, OnSetMethodNotFound.IGNORE)
            .set(Keys.ON_SET_METHOD_ERROR, OnSetMethodError.FAIL);

    @Test
    void createDynPerson() {
        final DynPerson person = Instancio.create(DynPerson.class);

        assertThat(person.getUuid()).isNotNull();
        assertThat(person.getName()).isNotBlank();
        assertThat(person.getGender()).isNotNull();
        assertThat(person.getAge()).isPositive();
        assertThat(person.getLastModified()).isNotNull();
        assertThat(person.getDate()).isNotNull();

        assertThat(person.getPets())
                .isNotEmpty()
                .allSatisfy(pet -> assertThat(pet.getName()).isNotBlank());

        assertThat(person.getData()).as("person properties").hasSize(8);

        final DynAddress address = person.getAddress();

        assertThat(address).isNotNull();
        assertThat(address.getCity()).isNotBlank();
        assertThat(address.getCountry()).isNotBlank();
        assertThat(address.getPhoneNumbers())
                .isNotEmpty()
                .allSatisfy(phone -> {
                    assertThat(phone.getCountryCode()).isNotBlank();
                    assertThat(phone.getNumber()).isNotBlank();
                    assertThat(phone.getData()).as("phone properties").hasSize(2);
                });

        assertThat(address.getData()).as("address properties").hasSize(4);
    }
}
