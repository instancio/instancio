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
package org.instancio.benchmark.pojo.person;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class Person {
    private UUID id;
    private String firstName;
    private String lastName;
    private LocalDate dob;
    private List<Address> addresses;
    private List<String> hobbies;
    private Pet[] pets;
    private Person mom;
    private Person dad;
    private List<Person> siblings;

    public UUID getId() {
        return id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(final LocalDate dob) {
        this.dob = dob;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(final List<Address> addresses) {
        this.addresses = addresses;
    }

    public List<String> getHobbies() {
        return hobbies;
    }

    public void setHobbies(final List<String> hobbies) {
        this.hobbies = hobbies;
    }

    public Pet[] getPets() {
        return pets;
    }

    public void setPets(final Pet[] pets) {
        this.pets = pets;
    }

    public Person getMom() {
        return mom;
    }

    public void setMom(final Person mom) {
        this.mom = mom;
    }

    public Person getDad() {
        return dad;
    }

    public void setDad(final Person dad) {
        this.dad = dad;
    }

    public List<Person> getSiblings() {
        return siblings;
    }

    public void setSiblings(final List<Person> siblings) {
        this.siblings = siblings;
    }
}
