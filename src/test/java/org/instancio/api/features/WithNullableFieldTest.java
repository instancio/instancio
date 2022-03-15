package org.instancio.api.features;

import org.instancio.Instancio;
import org.instancio.pojo.basic.ClassWithInitializedField;
import org.instancio.pojo.basic.StringHolder;
import org.instancio.testsupport.tags.NonDeterministicTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@NonDeterministicTag
class WithNullableFieldTest {

    @Test
    @DisplayName("A 'nullable' will randomly be set to null")
    void nullableField() {
        Set<Object> results = new HashSet<>();

        for (int i = 0; i < 20; i++) {
            final StringHolder holder = Instancio.of(StringHolder.class)
                    .withNullable("value")
                    .create();

            results.add(holder.getValue());
        }

        assertThat(results)
                .hasSizeGreaterThan(5)
                .containsNull();
    }

    @Test
    @DisplayName("A 'nullable' field with a default value will be randomly overwritten with null")
    void nullableInitializedField() {
        Set<Object> results = new HashSet<>();

        for (int i = 0; i < 20; i++) {
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
