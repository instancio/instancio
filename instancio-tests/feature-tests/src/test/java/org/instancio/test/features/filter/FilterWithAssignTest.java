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
package org.instancio.test.features.filter;

import org.instancio.Assign;
import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.exception.InstancioApiException;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.misc.StringFields;
import org.instancio.test.support.pojo.person.Address;
import org.instancio.test.support.pojo.person.Phone;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.allStrings;
import static org.instancio.Select.field;

@FeatureTag({Feature.FILTER, Feature.ASSIGN})
@ExtendWith(InstancioExtension.class)
class FilterWithAssignTest {

    @Test
    void list() {
        final List<Address> results = Instancio.ofList(Address.class)
                .size(10)
                .filter(field(Address::getPhoneNumbers), (List<Phone> list) -> list.size() == 5)
                .generate(field(Address::getCountry), gen -> gen.oneOf("foo", "bar"))
                .assign(Assign.given(Address::getCountry).is("foo").set(field(Phone::getCountryCode), "+111"))
                .assign(Assign.given(Address::getCountry).is("bar").set(field(Phone::getCountryCode), "+222"))
                .create();

        assertThat(results).hasSize(10).allSatisfy(address -> {
            final String expectedCountyCode = address.getCountry().equals("foo") ? "+111" : "+222";

            assertThat(address.getPhoneNumbers())
                    .hasSize(5)
                    .allSatisfy(phone -> assertThat(phone.getCountryCode()).isEqualTo(expectedCountyCode));
        });
    }

    @RepeatedTest(5)
    void originSelectorValue() {
        final StringFields result = Instancio.of(StringFields.class)
                .generate(field(StringFields::getOne), gen -> gen.oneOf("foo", "bar"))
                .filter(field(StringFields::getOne), (String one) -> one.equals("bar"))
                .assign(Assign.valueOf(StringFields::getOne).to(StringFields::getTwo))
                .create();

        assertThat(result.getOne()).isEqualTo(result.getTwo()).isEqualTo("bar");
    }

    /**
     * A destination populated via {@code valueOf().to()} mirrors the origin and cannot
     * be regenerated independently. A {@code filter()} that rejects the copied value
     * can therefore never be satisfied and should result in an error rather than
     * being silently ignored.
     */
    @Test
    void unsatisfiableFilterOnCopiedDestination() {
        final InstancioApi<StringFields> api = Instancio.of(StringFields.class)
                .filter(field(StringFields::getTwo), (String two) -> false)
                .assign(Assign.valueOf(StringFields::getOne).to(StringFields::getTwo));

        assertThatThrownBy(api::create)
                .isInstanceOf(InstancioApiException.class)
                .hasMessageContaining("failed generating a value for node");
    }

    @Test
    void satisfiableFilterOnCopiedDestination() {
        final StringFields result = Instancio.of(StringFields.class)
                .set(field(StringFields::getOne), "foo")
                .filter(field(StringFields::getTwo), "foo"::equals)
                .assign(Assign.valueOf(StringFields::getOne).to(StringFields::getTwo))
                .create();

        assertThat(result.getTwo()).isEqualTo("foo");
    }

    /**
     * {@code withUnique()} must not reject values copied via assignments:
     * such copies are duplicates by design.
     */
    @Test
    void withUniqueDoesNotApplyToCopiedDestination() {
        final StringFields result = Instancio.of(StringFields.class)
                .withUnique(allStrings())
                .assign(Assign.valueOf(StringFields::getOne).to(StringFields::getTwo))
                .create();

        assertThat(result.getTwo()).isEqualTo(result.getOne());
    }
}