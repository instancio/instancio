package org.instancio.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.fail;

class VerifyTest {

    private static final String MESSAGE = "a message: %s";

    @Test
    void notNull() {
        assertThatThrownBy(() -> Verify.notNull(null, MESSAGE, "msg-val"))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("a message: msg-val");

        assertThat(Verify.notNull("value", MESSAGE)).isEqualTo("value");
    }

    @Test
    void notEmpty() {
        fail("TODO");
    }

    @Test
    void isTrue() {
        fail("TODO");
    }
}
