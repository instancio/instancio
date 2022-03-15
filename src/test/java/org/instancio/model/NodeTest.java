package org.instancio.model;

import org.instancio.pojo.generics.basic.Item;
import org.instancio.pojo.generics.basic.Pair;
import org.instancio.pojo.generics.basic.Triplet;
import org.instancio.pojo.generics.foobarbaz.Baz;
import org.instancio.pojo.generics.foobarbaz.Foo;
import org.instancio.testsupport.TypeReference;
import org.instancio.testsupport.fixtures.Types;
import org.instancio.testsupport.tags.GenericsTag;
import org.instancio.testsupport.tags.ModelTag;
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

@ModelTag
class NodeTest {

    private final Map<TypeVariable<?>, Class<?>> rootTypeMap = new HashMap<>();

    @Nested
    class EqualsTests {

        @Test
        void equalsHashCode() {
            TypeReference<?> typeRefBazInteger = new TypeReference<Baz<Integer>>() {
            };
            TypeReference<?> typeRefBazString = new TypeReference<Baz<String>>() {
            };

            Node bazInteger = createNode(List.class, rootTypeMap, typeRefBazInteger);
            Node bazString = createNode(List.class, rootTypeMap, typeRefBazString);
            Node bazIntegerClassNode = new ClassNode(new NodeContext(rootTypeMap), Baz.class, null,
                    getTypeOf(typeRefBazInteger), null);

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
                    .hasEffectiveClass(List.class)
                    .hasEffectiveType(getTypeOf(Types.LIST_STRING));
        }

        @Test
        void mapOfIntegerString() {
            assertNode(createNode(Map.class, rootTypeMap, Types.MAP_INTEGER_STRING))
                    .hasEffectiveClass(Map.class)
                    .hasEffectiveType(getTypeOf(Types.MAP_INTEGER_STRING));
        }

        @Test
        void pairOfIntegerString() {
            assertNode(createNode(Pair.class, rootTypeMap, Types.PAIR_INTEGER_STRING))
                    .hasEffectiveClass(Pair.class)
                    .hasEffectiveType(getTypeOf(Types.PAIR_INTEGER_STRING));
        }

        @Test
        void tripletOfBooleanStringInteger() {
            assertNode(createNode(Triplet.class, rootTypeMap, Types.TRIPLET_BOOLEAN_INTEGER_STRING))
                    .hasEffectiveClass(Triplet.class)
                    .hasEffectiveType(getTypeOf(Types.TRIPLET_BOOLEAN_INTEGER_STRING));
        }

        @Test
        void pairOfGenericItemFooList() {
            final TypeReference<?> typeRef = new TypeReference<Pair<Item<Foo<List<Integer>>>, Map<Integer, Foo<String>>>>() {
            };

            assertNode(createNode(Pair.class, rootTypeMap, typeRef))
                    .hasEffectiveClass(Pair.class)
                    .hasEffectiveType(getTypeOf(typeRef));
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
            final Type typeRefLeft = getTypeOf(Types.ITEM_STRING);
            final Type typeRefRight = getTypeOf(Types.FOO_LIST_INTEGER);

            assertNode(createNode(Pair.class, rootTypeMap, new TypeReference<Pair<Item<String>, Foo<List<Integer>>>>() {
            }))
                    .hasTypeMappedTo(Pair.class, "L", typeRefLeft)
                    .hasTypeMappedTo(Pair.class, "R", typeRefRight)
                    .hasTypeMapWithSize(2);
        }
    }

    private static Node createNode(Class<?> klass, Map<TypeVariable<?>, Class<?>> rootTypeMap, TypeReference<?> typeRef) {
        final NodeContext nodeContext = new NodeContext(rootTypeMap);
        return new NodeImpl(nodeContext, klass, null, getTypeOf(typeRef), null);
    }

    private static Type getTypeOf(TypeReference<?> typeRef) {
        return typeRef.getType();
    }


    private static class NodeImpl extends Node {
        NodeImpl(NodeContext nodeContext,
                 Class<?> klass,
                 @Nullable Field field,
                 @Nullable Type genericType,
                 @Nullable Node parent) {

            super(nodeContext, klass, field, genericType, parent);
        }

        @Override
        protected List<Node> collectChildren() {
            return Collections.emptyList();
        }
    }
}
