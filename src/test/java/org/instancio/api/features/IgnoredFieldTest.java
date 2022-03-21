package org.instancio.api.features;

import org.instancio.Instancio;
import org.instancio.pojo.basic.ClassWithInitializedField;
import org.instancio.testsupport.tags.NonDeterministicTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Bindings.field;

@NonDeterministicTag
class IgnoredFieldTest {

    @Test
    @DisplayName("Ignored field should retain the original value")
    void fieldIsIgnored() {
        final ClassWithInitializedField holder = Instancio.of(ClassWithInitializedField.class)
                .ignore(field("value"))
                .create();

        assertThat(holder.getValue()).isEqualTo(ClassWithInitializedField.DEFAULT_FIELD_VALUE);
    }
}
