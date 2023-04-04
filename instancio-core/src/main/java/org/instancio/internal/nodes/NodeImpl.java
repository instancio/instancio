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
package org.instancio.internal.nodes;

import org.instancio.Node;
import org.instancio.internal.util.Verify;

import java.lang.reflect.Field;
import java.util.Objects;

public final class NodeImpl implements Node {

    private final Class<?> targetClass;
    private final Field field;
    private final Class<?> parentTargetClass;

    public NodeImpl(final Class<?> targetClass, final Field field, final Class<?> parentTargetClass) {
        this.targetClass = Verify.notNull(targetClass, "targetClass is null");
        this.field = field;
        this.parentTargetClass = parentTargetClass;
    }

    @Override
    public Class<?> getTargetClass() {
        return targetClass;
    }

    @Override
    public Field getField() {
        return field;
    }

    @Override
    public Class<?> getParentTargetClass() {
        return field == null ? null : field.getDeclaringClass();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof NodeImpl)) return false;
        final NodeImpl node = (NodeImpl) o;
        return Objects.equals(targetClass, node.targetClass)
                && Objects.equals(field, node.field)
                && Objects.equals(parentTargetClass, node.parentTargetClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(targetClass, field, parentTargetClass);
    }

    @Override
    public String toString() {
        return String.format("Node[targetClass=%s, field=%s, parentTargetClass=%s]",
                targetClass.getName(),
                field == null ? null : field.getName(),
                parentTargetClass == null ? null : parentTargetClass.getName());
    }
}
