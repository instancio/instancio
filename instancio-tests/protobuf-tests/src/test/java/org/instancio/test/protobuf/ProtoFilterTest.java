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
import static org.instancio.Select.all;
import static org.instancio.Select.field;

@ExtendWith(InstancioExtension.class)
class ProtoFilterTest {

    @Test
    void filterEnum() {
        final Proto.Gender gender = Proto.Gender.OTHER;
        final Proto.Person result = Instancio.of(Proto.Person.class)
                .filter(all(Proto.Gender.class), (Proto.Gender g) -> g == gender)
                .create();

        assertThat(result.getGender()).isEqualTo(gender);
    }

    @Test
    void filterInt() {
        final Proto.Person result = Instancio.of(Proto.Person.class)
                .filter(field(Proto.Person::getAge), (Integer age) -> age % 2 == 0)
                .create();

        assertThat(result.getAge()).isEven();
    }

    @Test
    void filterInNestedObject() {
        final int length = 5;
        final Proto.Person result = Instancio.of(Proto.Person.class)
                .filter(field(Proto.Phone::getNumber), (String s) -> s.length() == length)
                .create();

        assertThat(result.getAddress().getPhoneNumbersList())
                .isNotEmpty()
                .allSatisfy(phone -> assertThat(phone.getNumber()).hasSize(length));
    }
}
