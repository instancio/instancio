/*
 *  Copyright 2022-2023 the original author or authors.
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
import org.instancio.SetMethodSelector;
import org.instancio.TypeToken;
import org.instancio.exception.InstancioApiException;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.AssignmentType;
import org.instancio.settings.Keys;
import org.instancio.settings.OnSetMethodNotFound;
import org.instancio.settings.OnSetMethodUnmatched;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.collections.lists.ListInteger;
import org.instancio.test.support.pojo.dynamic.DynAddress;
import org.instancio.test.support.pojo.dynamic.DynAddressExt;
import org.instancio.test.support.pojo.dynamic.DynPerson;
import org.instancio.test.support.pojo.generics.basic.ItemInterfaceHolder;
import org.instancio.test.support.pojo.generics.basic.ItemInterfaceStringHolder;
import org.instancio.test.support.pojo.generics.basic.NonGenericItemStringExtension;
import org.instancio.test.support.pojo.interfaces.ItemInterface;
import org.instancio.test.support.pojo.person.AddressExtension;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.tags.RunWithMethodAssignmentOnly;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.setter;

@RunWithMethodAssignmentOnly
@FeatureTag(Feature.SUBTYPE)
@ExtendWith(InstancioExtension.class)
class SubtypeUsingSetterSelectorTest {

    @WithSettings
    private final Settings settings = Settings.create()
            .set(Keys.ON_SET_METHOD_UNMATCHED, OnSetMethodUnmatched.INVOKE)
            .set(Keys.ASSIGNMENT_TYPE, AssignmentType.METHOD)
            .set(Keys.ON_SET_METHOD_NOT_FOUND, OnSetMethodNotFound.FAIL);

    @Test
    void subtypeMappingSameClass() {
        final ListInteger result = Instancio.of(ListInteger.class)
                .subtype(setter(ListInteger::setList), List.class)
                .create();

        assertThat(result.getList()).isExactlyInstanceOf(ArrayList.class)
                .isNotEmpty()
                .doesNotContainNull();
    }

    @Test
    @DisplayName("Map non-generic method parameter type to non-generic subtype")
    void mapNonGenericTypeToNonGenericSubtype() {
        final Person result = Instancio.of(Person.class)
                .subtype(setter(Person::setAddress), AddressExtension.class)
                .create();

        assertThat(result.getAddress()).isExactlyInstanceOf(AddressExtension.class);
        assertThat(((AddressExtension) result.getAddress()).getAdditionalInfo()).isNotBlank();
    }

    @Test
    void mapGenericTypeToNonGenericSubtype_GenericRootClass() {
        final SetMethodSelector<ItemInterfaceHolder<String>, ItemInterface<String>> setter = ItemInterfaceHolder::setItemInterface;

        // e.g. ItemInterface<String> foo = new NonGenericItemStringExtension();
        final ItemInterfaceHolder<String> result = Instancio.of(new TypeToken<ItemInterfaceHolder<String>>() {})
                .subtype(setter, NonGenericItemStringExtension.class)
                .create();

        assertThat(result.getItemInterface().getValue()).isNotBlank();
    }

    @Test
    void mapGenericTypeToNonGenericSubtype_NonGenericRootClass() {
        final ItemInterfaceStringHolder result = Instancio.of(ItemInterfaceStringHolder.class)
                .subtype(setter(ItemInterfaceStringHolder::setItemInterfaceString), NonGenericItemStringExtension.class)
                .create();

        assertThat(result.getItemInterfaceString().getValue()).isNotBlank();
    }

    @Test
    void invalidSubtypeMappingArrayListToList() {
        final InstancioApi<ListInteger> api = Instancio.of(ListInteger.class)
                .subtype(setter(ListInteger::setList), String.class);

        assertThatThrownBy(api::create)
                .isInstanceOf(InstancioApiException.class)
                .hasMessageContaining("class '%s' is not a subtype of '%s'", String.class.getName(), List.class.getName());
    }

    @Test
    void dynamicPojoSubtype() {
        final DynPerson result = Instancio.of(DynPerson.class)
                .subtype(setter(DynPerson::setAddress), DynAddressExt.class)
                .create();

        final DynAddress address = result.getAddress();
        assertThat(address).isExactlyInstanceOf(DynAddressExt.class);

        final DynAddressExt addressExt = (DynAddressExt) address;
        assertThat(addressExt.getCity()).isNotBlank();
        assertThat(addressExt.getPostalCode()).isNotBlank();
        assertThat(addressExt.getPhoneNumbers()).isNotEmpty();
    }
}