package org.instancio.util;

import org.instancio.pojo.person.Address;
import org.instancio.pojo.person.Person;
import org.instancio.pojo.person.Phone;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReflectionUtilsTest {


    @Test
    void getField() {
        assertThat(ReflectionUtils.getField(Person.class, "name").getName()).isEqualTo("name");
        assertThat(ReflectionUtils.getField(Person.class, "address").getName()).isEqualTo("address");
        assertThat(ReflectionUtils.getField(Person.class, "address.city").getName()).isEqualTo("city");
        assertThat(ReflectionUtils.getField(Person.class, "address.phoneNumbers").getName()).isEqualTo("phoneNumbers");
        assertThat(ReflectionUtils.getField(Person.class, "pets").getName()).isEqualTo("pets");
        assertThat(ReflectionUtils.getField(Person.class, "age").getName()).isEqualTo("age");
        assertThat(ReflectionUtils.getField(Person.class, "gender").getName()).isEqualTo("gender");
        assertThat(ReflectionUtils.getField(Person.class, "address.phoneNumbers").getName()).isEqualTo("phoneNumbers");

        assertThat(ReflectionUtils.getField(Address.class, "phoneNumbers").getName()).isEqualTo("phoneNumbers");
        assertThat(ReflectionUtils.getField(Phone.class, "countryCode").getName()).isEqualTo("countryCode");
    }

    @Test
    void invalidFieldPath() {
        assertThatThrownBy(() -> ReflectionUtils.getField(Address.class, "phoneNumbers.countryCode"))
                .isInstanceOf(RuntimeException.class)
                // TODO  better error message
                .hasMessage("Error getting field 'phoneNumbers.countryCode' from class org.stubber.pojo.Address");
    }
}
