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
package org.instancio.creation.bulk;

import org.instancio.Instancio;
import org.instancio.test.support.pojo.generics.MiscFields;
import org.instancio.test.support.pojo.generics.TripletAFooBarBazStringListOfB;
import org.instancio.test.support.pojo.generics.basic.Item;
import org.instancio.test.support.pojo.generics.basic.Pair;
import org.instancio.test.support.pojo.generics.basic.Triplet;
import org.instancio.test.support.pojo.generics.container.OneItemContainer;
import org.instancio.test.support.pojo.generics.foobarbaz.Bar;
import org.instancio.test.support.pojo.generics.foobarbaz.Baz;
import org.instancio.test.support.pojo.generics.foobarbaz.Foo;
import org.instancio.test.support.pojo.person.Address;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.tags.CreateTag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.instancio.test.support.asserts.ReflectionAssert.assertThatObject;

/**
 * Tests for creating objects via {@link Instancio#of(Class)} and
 * {@link org.instancio.InstancioOfClassApi#withTypeParameters(Class[])}.
 */
@CreateTag
class CreateClassBulkAutoVerificationTest {

    @MethodSource("typesToCreate")
    @ParameterizedTest
    void verifyFullyPopulated(final TypeToCreate typeToCreate) {
        final Object result = Instancio.of(typeToCreate.targetClass)
                .withTypeParameters(typeToCreate.typeArgs)
                .create();

        assertThatObject(result)
                .as("Expected fully-populated object for argument '%s', but was: %s", typeToCreate, result)
                .isFullyPopulated();
    }

    private static Stream<Arguments> typesToCreate() {
        return Stream.of(
                Arguments.of(TypeToCreate.of(Bar.class, Address.class)),
                Arguments.of(TypeToCreate.of(Baz.class, Person.class)),
                Arguments.of(TypeToCreate.of(Collection.class, Person.class)),
                Arguments.of(TypeToCreate.of(Collection.class, String.class)),
                Arguments.of(TypeToCreate.of(Foo.class, String.class)),
                Arguments.of(TypeToCreate.of(Integer[].class)),
                Arguments.of(TypeToCreate.of(Item.class, Integer.class)),
                Arguments.of(TypeToCreate.of(Item.class, Integer[].class)),
                Arguments.of(TypeToCreate.of(Item.class, Person.class)),
                Arguments.of(TypeToCreate.of(Item.class, int[].class)),
                Arguments.of(TypeToCreate.of(Item[].class, Integer.class)),
                Arguments.of(TypeToCreate.of(List.class, Person.class)),
                Arguments.of(TypeToCreate.of(List.class, String.class)),
                Arguments.of(TypeToCreate.of(List[].class, Person.class)),
                Arguments.of(TypeToCreate.of(List[].class, int.class)),
                Arguments.of(TypeToCreate.of(Map.class, Person.class, Address.class)),
                Arguments.of(TypeToCreate.of(Map.class, String.class, Integer.class)),
                Arguments.of(TypeToCreate.of(Map.class, String.class, Person.class)),
                Arguments.of(TypeToCreate.of(Map[].class, Integer.class, String.class)),
                Arguments.of(TypeToCreate.of(Map[].class, Person.class, Address.class)),
                Arguments.of(TypeToCreate.of(MiscFields.class, Integer.class, String.class, Boolean.class)),
                Arguments.of(TypeToCreate.of(MiscFields[].class, Integer.class, String.class, Boolean.class)),
                Arguments.of(TypeToCreate.of(MiscFields[].class, Person.class, Address.class, Boolean.class)),
                Arguments.of(TypeToCreate.of(OneItemContainer[].class, Integer.class)),
                Arguments.of(TypeToCreate.of(Pair.class, Boolean.class, Byte.class)),
                Arguments.of(TypeToCreate.of(Pair.class, Person.class, Address.class)),
                Arguments.of(TypeToCreate.of(Pair.class, int[].class, Integer[].class)),
                Arguments.of(TypeToCreate.of(Pair[].class, String.class, int.class)),
                Arguments.of(TypeToCreate.of(TripletAFooBarBazStringListOfB[].class, Integer.class, String.class)),
                Arguments.of(TypeToCreate.of(Triplet[].class, String.class, Address.class, Person.class)),
                Arguments.of(TypeToCreate.of(int[].class)));
    }

    private static class TypeToCreate {
        private Class<?> targetClass;
        private Class<?>[] typeArgs;

        private static TypeToCreate of(final Class<?> targetClass, final Class<?>... typeArgs) {
            final TypeToCreate type = new TypeToCreate();
            type.targetClass = targetClass;
            type.typeArgs = typeArgs;
            return type;
        }

        @Override
        public String toString() {
            return String.format("'%s', type params: %s", targetClass.getSimpleName(), Arrays.toString(typeArgs));
        }
    }
}
