package org.instancio;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class BindingTargetImplTest {

    @Test
    void verifyEqualsAndHashcode() {
        EqualsVerifier.forClass(Bindings.BindingTargetImpl.class).verify();
    }
}
