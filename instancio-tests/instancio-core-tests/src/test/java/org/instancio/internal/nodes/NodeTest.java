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
import org.instancio.internal.context.SubtypeSelectorMap;
import org.instancio.internal.util.ReflectionUtils;
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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.TypeVariable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.testsupport.asserts.NodeAssert.assertNode;
import static org.instancio.testsupport.utils.NodeUtils.getChildNode;

@NodeTag
class NodeTest {
    private static final NodeContext NODE_CONTEXT = new NodeContext(
            Collections.emptyMap(),
            new SubtypeSelectorMap(Collections.emptyMap(), Collections.emptyMap()),
            Collections.emptyList());

    private static final NodeFactory NODE_FACTORY = new NodeFactory(NODE_CONTEXT);

    private final Map<TypeVariable<?>, Class<?>> rootTypeMap = new HashMap<>();

    @Test
    void getName() {
        final Node personNode = NODE_FACTORY.createRootNode(Person.class);
        assertThat(personNode.getNodeName()).isEqualTo("Person");
        assertThat(getChildNode(personNode, "age").getNodeName()).isEqualTo("Person.age");
        assertThat(getChildNode(personNode, "name").getNodeName()).isEqualTo("Person.name");
        assertThat(getChildNode(personNode, "address").getNodeName()).isEqualTo("Person.address");
    }

    @Test
    void getNodeKind() {
        assertThat(NODE_FACTORY.createRootNode(Person.class).getNodeKind()).isEqualTo(NodeKind.DEFAULT);
        assertThat(NODE_FACTORY.createRootNode(int.class).getNodeKind()).isEqualTo(NodeKind.DEFAULT);
        assertThat(NODE_FACTORY.createRootNode(Person[].class).getNodeKind()).isEqualTo(NodeKind.ARRAY);
        assertThat(NODE_FACTORY.createRootNode(Types.LIST_STRING.get()).getNodeKind()).isEqualTo(NodeKind.COLLECTION);
        assertThat(NODE_FACTORY.createRootNode(Types.MAP_INTEGER_STRING.get()).getNodeKind()).isEqualTo(NodeKind.MAP);
        assertThat(NODE_FACTORY.createRootNode(new TypeToken<Optional<Integer>>() {}.get()).getNodeKind()).isEqualTo(NodeKind.CONTAINER);
    }

    @Test
    void toBuilder() {
        final Node parent = createNode(Person.class, rootTypeMap, new TypeToken<Person>() {});
        final List<Node> children = Collections.singletonList(createNode(String.class, rootTypeMap, new TypeToken<String>() {}));

        final Node node = Node.builder()
                .type(Types.LIST_STRING.get())
                .rawType(List.class)
                .targetClass(List.class)
                .nodeKind(NodeKind.COLLECTION)
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
        assertThat(copy.getNodeKind()).isEqualTo(node.getNodeKind());
    }

    @Nested
    class EqualsTest {

        @Test
        void equalsHashCode() {
            TypeToken<?> typeBazInteger = new TypeToken<Baz<Integer>>() {};
            TypeToken<?> typeBazString = new TypeToken<Baz<String>>() {};

            Node bazInteger = createNode(List.class, rootTypeMap, typeBazInteger);
            Node bazString = createNode(List.class, rootTypeMap, typeBazString);
            NodeContext nodeContext = new NodeContext(rootTypeMap, null, Collections.emptyList());
            Node bazIntegerClassNode = Node.builder()
                    .nodeContext(nodeContext)
                    .type(typeBazInteger.get())
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
                    .hasType(Types.LIST_STRING.get());
        }

        @Test
        void mapOfIntegerString() {
            assertNode(createNode(Map.class, rootTypeMap, Types.MAP_INTEGER_STRING))
                    .hasTargetClass(Map.class)
                    .hasType(Types.MAP_INTEGER_STRING.get());
        }

        @Test
        void pairOfIntegerString() {
            assertNode(createNode(Pair.class, rootTypeMap, Types.PAIR_INTEGER_STRING))
                    .hasTargetClass(Pair.class)
                    .hasType(Types.PAIR_INTEGER_STRING.get());
        }

        @Test
        void tripletOfBooleanStringInteger() {
            assertNode(createNode(Triplet.class, rootTypeMap, Types.TRIPLET_BOOLEAN_INTEGER_STRING))
                    .hasTargetClass(Triplet.class)
                    .hasType(Types.TRIPLET_BOOLEAN_INTEGER_STRING.get());
        }

        @Test
        void pairOfGenericItemFooList() {
            final TypeToken<?> type = new TypeToken<Pair<Item<Foo<List<Integer>>>, Map<Integer, Foo<String>>>>() {};

            assertNode(createNode(Pair.class, rootTypeMap, type))
                    .hasTargetClass(Pair.class)
                    .hasType(type.get());
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
            assertNode(createNode(Pair.class, rootTypeMap, new TypeToken<Pair<Item<String>, Foo<List<Integer>>>>() {}))
                    .hasTypeMappedTo(Pair.class, "L", Types.ITEM_STRING.get())
                    .hasTypeMappedTo(Pair.class, "R", Types.FOO_LIST_INTEGER.get())
                    .hasTypeMapWithSize(2);
        }
    }

    @Nested
    class ToStringTest {

        @Test
        void verifyToString() {
            final Node personNode = NODE_FACTORY.createRootNode(Person.class);
            assertThat(personNode).hasToString("Node[Person, #chn=9, Person]");
            assertThat(getChildNode(personNode, "age")).hasToString("Node[Person.age, #chn=0, int]");
            assertThat(getChildNode(personNode, "name")).hasToString("Node[Person.name, #chn=0, String]");
            assertThat(getChildNode(personNode, "address")).hasToString("Node[Person.address, #chn=4, Address]");

            assertThat(NODE_FACTORY.createRootNode(String.class)).hasToString("Node[String, #chn=0, String]");
            assertThat(NODE_FACTORY.createRootNode(new TypeToken<Pair<Item<String>, Foo<List<Integer>>>>() {}.get()))
                    .hasToString("Node[Pair, #chn=2, Pair<Item<String>, Foo<List<Integer>>>]");
        }
    }

    private static Node createNode(Class<?> klass, Map<TypeVariable<?>, Class<?>> rootTypeMap, TypeToken<?> type) {
        final NodeContext nodeContext = new NodeContext(rootTypeMap, null, Collections.emptyList());
        return Node.builder()
                .nodeContext(nodeContext)
                .type(type.get())
                .rawType(klass)
                .targetClass(klass)
                .build();
    }
}
