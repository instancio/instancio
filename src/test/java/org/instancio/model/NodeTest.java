package org.instancio.model;

import org.instancio.pojo.generics.Item;
import org.instancio.pojo.generics.Pair;
import org.instancio.pojo.generics.Triplet;
import org.instancio.pojo.generics.foobarbaz.Baz;
import org.instancio.pojo.generics.foobarbaz.Foo;
import org.instancio.testsupport.TypeReference;
import org.instancio.testsupport.fixtures.Types;
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
import static org.instancio.testsupport.utils.TypeUtils.getTypeVar;

@ModelTag
class NodeTest {
    private static final NodeContext EMPTY_CONTEXT = new NodeContext(Collections.emptyMap());

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
            Node bazIntegerClassNode = new ClassNode(new NodeContext(rootTypeMap), null,
                    Baz.class, getTypeOf(typeRefBazInteger), null);

            assertThat(bazString).isEqualTo(bazString).hasSameHashCodeAs(bazString);
            assertThat(bazString).isNotEqualTo(bazInteger).doesNotHaveSameHashCodeAs(bazInteger);
            assertThat(bazInteger).isNotEqualTo(bazIntegerClassNode).doesNotHaveSameHashCodeAs(bazIntegerClassNode);
        }

        @Test
        void equalsHashCodeWithDifferentParents() {
            Node parent1 = createNode(List.class, rootTypeMap, Types.STRING);
            Node parent2 = createNode(List.class, rootTypeMap, Types.INTEGER);

            Node node1 = new NodeImpl(EMPTY_CONTEXT, null, String.class, null, parent1);
            Node node2 = new NodeImpl(EMPTY_CONTEXT, null, String.class, null, parent2);

            assertThat(parent1).isNotEqualTo(parent2).doesNotHaveSameHashCodeAs(parent2);
            assertThat(node1).isNotEqualTo(node2).doesNotHaveSameHashCodeAs(node2);
        }
    }

    @Nested
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
    class TypeMapTests {

        @Test
        void listOfStrings() {
            assertNode(createNode(List.class, rootTypeMap, Types.LIST_STRING))
                    .hasTypeMappedTo(getTypeVar(List.class, "E"), String.class)
                    .hasTypeMapWithSize(1);
        }

        @Test
        void mapOfIntegerString() {
            assertNode(createNode(Map.class, rootTypeMap, Types.MAP_INTEGER_STRING))
                    .hasTypeMappedTo(getTypeVar(Map.class, "K"), Integer.class)
                    .hasTypeMappedTo(getTypeVar(Map.class, "V"), String.class)
                    .hasTypeMapWithSize(2);
        }

        @Test
        void tripletOfBooleanStringInteger() {
            assertNode(createNode(Triplet.class, rootTypeMap, Types.TRIPLET_BOOLEAN_INTEGER_STRING))
                    .hasTypeMappedTo(getTypeVar(Triplet.class, "M"), Boolean.class)
                    .hasTypeMappedTo(getTypeVar(Triplet.class, "N"), Integer.class)
                    .hasTypeMappedTo(getTypeVar(Triplet.class, "O"), String.class)
                    .hasTypeMapWithSize(3);
        }

        @Test
        void pairOfGenericItemFooList() {
            final Type typeRefLeft = getTypeOf(Types.GENERIC_ITEM_STRING);
            final Type typeRefRight = getTypeOf(Types.FOO_LIST_INTEGER);

            assertNode(createNode(Pair.class, rootTypeMap, new TypeReference<Pair<Item<String>, Foo<List<Integer>>>>() {
            }))
                    .hasTypeMappedTo(getTypeVar(Pair.class, "L"), typeRefLeft)
                    .hasTypeMappedTo(getTypeVar(Pair.class, "R"), typeRefRight)
                    .hasTypeMapWithSize(2);
        }
    }

    private static Node createNode(Class<?> klass, Map<TypeVariable<?>, Class<?>> rootTypeMap, TypeReference<?> typeRef) {
        final NodeContext nodeContext = new NodeContext(rootTypeMap);
        return new NodeImpl(nodeContext, null, klass, getTypeOf(typeRef), null);
    }

    private static Type getTypeOf(TypeReference<?> typeRef) {
        return typeRef.getType();
    }


    private static class NodeImpl extends Node {
        NodeImpl(NodeContext nodeContext,
                 @Nullable Field field,
                 Class<?> klass,
                 @Nullable Type genericType,
                 @Nullable Node parent) {

            super(nodeContext, field, klass, genericType, parent);
        }

        // @formatter:off
        @Override List<Node> collectChildren() { return Collections.emptyList(); }
        @Override public String getNodeName() { return "Test-Node"; }
        // @formatter:on
    }
}
