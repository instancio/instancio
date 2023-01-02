/*
 * Copyright 2022-2023 the original author or authors.
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
import org.instancio.InstancioApi;
import org.instancio.TargetSelector;
import org.instancio.generator.AfterGenerate;
import org.instancio.generator.Generator;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.person.Address;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.pojo.person.RichPerson;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatObject;
import static org.instancio.Select.all;
import static org.instancio.Select.field;

/**
 * Tests combining the following methods when creating an object:
 *
 * <ul>
 *   <li>{@link InstancioApi#supply(TargetSelector, Generator)}</li>
 *   <li>{@link InstancioApi#supply(TargetSelector, Supplier)}</li>
 * </ul>
 */
@FeatureTag({Feature.GENERATOR, Feature.SUPPLY})
@ExtendWith(InstancioExtension.class)
class CustomGeneratorAndSupplierTest {

    @ParameterizedTest
    @EnumSource(AfterGenerate.class)
    @DisplayName("Objects returned by supply(Supplier) should NEVER be populated regardless of AfterGenerate")
    void objectsReturnBySupplyShouldNeverBePopulated(final AfterGenerate afterGenerate) {
        final Person result = Instancio.of(Person.class)
                .supply(all(Person.class), () -> Person.builder().build())
                .withSettings(Settings.create()
                        .set(Keys.AFTER_GENERATE_HINT, afterGenerate))
                .create();

        assertThatObject(result).hasAllNullFieldsOrPropertiesExcept("age", "finalField");
        assertThat(result.getAge()).isZero();
    }

    @Test
    @DisplayName("Combine supply(Supplier) and supply(Generator)")
    void combineSupplierAndGeneratorTest() {
        final Address addressFromSupplier = Address.builder().build();
        final Supplier<Address> addressSupplier = () -> addressFromSupplier;
        final Generator<Address> addressGenerator = random -> Address.builder().build();

        final int[] callbackCount = new int[1];

        final String expectedCity = "a-city";
        final RichPerson result = Instancio.of(RichPerson.class)
                .supply(field("address1"), addressSupplier)
                .supply(field("address2"), addressGenerator)
                .set(field(Address.class, "city"), expectedCity)
                .onComplete(all(Address.class), (Address a) -> {
                    assertThat(a).isNotSameAs(addressFromSupplier);
                    callbackCount[0]++;
                })
                .withSettings(Settings.create()
                        .set(Keys.AFTER_GENERATE_HINT, AfterGenerate.POPULATE_NULLS))
                .create();

        assertThat(callbackCount[0])
                .as("Callbacks should not be executed object from Supplier")
                .isEqualTo(1);

        assertThatObject(result.getAddress1())
                .as("Address from Supplier should NOT be populated")
                .hasAllNullFieldsOrProperties();

        assertThatObject(result.getAddress2())
                .as("Address from Generator SHOULD be populated")
                .hasNoNullFieldsOrProperties();

        assertThat(result.getAddress1().getCity())
                .as("Address from Supplier should NOT have selectors applied to it")
                .isNull();

        assertThat(result.getAddress2().getCity())
                .as("Address from Generator SHOULD not have selectors applied to it")
                .isEqualTo(expectedCity);
    }
}
