/*
 * Copyright 2022 the original author or authors.
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
package org.instancio.api.features.generators;

import org.instancio.Instancio;
import org.instancio.test.support.pojo.collections.lists.TwoListsOfItemString;
import org.instancio.test.support.pojo.person.Address;
import org.instancio.test.support.pojo.person.Person;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.allStrings;
import static org.instancio.Select.field;

class BuiltInStringGeneratorTest {

    private static final int MIN_LENGTH = 50;
    private static final String PREFIX = "PREFIX-";

    @Test
    @DisplayName("All strings should start with prefix: when strings are collection elements")
    void stringPrefixForAllStringsInsideCollection() {
        final TwoListsOfItemString result = Instancio.of(TwoListsOfItemString.class)
                .generate(allStrings(), gen -> gen.string().minLength(MIN_LENGTH).prefix(PREFIX))
                .create();

        assertThat(result.getList1()).isNotEmpty()
                .allSatisfy(item -> assertThat(item.getValue())
                        .startsWith(PREFIX)
                        .hasSizeGreaterThanOrEqualTo(MIN_LENGTH + PREFIX.length()));

        assertThat(result.getList2()).isNotEmpty()
                .allSatisfy(item -> assertThat(item.getValue())
                        .startsWith(PREFIX)
                        .hasSizeGreaterThanOrEqualTo(MIN_LENGTH + PREFIX.length()));
    }

    @Test
    @DisplayName("All strings should be random values with a prefix except certain fields")
    void stringPrefixForAllStringsExceptCertainFields() {
        final String homer = "Homer";
        final String springfield = "Springfield";
        final Person person = Instancio.of(Person.class)
                .supply(field("name"), () -> homer)
                .supply(field(Address.class, "city"), () -> springfield)
                .generate(allStrings(), gen -> gen.string().prefix(PREFIX))
                .create();

        assertThat(person.getName()).isEqualTo(homer);
        assertThat(person.getAddress().getCity()).isEqualTo(springfield);
        assertThat(person.getAddress().getCountry()).startsWith(PREFIX);
    }


    @Test
    @DisplayName("Customising one field should not affect other fields of the same type")
    void onlySelectedElementShouldBeCustomised() {
        final Person result = Instancio.of(Person.class)
                .generate(field("name"), gen -> gen.string().maxLength(0))
                .create();

        assertThat(result.getName()).isEmpty();
        assertThat(result.getAddress().getCity()).isNotBlank();
    }
}