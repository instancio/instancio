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
package org.instancio.test.support.pojo.dynamic;

import org.instancio.test.support.pojo.person.Gender;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

public class DynPerson extends DynPojoBase {

    // This field is intentionatilly non-static
    @SuppressWarnings("all")
    private final String finalField = "a final field";

    private static final String UUID = "uuid";
    private static final String NAME = "name";
    private static final String ADDRESS = "address";
    private static final String GENDER = "gender";
    private static final String AGE = "age";
    private static final String LAST_MODIFIED = "lastModified";
    private static final String DATE = "date";
    private static final String PETS = "pets";

    protected DynPerson() {
        // cannot be populated without the default constructor (the data map is null)
    }

    public DynPerson(final String name, final int age) {
        setName(name);
        setAge(age);
    }

    public UUID getUuid() {
        return get(UUID);
    }

    public void setUuid(final UUID uuid) {
        set(UUID, uuid);
    }

    public String getName() {
        return get(NAME);
    }

    public void setName(final String name) {
        set(NAME, name);
    }

    public DynAddress getAddress() {
        return get(ADDRESS);
    }

    public void setAddress(final DynAddress address) {
        set(ADDRESS, address);
    }

    public Gender getGender() {
        return get(GENDER);
    }

    public void setGender(final Gender gender) {
        set(GENDER, gender);
    }

    public int getAge() {
        return get(AGE);
    }

    public void setAge(final int age) {
        set(AGE, age);
    }

    public LocalDateTime getLastModified() {
        return get(LAST_MODIFIED);
    }

    public void setLastModified(final LocalDateTime lastModified) {
        set(LAST_MODIFIED, lastModified);
    }

    public Date getDate() {
        return get(DATE);
    }

    public void setDate(final Date date) {
        set(DATE, date);
    }

    public DynPet[] getPets() {
        return get(PETS);
    }

    public void setPets(final DynPet[] pets) {
        set(PETS, pets);
    }
}