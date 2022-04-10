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

import org.instancio.Instancio;
import org.instancio.TypeToken;
import org.instancio.test.support.pojo.generics.ListExtendsNumber;
import org.instancio.test.support.pojo.generics.MiscFields;
import org.instancio.test.support.pojo.generics.foobarbaz.Bar;
import org.instancio.test.support.pojo.generics.foobarbaz.Baz;
import org.instancio.test.support.pojo.generics.foobarbaz.FooBarBazContainer;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.tags.GenericsTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Bindings.all;
import static org.instancio.Bindings.allLongs;
import static org.instancio.Bindings.allStrings;

@GenericsTag
@FeatureTag(Feature.BINDING)
class BindingsWithGenericFieldsTest {

    @Test
    @DisplayName("Bound type parameters should have expected values")
    void verifyBoundTypeParametersHaveExpectedValues() {
        final String expectedString = "test";
        final long expectedLongValue = 12345L;
        final UUID expectedUUID = UUID.randomUUID();

        final MiscFields<String, Long, UUID> result = Instancio.of(new TypeToken<MiscFields<String, Long, UUID>>() {})
                .supply(allStrings(), () -> expectedString)
                .supply(allLongs(), () -> expectedLongValue)
                .supply(all(UUID.class), () -> expectedUUID)
                .create();

        // verify String
        assertThat(result.getFieldA()).isEqualTo(expectedString);
        assertThat(result.getFooBarBazString().getFooValue().getBarValue().getBazValue()).isEqualTo(expectedString);
        assertThat(result.getPairAB().getLeft()).isEqualTo(expectedString);
        assertThat(result.getPairBA().getRight()).isEqualTo(expectedString);
        assertThat(result.getPairFooBarStringB().getLeft().getFooValue().getBarValue()).isEqualTo(expectedString);

        // verify long
        assertThat(result.getFooBarPairBC().getFooValue().getBarValue().getLeft()).isEqualTo(expectedLongValue);
        assertThat(result.getPairAB().getRight()).isEqualTo(expectedLongValue);
        assertThat(result.getPairBA().getLeft()).isEqualTo(expectedLongValue);
        assertThat(result.getPairFooBarStringB().getRight()).isEqualTo(expectedLongValue);

        // verify UUID
        assertThat(result.getArrayOfCs()).allSatisfy(it -> assertThat(it).isEqualTo(expectedUUID));
        assertThat(result.getFooBarPairBC().getFooValue().getBarValue().getRight()).isEqualTo(expectedUUID);
        assertThat(result.getListOfCs()).allSatisfy(it -> assertThat(it).isEqualTo(expectedUUID));
    }

    @Test
    @DisplayName("Binding wildcard extends")
    void bindingWildcardExtends() {
        // Given declared field: List<? extends Number> list
        final double expectedValue = 1.234;
        final ListExtendsNumber result = Instancio.of(ListExtendsNumber.class)
                .supply(all(Number.class), () -> expectedValue)
                .create();

        assertThat(result.getList())
                .as("All numbers in the list should have expected value")
                .isNotEmpty()
                .allSatisfy(it -> assertThat(it).isEqualTo(expectedValue));
    }

    @Test
    @DisplayName("Binding parameterized class with matching type argument")
    void bindingParameterizedClassWithMatchingTypeArgument() {
        // Given declared field: Foo<Bar<Baz<String>>> item
        // and supplied value:       Bar<Baz<String>>
        final Bar<Baz<String>> expectedBar = Instancio.create(new TypeToken<Bar<Baz<String>>>() {});

        final FooBarBazContainer result = Instancio.of(FooBarBazContainer.class)
                .supply(all(Bar.class), () -> expectedBar)
                .create();

        // Supplied value should be same as actual
        assertThat(result.getItem().getFooValue()).isSameAs(expectedBar);
        assertThat(result.getItem().getFooValue().getBarValue().getBazValue())
                .isEqualTo(expectedBar.getBarValue().getBazValue());
    }

    @Test
    @SuppressWarnings("AssertBetweenInconvertibleTypes")
    @DisplayName("Binding parameterized type with incorrect type argument")
    void bindingParameterizedClassWithIncorrectTypeArgument() {
        // Given declared field: Foo<Bar<Baz<String>>> item
        // and supplied value:       Bar<Baz<Integer>>
        final Bar<Baz<Integer>> expectedBar = Instancio.create(new TypeToken<Bar<Baz<Integer>>>() {});

        final FooBarBazContainer result = Instancio.of(FooBarBazContainer.class)
                .supply(all(Bar.class), () -> expectedBar)
                .create();

        // Supplied value is same as actual
        assertThat(result.getItem().getFooValue()).isSameAs(expectedBar);

        // Class cast is thrown when accessing the value
        assertThatThrownBy(() -> {
            // Assign to a variable to trigger class cast error
            @SuppressWarnings("unused") final String bazValue = result.getItem()
                    .getFooValue()
                    .getBarValue()
                    .getBazValue();
        }).as("Expecting an error since generic type parameter does not match: " +
                        "supplied Integer but required String")
                .isInstanceOf(ClassCastException.class);
    }

}
