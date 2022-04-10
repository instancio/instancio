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
package org.instancio.test.client.subtype;

import org.instancio.Instancio;
import org.instancio.TypeToken;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.generics.basic.ItemInterfaceHolder;
import org.instancio.test.support.pojo.generics.basic.ItemInterfaceStringHolder;
import org.instancio.test.support.pojo.generics.basic.NonGenericItemStringExtension;
import org.instancio.test.support.pojo.person.AddressExtension;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.util.Sonar;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Bindings.field;

@FeatureTag(Feature.MAP_FIELD_SUBTYPE)
@ExtendWith(InstancioExtension.class)
class MapFieldSubtypeTest {

    @Test
    @DisplayName("Map non-generic field type to non-generic subtype")
    void mapNonGenericFieldTypeToNonGenericSubtype() {
        final Person result = Instancio.of(Person.class)
                .map(field("address"), AddressExtension.class)
                .create();

        assertThat(result.getAddress()).isExactlyInstanceOf(AddressExtension.class);
        assertThat(((AddressExtension) result.getAddress()).getAdditionalInfo()).isNotBlank();
    }

    @Test
    @Disabled
    @FeatureTag(Feature.UNSUPPORTED)
    @SuppressWarnings(Sonar.DISABLED_TEST)
    void mapGenericFieldTypeToNonGenericSubtype_GenericRootClass() {
        // e.g. ItemInterface<String> foo = new NonGenericItemStringExtension();
        final ItemInterfaceHolder<String> result = Instancio.of(new TypeToken<ItemInterfaceHolder<String>>() {})
                .map(field("itemInterface"), NonGenericItemStringExtension.class)
                .create();

        assertThat(result.getItemInterface().getValue()).isNotBlank();
    }

    @Test
    @Disabled
    @FeatureTag(Feature.UNSUPPORTED)
    @SuppressWarnings(Sonar.DISABLED_TEST)
    void mapGenericFieldTypeToNonGenericSubtype_NonGenericRootClass() {
        final ItemInterfaceStringHolder result = Instancio.of(ItemInterfaceStringHolder.class)
                .map(field("itemInterfaceString"), NonGenericItemStringExtension.class)
                .create();

        assertThat(result.getItemInterfaceString().getValue()).isNotBlank();
    }

}
