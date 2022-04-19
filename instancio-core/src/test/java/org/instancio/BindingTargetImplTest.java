package org.instancio;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BindingTargetImplTest {

    @Test
    void verifyEqualsAndHashcode() {
        EqualsVerifier.forClass(Bindings.BindingTargetImpl.class).verify();
    }
}
