package org.instancio.testsupport.asserts;

import org.assertj.core.api.AbstractAssert;
import org.instancio.model.ClassNode;
import org.instancio.model.Node;

import java.lang.reflect.Type;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("UnusedReturnValue")
public class ClassNodeAssert extends AbstractAssert<ClassNodeAssert, ClassNode> {

    private ClassNodeAssert(ClassNode actual) {
        super(actual, ClassNodeAssert.class);
    }

    public static ClassNodeAssert assertClassNode(ClassNode actual) {
        return new ClassNodeAssert(actual);
    }

    public ClassNodeAssert hasKlass(Class<?> expected) {
        isNotNull();
        assertThat(actual.getKlass()).isEqualTo(expected);
        return this;
    }

    public ClassNodeAssert hasEffectiveClass(Class<?> expected) {
        isNotNull();
        assertThat(actual.getEffectiveType().getRawType()).isEqualTo(expected);
        return this;
    }

    public ClassNodeAssert hasGenericType(Type expected) {
        isNotNull();
        assertThat(actual.getGenericType()).isEqualTo(expected);
        return this;
    }

    public ClassNodeAssert hasGenericTypeName(String expected) {
        isNotNull();
        assertThat(actual.getGenericType()).isNotNull();
        assertThat(actual.getGenericType().getTypeName()).isEqualTo(expected);
        return this;
    }

    public ClassNodeAssert hasFieldName(String expected) {
        isNotNull();
        assertThat(actual.getField()).isNotNull();
        assertThat(actual.getField().getName()).isEqualTo(expected);
        return this;

    }

    public ClassNodeAssert hasParent(Node expected) {
        isNotNull();
        assertThat(actual.getParent()).isSameAs(expected);
        return this;
    }

    public ClassNodeAssert hasChildrenOfSize(int expected) {
        isNotNull();
        assertThat(actual.getChildren()).hasSize(expected);
        return this;
    }

    public ClassNodeAssert hasNoChildren() {
        isNotNull();
        assertThat(actual.getChildren()).isEmpty();
        return this;
    }
}