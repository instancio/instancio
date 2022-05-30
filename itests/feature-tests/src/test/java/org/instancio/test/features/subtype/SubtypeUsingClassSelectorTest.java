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
package org.instancio.test.features.subtype;

import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.exception.InstancioApiException;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.arrays.ArrayCharSequence;
import org.instancio.test.support.pojo.collections.lists.ListInteger;
import org.instancio.test.support.pojo.person.Address;
import org.instancio.test.support.pojo.person.AddressExtension;
import org.instancio.test.support.pojo.person.PersonHolder;
import org.instancio.test.support.pojo.person.Phone;
import org.instancio.test.support.pojo.person.PhoneWithType;
import org.instancio.test.support.pojo.person.RichPerson;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.all;
import static org.instancio.Select.scope;

@FeatureTag(Feature.SUBTYPE)
@ExtendWith(InstancioExtension.class)
class SubtypeUsingClassSelectorTest {

    @Test
    @DisplayName("Map non-generic type to non-generic subtype")
    void mapNonGenericTypeToNonGenericSubtype() {
        final PhoneWithType result = (PhoneWithType) Instancio.of(Phone.class)
                .subtype(all(Phone.class), PhoneWithType.class)
                .create();

        assertThat(result.getPhoneType()).isNotNull();
        assertThat(result.getNumber()).isNotNull();
    }

    @Test
    @DisplayName("Map class selector with scope to a subtype")
    void mapClassSelectorWithScope() {
        final PersonHolder result = Instancio.of(PersonHolder.class)
                .subtype(all(Address.class).within(scope(RichPerson.class)), AddressExtension.class)
                .create();

        assertThat(result.getPerson().getAddress()).isExactlyInstanceOf(Address.class);
        assertThat(result.getRichPerson().getAddress1())
                .isExactlyInstanceOf(AddressExtension.class)
                .extracting(it -> ((AddressExtension) it).getAdditionalInfo()).isNotNull();

        assertThat(result.getRichPerson().getAddress2())
                .isExactlyInstanceOf(AddressExtension.class)
                .extracting(it -> ((AddressExtension) it).getAdditionalInfo()).isNotNull();
    }

    @Test
    @DisplayName("Map non-generic type to non-generic subtype in a collection")
    void mapNonGenericTypeToNonGenericSubtypeInCollectionElement() {
        final Address result = Instancio.of(Address.class)
                .subtype(all(Phone.class), PhoneWithType.class)
                .create();

        assertThat(result.getPhoneNumbers())
                .hasOnlyElementsOfType(PhoneWithType.class)
                .allSatisfy(phone -> {
                    assertThat(((PhoneWithType) phone).getPhoneType()).isNotNull();
                    assertThat(phone.getNumber()).isNotNull();
                });
    }

    @Test
    @DisplayName("Map non-generic type to non-generic subtype in an array")
    void mapNonGenericTypeToNonGenericSubtypeInArrayElement() {
        final ArrayCharSequence result = Instancio.of(ArrayCharSequence.class)
                .subtype(all(CharSequence.class), StringBuilder.class)
                .create();

        assertThat(result.getArray())
                .hasOnlyElementsOfType(StringBuilder.class)
                .allSatisfy(elem -> assertThat(elem).isNotBlank());
    }

    @Test
    @DisplayName("Map non-generic type to non-generic subtype in an array (create array directly)")
    void mapNonGenericTypeToNonGenericSubtypeInArrayElementDirect() {
        final CharSequence[] result = Instancio.of(CharSequence[].class)
                .subtype(all(CharSequence.class), StringBuilder.class)
                .create();

        assertThat(result)
                .hasOnlyElementsOfType(StringBuilder.class)
                .allSatisfy(elem -> assertThat(elem).isNotBlank());
    }


    @Nested
    class ValidationTest {
        @Test
        void invalidSubtypeMappingArrayListToList() {
            final InstancioApi<ListInteger> api = Instancio.of(ListInteger.class)
                    .subtype(all(List.class), String.class);

            assertThatThrownBy(api::create)
                    .isInstanceOf(InstancioApiException.class)
                    .hasMessage("Class '%s' is not a subtype of '%s'", String.class.getName(), List.class.getName());
        }

        @Test
        void invalidSubtypeMappingListToList() {
            final InstancioApi<ListInteger> api = Instancio.of(ListInteger.class)
                    .subtype(all(List.class), List.class);

            assertThatThrownBy(api::create)
                    .isInstanceOf(InstancioApiException.class)
                    .hasMessage("Invalid subtype mapping from '%s' to '%s'", List.class.getName(), List.class.getName());
        }
    }
}