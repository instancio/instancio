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

import org.instancio.GroupableSelector;
import org.instancio.Instancio;
import org.instancio.junit.Given;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.prototobuf.Proto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.allStrings;
import static org.instancio.Select.field;
import static org.instancio.Select.scope;

@ExtendWith(InstancioExtension.class)
class ProtoGenerateSupplyTest {

    @Test
    void supplyViaSupplier(@Given final String value) {
        final Proto.Person result = Instancio.of(Proto.Person.class)
                .supply(field(Proto.Person::getName), () -> value)
                .create();

        assertThat(result.getName()).isEqualTo(value);
    }

    @Test
    void supplyViaGenerator() {
        final int length = 10;
        final Proto.Person result = Instancio.of(Proto.Person.class)
                .supply(allStrings(), random -> random.alphanumeric(length))
                .create();

        assertThat(result.getName()).hasSize(length);
        assertThat(result.getNickname().getValue()).hasSize(length); // com.google.protobuf.StringValue
        assertThat(result.getAddress().getCity()).hasSize(length);
    }

    @Test
    void generateStringViaSpec() {
        final int length = 8;
        final GroupableSelector[] selectors = {
                field(Proto.Person::getName),
                allStrings().within(scope(Proto.Person::getNickname))
        };

        final Proto.Person result = Instancio.of(Proto.Person.class)
                .generate(all(selectors), gen -> gen.string().length(length).lowerCase())
                .create();

        assertThat(result.getName())
                .hasSize(length)
                .isLowerCase();

        assertThat(result.getNickname().getValue())
                .hasSize(length)
                .isLowerCase();
    }

    @Test
    void generateIntViaSpec() {
        final Proto.Person result = Instancio.of(Proto.Person.class)
                .generate(field("age_"), gen -> gen.ints().range(18, 65))
                .create();

        assertThat(result.getAge()).isBetween(18, 65);
    }

    @Test
    void setNestedObject() {
        final Proto.Address fixedAddress = Proto.Address.newBuilder()
                .setCity("SuppliedCity")
                .setCountry("SuppliedCountry")
                .build();

        final Proto.Person result = Instancio.of(Proto.Person.class)
                .set(all(Proto.Address.class), fixedAddress)
                .create();

        assertThat(result.getAddress()).isSameAs(fixedAddress);
    }

    @Test
    void generateCollectionViaSpec() {
        final int size = 100;
        final Proto.Person result = Instancio.of(Proto.Person.class)
                .generate(field(Proto.Address::getPhoneNumbersList), gen -> gen.collection().size(size))
                .create();

        assertThat(result.getAddress().getPhoneNumbersList()).hasSize(size);
    }

    @Test
    void generateMapViaSpec() {
        final int size = 100;
        final Proto.Person result = Instancio.of(Proto.Person.class)
                .generate(field(Proto.Person::getAddressesMap), gen -> gen.map().size(size))
                .create();

        assertThat(result.getAddressesMap().values())
                .hasSize(size)
                .allSatisfy(it -> assertThat(it.getCity()).isNotBlank());

        assertThat(result.getAddressesMap().keySet())
                .hasSize(size)
                .allSatisfy(it -> assertThat(it).isNotBlank());
    }
}
