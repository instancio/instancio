package org.instancio.testsupport.asserts;

import org.assertj.core.api.AbstractAssert;
import org.instancio.model.Node;

import java.lang.reflect.Type;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.testsupport.utils.TypeUtils.getTypeVar;

@SuppressWarnings("UnusedReturnValue")
public class NodeAssert extends AbstractAssert<NodeAssert, Node> {

    private NodeAssert(Node actual) {
        super(actual, NodeAssert.class);
    }

    public static NodeAssert assertNode(Node actual) {
        return new NodeAssert(actual);
    }

    public <T extends Node> T getAs(Class<T> nodeType) {
        isNotNull();
        assertThat(nodeType).isAssignableFrom(actual.getClass());
        return nodeType.cast(actual);
    }


    public NodeAssert hasKlass(Class<?> expected) {
        isNotNull();
        assertThat(actual.getKlass()).isEqualTo(expected);
        return this;
    }

    public NodeAssert hasGenericType(Type expected) {
        isNotNull();
        assertThat(actual.getGenericType()).isEqualTo(expected);
        return this;
    }

    public NodeAssert hasGenericTypeName(String expected) {
        isNotNull();
        assertThat(actual.getGenericType())
                .as("Generic type is null")
                .isNotNull();
        assertThat(actual.getGenericType().getTypeName()).isEqualTo(expected);
        return this;
    }

    public NodeAssert hasNullField() {
        isNotNull();
        assertThat(actual.getField()).isNull();
        return this;
    }

    public NodeAssert hasFieldName(String expected) {
        isNotNull();
        assertThat(actual.getField()).isNotNull();
        assertThat(actual.getField().getName()).isEqualTo(expected);
        return this;
    }

    public NodeAssert hasTypeMapWithSize(int expected) {
        isNotNull();
        assertThat(actual.getTypeMap())
                .as("Actual type map: %s", actual.getTypeMap())
                .hasSize(expected);
        return this;
    }

    public NodeAssert hasTypeMappedTo(Class<?> klass, String typeVariable, Type expectedMapping) {
        isNotNull();
        final Type typeVar = getTypeVar(klass, typeVariable);
        final Map<Type, Type> typeMap = actual.getTypeMap();
        final Type actualMapping = typeMap.get(typeVar);

        assertThat(actualMapping)
                .as("Actual type map: %s", typeMap)
                .isEqualTo(expectedMapping);
        return this;
    }

    public NodeAssert hasTypeMappedTo(Class<?> klass, String typeVariable, String expectedTypeName) {
        isNotNull();
        final Type typeVar = getTypeVar(klass, typeVariable);
        final Map<Type, Type> typeMap = actual.getTypeMap();
        final Type actualMapping = typeMap.get(typeVar);

        assertThat(actualMapping)
                .as("Actual type map: %s", typeMap)
                .isNotNull()
                .extracting(Type::getTypeName)
                .isEqualTo(expectedTypeName);
        return this;
    }

    public NodeAssert hasEmptyTypeMap() {
        isNotNull();
        assertThat(actual.getTypeMap())
                .as("Actual type map: %s", actual.getTypeMap())
                .isEmpty();
        return this;
    }

    public NodeAssert hasParent(Node expected) {
        isNotNull();
        assertThat(actual.getParent()).isSameAs(expected);
        return this;
    }

    public NodeAssert hasChildrenOfSize(int expected) {
        isNotNull();
        assertThat(actual.getChildren()).hasSize(expected);
        return this;
    }

    public NodeAssert hasNoChildren() {
        isNotNull();
        assertThat(actual.getChildren()).isEmpty();
        return this;
    }
}