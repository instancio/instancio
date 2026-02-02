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
package org.instancio.test.features.generator.custom;

import org.instancio.Instancio;
import org.instancio.Random;
import org.instancio.generator.AfterGenerate;
import org.instancio.generator.Generator;
import org.instancio.generator.Hints;
import org.instancio.generator.hints.CollectionHint;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.person.Address;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.pojo.person.Phone;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.allStrings;
import static org.instancio.Select.field;

/**
 * Main use case: I want to set up a minimal Collection containing a couple of
 * "expected" objects using a custom generator. I want the engine to generate
 * additional random objects and add to the collection.
 * <p>
 * In addition, I want to have control over whether my custom objects
 * will have null fields populated or not.
 * Initialised values should be modifiable using selectors.
 *
 * <p>This leads to the following scenarios:</p>
 *
 * <pre>
 *   Case 1: my object is read-only
 *   - do NOT populate null fields in my object
 *   - do NOT allow my custom values to be modified via selectors
 *
 *   Case 2:
 *   - do NOT populate null fields in my objects
 *   - allow my custom values to be modified via selectors
 *
 *   Case 3: allow all
 *   - populate null fields in my objects
 *   - allow my custom values to be modified via selectors
 * </pre>
 *
 * <b>Note:</b> objects created by engine are always modifiable via selectors.
 */
@ExtendWith(InstancioExtension.class)
class CustomGeneratorUseCaseTest {

    private static final int INITIAL_SIZE = 2;
    private static final int GENERATE_ELEMENTS = 2;

    private static final String MY_COUNTRY_CODE = "+1";
    private static final String MY_NUMBER = "123";
    private static final String COUNTRY_CODE_OVERRIDE = "+9";
    private static final String ALL_STRINGS_OVERRIDE = "SELECTOR-OVERRIDE";

    private static Generator<List<Phone>> getGenerator(final AfterGenerate afterGenerate) {
        return new Generator<List<Phone>>() {
            @Override
            public List<Phone> generate(final Random random) {
                final List<Phone> result = new ArrayList<>();
                result.add(Phone.builder().countryCode(MY_COUNTRY_CODE).number(MY_NUMBER).build());
                result.add(Phone.builder().countryCode(MY_COUNTRY_CODE).number(null).build());
                return result;
            }

            @Override
            public Hints hints() {
                return Hints.builder().afterGenerate(afterGenerate)
                        .with(CollectionHint.builder().generateElements(GENERATE_ELEMENTS).build())
                        .build();
            }
        };
    }

    /**
     * <pre>
     *    Case 1: my object is read-only
     *   - do NOT populate null fields in my object
     *   - do NOT allow my custom values to be modified via selectors
     * </pre>
     */
    @Test
    void case1() {
        final Generator<?> generator = getGenerator(AfterGenerate.DO_NOT_MODIFY);

        final Person person = Instancio.of(Person.class)
                .supply(field(Address.class, "phoneNumbers"), generator)
                .set(allStrings(), ALL_STRINGS_OVERRIDE)
                .set(field(Phone.class, "countryCode"), COUNTRY_CODE_OVERRIDE)
                .create();

        assertThat(person.getAddress().getPhoneNumbers())
                .hasSize(INITIAL_SIZE + GENERATE_ELEMENTS)
                // my objects are not populated nor modified via selectors
                .anyMatch(p -> p.getCountryCode().equals(MY_COUNTRY_CODE) && p.getNumber().equals(MY_NUMBER))
                .anyMatch(p -> p.getCountryCode().equals(MY_COUNTRY_CODE) && p.getNumber() == null)
                // generated objects are modified via selectors
                .filteredOn(p -> !p.getCountryCode().equals(MY_COUNTRY_CODE))
                .hasSize(2)
                .allMatch(p -> p.getCountryCode().equals(COUNTRY_CODE_OVERRIDE) && p.getNumber().equals(ALL_STRINGS_OVERRIDE));
    }

    /**
     * <pre>
     *   Case 2:
     *   - do NOT populate null fields in my objects
     *   - allow my custom values to be modified via selectors
     * </pre>
     */
    @Test
    void case2() {
        final Generator<?> generator = getGenerator(AfterGenerate.APPLY_SELECTORS);

        final Person person = Instancio.of(Person.class)
                .supply(field(Address.class, "phoneNumbers"), generator)
                .set(field(Phone.class, "countryCode"), COUNTRY_CODE_OVERRIDE)
                .create();

        assertThat(person.getAddress().getPhoneNumbers())
                .hasSize(INITIAL_SIZE + GENERATE_ELEMENTS)
                // my objects are NOT populated
                // my custom values are overwritten by selectors
                .allMatch(p -> p.getCountryCode().equals(COUNTRY_CODE_OVERRIDE))
                .anyMatch(p -> p.getCountryCode().equals(COUNTRY_CODE_OVERRIDE) && p.getNumber().equals(MY_NUMBER))
                .anyMatch(p -> p.getCountryCode().equals(COUNTRY_CODE_OVERRIDE) && p.getNumber() == null);
    }

    /**
     * <pre>
     *   Case 3: allow all
     *   - populate null fields in my objects
     *   - allow my custom values to be modified via selectors
     * </pre>
     */
    @Test
    void case3() {
        final Generator<?> generator = getGenerator(AfterGenerate.POPULATE_NULLS);

        final Person person = Instancio.of(Person.class)
                .supply(field(Address.class, "phoneNumbers"), generator)
                .set(allStrings(), ALL_STRINGS_OVERRIDE)
                .set(field(Phone.class, "countryCode"), COUNTRY_CODE_OVERRIDE)
                .create();

        assertThat(person.getAddress().getPhoneNumbers())
                .hasSize(INITIAL_SIZE + GENERATE_ELEMENTS)
                .allMatch(p -> p.getCountryCode().equals(COUNTRY_CODE_OVERRIDE) && p.getNumber().equals(ALL_STRINGS_OVERRIDE));
    }
}
