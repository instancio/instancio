package org.instancio;

import org.instancio.exception.InstancioException;
import org.instancio.pojo.generics.container.ItemContainer;
import org.instancio.pojo.generics.foobarbaz.Foo;
import org.instancio.pojo.generics.foobarbaz.FooContainer;
import org.instancio.pojo.person.Address;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InstancioGenericsTest {

    @Test
    void address() {
        Address result = Instancio.of(Address.class).create();

        System.out.println(result);
        assertThat(result.getPhoneNumbers()).isNotEmpty();

        result.getPhoneNumbers().forEach(phone -> {
            assertThat(phone.getCountryCode()).isNotBlank();
            assertThat(phone.getNumber()).isNotBlank();
        });
    }

    @Test
    void fooContainerWithUserSuppliedInstance() {
        final String expectedFooString = "expected-foo";
        FooContainer result = Instancio.of(FooContainer.class)
                .with("stringFoo", () -> {
                    Foo<String> foo = new Foo<>();
                    foo.setFooValue(expectedFooString);
                    return foo;
                })
                .create();

        assertThat(result).isNotNull();
        assertThat(result.getStringFoo()).isNotNull();
        assertThat(result.getStringFoo().getFooValue()).isEqualTo(expectedFooString);
        assertThat(result.getStringFoo().getOtherFooValue()).as("Value was not set").isNull();
    }


    @Test
    void unboundTypeVariablesErrorMessage() {
        assertThatThrownBy(() -> Instancio.of(ItemContainer.class).create())
                .isInstanceOf(InstancioException.class)
                .hasMessage("Generic class %s " +
                        "has 2 type parameters: [X, Y]. Please specify all type parameters using " +
                        "'withType(Class... types)`", ItemContainer.class.getName());

        assertThatThrownBy(() -> Instancio.of(List.class).create())
                .hasMessage("Generic class java.util.List has 1 type parameters: [E]." +
                        " Please specify all type parameters using 'withType(Class... types)`");
    }


}
