/*
 *  Copyright 2022 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.instancio.test.support.pojo.person;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;

@Getter
@EqualsAndHashCode
public class PopulatedPerson {

    private Person person;
    private String someString = "test";
    private int someInt = 1234;

    public PopulatedPerson() {
        this.person = createPerson();
    }

    private Person createPerson() {
        Pet pet = new Pet();
        pet.setName("pet-name");

        final LocalDateTime lastModified = LocalDateTime.of(2020, 12, 15, 9, 30, 55);
        final Person person = new Person();
        person.setUuid(UUID.fromString("57f59ab6-d07b-4cbb-aa55-5c141d420978"));
        person.setName("name");
        person.setAddress(createAddress());
        person.setGender(Gender.OTHER);
        person.setAge(22);
        person.setLastModified(lastModified);
        person.setDate(Date.from(lastModified.atZone(ZoneId.systemDefault()).toInstant()));
        person.setPets(new Pet[]{pet});
        return person;
    }

    private Address createAddress() {
        final Phone phone = new Phone();
        phone.setCountryCode("+1");
        phone.setNumber("123-445-667");

        final Address address = new Address();
        address.setAddress("address");
        address.setCity("city");
        address.setCountry("country");
        address.setPhoneNumbers(Collections.singletonList(phone));
        return address;
    }
}
