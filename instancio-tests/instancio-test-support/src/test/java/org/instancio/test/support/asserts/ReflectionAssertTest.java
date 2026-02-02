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
package org.instancio.test.support.asserts;

import org.instancio.test.support.pojo.person.Address;
import org.instancio.test.support.pojo.person.Phone;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.test.support.asserts.ReflectionAssert.assertThatObject;

@SuppressWarnings("java:S5778")
class ReflectionAssertTest {

    private static final String FOO = "foo";

    @Test
    void hasAllFieldsOfTypeSetToNull() {
        assertThatObject(new Address()).hasAllFieldsOfTypeSetToNull(String.class);

        final Address address = Address.builder()
                .country(FOO)
                .build();

        assertThatThrownBy(() -> assertThatObject(address).hasAllFieldsOfTypeSetToNull(String.class))
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("Expected 'Address.country' to be null, but was: 'foo'");
    }

    @Test
    void hasAllFieldsOfTypeEqualToFoo() {
        assertThatObject(createAddressFilledWithFoo()).hasAllFieldsOfTypeEqualTo(String.class, FOO);
    }

    @Test
    void hasAllFieldsOfTypeEqualToWithBar() {
        final Address address = createAddressFilledWithFoo();
        address.setCity("bar");

        assertThatThrownBy(() -> assertThatObject(address).hasAllFieldsOfTypeEqualTo(String.class, FOO))
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("Expected 'Address.city' to be equal to 'foo', but was: 'bar'");
    }

    @Test
    void hasAllFieldsOfTypeNotEqualTo() {
        final Address address = Address.builder().build();
        address.setCity(FOO);

        assertThatThrownBy(() -> assertThatObject(address).doesNotHaveAllFieldsOfTypeEqualTo(String.class, FOO))
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("Expected 'Address.city' to NOT be equal to 'foo'");
    }

    @Test
    void isFullyPopulatedWithEmptyList() {
        final Address address = createAddressFilledWithFoo();
        address.setPhoneNumbers(Collections.emptyList());

        assertThatThrownBy(() -> assertThatObject(address).isFullyPopulated())
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("Unexpected collection results; field: Address.phoneNumbers")
                .hasMessageContaining("Expected size to be between: 2 and 6 but was: 0");
    }

    @Test
    void isFullyPopulatedWithNullObject() {
        final Address address = createAddressFilledWithFoo();

        address.setPhoneNumbers(Arrays.asList(
                new Phone("+1", "123"),
                new Phone("+2", null)));

        assertThatThrownBy(() -> assertThatObject(address).isFullyPopulated())
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("[Field 'Phone.number' is null]");
    }

    @Test
    void isFullyPopulatedMap() {
        assertThatThrownBy(() -> assertThatObject(new HashMap<>()).isFullyPopulated())
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("Expected size to be between: 2 and 6 but was: 0");

    }

    private static Address createAddressFilledWithFoo() {
        return Address.builder()
                .street(FOO)
                .city(FOO)
                .country(FOO)
                .phoneNumbers(Arrays.asList(
                        new Phone(FOO, FOO),
                        new Phone(FOO, FOO)))
                .build();
    }
}
