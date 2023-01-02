/*
 * Copyright 2022-2023 the original author or authors.
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
package org.instancio.testsupport.asserts;

import org.assertj.core.api.AbstractAssert;
import org.instancio.internal.nodes.Node;
import org.instancio.internal.nodes.NodeKind;
import org.instancio.internal.nodes.TypeMap;

import java.lang.reflect.Type;

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

    public Node get() {
        return actual;
    }

    public NodeAssert hasType(Type expected) {
        isNotNull();
        assertThat(actual.getType()).isEqualTo(expected);
        return this;
    }

    public NodeAssert hasRawType(Class<?> expected) {
        isNotNull();
        assertThat(actual.getRawType()).isEqualTo(expected);
        return this;
    }

    public NodeAssert hasTargetClass(Class<?> expected) {
        isNotNull();
        assertThat(actual.getTargetClass()).isEqualTo(expected);
        return this;
    }

    public NodeAssert hasTypeName(String expected) {
        isNotNull();
        assertThat(actual.getType())
                .as("Generic type is null")
                .isNotNull();
        assertThat(actual.getType().getTypeName()).isEqualTo(expected);
        return this;
    }

    public NodeAssert hasNullField() {
        isNotNull();
        assertThat(actual.getField()).isNull();
        return this;
    }

    public NodeAssert hasFieldName(String expected) {
        isNotNull();
        assertThat(actual.getField()).as("Field is null").isNotNull();
        assertThat(actual.getField().getName()).isEqualTo(expected);
        return this;
    }

    public NodeAssert hasTypeMapWithSize(int expected) {
        isNotNull();
        assertThat(actual.getTypeMap().size())
                .as("Actual type map: %s", actual.getTypeMap())
                .isEqualTo(expected);
        return this;
    }

    public NodeAssert hasTypeMappedTo(Class<?> klass, String typeVariable, Type expectedMapping) {
        isNotNull();
        final Type typeVar = getTypeVar(klass, typeVariable);
        final TypeMap typeMap = actual.getTypeMap();
        final Type actualMapping = typeMap.get(typeVar);

        assertThat(actualMapping)
                .as("Actual type map: %s", typeMap)
                .isEqualTo(expectedMapping);
        return this;
    }

    public NodeAssert hasTypeMappedTo(Class<?> klass, String typeVariable, String expectedTypeName) {
        isNotNull();
        final Type typeVar = getTypeVar(klass, typeVariable);
        final TypeMap typeMap = actual.getTypeMap();
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
        assertThat(actual.getTypeMap().size())
                .as("Actual type map: %s", actual.getTypeMap())
                .isZero();
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

    public NodeAssert isOfKind(final NodeKind expected) {
        isNotNull();
        assertThat(actual.getNodeKind()).isEqualTo(expected);
        return this;
    }
}