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
package org.instancio.test.features.subtype;

import org.instancio.Instancio;
import org.instancio.TargetSelector;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.basic.StringHolderAlternativeImpl;
import org.instancio.test.support.pojo.generics.basic.ItemAlternativeImpl;
import org.instancio.test.support.pojo.interfaces.ItemInterface;
import org.instancio.test.support.pojo.interfaces.MapOfItemInterfaceString;
import org.instancio.test.support.pojo.interfaces.MapOfStringHolderInterfaceItemInterfaceString;
import org.instancio.test.support.pojo.interfaces.StringHolderInterface;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.field;
import static org.instancio.Select.fields;
import static org.instancio.Select.types;

@FeatureTag({
        Feature.SELECTOR,
        Feature.PREDICATE_SELECTOR,
        Feature.SUBTYPE,
        Feature.MAP_GENERATOR_SUBTYPE
})
@ExtendWith(InstancioExtension.class)
class MapKeyValueSubtypeMappingTest {

    @ParameterizedTest
    @MethodSource("mapAndKeyValueSelectors")
    @DisplayName("Use subtype() for map keys/values, and generator spec subtype() for the map itself")
    void subtypeKeysValuesAndGeneratorSubtypeOfMap(
            final TargetSelector mapSelector,
            final TargetSelector keySelector,
            final TargetSelector valueSelector) {

        // key:   StringHolderInterface
        // value: ItemInterface<String>
        final MapOfStringHolderInterfaceItemInterfaceString result = Instancio.of(MapOfStringHolderInterfaceItemInterfaceString.class)
                .generate(mapSelector, gen -> gen.map().subtype(ConcurrentHashMap.class))
                .subtype(keySelector, StringHolderAlternativeImpl.class)
                .subtype(valueSelector, ItemAlternativeImpl.class)
                .create();

        assertThat(result.getMap())
                .isExactlyInstanceOf(ConcurrentHashMap.class)
                .isNotEmpty();

        assertThat(result.getMap().keySet())
                .hasOnlyElementsOfType(StringHolderAlternativeImpl.class)
                .extracting(StringHolderInterface::getValue)
                .doesNotContainNull();

        assertThat(result.getMap().values())
                .hasOnlyElementsOfType(ItemAlternativeImpl.class)
                .extracting(ItemInterface::getValue)
                .doesNotContainNull();
    }

    @ParameterizedTest
    @MethodSource("itemInterfaceSelectors")
    @DisplayName("Subtype mapping of a generic class (as map key and map value)")
    void subtypeMappingOfGenericClass(final TargetSelector selector) {
        final MapOfItemInterfaceString result = Instancio.of(MapOfItemInterfaceString.class)
                .subtype(selector, ItemAlternativeImpl.class)
                .create();

        assertThat(result.getMap().keySet()).isNotEmpty()
                .hasOnlyElementsOfType(ItemAlternativeImpl.class)
                .allSatisfy(item -> assertThat(item.getValue()).isNotBlank());

        assertThat(result.getMap().values()).isNotEmpty()
                .hasOnlyElementsOfType(ItemAlternativeImpl.class)
                .allSatisfy(item -> assertThat(item.getValue()).isNotBlank());
    }

    private static Stream<Arguments> itemInterfaceSelectors() {
        return Stream.of(
                Arguments.of(all(ItemInterface.class)),
                Arguments.of(types().of(ItemInterface.class)));
    }

    private static Stream<Arguments> mapAndKeyValueSelectors() {
        return Stream.of(
                // regular map + regular key/values
                Arguments.of(all(Map.class), all(StringHolderInterface.class), all(ItemInterface.class)),
                Arguments.of(field("map"), all(StringHolderInterface.class), all(ItemInterface.class)),

                // regular map + predicate key/values
                Arguments.of(all(Map.class), types().of(StringHolderInterface.class), types().of(ItemInterface.class)),
                Arguments.of(field("map"), types().of(StringHolderInterface.class), types().of(ItemInterface.class)),

                // predicate map + predicate key/values
                Arguments.of(types().of(Map.class), types().of(StringHolderInterface.class), types().of(ItemInterface.class)),
                Arguments.of(fields().named("map"), types().of(StringHolderInterface.class), types().of(ItemInterface.class))
        );
    }
}
