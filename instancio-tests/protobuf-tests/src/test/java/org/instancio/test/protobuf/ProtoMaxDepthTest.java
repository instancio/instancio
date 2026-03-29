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
import org.instancio.test.prototobuf.Proto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(InstancioExtension.class)
class ProtoMaxDepthTest {

    @Test
    void maxDepthZero() {
        final Proto.Person person = Instancio.of(Proto.Person.class)
                .withMaxDepth(0)
                .create();

        assertThat(person.getName()).isEmpty();
        assertThat(person.getNickname().getValue()).isEmpty();
        assertThat(person.getAge()).isZero();
        assertThat(person.getAttributesMap()).isEmpty();
        assertThat(person.getAddress()).isEqualTo(Proto.Address.getDefaultInstance());
    }

    @Test
    void maxDepthOne() {
        final Proto.Person person = Instancio.of(Proto.Person.class)
                .withMaxDepth(1)
                .create();

        assertThat(person.getName()).isNotBlank();
        assertThat(person.getAddress()).isEqualTo(Proto.Address.getDefaultInstance());
    }

    @Test
    void maxDepthTwo() {
        final Proto.Person person = Instancio.of(Proto.Person.class)
                .withMaxDepth(2)
                .create();

        assertThat(person.getAddress()).isNotEqualTo(Proto.Address.getDefaultInstance());
        assertThat(person.getAddress().getCity()).isNotBlank();
        assertThat(person.getAddress().getPhoneNumbersList()).isEmpty();
    }

    @Test
    void maxDepthThree() {
        final Proto.Person person = Instancio.of(Proto.Person.class)
                .withMaxDepth(3)
                .create();

        assertThat(person.getAddress().getPhoneNumbersList()).isNotEmpty();
        assertThat(person.getAddress().getPhoneNumbersList())
                .allMatch(phone -> phone.equals(Proto.Phone.getDefaultInstance()));
    }

    @Test
    void maxDepthFour() {
        final Proto.Person person = Instancio.of(Proto.Person.class)
                .withMaxDepth(4)
                .create();

        assertThat(person.getAddress().getPhoneNumbersList()).isNotEmpty();
        assertThat(person.getAddress().getPhoneNumbersList())
                .noneMatch(phone -> phone.equals(Proto.Phone.getDefaultInstance()));
    }
}
