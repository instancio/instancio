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
package org.instancio.test.protobuf;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.Keys;
import org.instancio.test.prototobuf.Proto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(InstancioExtension.class)
class ProtoSettingsTest {

    @Test
    void stringFieldPrefixEnabled() {
        final Proto.Person person = Instancio.of(Proto.Person.class)
                .withSetting(Keys.STRING_FIELD_PREFIX_ENABLED, true)
                .create();

        assertThat(person.getName()).startsWith("name__");
        assertThat(person.getAddress().getCity()).startsWith("city__");
        assertThat(person.getAddressesMap().values())
                .isNotEmpty()
                .extracting(Proto.Address::getCity)
                .allMatch(city -> city.startsWith("city__"));
    }

    @Test
    void collectionSize() {
        final int size = 3;
        final Proto.Person person = Instancio.of(Proto.Person.class)
                .withSetting(Keys.COLLECTION_MIN_SIZE, size)
                .withSetting(Keys.COLLECTION_MAX_SIZE, size)
                .create();

        assertThat(person.getAddress().getPhoneNumbersList()).hasSize(size);
    }

    @Test
    void mapSize() {
        final Proto.Person person = Instancio.of(Proto.Person.class)
                .withSetting(Keys.MAP_MIN_SIZE, 0)
                .withSetting(Keys.MAP_MAX_SIZE, 0)
                .create();

        assertThat(person.getAddressesMap()).isEmpty();
        assertThat(person.getAttributesMap()).isEmpty();
    }

    @Test
    void stringLength() {
        final int length = 10;
        final Proto.Person person = Instancio.of(Proto.Person.class)
                .withSetting(Keys.STRING_MIN_LENGTH, length)
                .withSetting(Keys.STRING_MAX_LENGTH, length)
                .create();

        assertThat(person.getName())
                .hasSameSizeAs(person.getNickname().getValue())
                .hasSize(length);
    }
}
