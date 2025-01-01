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
package org.instancio.creation.generics.inheritance;

import org.instancio.test.support.pojo.generics.basic.Item;
import org.instancio.test.support.pojo.generics.inheritance.GenericTypesWithInheritance.GenericItemHolderWithInheritance;
import org.instancio.test.support.pojo.person.PhoneWithType;
import org.instancio.test.support.tags.GenericsTag;
import org.instancio.testsupport.templates.CreationTestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@GenericsTag
class GenericItemHolderWithInheritanceCreationTest
        extends CreationTestTemplate<GenericItemHolderWithInheritance<PhoneWithType, Item<PhoneWithType>>> {

    @Override
    protected void verify(final GenericItemHolderWithInheritance<PhoneWithType, Item<PhoneWithType>> result) {
        assertThat(result.getId()).isInstanceOf(Long.class);
        assertThat(result.getItem()).isExactlyInstanceOf(Item.class);

        final PhoneWithType value = result.getItem().getValue();
        assertThat(value.getCountryCode()).isNotBlank();
        assertThat(value.getNumber()).isNotBlank();
        assertThat(value.getPhoneType()).isNotNull();
    }
}
