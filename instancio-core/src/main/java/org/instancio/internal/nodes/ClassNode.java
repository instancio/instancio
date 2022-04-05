/*
 *  Copyright 2022 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.instancio.internal.nodes;

import org.instancio.util.TypeUtils;
import org.instancio.util.Verify;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

public class ClassNode extends Node {

    public ClassNode(final NodeContext nodeContext,
                     final Class<?> klass,
                     @Nullable final Field field,
                     @Nullable final Type genericType,
                     @Nullable final Node parent) {

        super(nodeContext, klass, field, genericType, parent, Collections.emptyMap());

        Verify.isNotArrayCollectionOrMap(klass);
    }

    public ClassNode(final NodeContext nodeContext,
                     final Class<?> klass,
                     @Nullable final Field field,
                     @Nullable final Type genericType,
                     @Nullable final Node parent,
                     final Map<Type, Type> additionalTypeMap) {

        super(nodeContext, klass, field, genericType, parent, additionalTypeMap);

        Verify.isNotArrayCollectionOrMap(klass);
    }

    @Override
    public void accept(final NodeVisitor visitor) {
        visitor.visitClassNode(this);
    }

    @Override
    protected List<Node> collectChildren() {
        if (getTargetClass().getPackage() == null || getTargetClass().getPackage().getName().startsWith(JAVA_PKG_PREFIX)) {
            return Collections.emptyList();
        }
        return makeChildren(getNodeContext(), getTargetClass());
    }

    private List<Node> makeChildren(final NodeContext nodeContext, final Class<?> klass) {
        final NodeFactory nodeFactory = new NodeFactory(nodeContext);
        final List<Field> fields = nodeContext.getFieldCollector().getFields(klass);

        return fields.stream()
                .map(field -> {
                    Type genericType = field.getGenericType();
                    Class<?> type = field.getType();

                    if (genericType instanceof Class) {
                        type = (Class<?>) field.getGenericType();
                    } else if (genericType instanceof TypeVariable) {
                        final Type mappedType = getTypeMap().get(genericType);
                        if (mappedType instanceof Class) {
                            type = (Class<?>) mappedType;
                        } else if (mappedType instanceof ParameterizedType) {
                            genericType = mappedType;
                            type = TypeUtils.getRawType(mappedType);
                        }
                    }

                    return nodeFactory.createNode(type, genericType, field, this);
                })
                .collect(toList());
    }

}
