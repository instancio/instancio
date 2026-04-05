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

import com.google.protobuf.ByteString;
import org.instancio.Instancio;
import org.instancio.TypeToken;
import org.instancio.internal.spi.InternalExtension.InternalNullSubstitutor;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.prototobuf.Proto;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.field;
import static org.instancio.Select.root;

@ExtendWith(InstancioExtension.class)
class ProtoWithNullableTest {

    private static final int MAP_SIZE = 100;

    @WithSettings
    private static final Settings settings = Settings.create()
            // long strings to avoid chance of duplicate map keys
            .set(Keys.STRING_MIN_LENGTH, 32)
            // larger map size to increase chance of nullable values
            .set(Keys.MAP_MIN_SIZE, MAP_SIZE)
            .set(Keys.MAP_MAX_SIZE, MAP_SIZE);

    @Test
    void nullableRoot() {
        final Stream<Proto.Person> results = Instancio.of(Proto.Person.class)
                .withNullable(root())
                .stream()
                .limit(Constants.SAMPLE_SIZE_DD);

        assertThat(results)
                .hasSize(Constants.SAMPLE_SIZE_DD)
                .containsNull();
    }

    @RepeatedTest(Constants.SAMPLE_SIZE_D)
    void nullableNestedObject() {
        // must be large enough to ensure at least one "null" value is generated
        final int size = 100;

        final List<Proto.Person> results = Instancio.of(Proto.Person.class)
                .withNullable(all(Proto.Address.class))
                .generate(all(Map.class), gen -> gen.map().nullableValues())
                .stream()
                .limit(size)
                .toList();

        assertThat(results)
                .extracting(Proto.Person::getAddress)
                .hasSize(size)
                .anyMatch(address -> address.equals(Proto.Address.getDefaultInstance()))
                .anyMatch(address -> !address.equals(Proto.Address.getDefaultInstance()));

        assertThat(results)
                .extracting(Proto.Person::getAddressesMap)
                .extracting(Map::values)
                .hasSize(size)
                .allSatisfy(mapAddressValues ->
                        assertThat(mapAddressValues)
                                .anyMatch(it -> it.equals(Proto.Address.getDefaultInstance()))
                                .anyMatch(it -> !it.equals(Proto.Address.getDefaultInstance())));
    }

    @RepeatedTest(Constants.SAMPLE_SIZE_D)
    void nullableField() {
        // must be large enough to ensure at least one "null" value is generated
        final int size = 100;

        final List<Proto.Person> results = Instancio.of(Proto.Person.class)
                .withNullable(field(Proto.Person::getName))
                .withNullable(field(Proto.Address::getCity))
                .stream()
                .limit(size)
                .toList();

        assertThat(results)
                .hasSize(size)
                .anySatisfy(person -> assertThat(person.getName()).isEmpty())
                .anySatisfy(person -> assertThat(person.getAddress().getCity()).isEmpty());

        assertThat(results)
                .extracting(Proto.Person::getAddressesMap)
                .extracting(Map::values)
                .hasSize(size)
                .allSatisfy(mapAddressValues ->
                        assertThat(mapAddressValues)
                                .anyMatch(it -> it.getCity().isEmpty())
                                .anyMatch(it -> !it.getCity().isEmpty()));
    }

    /**
     * This test is to cover a specific null-guard in proto SPI's
     * {@link InternalNullSubstitutor} implementation.
     */
    @RepeatedTest(Constants.SAMPLE_SIZE_D)
    void nullableFieldOnNonProtoType() {
        record Pojo(String name) {}

        final int size = 100;

        final List<Pojo> results = Instancio.of(Pojo.class)
                .withNullable(all(String.class))
                .stream()
                .limit(size)
                .toList();

        assertThat(results)
                .hasSize(size)
                .anyMatch(p -> p.name() == null)
                .anyMatch(p -> p.name() != null);
    }

    /**
     * This test is to cover a specific null-guard in proto SPI's
     * {@link InternalNullSubstitutor} implementation.
     */
    @RepeatedTest(Constants.SAMPLE_SIZE_D)
    void nullableElementInRootLevelCollection_contextFieldIsNull() {
        final List<String> result = Instancio.of(new TypeToken<List<String>>() {})
                .withNullable(all(String.class))
                .create();

        assertThat(result).doesNotContainNull();
    }

    @RepeatedTest(Constants.SAMPLE_SIZE_D)
    void nullableBytesField() {
        final int size = 100;

        final List<Proto.SupportedOtherTypes> results = Instancio.of(Proto.SupportedOtherTypes.class)
                .withNullable(field(Proto.SupportedOtherTypes::getBytesField))
                .stream()
                .limit(size)
                .toList();

        assertThat(results)
                .hasSize(size)
                .anySatisfy(r -> assertThat(r.getBytesField()).isEqualTo(ByteString.EMPTY))
                .anySatisfy(r -> assertThat(r.getBytesField()).isNotEmpty());
    }
}
