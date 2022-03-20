package org.instancio.api.errorhandling;

import org.instancio.Instancio;
import org.instancio.exception.InstancioApiException;
import org.instancio.pojo.generics.container.ItemContainer;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ErrorMessagesTest {

    @Test
    void unboundTypeVariablesErrorMessage() {
        assertThatThrownBy(() -> Instancio.of(ItemContainer.class).create())
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContainingAll(
                        "Class 'org.instancio.pojo.generics.container.ItemContainer' has 2 type parameters: [X, Y].",
                        "Please specify the required type parameters using 'withTypeParameters(Class... types)`");

        assertThatThrownBy(() -> Instancio.of(List.class).create())
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContainingAll(
                        "Class 'java.util.List' has 1 type parameters: [E].",
                        "Please specify the required type parameters using 'withTypeParameters(Class... types)`");
    }
}
