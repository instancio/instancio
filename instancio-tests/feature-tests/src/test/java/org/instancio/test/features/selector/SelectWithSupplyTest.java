/*
 *  Copyright 2022-2024 the original author or authors.
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
package org.instancio.test.features.selector;

import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.Select;
import org.instancio.exception.InstancioApiException;
import org.instancio.generator.Generator;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.basic.LongHolder;
import org.instancio.test.support.pojo.person.Address;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.all;
import static org.instancio.Select.allInts;
import static org.instancio.Select.allLongs;
import static org.instancio.Select.allStrings;
import static org.instancio.Select.field;

@FeatureTag(Feature.SELECTOR)
@ExtendWith(InstancioExtension.class)
class SelectWithSupplyTest {

    private static final long EXPECTED_VALUE = -2L;

    @Test
    @DisplayName("Should select primitive but not wrapper")
    void primitiveSelectorGroup() {
        final LongHolder result = Instancio.of(LongHolder.class)
                .supply(all(long.class), () -> EXPECTED_VALUE)
                .create();

        assertThat(result.getPrimitive()).isEqualTo(EXPECTED_VALUE);
        assertThat(result.getWrapper()).isNotEqualTo(EXPECTED_VALUE);
    }

    @Test
    @DisplayName("Should select wrapper but not primitive")
    void wrapperSelectorGroup() {
        final LongHolder result = Instancio.of(LongHolder.class)
                .supply(all(Long.class), () -> EXPECTED_VALUE)
                .create();

        assertThat(result.getWrapper()).isEqualTo(EXPECTED_VALUE);
        assertThat(result.getPrimitive()).isNotEqualTo(EXPECTED_VALUE);
    }

    @Test
    @DisplayName("Should select both, primitive and wrapper")
    void primitiveAndWrapperSelectorGroup() {
        final LongHolder result = Instancio.of(LongHolder.class)
                .supply(allLongs(), () -> EXPECTED_VALUE)
                .create();

        assertThat(result.getWrapper()).isEqualTo(EXPECTED_VALUE);
        assertThat(result.getPrimitive()).isEqualTo(EXPECTED_VALUE);
    }

    @Test
    @DisplayName("Selector group with compatible types")
    void selectorGroup() {
        final String expectedString = "foo";

        final Person result = Instancio.of(Person.class)
                .supply(Select.all(
                                field("name"),
                                field(Address.class, "city")),
                        () -> expectedString)
                .create();

        assertThat(result.getName()).isEqualTo(expectedString);
        assertThat(result.getAddress().getCity()).isEqualTo(expectedString);
        assertThat(result.getAddress().getCountry()).isNotEqualTo(expectedString);
    }

    @Test
    @DisplayName("Selector group with incompatible types")
    void selectorGroupWithIncompatibleTypes() {
        final InstancioApi<Person> api = Instancio.of(Person.class)
                .supply(all(allInts(), allStrings()), () -> "some value");

        assertThatThrownBy(api::create)
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContainingAll("Can not set int field", "to java.lang.String");
    }

    @Test
    @DisplayName("Supply with custom Generator")
    void supplyWithCustomerGenerator() {
        final int expectedLength = 20;
        final Generator<String> generator = random -> random.upperCaseAlphabetic(expectedLength);

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
        final InstancioApi<Person> api = Instancio.of(Person.class)
                .supply(allInts(), generator);

        assertThatThrownBy(api::create)
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContainingAll("Can not set int field", " to java.lang.String");
    }
}