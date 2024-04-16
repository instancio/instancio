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
package org.instancio.test.features.subtype;

import org.instancio.Instancio;
import org.instancio.TargetSelector;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.basic.StringHolderAlternativeImpl;
import org.instancio.test.support.pojo.generics.basic.ItemAlternativeImpl;
import org.instancio.test.support.pojo.interfaces.ArrayOfItemInterfaceString;
import org.instancio.test.support.pojo.interfaces.ArrayOfStringHolderInterface;
import org.instancio.test.support.pojo.interfaces.ItemInterface;
import org.instancio.test.support.pojo.interfaces.StringHolderInterface;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.types;

@FeatureTag({
        Feature.SELECTOR,
        Feature.PREDICATE_SELECTOR,
        Feature.SUBTYPE,
        Feature.ARRAY_GENERATOR_SUBTYPE
})
@ExtendWith(InstancioExtension.class)
class ArrayElementSubtypeMappingTest {

    @ParameterizedTest
    @MethodSource("itemInterfaceSelectors")
    @DisplayName("Subtype mapping of a generic class (as array element)")
    void subtypeMappingOfGenericClass(final TargetSelector selector) {
        final ArrayOfItemInterfaceString result = Instancio.of(ArrayOfItemInterfaceString.class)
                .subtype(selector, ItemAlternativeImpl.class)
                .create();

        assertThat(result.getArray()).isNotEmpty()
                .hasOnlyElementsOfType(ItemAlternativeImpl.class)
                .allSatisfy(item -> assertThat(item.getValue()).isNotBlank());
    }

    @ParameterizedTest
    @MethodSource("stringHolderInterfaceSelectors")
    @DisplayName("Subtype mapping of non-generic class (as array element)")
    void subtypeMappingOfNonGenericClass(final TargetSelector selector) {
        final ArrayOfStringHolderInterface result = Instancio.of(ArrayOfStringHolderInterface.class)
                .subtype(selector, StringHolderAlternativeImpl.class)
                .create();

        assertThat(result.getArray()).isNotEmpty()
                .hasOnlyElementsOfType(StringHolderAlternativeImpl.class)
                .allSatisfy(item -> assertThat(item.getValue()).isNotBlank());
    }


    private static Stream<Arguments> itemInterfaceSelectors() {
        return Stream.of(
                Arguments.of(all(ItemInterface.class)),
                Arguments.of(types().of(ItemInterface.class)));
    }

    private static Stream<Arguments> stringHolderInterfaceSelectors() {
        return Stream.of(
                Arguments.of(all(StringHolderInterface.class)),
                Arguments.of(types().of(StringHolderInterface.class)));
    }

}