/*
 * Copyright 2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.instancio.api.features;

import org.instancio.Instancio;
import org.instancio.exception.InstancioException;
import org.instancio.pojo.basic.ClassWithInitializedField;
import org.instancio.pojo.basic.IntegerHolder;
import org.instancio.testsupport.tags.NonDeterministicTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Bindings.field;

@NonDeterministicTag
class WithNullableFieldTest {

    private static final int SAMPLE_SIZE = 30;

    @Test
    @DisplayName("A nullable field will be randomly set to null")
    void nullableFieldIsRandomlySetToNull() {
        Set<Object> results = new HashSet<>();

        for (int i = 0; i < SAMPLE_SIZE; i++) {
            final IntegerHolder holder = Instancio.of(IntegerHolder.class)
                    .withNullable(field("wrapper"))
                    .create();

            results.add(holder.getWrapper());
        }

        assertThat(results)
                .hasSizeGreaterThan(5)
                .containsNull();
    }

    @Test
    @DisplayName("Specifying nullable for a primitive field throws an exception")
    void nullableWithPrimitiveFieldThrowsException() {
        assertThatThrownBy(
                () -> Instancio.of(IntegerHolder.class)
                        .withNullable(field("primitive"))
                        .create())
                .isInstanceOf(InstancioException.class)
                .hasMessage("Primitive field 'private int org.instancio.pojo.basic.IntegerHolder.primitive' cannot be set to null");
    }

    @Test
    @DisplayName("A nullable field with a default value will be randomly overwritten with null")
    void nullableInitializedField() {
        Set<Object> results = new HashSet<>();

        for (int i = 0; i < SAMPLE_SIZE; i++) {
            final ClassWithInitializedField holder = Instancio.of(ClassWithInitializedField.class)
                    .withNullable(field("value"))
                    .create();

            results.add(holder.getValue());
        }

        assertThat(results)
                .hasSizeGreaterThan(5)
                .containsNull();
    }
}
