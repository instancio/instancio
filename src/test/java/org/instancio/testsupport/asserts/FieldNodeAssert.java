package org.instancio.testsupport.asserts;

import experimental.reflection.nodes.FieldNode;
import org.assertj.core.api.AbstractAssert;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("UnusedReturnValue")
public class FieldNodeAssert extends AbstractAssert<FieldNodeAssert, FieldNode> {

    private FieldNodeAssert(FieldNode actual) {
        super(actual, FieldNodeAssert.class);
    }

    public static FieldNodeAssert assertFieldNode(FieldNode actual) {
        return new FieldNodeAssert(actual);
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
        assertThat(actual.getTypeMap()).hasSize(expected);
        return this;
    }

    public FieldNodeAssert hasTypeMappedTo(String expectedTypeParameter, Class<?> expectedClass) {
        isNotNull();
        assertThat(actual.getTypeMap()).containsEntry(expectedTypeParameter, expectedClass);
        return this;
    }
    public FieldNodeAssert hasTypeMappedTo(String expectedTypeParameter, String typeVariable) {
        isNotNull();
        final Map<String, Type> typeMap = actual.getTypeMap();
        final Type expectedType = typeMap.get(expectedTypeParameter);
        assertThat(expectedType).isNotNull()
                .isInstanceOf(TypeVariable.class)
                .extracting(Type::getTypeName)
                .isEqualTo(typeVariable);

        return this;
    }

    public FieldNodeAssert hasEmptyTypeMap() {
        isNotNull();
        assertThat(actual.getTypeMap()).isEmpty();
        return this;
    }

    public FieldNodeAssert hasChildrenOfSize(int expected) {
        isNotNull();
        assertThat(actual.getChildren()).hasSize(expected);
        return this;
    }

    public FieldNodeAssert hasNoChildren() {
        isNotNull();
        assertThat(actual.getChildren()).isEmpty();
        return this;
    }

}