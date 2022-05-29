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
import org.instancio.TypeToken;
import org.instancio.exception.InstancioApiException;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.collections.lists.ListInteger;
import org.instancio.test.support.pojo.generics.basic.ItemInterfaceHolder;
import org.instancio.test.support.pojo.generics.basic.ItemInterfaceStringHolder;
import org.instancio.test.support.pojo.generics.basic.NonGenericItemStringExtension;
import org.instancio.test.support.pojo.person.AddressExtension;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.pojo.person.Person_;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.field;

@FeatureTag(Feature.SUBTYPE)
@ExtendWith(InstancioExtension.class)
class SubtypeUsingFieldSelectorTest {

    @Test
    @DisplayName("Map non-generic field type to non-generic subtype")
    void mapNonGenericTypeToNonGenericSubtype() {
        final Person result = Instancio.of(Person.class)
                .subtype(Person_.address, AddressExtension.class)
                .create();

        assertThat(result.getAddress()).isExactlyInstanceOf(AddressExtension.class);
        assertThat(((AddressExtension) result.getAddress()).getAdditionalInfo()).isNotBlank();
    }

    @Test
    void mapGenericTypeToNonGenericSubtype_GenericRootClass() {
        // e.g. ItemInterface<String> foo = new NonGenericItemStringExtension();
        final ItemInterfaceHolder<String> result = Instancio.of(new TypeToken<ItemInterfaceHolder<String>>() {})
                .subtype(field("itemInterface"), NonGenericItemStringExtension.class)
                .create();

        assertThat(result.getItemInterface().getValue()).isNotBlank();
    }

    @Test
    void mapGenericTypeToNonGenericSubtype_NonGenericRootClass() {
        final ItemInterfaceStringHolder result = Instancio.of(ItemInterfaceStringHolder.class)
                .subtype(field("itemInterfaceString"), NonGenericItemStringExtension.class)
                .create();

        assertThat(result.getItemInterfaceString().getValue()).isNotBlank();
    }

    @Nested
    class ValidationTest {
        @Test
        void invalidSubtypeMappingArrayListToList() {
            final InstancioApi<ListInteger> api = Instancio.of(ListInteger.class)
                    .subtype(field("list"), String.class);

            assertThatThrownBy(api::create)
                    .isInstanceOf(InstancioApiException.class)
                    .hasMessage("Class '%s' is not a subtype of '%s'", String.class.getName(), List.class.getName());
        }

        @Test
        void invalidSubtypeMappingListToList() {
            final InstancioApi<ListInteger> api = Instancio.of(ListInteger.class)
                    .subtype(field("list"), List.class);

            assertThatThrownBy(api::create)
                    .isInstanceOf(InstancioApiException.class)
                    .hasMessage("Invalid subtype mapping from '%s' to '%s'", List.class.getName(), List.class.getName());
        }
    }
}