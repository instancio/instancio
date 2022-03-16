package org.instancio.api.features;

import org.instancio.Instancio;
import org.instancio.exception.InstancioException;
import org.instancio.pojo.basic.ClassWithInitializedField;
import org.instancio.pojo.basic.IntHolder;
import org.instancio.pojo.basic.IntegerHolder;
import org.instancio.testsupport.tags.NonDeterministicTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@NonDeterministicTag
class WithNullableFieldTest {

    private static final int SAMPLE_SIZE = 30;

    @Test
    @DisplayName("A nullable field will be randomly set to null")
    void nullableFieldIsRandomlySetToNull() {
        Set<Object> results = new HashSet<>();

        for (int i = 0; i < SAMPLE_SIZE; i++) {
            final IntegerHolder holder = Instancio.of(IntegerHolder.class)
                    .withNullable("value")
                    .create();

            results.add(holder.getValue());
        }

        assertThat(results)
                .hasSizeGreaterThan(5)
                .containsNull();
    }

    @Test
    @DisplayName("Specifying nullable for a primitive field throws an exception")
    void nullableWithPrimitiveFieldThrowsException() {
        assertThatThrownBy(
                () -> Instancio.of(IntHolder.class)
                        .withNullable("value")
                        .create())
                .isInstanceOf(InstancioException.class)
                .hasMessage("Primitive field 'private int org.instancio.pojo.basic.IntHolder.value' cannot be set to null");
    }

    @Test
    @DisplayName("A nullable field with a default value will be randomly overwritten with null")
    void nullableInitializedField() {
        Set<Object> results = new HashSet<>();

        for (int i = 0; i < SAMPLE_SIZE; i++) {
            final ClassWithInitializedField holder = Instancio.of(ClassWithInitializedField.class)
                    .withNullable("value")
                    .create();

            results.add(holder.getValue());
        }

        assertThat(results)
                .hasSizeGreaterThan(5)
                .containsNull();
    }
}
