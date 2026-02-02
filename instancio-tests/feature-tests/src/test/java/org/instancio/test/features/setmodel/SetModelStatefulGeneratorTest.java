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
package org.instancio.test.features.setmodel;

import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.person.Address;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.pojo.person.Phone;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.field;

@FeatureTag({Feature.MODEL, Feature.SET_MODEL, Feature.EMIT_GENERATOR})
@ExtendWith(InstancioExtension.class)
class SetModelStatefulGeneratorTest {

    @Test
    void emitAndIntSeq() {
        final String[] countryCodes = {"1", "2", "3", "4", "5", "6"};

        final Model<Phone> phoneModel = Instancio.of(Phone.class)
                .generate(field("countryCode"), gen -> gen.emit().items(countryCodes).ignoreUnused())
                .generate(field("number"), gen -> gen.intSeq().start(10).as(String::valueOf))
                .toModel();

        // Each instance created from the model should have its own copy of stateful generators.
        // This object should not affect subsequent objects
        Instancio.create(phoneModel);

        final Person result = Instancio.of(Person.class)
                .setModel(all(Phone.class), phoneModel)
                .generate(field(Address::getPhoneNumbers), gen -> gen.collection().size(6))
                .create();

        assertThat(result.getAddress().getPhoneNumbers())
                .extracting(Phone::getCountryCode)
                .containsExactly(countryCodes);

        assertThat(result.getAddress().getPhoneNumbers())
                .extracting(Phone::getNumber)
                .containsExactly("10", "11", "12", "13", "14", "15");
    }
}
