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
import org.instancio.test.support.pojo.generics.basic.Item;
import org.instancio.test.support.pojo.generics.basic.Pair;
import org.instancio.test.support.pojo.generics.basic.Triplet;
import org.instancio.test.support.pojo.generics.foobarbaz.Baz;
import org.instancio.test.support.pojo.generics.foobarbaz.Foo;
import org.instancio.test.support.tags.GenericsTag;
import org.instancio.test.support.tags.NodeTag;
import org.instancio.testsupport.fixtures.Types;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
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

    private final Map<TypeVariable<?>, Class<?>> rootTypeMap = new HashMap<>();

    @Nested
    class EqualsTests {

        @Test
        void equalsHashCode() {
            TypeToken<?> typeBazInteger = new TypeToken<Baz<Integer>>() {
            };
            TypeToken<?> typeBazString = new TypeToken<Baz<String>>() {
            };

            Node bazInteger = createNode(List.class, rootTypeMap, typeBazInteger);
            Node bazString = createNode(List.class, rootTypeMap, typeBazString);
            NodeContext nodeContext = new NodeContext(rootTypeMap, Collections.emptyMap(), Collections.emptyMap());
            Node bazIntegerClassNode = new ClassNode(nodeContext, Baz.class, null,
                    getTypeOf(typeBazInteger), null);

            assertThat(bazString).isEqualTo(bazString).hasSameHashCodeAs(bazString);
            assertThat(bazString).isNotEqualTo(bazInteger).doesNotHaveSameHashCodeAs(bazInteger);
            assertThat(bazInteger).isNotEqualTo(bazIntegerClassNode).doesNotHaveSameHashCodeAs(bazIntegerClassNode);
        }
    }

    @Nested
    @GenericsTag
    class EffectiveTypeTests {

        @Test
        void listOfString() {
            assertNode(createNode(List.class, rootTypeMap, Types.LIST_STRING))
                    .hasTargetClass(List.class)
                    .hasGenericType(getTypeOf(Types.LIST_STRING));
        }

        @Test
        void mapOfIntegerString() {
            assertNode(createNode(Map.class, rootTypeMap, Types.MAP_INTEGER_STRING))
                    .hasTargetClass(Map.class)
                    .hasGenericType(getTypeOf(Types.MAP_INTEGER_STRING));
        }

        @Test
        void pairOfIntegerString() {
            assertNode(createNode(Pair.class, rootTypeMap, Types.PAIR_INTEGER_STRING))
                    .hasTargetClass(Pair.class)
                    .hasGenericType(getTypeOf(Types.PAIR_INTEGER_STRING));
        }

        @Test
        void tripletOfBooleanStringInteger() {
            assertNode(createNode(Triplet.class, rootTypeMap, Types.TRIPLET_BOOLEAN_INTEGER_STRING))
                    .hasTargetClass(Triplet.class)
                    .hasGenericType(getTypeOf(Types.TRIPLET_BOOLEAN_INTEGER_STRING));
        }

        @Test
        void pairOfGenericItemFooList() {
            final TypeToken<?> type = new TypeToken<Pair<Item<Foo<List<Integer>>>, Map<Integer, Foo<String>>>>() {
            };

            assertNode(createNode(Pair.class, rootTypeMap, type))
                    .hasTargetClass(Pair.class)
                    .hasGenericType(getTypeOf(type));
        }
    }

    @Nested
    @GenericsTag
    class TypeMapTests {

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

            assertNode(createNode(Pair.class, rootTypeMap, new TypeToken<Pair<Item<String>, Foo<List<Integer>>>>() {
            }))
                    .hasTypeMappedTo(Pair.class, "L", typeLeft)
                    .hasTypeMappedTo(Pair.class, "R", typeRight)
                    .hasTypeMapWithSize(2);
        }
    }

    private static Node createNode(Class<?> klass, Map<TypeVariable<?>, Class<?>> rootTypeMap, TypeToken<?> type) {
        final NodeContext nodeContext = new NodeContext(rootTypeMap, Collections.emptyMap(), Collections.emptyMap());
        return new NodeImpl(nodeContext, klass, null, getTypeOf(type), null);
    }

    private static Type getTypeOf(TypeToken<?> type) {
        return type.get();
    }


    private static class NodeImpl extends Node {
        NodeImpl(NodeContext nodeContext,
                 Class<?> klass,
                 @Nullable Field field,
                 @Nullable Type genericType,
                 @Nullable Node parent) {

            super(nodeContext, klass, field, genericType, parent, Collections.emptyMap());
        }

        @Override
        public void accept(final NodeVisitor visitor) {
            // no-op
        }

        @Override
        protected List<Node> collectChildren() {
            return Collections.emptyList();
        }
    }
}
