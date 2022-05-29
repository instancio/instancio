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
package org.instancio.internal.nodes;

import org.instancio.TypeToken;
import org.instancio.test.support.pojo.collections.lists.ListString;
import org.instancio.test.support.pojo.generics.basic.Item;
import org.instancio.test.support.pojo.generics.basic.Pair;
import org.instancio.test.support.pojo.generics.basic.Triplet;
import org.instancio.test.support.pojo.generics.foobarbaz.Baz;
import org.instancio.test.support.pojo.generics.foobarbaz.Foo;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.tags.GenericsTag;
import org.instancio.test.support.tags.NodeTag;
import org.instancio.testsupport.fixtures.Types;
import org.instancio.util.ReflectionUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.testsupport.asserts.NodeAssert.assertNode;

@NodeTag
class NodeTest {
    private static final NodeContext NODE_CONTEXT = new NodeContext(Collections.emptyMap(), null);
    private final Map<TypeVariable<?>, Class<?>> rootTypeMap = new HashMap<>();

    @Test
    void toBuilder() {
        final Node parent = createNode(Person.class, rootTypeMap, new TypeToken<Person>() {});
        final List<Node> children = Collections.singletonList(createNode(String.class, rootTypeMap, new TypeToken<String>() {}));

        final Node node = Node.builder()
                .type(Types.LIST_STRING.get())
                .rawType(List.class)
                .targetClass(List.class)
                .field(ReflectionUtils.getField(ListString.class, "list"))
                .parent(parent)
                .nodeContext(NODE_CONTEXT)
                .children(children)
                .build();

        final Node copy = node.toBuilder().build();

        assertThat(copy.getNodeContext()).isEqualTo(node.getNodeContext());
        assertThat(copy.getType()).isEqualTo(node.getType());
        assertThat(copy.getRawType()).isEqualTo(node.getRawType());
        assertThat(copy.getTargetClass()).isEqualTo(node.getTargetClass());
        assertThat(copy.getField()).isEqualTo(node.getField());
        assertThat(copy.getParent()).isEqualTo(node.getParent());
        assertThat(copy.getTypeMap()).isEqualTo(node.getTypeMap());
        assertThat(copy.getOnlyChild()).isEqualTo(node.getOnlyChild());
        assertThat(copy.getChildren()).isEqualTo(node.getChildren());
    }

    @Nested
    class EqualsTest {

        @Test
        void equalsHashCode() {
            TypeToken<?> typeBazInteger = new TypeToken<Baz<Integer>>() {};
            TypeToken<?> typeBazString = new TypeToken<Baz<String>>() {};

            Node bazInteger = createNode(List.class, rootTypeMap, typeBazInteger);
            Node bazString = createNode(List.class, rootTypeMap, typeBazString);
            NodeContext nodeContext = new NodeContext(rootTypeMap, null);
            Node bazIntegerClassNode = Node.builder()
                    .nodeContext(nodeContext)
                    .type(getTypeOf(typeBazInteger))
                    .rawType(Baz.class)
                    .targetClass(Baz.class)
                    .build();

            assertThat(bazString).isEqualTo(bazString).hasSameHashCodeAs(bazString);
            assertThat(bazString).isNotEqualTo(bazInteger).doesNotHaveSameHashCodeAs(bazInteger);
            assertThat(bazInteger).isNotEqualTo(bazIntegerClassNode).doesNotHaveSameHashCodeAs(bazIntegerClassNode);
        }
    }

    @Nested
    @GenericsTag
    class GenericTypeTest {

        @Test
        void listOfString() {
            assertNode(createNode(List.class, rootTypeMap, Types.LIST_STRING))
                    .hasTargetClass(List.class)
                    .hasType(getTypeOf(Types.LIST_STRING));
        }

        @Test
        void mapOfIntegerString() {
            assertNode(createNode(Map.class, rootTypeMap, Types.MAP_INTEGER_STRING))
                    .hasTargetClass(Map.class)
                    .hasType(getTypeOf(Types.MAP_INTEGER_STRING));
        }

        @Test
        void pairOfIntegerString() {
            assertNode(createNode(Pair.class, rootTypeMap, Types.PAIR_INTEGER_STRING))
                    .hasTargetClass(Pair.class)
                    .hasType(getTypeOf(Types.PAIR_INTEGER_STRING));
        }

