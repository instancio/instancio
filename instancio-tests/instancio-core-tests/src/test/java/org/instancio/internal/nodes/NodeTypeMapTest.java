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
package org.instancio.internal.nodes;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.instancio.internal.RootType;
import org.instancio.internal.util.ReflectionUtils;
import org.instancio.test.support.pojo.generics.PairAPairIntegerString;
import org.instancio.test.support.pojo.generics.PairLongPairIntegerString;
import org.instancio.test.support.pojo.generics.TripletAFooBarBazStringListOfB;
import org.instancio.test.support.pojo.generics.basic.Pair;
import org.instancio.test.support.pojo.generics.basic.Triplet;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.tags.GenericsTag;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.instancio.testsupport.asserts.NodeTypeMapAssert.assertNodeTypeMap;

@GenericsTag
class NodeTypeMapTest {

    private static final Map<Type, Type> EMPTY_SUBTYPE_MAP = Collections.emptyMap();

    @Test
    void verifyEqualsAndHashcode() {
        EqualsVerifier.forClass(NodeTypeMap.class)
                .withIgnoredFields("rootType")
                .verify();
    }

    @Test
    void personAge() {
        final Class<?> klass = Person.class;
        final String field = "age";
        final RootType rootType = new RootType(Person.class, Collections.emptyList());

        final NodeTypeMap typeMap = new NodeTypeMap(getGenericType(klass, field), rootType, EMPTY_SUBTYPE_MAP);

        assertNodeTypeMap(typeMap).hasEmptyTypeMap();
    }

    @Test
    void pairAPairIntegerString() {
        final Class<?> klass = PairAPairIntegerString.class;
        final String field = "pairAPairIntegerString";
        final RootType rootType = new RootType(klass, List.of(Boolean.class));

        final NodeTypeMap typeMap = new NodeTypeMap(getGenericType(klass, field), rootType, EMPTY_SUBTYPE_MAP);

        assertNodeTypeMap(typeMap)
                .hasTypeMapping(Pair.class, "L", Boolean.class)
                .hasTypeMapping(Pair.class, "R", "org.instancio.test.support.pojo.generics.basic." +
                        "Pair<java.lang.Integer, java.lang.String>")
                .hasTypeMapWithSize(2);
    }

    @Test
    void pairLongPairIntegerString() {
        final Class<?> klass = PairLongPairIntegerString.class;
        final String field = "pairLongPairIntegerString";
        final RootType rootType = new RootType(klass, Collections.emptyList());

        final NodeTypeMap typeMap = new NodeTypeMap(getGenericType(klass, field), rootType, EMPTY_SUBTYPE_MAP);

        assertNodeTypeMap(typeMap)
                .hasTypeMapping(Pair.class, "L", Long.class)
                .hasTypeMapping(Pair.class, "R", "org.instancio.test.support.pojo.generics.basic." +
                        "Pair<java.lang.Integer, java.lang.String>")
                .hasTypeMapWithSize(2);
    }

    @Test
    void tripletAFooBarBazStringListOfB() {
        final Class<?> klass = TripletAFooBarBazStringListOfB.class;
        final String field = "tripletA_FooBarBazString_ListOfB";
        final RootType rootType = new RootType(klass, List.of(Long.class, Long.class));

        final NodeTypeMap typeMap = new NodeTypeMap(getGenericType(klass, field), rootType, EMPTY_SUBTYPE_MAP);

        assertNodeTypeMap(typeMap)
                .hasTypeMapping(Triplet.class, "M", Long.class)
                .hasTypeMapping(Triplet.class, "N", "org.instancio.test.support.pojo.generics.foobarbaz." +
                        "Foo<org.instancio.test.support.pojo.generics.foobarbaz." +
                        "Bar<org.instancio.test.support.pojo.generics.foobarbaz." +
                        "Baz<java.lang." +
                        "String>>>")
                .hasTypeMapping(Triplet.class, "O", "java.util.List<B>")
                .hasTypeMapWithSize(3);
    }

    private static Type getGenericType(Class<?> klass, String fieldName) {
        return ReflectionUtils.getField(klass, fieldName).getGenericType();
    }

}
