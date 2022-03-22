package org.instancio.api.errorhandling;

import org.instancio.Instancio;
import org.instancio.exception.InstancioApiException;
import org.instancio.pojo.generics.container.ItemContainer;
import org.instancio.pojo.person.Person;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Bindings.field;

class ErrorMessagesTest {

    private static final Class<InstancioApiException> API_EXCEPTION = InstancioApiException.class;

    @Test
    void unboundTypeVariablesErrorMessage() {
        assertThatThrownBy(() -> Instancio.of(ItemContainer.class).create())
                .isExactlyInstanceOf(API_EXCEPTION)
                .hasMessageContainingAll(
                        "Class 'org.instancio.pojo.generics.container.ItemContainer' has 2 type parameters: [X, Y].",
                        "Please specify the required type parameters using 'withTypeParameters(Class... types)`");

        assertThatThrownBy(() -> Instancio.of(List.class).create())
                .isExactlyInstanceOf(API_EXCEPTION)
                .hasMessageContainingAll(
                        "Class 'java.util.List' has 1 type parameters: [E].",
                        "Please specify the required type parameters using 'withTypeParameters(Class... types)`");
    }

    @Test
    void invalidTypeCreatedByGenerator() {
        assertThatThrownBy(() ->
                Instancio.of(Person.class)
                        .supply(field("name"), () -> 123)
                        .create())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Can not set java.lang.String field org.instancio.pojo.person.Person.name to java.lang.Integer");
    }

    @Test
    void invalidFieldBinding() {
        final String invalidField = "does-not-exist";
        assertThatThrownBy(() ->
                Instancio.of(Person.class)
                        .supply(field(invalidField), () -> null)
                        .create())
                .isInstanceOf(API_EXCEPTION)
                .hasMessage("Invalid field '%s' for class %s", invalidField, Person.class.getName());
    }
}
