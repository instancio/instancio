/*
 * Copyright 2022-2024 the original author or authors.
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

import org.apache.commons.lang3.StringUtils;
import org.instancio.Assign;
import org.instancio.Instancio;
import org.instancio.generator.Generator;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.person.Address;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.pojo.person.Phone;
import org.instancio.test.support.pojo.person.RichPerson;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Assign.valueOf;
import static org.instancio.Select.all;
import static org.instancio.Select.field;
import static org.instancio.test.support.asserts.ReflectionAssert.assertThatObject;

@FeatureTag(Feature.ASSIGN)
@ExtendWith(InstancioExtension.class)
class AssignPojoTest {

    @Test
    void setPojo() {
        final Address address = new Address();

        final RichPerson result = Instancio.of(RichPerson.class)
                .set(field(RichPerson::getAddress2), address)
                .assign(valueOf(RichPerson::getAddress2).to(RichPerson::getAddress1))
                .create();

        assertThat(result.getAddress1())
                .isSameAs(result.getAddress2())
                .isSameAs(address);

        // should not be populated
        assertThatObject(address).hasAllFieldsOfTypeSetToNull(String.class);
        assertThat(address.getPhoneNumbers()).isNull();
    }

    @Test
    void supplyPojoViaGenerator() {
        final Address address = Address.builder().build();

        final RichPerson result = Instancio.of(RichPerson.class)
                .supply(field(RichPerson::getAddress2), random -> address)
                .assign(valueOf(RichPerson::getAddress2).to(RichPerson::getAddress1))
                .create();

        assertThat(result.getAddress1())
                .isSameAs(result.getAddress2())
                .isSameAs(address);

        // should be populated due to default generator hint
        assertThat(address.getAddress()).isNotBlank();
        assertThat(address.getCity()).isNotBlank();
        assertThat(address.getCountry()).isNotBlank();
        assertThat(address.getPhoneNumbers()).isNotEmpty();
    }

    @RepeatedTest(Constants.SAMPLE_SIZE_D)
    void conditionalSupplyPojoViaGenerator() {
        final Generator<?> phoneGenerator = random -> Phone.builder()
                .countryCode("+1")
                .number(random.digits(7))
                .build();

        final Person result = Instancio.of(Person.class)
                .generate(field(Address::getCountry), gen -> gen.oneOf("Canada", "US", "Other"))
                .assign(Assign.given(field(Address::getCountry))
                        .isIn("Canada", "US")
                        .supply(all(Phone.class), phoneGenerator))
                .create();

        final List<Phone> phoneNumbers = result.getAddress().getPhoneNumbers();

        if (StringUtils.equalsAny(result.getAddress().getCountry(), "US", "Canada")) {
            assertThat(phoneNumbers).allSatisfy(phone -> {
                assertThat(phone.getCountryCode()).isEqualTo("+1");
                assertThat(phone.getNumber()).containsOnlyDigits().hasSize(7);
            });
        } else {
            assertThat(phoneNumbers).noneSatisfy(phone -> {
                assertThat(phone.getCountryCode()).isNotEqualTo("+1");
                assertThat(phone.getNumber()).containsOnlyDigits().hasSize(7);
            });
        }
    }
}
