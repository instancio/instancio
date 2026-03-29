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

import org.instancio.Assign;
import org.instancio.Instancio;
import org.instancio.junit.Given;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.prototobuf.Proto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

@ExtendWith(InstancioExtension.class)
class ProtoAssignTest {

    @Test
    void valueOf_toSelector(@Given final String prefix) {
        final Proto.Person result = Instancio.of(Proto.Person.class)
                .assign(Assign.valueOf(Proto.Person::getName)
                        .to(Proto.Address::getCity)
                        .as((String name) -> prefix + name))
                .create();

        assertThat(result.getAddress().getCity()).isEqualTo(prefix + result.getName());
    }

    @Test
    void givenOriginSatisfiesPredicate_thenSetDestination() {
        final String city = "foo";
        final Proto.Person result = Instancio.of(Proto.Person.class)
                .assign(Assign.given(Proto.Person::getGender)
                        .satisfies(g -> true)
                        .set(field(Proto.Address::getCity), city))
                .create();

        assertThat(result.getAddress().getCity()).isEqualTo(city);
        assertThat(result.getAddressesMap().values())
                .extracting(Proto.Address::getCity)
                .containsOnly(city);
    }
}
