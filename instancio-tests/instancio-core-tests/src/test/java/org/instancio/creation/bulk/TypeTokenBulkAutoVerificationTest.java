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
package org.instancio.creation.bulk;

import org.instancio.Instancio;
import org.instancio.TypeToken;
import org.instancio.TypeTokenSupplier;
import org.instancio.internal.util.Format;
import org.instancio.test.support.pojo.generics.ListWithTypeVariable;
import org.instancio.test.support.pojo.generics.MapWithTypeVariables;
import org.instancio.test.support.pojo.generics.MiscFields;
import org.instancio.test.support.pojo.generics.basic.Item;
import org.instancio.test.support.pojo.generics.basic.Pair;
import org.instancio.test.support.pojo.generics.basic.Triplet;
import org.instancio.test.support.pojo.generics.foobarbaz.Bar;
import org.instancio.test.support.pojo.generics.foobarbaz.Baz;
import org.instancio.test.support.pojo.generics.foobarbaz.Foo;
import org.instancio.test.support.pojo.generics.inheritance.GenericTypesWithInheritance;
import org.instancio.test.support.pojo.person.Address;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.pojo.person.Phone;
import org.instancio.test.support.tags.CreateTag;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static org.instancio.test.support.asserts.ReflectionAssert.assertThatObject;

/**
 * Tests for creating objects via {@link Instancio#create(TypeTokenSupplier)}.
 */
@CreateTag
class TypeTokenBulkAutoVerificationTest {

    @MethodSource("typesToCreate")
    @ParameterizedTest
    void verifyFullyPopulated(final TypeToken<?> typeToken) {
        final Object result = Instancio.of(typeToken)
                .withMaxDepth(Integer.MAX_VALUE)
                .create();

        assertThatObject(result)
                .as("Expected fully-populated object for argument '%s', but was: %s", typeToken, result)
                .isFullyPopulated();
    }

    private static Stream<Arguments> typesToCreate() {
        return TYPES.stream().map(type -> Arguments.of(Named.of(Format.withoutPackage(type.get()), type)));
    }

    private static final List<TypeToken<?>> TYPES = Arrays.asList(
            new TypeToken<GenericTypesWithInheritance.GenericPhoneHolderWithInheritance<Phone>>() {},
            new TypeToken<Foo<Bar<Baz<Foo<Bar<Baz<String>>>>>>>() {},
            new TypeToken<Foo<Foo<Foo<Baz<Baz<Baz<Foo<Short>>>>>>>>() {},
            new TypeToken<Integer[]>() {},
            new TypeToken<Item<Integer>[]>() {},
            new TypeToken<Item<Integer[]>>() {},
            new TypeToken<Item<Item<Integer>>>() {},
            new TypeToken<Item<Item<Integer>[]>[]>() {},
            new TypeToken<Item<Item<Item<Item<String>>>>>() {},
            new TypeToken<Item<List<Pair<String, Integer>>>>() {},
            new TypeToken<Item<Map<String, Integer>>>() {},
            new TypeToken<Item<int[]>>() {},
            new TypeToken<List<Item<Item<List<String>>>>>() {},
            new TypeToken<List<Item<List<Item<List<Item<String>>>>>>>() {},
            new TypeToken<List<Item<Pair<Integer, String>>[]>>() {},
            new TypeToken<List<Item<Pair<Integer, String>[]>>>() {},
            new TypeToken<List<List<List<List<String>>>>>() {},
            new TypeToken<List<Map<Item<Pair<String, Boolean>>, Item<String>[]>>>() {},
            new TypeToken<List<Map<MiscFields<Item<String>, Pair<Boolean, Long>, Triplet<Person, Address, Boolean>>, Pair<Map<Item<String>, Item<Short>>, Long>>>>() {},
            new TypeToken<List<Map<Short, List<String>>>>() {},
            new TypeToken<List<MiscFields<Pair<Integer, String>, Item<UUID>, Triplet<Long, Byte, Boolean>>>>() {},
            new TypeToken<List<MiscFields<String, Item<UUID>, Integer>>>() {},
            new TypeToken<List<Pair<Integer, String>>>() {},
            new TypeToken<List<Triplet<Item<Long>, Pair<Integer, String>, Item<UUID>>>>() {},
            new TypeToken<List<int[]>>() {},
            new TypeToken<List<int[]>>() {},
            new TypeToken<ListWithTypeVariable<MiscFields<Pair<Integer, String>, Item<UUID>, Triplet<Long, Byte, Boolean>>>>() {},
            new TypeToken<Map<Integer, Person>>() {},
            new TypeToken<Map<Item<Map<Integer, Pair<Integer, String>[]>>, String>>() {},
            new TypeToken<Map<Item<Map<Integer, String>>, String>>() {},
            new TypeToken<Map<Item<String>, MiscFields<Pair<Integer, String>, Item<UUID>, Triplet<Long, Byte, Boolean>>>>() {},
            new TypeToken<Map<MiscFields<Item<String>, Pair<Boolean, Long>, Triplet<Person, Address, Boolean>>, Pair<Map<Item<String>, Item<Short>>, Long>>>() {},
            new TypeToken<Map<Pair<Integer, String>, Pair<Map<String, Short>, Long>>>() {},
            new TypeToken<Map<String, Item<Integer>[]>>() {},
            new TypeToken<Map<String, Map<Integer, List<String>>>>() {},
            new TypeToken<Map<String, Map<Integer, String>>>() {},
            new TypeToken<MapWithTypeVariables<Item<String>, MiscFields<Pair<Integer, String>, Item<UUID>, Triplet<Long, Byte, Boolean>>>>() {},
            new TypeToken<Pair<Boolean, Pair<Byte, Pair<Integer, String>>>>() {},
            new TypeToken<Pair<Boolean, Pair<Byte, String>>>() {},
            new TypeToken<Pair<Item<int[]>, Item<Integer[]>>>() {},
            new TypeToken<Pair<Pair<Byte, String>, Pair<Integer, String>>>() {},
            new TypeToken<Pair<String, int[]>>() {},
            new TypeToken<Pair<int[], Integer[]>>() {},
            new TypeToken<Person>() {},
            new TypeToken<int[]>() {});

}
