/*
 * Copyright 2022-2025 the original author or authors.
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
package org.instancio.test.features.generator.array;

import org.instancio.Assign;
import org.instancio.Instancio;
import org.instancio.TargetSelector;
import org.instancio.TypeToken;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.arrays.ArrayCharSequence;
import org.instancio.test.support.pojo.arrays.TwoArraysOfItemInterfaceString;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.pojo.generics.basic.Item;
import org.instancio.test.support.pojo.interfaces.ArrayOfStringHolderInterface;
import org.instancio.test.support.pojo.interfaces.ItemInterface;
import org.instancio.test.support.pojo.interfaces.StringHolderInterface;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.field;
import static org.instancio.Select.fields;
import static org.instancio.Select.types;

@FeatureTag({Feature.GENERATE, Feature.ARRAY_GENERATOR_SUBTYPE})
@ExtendWith(InstancioExtension.class)
class ArrayGeneratorSubtypeTest {

    @Test
    void shouldCreateArrayOfTypeSpecifiedViaGenerate() {
        final TwoArraysOfItemInterfaceString result = Instancio.of(TwoArraysOfItemInterfaceString.class)
                .generate(all(ItemInterface[].class), gen -> gen.array().subtype(Item[].class))
                .create();

        assertArrayContainsExpectedSubtype(result);
    }

    @Test
    void shouldCreateArrayOfTypeSpecifiedViaAssign() {
        final TwoArraysOfItemInterfaceString result = Instancio.of(TwoArraysOfItemInterfaceString.class)
                .assign(Assign.valueOf(all(ItemInterface[].class)).generate(gen -> gen.array().subtype(Item[].class)))
                .create();

        assertArrayContainsExpectedSubtype(result);
    }

    private static void assertArrayContainsExpectedSubtype(final TwoArraysOfItemInterfaceString result) {
        assertThat(result.getArray1()).isNotEmpty()
                .hasOnlyElementsOfType(Item.class)
                .allSatisfy((ItemInterface<String> it) -> assertThat(it).hasNoNullFieldsOrProperties());

        assertThat(result.getArray2()).isNotEmpty()
                .hasOnlyElementsOfType(Item.class)
                .allSatisfy((ItemInterface<String> it) -> assertThat(it).hasNoNullFieldsOrProperties());
    }

    @Test
    void usingArraySubtype() {
        final ItemInterface<String>[] result = Instancio.of(new TypeToken<ItemInterface<String>[]>() {})
                .subtype(all(ItemInterface[].class), Item[].class)
                .create();

        assertThat(result).isNotEmpty().allSatisfy(it -> {
            assertThat(it).isNotNull();
            assertThat(it.getValue()).isNotBlank();
        });
    }

    @Test
    void usingArrayGeneratorSubtype() {
        final ItemInterface<String>[] result = Instancio.of(new TypeToken<ItemInterface<String>[]>() {})
                .generate(all(ItemInterface[].class), gen -> gen.array().subtype(Item[].class))
                .create();

        assertThat(result).isNotEmpty().allSatisfy(it -> {
            assertThat(it).isNotNull();
            assertThat(it.getValue()).isNotBlank();
        });
    }

    @Test
    void usingComponentSubtype() {
        final ItemInterface<String>[] result = Instancio.of(new TypeToken<ItemInterface<String>[]>() {})
                .subtype(all(ItemInterface.class), Item.class)
                .create();

        assertThat(result).isNotEmpty().allSatisfy(it -> {
            assertThat(it).isNotNull();
            assertThat(it.getValue()).isNotBlank();
        });
    }

    @Test
    @DisplayName("Create array with non-generic component type")
    void nonGenericSubtype() {
        final ArrayCharSequence result = Instancio.of(ArrayCharSequence.class)
                .generate(all(CharSequence[].class), gen -> gen.array().subtype(String[].class))
                .create();

        assertThat(result.getArray())
                .isNotEmpty()
                .allSatisfy(it -> assertThat(it).isNotBlank());
    }

    @ParameterizedTest
    @MethodSource("arraySelectors")
    void arrayGeneratorSubtypeBug_Fails(final TargetSelector selector) {
        final ArrayOfStringHolderInterface result = Instancio.of(ArrayOfStringHolderInterface.class)
                .generate(selector, gen -> gen.array().subtype(StringHolder[].class))
                .create();

        assertThat(result.getArray()).hasOnlyElementsOfType(StringHolder.class);
    }

    @ParameterizedTest
    @MethodSource("arraySelectors")
    void arraySubtype_Passes(final TargetSelector selector) {
        final ArrayOfStringHolderInterface result = Instancio.of(ArrayOfStringHolderInterface.class)
                .subtype(selector, StringHolder[].class)
                .create();

        assertThat(result.getArray()).hasOnlyElementsOfType(StringHolder.class);
    }

    private static Stream<Arguments> arraySelectors() {
        return Stream.of(
                Arguments.of(all(StringHolderInterface[].class)),
                Arguments.of(field("array")),
                Arguments.of(types().of(StringHolderInterface[].class)),
                Arguments.of(fields().ofType(StringHolderInterface[].class))
        );
    }
}