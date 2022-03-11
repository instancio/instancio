package org.instancio.model;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class GenericTypeTest {

    @Test
    void verifyEquals() {
        EqualsVerifier.forClass(GenericType.class).usingGetClass().verify();
    }
}
