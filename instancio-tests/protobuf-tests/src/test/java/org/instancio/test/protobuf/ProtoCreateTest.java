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

import com.google.protobuf.Timestamp;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.prototobuf.Proto;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(InstancioExtension.class)
class ProtoCreateTest {

    @RepeatedTest(Constants.SAMPLE_SIZE_DD)
    void createEnumGeneratesValidValues() {
        final Proto.Gender result = Instancio.create(Proto.Gender.class);

        assertThat(result)
                .isNotNull()
                .isNotEqualTo(Proto.Gender.UNRECOGNIZED);
    }

    @Test
    void createTimestamp() {
        final Set<Timestamp> result = Instancio.ofSet(Timestamp.class)
                .size(10)
                .create();

        assertThat(result).hasSize(10);

        final Set<Integer> nanosSet = result.stream()
                .map(Timestamp::getNanos)
                .collect(Collectors.toSet());

        // ensure the values are not all zero
        assertThat(nanosSet).hasSizeGreaterThan(5);
    }

    @Test
    void createPerson() {
        final Proto.Person result = Instancio.create(Proto.Person.class);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isNotBlank();
        assertThat(result.getAge()).isNotZero();
        assertThat(result.getGender()).isNotNull();
        assertThat(result.getAddress()).isNotNull();
        assertThat(result.getAddress().getCity()).isNotBlank();
        assertThat(result.getAddress().getCountry()).isNotBlank();
        assertThat(result.getNickname().getValue()).isNotBlank();

        assertThat(result.getAddressesMap()).isNotEmpty().allSatisfy((key, address) -> {
            assertThat(address.getPhoneNumbersList()).isNotEmpty().allSatisfy(phone -> {
                assertThat(phone.getCountryCode()).isNotBlank();
                assertThat(phone.getNumber()).isNotBlank();
            });
        });

        assertThat(result.getAddress().getPhoneNumbersList()).isNotEmpty().allSatisfy(phone -> {
            assertThat(phone.getCountryCode()).isNotBlank();
            assertThat(phone.getNumber()).isNotBlank();
        });

        // Cycles are terminated with a default instance
        assertThat(result.getSpouse()).isEqualTo(Proto.Person.getDefaultInstance());
        assertThat(result.getChildrenList()).isEmpty();
    }
}
