/*
 * Copyright 2022-2024 the original author or authors.
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
package org.instancio.test.features.selector;

import org.instancio.Instancio;
import org.instancio.TargetSelector;
import org.instancio.TypeToken;
import org.instancio.internal.util.Sonar;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.generics.ListExtendsItemInterface;
import org.instancio.test.support.pojo.generics.ListExtendsNumber;
import org.instancio.test.support.pojo.generics.ListExtendsPair;
import org.instancio.test.support.pojo.generics.MiscFields;
import org.instancio.test.support.pojo.generics.basic.Item;
import org.instancio.test.support.pojo.generics.basic.Pair;
import org.instancio.test.support.pojo.generics.foobarbaz.Bar;
import org.instancio.test.support.pojo.generics.foobarbaz.Baz;
import org.instancio.test.support.pojo.generics.foobarbaz.FooBarBazContainer;
import org.instancio.test.support.pojo.interfaces.ItemInterface;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.tags.GenericsTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.all;
import static org.instancio.Select.allLongs;
import static org.instancio.Select.allStrings;
import static org.instancio.Select.types;

@GenericsTag
@FeatureTag(Feature.SELECTOR)
@ExtendWith(InstancioExtension.class)
class SelectWithGenericFieldsTest {

    @Test
    @DisplayName("Bound type parameters should have expected values")
    void verifyBoundTypeParametersHaveExpectedValues() {
        final String expectedString = "test";
        final long expectedLongValue = 12345L;
        final UUID expectedUUID = UUID.randomUUID();

        final MiscFields<String, Long, UUID> result = Instancio.of(new TypeToken<MiscFields<String, Long, UUID>>() {})
                .set(allStrings(), expectedString)
                .set(allLongs(), expectedLongValue)
                .set(all(UUID.class), expectedUUID)
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
    @DisplayName("Selecting wildcard extends non-generic type")
    void selectingWildcardExtendsNumber() {
        // Given declared field: List<? extends Number> list
        final double expectedValue = 1.234;
        final ListExtendsNumber result = Instancio.of(ListExtendsNumber.class)
                .set(all(Number.class), expectedValue)
                .create();

        assertThat(result.getList())
                .as("All numbers in the list should have expected value")
                .isNotEmpty()
                .allSatisfy(it -> assertThat(it).isEqualTo(expectedValue));
    }

    @Test
    @DisplayName("Selecting wildcard extends generic concrete type")
    void selectingWildcardExtendsPair() {
        // Given declared field: List<? extends Pair<Long, String>> list
        final String expectedValue = "foo";
        final ListExtendsPair result = Instancio.of(ListExtendsPair.class)
                .set(allStrings(), expectedValue)
                .create();

        assertThat(result.getList())
                .as("All pairs in the list should have expected value")
                .isNotEmpty()
                .hasOnlyElementsOfTypes(Pair.class)
                .allSatisfy(it -> assertThat(it.getRight()).isEqualTo(expectedValue));
    }

    @Test
    @DisplayName("Selecting wildcard extends generic abstract type")
    void selectingWildcardExtendsItemInterface() {
        // Given declared field: List<? extends ItemInterface<String>> list
        final String expectedValue = "foo";
        final ListExtendsItemInterface result = Instancio.of(ListExtendsItemInterface.class)
                .subtype(types().of(ItemInterface.class), Item.class)
                .set(allStrings(), expectedValue)
                .create();

        assertThat(result.getList())
                .as("All pairs in the list should have expected value")
                .isNotEmpty()
                .hasOnlyElementsOfTypes(Item.class)
                .allSatisfy(it -> assertThat(it.getValue()).isEqualTo(expectedValue));
    }

    @ParameterizedTest
    @MethodSource("selectAllBars")
    @DisplayName("Selecting parameterized class with matching type argument")
    void selectingParameterizedClassWithMatchingTypeArgument(final TargetSelector selector) {
        // Given declared field: Foo<Bar<Baz<String>>> item
        // and supplied value:       Bar<Baz<String>>
        final Bar<Baz<String>> expectedBar = Instancio.create(new TypeToken<Bar<Baz<String>>>() {});

        final FooBarBazContainer result = Instancio.of(FooBarBazContainer.class)
                .supply(selector, () -> expectedBar)
                .create();

        // Supplied value should be same as actual
        assertThat(result.getItem().getFooValue()).isSameAs(expectedBar);
        assertThat(result.getItem().getFooValue().getBarValue().getBazValue())
                .isEqualTo(expectedBar.getBarValue().getBazValue());
    }

    @ParameterizedTest
    @MethodSource("selectAllBars")
    @DisplayName("Selecting parameterized type with incorrect type argument")
    @SuppressWarnings(Sonar.ONE_METHOD_WHEN_TESTING_EXCEPTIONS)
    void selectingParameterizedClassWithIncorrectTypeArgument(final TargetSelector selector) {
        // Given declared field: Foo<Bar<Baz<String>>> item
        // and supplied value:       Bar<Baz<Integer>>
        final Bar<Baz<Integer>> expectedBar = Instancio.create(new TypeToken<Bar<Baz<Integer>>>() {});

        final FooBarBazContainer result = Instancio.of(FooBarBazContainer.class)
                .supply(selector, () -> expectedBar)
                .create();

        // Supplied value is same as actual
        //noinspection AssertBetweenInconvertibleTypes
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

    private static Stream<Arguments> selectAllBars() {
        return Stream.of(
                Arguments.of(all(Bar.class)),
                Arguments.of(types().of(Bar.class)));
    }
}