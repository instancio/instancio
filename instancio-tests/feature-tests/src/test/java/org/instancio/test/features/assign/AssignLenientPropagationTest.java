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
package org.instancio.test.features.assign;

import org.instancio.Assign;
import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.TargetSelector;
import org.instancio.exception.UnusedSelectorException;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.person.Address;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.all;
import static org.instancio.Select.field;

@FeatureTag(Feature.ASSIGN)
@ExtendWith(InstancioExtension.class)
class AssignLenientPropagationTest {

    private static final String EXPECTED_CITY = "_city_";

    @Test
    void neitherLenient_reportsUnusedSelectors() {
        final InstancioApi<Person> api = Instancio.of(Person.class)
                .supply(all(Person.class), this::personWithExpectedCity)
                .assign(Assign.valueOf(Address::getCity).to(field(Person::getName)));

        assertThatThrownBy(api::create)
                .isExactlyInstanceOf(UnusedSelectorException.class)
                .hasMessageContainingAll("assign() origin", "field(Address::getCity)");
    }

    @Test
    void leniencyPropagatedFromDestinationToOrigin() {
        // Only the destination is marked lenient; the origin is not.
        final TargetSelector destination = field(Person::getName).lenient();

        final Person person = Instancio.of(Person.class)
                .supply(all(Person.class), this::personWithExpectedCity)
                .assign(Assign.valueOf(Address::getCity).to(destination))
                .create();

        assertThat(person.getAddress().getCity()).isEqualTo(EXPECTED_CITY);
        assertThat(person.getName()).isNull();
    }

    private Person personWithExpectedCity() {
        return Person.builder()
                .address(Address.builder().city(EXPECTED_CITY).build())
                .build();
    }
}
