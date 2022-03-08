package org.instancio.testsupport.asserts;

import org.assertj.core.api.AbstractAssert;
import org.instancio.model.FieldNode;
import org.instancio.model.Node;

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
        assertThat(actual.getTypeMap()).hasSize(expected);
        return this;
    }

//    public FieldNodeAssert hasTypeMappedTo(String expectedTypeParameter, Class<?> expectedClass) {
//        isNotNull();
//
//        // FIXME - do lookup by Type
//        final Set<Map.Entry<Type, Type>> entries = actual.getTypeMap().entrySet();
//        for (Map.Entry<Type, Type> entry : entries) {
//            if (entry.getValue().getTypeName().equals(expectedTypeParameter)) {
//                assertThat(entry.getValue()).isEqualTo(expectedClass);
//                return this;
//            }
//        }
//
//        fail("Did not find expected type parameter '%s' in the type map: %s",
//                expectedTypeParameter, actual.getTypeMap());
//        //assertThat(actual.getTypeMap()).containsEntry(expectedTypeParameter, expectedClass);
//        return this;
//    }

    public FieldNodeAssert hasTypeMappedTo(TypeVariable<?> typeVariable, Type typeMapping) {
        isNotNull();
        final Map<TypeVariable<?>, Type> typeMap = actual.getTypeMap();
        assertThat(typeMap.get(typeVariable)).isEqualTo(typeMapping);
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