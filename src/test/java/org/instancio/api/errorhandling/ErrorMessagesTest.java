package org.instancio.api.errorhandling;

import org.instancio.Instancio;
import org.instancio.exception.InstancioException;
import org.instancio.pojo.generics.container.ItemContainer;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ErrorMessagesTest {

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
