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

import org.instancio.util.ObjectUtils;
import org.instancio.util.Verify;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public abstract class Node {
    static final String JAVA_PKG_PREFIX = "java";

    private final NodeContext nodeContext;
    private final Field field;
    private final Class<?> targetClass;
    private final Type genericType;
    private Node parent;
    private List<Node> children;
    private final TypeMap typeMap;

    Node(final NodeContext nodeContext,
         final Class<?> targetClass,
         @Nullable final Field field,
         @Nullable final Type genericType,
         @Nullable final Node parent) {

        this.nodeContext = Verify.notNull(nodeContext, "nodeContext is null");
        this.targetClass = Verify.notNull(targetClass, "targetClass is null");
        this.field = field;
        this.genericType = genericType;
        this.parent = parent;
        this.typeMap = new TypeMap(ObjectUtils.defaultIfNull(genericType, targetClass), nodeContext.getRootTypeMap());
    }

    protected abstract List<Node> collectChildren();

    public abstract void accept(NodeVisitor visitor);

    void setParent(final Node parent) {
        this.parent = parent;
    }

    public NodeContext getNodeContext() {
        return nodeContext;
    }

    public Field getField() {
        return field;
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    public Type getGenericType() {
        return genericType;
    }

    public Node getParent() {
        return parent;
    }

    public TypeMap getTypeMap() {
        return typeMap;
    }

    public List<Node> getChildren() {
        if (children == null) {
            children = Collections.unmodifiableList(collectChildren());
        }
        return children;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node other = (Node) o;

        return this.getTargetClass().equals(other.getTargetClass())
                && Objects.equals(this.getGenericType(), other.getGenericType())
                && Objects.equals(this.getField(), other.getField());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTargetClass(), getGenericType(), getField());
    }

    @Override
    public final String toString() {
        String fieldName = field == null ? "null" : field.getName();
        String numChildren = String.format("[%s]", (children == null ? 0 : children.size()));
        return this.getClass().getSimpleName() + numChildren + "["
                + targetClass.getSimpleName() + ", " + genericType + ", field: " + fieldName + "]";
    }
}
