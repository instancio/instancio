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
package org.instancio.test.features.nullable;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.basic.ClassWithInitializedField;
import org.instancio.test.support.pojo.basic.IntegerHolder;
import org.instancio.test.support.pojo.basic.LongHolder;
import org.instancio.test.support.pojo.person.Address;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.pojo.person.Pet;
import org.instancio.test.support.pojo.person.Phone;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.tags.NonDeterministicTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.withinPercentage;
import static org.instancio.Select.all;
import static org.instancio.Select.allInts;
import static org.instancio.Select.allStrings;
import static org.instancio.Select.field;
import static org.instancio.Select.scope;

@NonDeterministicTag
@FeatureTag({Feature.NULLABILITY, Feature.WITH_NULLABLE})
@ExtendWith(InstancioExtension.class)
class WithNullableAdhocTest {

    private static final int SAMPLE_SIZE = 500;

    @Test
    @DisplayName("Set selector with scope as nullable")
    void nullableWithSelectorScope() {
        Set<String> nullableCountryCode = new HashSet<>();
        Set<String> nullablePhoneNumber = new HashSet<>();
        Set<String> nonNullableResults = new HashSet<>();

        for (int i = 0; i < SAMPLE_SIZE; i++) {
            final Person person = Instancio.of(Person.class)
                    .withNullable(allStrings().within(scope(Phone.class)))
                    .create();

            final Address address = person.getAddress();
            final List<Phone> phoneNumbers = address.getPhoneNumbers();
            nullableCountryCode.addAll(phoneNumbers.stream().map(Phone::getCountryCode).collect(toSet()));
            nullablePhoneNumber.addAll(phoneNumbers.stream().map(Phone::getNumber).collect(toSet()));
            nonNullableResults.add(person.getName());
            nonNullableResults.add(address.getAddress());
            nonNullableResults.add(address.getCity());
            nonNullableResults.add(address.getCountry());
            nonNullableResults.addAll(Arrays.stream(person.getPets()).map(Pet::getName).collect(toSet()));
        }

        assertThat(nullableCountryCode).containsNull();
        assertThat(nullablePhoneNumber).containsNull();
        assertThat(nonNullableResults).doesNotContainNull();
    }

    @Test
    void nullableWithSetAndStream() {
        final Set<IntegerHolder> results = Instancio.of(IntegerHolder.class)
                .withNullable(all(
                        field(IntegerHolder::getPrimitive),
                        field(IntegerHolder::getWrapper)))
                .set(field(IntegerHolder::getPrimitive), -123)
                .set(field(IntegerHolder::getWrapper), -123)
                .stream()
                .limit(SAMPLE_SIZE)
                .collect(toSet());

        assertThat(results)
                .doesNotContainNull()
                .anyMatch(r -> r.getWrapper() == null)
                .anyMatch(r -> r.getPrimitive() == 0);
    }

    @Test
    @DisplayName("Specifying nullable for a primitive field leaves the field with a default value")
    void nullableWithPrimitiveFieldResultsInDefaultValue() {
        final int sampleSize = 10_000;
        final List<Long> results = Instancio.of(LongHolder.class)
                .withNullable(field("primitive"))
                .stream()
                .limit(sampleSize)
                .map(LongHolder::getPrimitive)
                .collect(toList());

        final int frequency = Collections.frequency(results, 0L);
        final double percentage = frequency / (double) sampleSize * 100;
        final double diceRoll = 1 / 6d * 100;
        assertThat(percentage).isCloseTo(diceRoll, withinPercentage(25));
    }

    @Test
    @DisplayName("Nullable with 'allInts()' should randomly set Integer to null")
    void nullableWithAllInts() {
        Set<Object> results = new HashSet<>();
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            final IntegerHolder result = Instancio.of(IntegerHolder.class)
                    .withNullable(allInts())
                    .create();

            results.add(result.getWrapper());
        }
        assertThat(results).containsNull();
    }

    @Test
    @DisplayName("A nullable field with a default value will be randomly overwritten with null")
    void nullableInitializedField() {
        Set<Object> results = new HashSet<>();

        for (int i = 0; i < SAMPLE_SIZE; i++) {
            final ClassWithInitializedField holder = Instancio.of(ClassWithInitializedField.class)
                    .withNullable(field("stringValue"))
                    .create();

            results.add(holder.getStringValue());
        }

        assertThat(results)
                .hasSizeGreaterThan(5)
                .containsNull();
    }
}