        @Test
        void tripletOfBooleanStringInteger() {
            assertNode(createNode(Triplet.class, rootTypeMap, Types.TRIPLET_BOOLEAN_INTEGER_STRING))
                    .hasTargetClass(Triplet.class)
                    .hasType(getTypeOf(Types.TRIPLET_BOOLEAN_INTEGER_STRING));
        }

        @Test
        void pairOfGenericItemFooList() {
            final TypeToken<?> type = new TypeToken<Pair<Item<Foo<List<Integer>>>, Map<Integer, Foo<String>>>>() {};

            assertNode(createNode(Pair.class, rootTypeMap, type))
                    .hasTargetClass(Pair.class)
                    .hasType(getTypeOf(type));
        }
    }

    @Nested
    @GenericsTag
    class NodeTypeMapTest {

        @Test
        void listOfStrings() {
            assertNode(createNode(List.class, rootTypeMap, Types.LIST_STRING))
                    .hasTypeMappedTo(List.class, "E", String.class)
                    .hasTypeMapWithSize(1);
        }

        @Test
        void mapOfIntegerString() {
            assertNode(createNode(Map.class, rootTypeMap, Types.MAP_INTEGER_STRING))
                    .hasTypeMappedTo(Map.class, "K", Integer.class)
                    .hasTypeMappedTo(Map.class, "V", String.class)
                    .hasTypeMapWithSize(2);
        }

        @Test
        void tripletOfBooleanStringInteger() {
            assertNode(createNode(Triplet.class, rootTypeMap, Types.TRIPLET_BOOLEAN_INTEGER_STRING))
                    .hasTypeMappedTo(Triplet.class, "M", Boolean.class)
                    .hasTypeMappedTo(Triplet.class, "N", Integer.class)
                    .hasTypeMappedTo(Triplet.class, "O", String.class)
                    .hasTypeMapWithSize(3);
        }

        @Test
        void pairOfGenericItemFooList() {
            final Type typeLeft = getTypeOf(Types.ITEM_STRING);
            final Type typeRight = getTypeOf(Types.FOO_LIST_INTEGER);

            assertNode(createNode(Pair.class, rootTypeMap, new TypeToken<Pair<Item<String>, Foo<List<Integer>>>>() {}))
                    .hasTypeMappedTo(Pair.class, "L", typeLeft)
                    .hasTypeMappedTo(Pair.class, "R", typeRight)
                    .hasTypeMapWithSize(2);
        }
    }

    @Nested
    class ToStringTest {

        @Test
        void verifyToString() {
            final Node age = Node.builder()
                    .nodeContext(NODE_CONTEXT)
                    .type(Person.class)
                    .rawType(Person.class)
                    .targetClass(Person.class)
                    .field(ReflectionUtils.getField(Person.class, "age"))
                    .build();

            assertThat(age).hasToString("Node[Person.age, #chn=0, Person]");

            final Node node = Node.builder().nodeContext(NODE_CONTEXT)
                    .type(Person.class)
                    .rawType(Person.class)
                    .targetClass(Person.class)
                    .build();

            node.setChildren(Collections.singletonList(age));
            assertThat(node).hasToString("Node[Person, #chn=1, Person]");

            assertThat(Node.builder()
                    .nodeContext(NODE_CONTEXT)
                    .type(String.class)
                    .rawType(String.class)
                    .targetClass(String.class)
                    .field(null)
                    .build()).hasToString("Node[String, #chn=0, String]");

            assertThat(Node.builder()
                    .nodeContext(NODE_CONTEXT)
                    .type(getTypeOf(new TypeToken<Pair<Item<String>, Foo<List<Integer>>>>() {}))
                    .rawType(Pair.class)
                    .targetClass(Pair.class)
                    .build())
                    .hasToString("Node[Pair, #chn=0, Pair<Item<String>, Foo<List<Integer>>>]");
        }
    }

    private static Node createNode(Class<?> klass, Map<TypeVariable<?>, Class<?>> rootTypeMap, TypeToken<?> type) {
        final NodeContext nodeContext = new NodeContext(rootTypeMap, null);
        return Node.builder()
                .nodeContext(nodeContext)
                .type(getTypeOf(type))
                .rawType(klass)
                .targetClass(klass)
                .build();
    }

    private static Type getTypeOf(TypeToken<?> type) {
        return type.get();
    }
}
