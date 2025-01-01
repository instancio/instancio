/*
 * Copyright 2022-2025 the original author or authors.
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
import org.instancio.internal.nodes.NodeTypeMap;

import java.lang.reflect.Type;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.testsupport.utils.TypeUtils.getTypeVar;

@SuppressWarnings("UnusedReturnValue")
public class NodeTypeMapAssert extends AbstractAssert<NodeTypeMapAssert, NodeTypeMap> {

    private NodeTypeMapAssert(NodeTypeMap actual) {
        super(actual, NodeTypeMapAssert.class);
    }

    public static NodeTypeMapAssert assertNodeTypeMap(NodeTypeMap actual) {
        return new NodeTypeMapAssert(actual);
    }

    public NodeTypeMapAssert hasTypeMapping(Class<?> klass, String typeVar, Type expectedMappedType) {
        Type mappedType = actual.get(getTypeVar(klass, typeVar));
        assertThat(mappedType)
                .as("Wrong mapping for klass: %s, type variable: %s", klass.getName(), typeVar)
                .isEqualTo(expectedMappedType);
        return this;
    }

    public NodeTypeMapAssert hasTypeMapping(Class<?> klass, String typeVar, String expectedTypeName) {
        Type mappedType = actual.get(getTypeVar(klass, typeVar));
        assertThat(mappedType)
                .as("Wrong mapping for klass: %s, type variable: %s", klass.getName(), typeVar)
                .isNotNull()
                .extracting(Type::getTypeName)
                .isEqualTo(expectedTypeName);
        return this;
    }

    public NodeTypeMapAssert hasTypeMapWithSize(int expected) {
        assertThat(actual.size()).isEqualTo(expected);
        return this;
    }

    public NodeTypeMapAssert hasEmptyTypeMap() {
        assertThat(actual.size()).isZero();
        return this;
    }

}