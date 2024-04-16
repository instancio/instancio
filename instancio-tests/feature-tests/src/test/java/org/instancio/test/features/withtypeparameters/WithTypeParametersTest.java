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
package org.instancio.test.features.withtypeparameters;

import org.instancio.Instancio;
import org.instancio.TargetSelector;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.pojo.generics.basic.ItemAlternativeImpl;
import org.instancio.test.support.pojo.generics.basic.Triplet;
import org.instancio.test.support.pojo.interfaces.ItemInterface;
import org.instancio.test.support.pojo.interfaces.StringHolderInterface;
import org.instancio.test.support.pojo.person.Phone;
import org.instancio.test.support.pojo.person.PhoneType;
import org.instancio.test.support.pojo.person.PhoneWithType;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Instant;
import java.time.temporal.Temporal;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.field;
import static org.instancio.Select.fields;
import static org.instancio.Select.types;

/**
 * Adhoc tests for {@link org.instancio.InstancioOfClassApi#withTypeParameters(Class[])}.
 */
@FeatureTag({
        Feature.WITH_TYPE_PARAMETERS,
        Feature.ON_COMPLETE,
        Feature.PREDICATE_SELECTOR,
        Feature.SELECTOR,
        Feature.SUBTYPE
})
@SuppressWarnings("unchecked") // withTypeParameters() results in "unchecked assignment"
@ExtendWith(InstancioExtension.class)
class WithTypeParametersTest {

    private static Stream<Arguments> args() {
        return Stream.of(
                Arguments.of(all(ItemInterface.class), all(Phone.class), all(PhoneType.class)),
                Arguments.of(all(ItemInterface.class), all(Phone.class), fields().ofType(PhoneType.class)),
                Arguments.of(types().of(ItemInterface.class), types().of(Phone.class), types().of(PhoneType.class))
        );
    }

    @ParameterizedTest
    @MethodSource("args")
    @DisplayName("Map the argument class of withTypeParameters() using subtype()")
    void mapTypeArgumentUsingSubtype(
            final TargetSelector itemInterfaceClassSelector,
            final TargetSelector phoneClassSelector,
            final TargetSelector phoneTypeEnumSelector) {

        final PhoneType expectedPhoneType = PhoneType.OTHER;

        // Given root class: interface ItemInterface<T>
        // Map type parameter T -> Phone, using withTypeParameters(Phone.class)
        // Map Phone -> PhoneWithType using subtype() mapping
        // Map 'interface ItemInterface<T>' -> 'class ItemAlternativeImpl<K>' using subtype() mapping
        final ItemInterface<PhoneWithType> result = Instancio.of(ItemInterface.class)
                .withTypeParameters(Phone.class)
                .subtype(itemInterfaceClassSelector, ItemAlternativeImpl.class) // map root class to implementation
                .subtype(phoneClassSelector, PhoneWithType.class) // map type argument to a subclass
                .generate(phoneTypeEnumSelector, gen -> gen.oneOf(expectedPhoneType))
                .create();

        assertThat(result)
                .isExactlyInstanceOf(ItemAlternativeImpl.class)
                .extracting(ItemInterface::getValue)
                .isExactlyInstanceOf(PhoneWithType.class)
                .extracting(PhoneWithType::getPhoneType)
                .isEqualTo(expectedPhoneType);
    }

    @Test
    @DisplayName("Multiple type arguments with subtype(), generate(), and onComplete()")
    void mapMultipleTypeArguments() {
        final String expectedStringBuilderSuffix = "-foo";
        final String expectedStringHolderValue = "bar";
        final Instant expectedInstant = Instant.now();

        final Triplet<CharSequence, StringHolderInterface, Temporal> result = Instancio.of(Triplet.class)
                .withTypeParameters(CharSequence.class, StringHolderInterface.class, Temporal.class)
                .subtype(field("left"), StringBuilder.class)
                .subtype(types().of(StringHolderInterface.class), StringHolder.class)
                .set(field(StringHolder.class, "value"), expectedStringHolderValue)
                .subtype(all(Temporal.class), Instant.class)
                .generate(types().of(Instant.class), gen -> gen.temporal().instant().range(expectedInstant, expectedInstant))
                .onComplete(all(CharSequence.class), (StringBuilder sb) -> sb.append(expectedStringBuilderSuffix))
                .create();

        assertThat(result.getLeft())
                .isExactlyInstanceOf(StringBuilder.class)
                .endsWith(expectedStringBuilderSuffix);

        assertThat(result.getMid())
                .isExactlyInstanceOf(StringHolder.class)
                .extracting(StringHolderInterface::getValue)
                .isEqualTo(expectedStringHolderValue);

        assertThat(result.getRight()).isEqualTo(expectedInstant);
    }
}
