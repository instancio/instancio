package org.instancio;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class SelectorImplTest {

    @Test
    void verifyEqualsAndHashcode() {
        EqualsVerifier.forClass(Select.SelectorImpl.class).verify();
    }
}