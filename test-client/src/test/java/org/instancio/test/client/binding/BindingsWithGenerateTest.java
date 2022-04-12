/*
 *  Copyright 2022 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.instancio.test.client.binding;

import org.instancio.Bindings;
import org.instancio.Generators;
import org.instancio.Instancio;
import org.instancio.exception.InstancioApiException;
import org.instancio.test.support.pojo.arrays.MiscArrays;
import org.instancio.test.support.pojo.basic.LongHolder;
import org.instancio.test.support.pojo.generics.basic.Item;
import org.instancio.test.support.pojo.person.Address_;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.pojo.person.Person_;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Bindings.all;
import static org.instancio.Bindings.allInts;
import static org.instancio.Bindings.allLongs;
import static org.instancio.Bindings.allStrings;
import static org.instancio.Bindings.of;

@FeatureTag(Feature.BINDING)
class BindingsWithGenerateTest {

    private static final long EXPECTED_VALUE = 2L;

    @Test
    @DisplayName("Should bind to primitive but not wrapper")
    void primitiveBinding() {
        final LongHolder result = Instancio.of(LongHolder.class)
                .generate(all(long.class), gen -> gen.longs().range(EXPECTED_VALUE, EXPECTED_VALUE + 1))
                .create();

        assertThat(result.getPrimitive()).isEqualTo(EXPECTED_VALUE);
        assertThat(result.getWrapper()).isNotEqualTo(EXPECTED_VALUE);
    }

    @Test
    @DisplayName("Should bind to wrapper but not primitive")
    void wrapperBinding() {
        final LongHolder result = Instancio.of(LongHolder.class)
                .generate(all(Long.class), gen -> gen.longs().range(EXPECTED_VALUE, EXPECTED_VALUE + 1))
                .create();

        assertThat(result.getWrapper()).isEqualTo(EXPECTED_VALUE);
        assertThat(result.getPrimitive()).isNotEqualTo(EXPECTED_VALUE);
    }

    @Test
    @DisplayName("Should bind to both, primitive and wrapper")
    void primitiveAndWrapperBinding() {
        final LongHolder result = Instancio.of(LongHolder.class)
                .generate(allLongs(), gen -> gen.longs().range(EXPECTED_VALUE, EXPECTED_VALUE + 1))
                .create();

        assertThat(result.getWrapper()).isEqualTo(EXPECTED_VALUE);
        assertThat(result.getPrimitive()).isEqualTo(EXPECTED_VALUE);
    }

    @Test
    @DisplayName("Composite binding with compatible types")
    void compositeBinding() {
        final int expectedLength = 100;
        final Person result = Instancio.of(Person.class)
                .generate(Bindings.of(Person_.name, Address_.city),
                        gen -> gen.string().length(expectedLength))
                .create();

        assertThat(result.getName()).hasSize(expectedLength);
        assertThat(result.getAddress().getCity()).hasSize(expectedLength);
        assertThat(result.getAddress().getCountry().length()).isNotEqualTo(expectedLength);
    }

    @Test
    @DisplayName("Composite binding with non-compatible types")
    void compositeBindingWithNonCompatibleTypes() {
        assertThatThrownBy(() -> Instancio.of(Person.class)
                .generate(of(allInts(), allStrings()), Generators::ints)
                .create())
                .isInstanceOf(InstancioApiException.class)
                .hasMessageContaining("Method 'ints()' cannot be used for type: java.lang.String");
    }

    @Test
    @DisplayName("Composite binding with different array types")
    void compositeBindingWithDifferentArrayTypes() {
        final int expectedLength = 100;
        final MiscArrays result = Instancio.of(MiscArrays.class)
                .generate(of(
                                all(long[].class),
                                all(Long[].class),
                                all(String[].class),
                                all(Item[].class)),
                        gen -> gen.array().length(expectedLength))
                .create();

        assertThat(result.getPrimitiveLongArray()).hasSize(expectedLength);
        assertThat(result.getWrapperLongArray()).hasSize(expectedLength);
        assertThat(result.getStringArray()).hasSize(expectedLength);
        assertThat(result.getItemStringArray()).hasSize(expectedLength);
        assertThat(result.getItemIntegerArray()).hasSize(expectedLength);
    }
}
