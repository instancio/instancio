/*
 * Copyright 2022-2026 the original author or authors.
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
package org.instancio.test.features.model;

import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.person.Address;
import org.instancio.test.support.pojo.person.AddressExtension;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.pojo.person.Phone;
import org.instancio.test.support.pojo.person.PhoneWithType;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.types;

@FeatureTag({Feature.MODEL, Feature.SUBTYPE})
@ExtendWith(InstancioExtension.class)
class ModelWithSubtypeTest {

    @Test
    void subtypeWithClassSelector() {
        final Model<Person> model = Instancio.of(Person.class)
                .subtype(all(Address.class), AddressExtension.class)
                .subtype(types().of(Phone.class), PhoneWithType.class)
                .toModel();

        assertSubtype(model);
    }

    @Test
    void subtypeViaSettings() {
        final Model<Person> model = Instancio.of(Person.class)
                .withSettings(Settings.create()
                        .mapType(Address.class, AddressExtension.class)
                        .mapType(Phone.class, PhoneWithType.class))
                .toModel();

        assertSubtype(model);
    }

    @Test
    void subtypeWithCollectionGenerator() {
        final Model<Person> model = Instancio.of(Person.class)
                .generate(all(List.class), gen -> gen.collection().subtype(LinkedList.class))
                .toModel();

        final Person result = Instancio.create(model);
        assertThat(result.getAddress().getPhoneNumbers())
                .isNotEmpty()
                .isExactlyInstanceOf(LinkedList.class);
    }

    private static void assertSubtype(final Model<Person> model) {
        final Person result = Instancio.create(model);
        assertThat(result.getAddress()).isExactlyInstanceOf(AddressExtension.class);
        assertThat(result.getAddress().getPhoneNumbers()).hasOnlyElementsOfType(PhoneWithType.class)
                .allSatisfy(phone -> assertThat(((PhoneWithType) phone).getPhoneType()).isNotNull());
        assertThat(((AddressExtension) result.getAddress()).getAdditionalInfo()).isNotBlank();
    }
}