package org.instancio.testsupport.asserts;

import org.assertj.core.api.AbstractAssert;
import org.instancio.model.FieldNode;
import org.instancio.model.Node;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("UnusedReturnValue")
public class FieldNodeAssert extends AbstractAssert<FieldNodeAssert, FieldNode> {

    private FieldNodeAssert(FieldNode actual) {
        super(actual, FieldNodeAssert.class);
    }

    public static FieldNodeAssert assertFieldNode(FieldNode actual) {
        return new FieldNodeAssert(actual);
    }

    public FieldNodeAssert hasParent(Node expected) {
        isNotNull();
        assertThat(actual.getParent()).isSameAs(expected);
        return this;
    }

    public FieldNodeAssert hasFieldName(String expected) {
        isNotNull();
        assertThat(actual.getFieldName()).isEqualTo(expected);
        return this;
    }

    public FieldNodeAssert hasActualFieldType(Class<?> expected) {
        isNotNull();
        assertThat(actual.getActualFieldType()).isEqualTo(expected);
        return this;
    }

    public FieldNodeAssert hasTypeMapWithSize(int expected) {
        isNotNull();
        assertThat(actual.getTypeMap())
                .as("Actual type map: %s", actual.getTypeMap())
                .hasSize(expected);
        return this;
    }

    public FieldNodeAssert hasTypeMappedTo(TypeVariable<?> typeVariable, Type typeMapping) {
        isNotNull();
        final Map<TypeVariable<?>, Type> typeMap = actual.getTypeMap();
        assertThat(typeMap.get(typeVariable))
                .as("Actual type map: %s", typeMap)
                .isEqualTo(typeMapping);
        return this;
    }

    public FieldNodeAssert hasEmptyTypeMap() {
        isNotNull();
        assertThat(actual.getTypeMap())
                .as("Actual type map: %s", actual.getTypeMap())
                .isEmpty();
        return this;
    }

    public FieldNodeAssert hasChildrenOfSize(int expected) {
        isNotNull();
        assertThat(actual.getChildren())
                .as("Actual children:\n%s", actual.getChildren().stream()
                        .map(Node::getNodeName)
                        .collect(Collectors.joining("\n")))
                .hasSize(expected);
        return this;
    }

    public FieldNodeAssert hasNoChildren() {
        isNotNull();
        assertThat(actual.getChildren()).isEmpty();
        return this;
    }

}