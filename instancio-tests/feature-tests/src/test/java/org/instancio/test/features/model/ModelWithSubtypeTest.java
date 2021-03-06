/*
 * Copyright 2022 the original author or authors.
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
import org.instancio.test.support.pojo.person.Address;
import org.instancio.test.support.pojo.person.AddressExtension;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;

@FeatureTag({Feature.MODEL, Feature.SUBTYPE})
class ModelWithSubtypeTest {

    @Test
    void subtypeWithClassSelector() {
        final Model<Person> model = Instancio.of(Person.class)
                .subtype(all(Address.class), AddressExtension.class)
                .toModel();

        final Person result = Instancio.create(model);
        assertThat(result.getAddress()).isExactlyInstanceOf(AddressExtension.class);
        assertThat(((AddressExtension) result.getAddress()).getAdditionalInfo()).isNotBlank();
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
}