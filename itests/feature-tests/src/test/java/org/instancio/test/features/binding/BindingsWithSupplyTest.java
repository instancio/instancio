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
package org.instancio.test.features.binding;

import org.instancio.Bindings;
import org.instancio.Generator;
import org.instancio.Instancio;
import org.instancio.exception.InstancioApiException;
import org.instancio.test.support.pojo.basic.LongHolder;
import org.instancio.test.support.pojo.person.Address;
import org.instancio.test.support.pojo.person.Person;
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
import static org.instancio.Bindings.field;
import static org.instancio.Bindings.of;

@FeatureTag(Feature.BINDING)
class BindingsWithSupplyTest {

    private static final long EXPECTED_VALUE = 2L;

    @Test
    @DisplayName("Should bind to primitive but not wrapper")
    void primitiveBinding() {
        final LongHolder result = Instancio.of(LongHolder.class)
                .supply(all(long.class), () -> EXPECTED_VALUE)
                .create();

        assertThat(result.getPrimitive()).isEqualTo(EXPECTED_VALUE);
        assertThat(result.getWrapper()).isNotEqualTo(EXPECTED_VALUE);
    }

    @Test
    @DisplayName("Should bind to wrapper but not primitive")
    void wrapperBinding() {
        final LongHolder result = Instancio.of(LongHolder.class)
                .supply(all(Long.class), () -> EXPECTED_VALUE)
                .create();

        assertThat(result.getWrapper()).isEqualTo(EXPECTED_VALUE);
        assertThat(result.getPrimitive()).isNotEqualTo(EXPECTED_VALUE);
    }

    @Test
    @DisplayName("Should bind to both, primitive and wrapper")
    void primitiveAndWrapperBinding() {
        final LongHolder result = Instancio.of(LongHolder.class)
                .supply(allLongs(), () -> EXPECTED_VALUE)
                .create();

        assertThat(result.getWrapper()).isEqualTo(EXPECTED_VALUE);
        assertThat(result.getPrimitive()).isEqualTo(EXPECTED_VALUE);
    }

    @Test
    @DisplayName("Composite binding with compatible types")
    void compositeBinding() {
        final String expectedString = "foo";

        final Person result = Instancio.of(Person.class)
                .supply(Bindings.of(
                                field("name"),
                                field(Address.class, "city")),
                        () -> expectedString)
                .create();

        assertThat(result.getName()).isEqualTo(expectedString);
        assertThat(result.getAddress().getCity()).isEqualTo(expectedString);
        assertThat(result.getAddress().getCountry()).isNotEqualTo(expectedString);
    }

    @Test
    @DisplayName("Composite binding with non-compatible types")
    void compositeBindingWithNonCompatibleTypes() {
        assertThatThrownBy(() -> Instancio.of(Person.class)
                .supply(of(allInts(), allStrings()), () -> "some value")
                .create())
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContainingAll("Caused by: Can not set int field", "to java.lang.String");
    }


    @Test
    @DisplayName("Supply with custom Generator")
    void supplyWithCustomerGenerator() {
        final int expectedLength = 20;
        final Generator<String> generator = random -> random.alphabetic(expectedLength);

        final Person result = Instancio.of(Person.class)
                .supply(allStrings(), generator)
                .create();

        assertThat(result.getName()).hasSize(expectedLength);
        assertThat(result.getAddress().getCity()).hasSize(expectedLength);
        assertThat(result.getName()).isNotEqualTo(result.getAddress().getCity());
    }

    @Test
    @DisplayName("Supply with custom Generator, targeting wrong class")
    void supplyWithCustomerGeneratorWithWrongType() {
        final Generator<String> generator = random -> "some value";
        assertThatThrownBy(() -> Instancio.of(Person.class)
                .supply(allInts(), generator)
                .create())
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContainingAll("Caused by: Can not set int field", " to java.lang.String");
    }
}
