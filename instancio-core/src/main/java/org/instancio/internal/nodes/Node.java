/*
 *  Copyright 2022-2023 the original author or authors.
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

import org.instancio.internal.util.Format;
import org.instancio.internal.util.Verify;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class Node {

    private final NodeContext nodeContext;
    private final Type type;
    private final Class<?> rawType;
    private final Class<?> targetClass;
    private final Field field;
    private final Node parent;
    private final TypeMap typeMap;
    private final NodeKind nodeKind;
    private final int depth;
    private List<Node> children;

    private Node(final Builder builder) {
        nodeContext = builder.nodeContext;
        type = Verify.notNull(builder.type, "null type");
        rawType = Verify.notNull(builder.rawType, "null rawType");
        targetClass = Verify.notNull(builder.targetClass, "null targetClass");
        field = builder.field;
        parent = builder.parent;
        children = builder.children == null ? Collections.emptyList() : Collections.unmodifiableList(builder.children);
        nodeKind = builder.nodeKind;
        typeMap = new TypeMap(type, nodeContext.getRootTypeMap(), builder.additionalTypeMap);
        depth = parent == null ? 0 : parent.depth + 1;
    }

    public NodeKind getNodeKind() {
        return nodeKind;
    }

    public boolean is(final NodeKind nodeKind) {
        return this.nodeKind == nodeKind;
    }

    boolean isContainer() {
        return is(NodeKind.COLLECTION)
                || is(NodeKind.MAP)
                || is(NodeKind.ARRAY)
                || is(NodeKind.CONTAINER);
    }

    public Builder toBuilder() {
        final Builder builder = new Builder();
        builder.nodeContext = nodeContext;
        builder.type = type;
        builder.rawType = rawType;
        builder.targetClass = targetClass;
        builder.field = field;
        builder.parent = parent;
        builder.children = children;
        builder.nodeKind = nodeKind;
        return builder;
    }

    public static Builder builder() {
        return new Builder();
    }

    public NodeContext getNodeContext() {
        return nodeContext;
    }

    /**
     * Returns the type represented by this node,
     * either a {@link Class} or {@link java.lang.reflect.ParameterizedType}.
     *
     * @return type represented by this node
     */
    public Type getType() {
        return type;
    }

    /**
     * Returns the raw type equivalent to this node's {@link #getType()}.
     * In the absence of subtype mapping, this method and {@link #getTargetClass()}
     * return the same value.
     *
     * @return raw type represented by this node.
     * @see #getTargetClass()
     */
    public Class<?> getRawType() {
        return rawType;
    }

    /**
     * Returns the target class represented by this node.
     *
     * @return target class represented by this node
     * @see #getRawType()
     */
    public Class<?> getTargetClass() {
        return targetClass;
    }

    /**
     * Returns a field associated with this node, or {@code null} if none.
     *
     * @return field, if present, or {@code null}
     */
    public Field getField() {
        return field;
    }

    public Node getParent() {
        return parent;
    }

    public TypeMap getTypeMap() {
        return typeMap;
    }

    public Node getOnlyChild() {
        Verify.state(getChildren().size() == 1, "Expected one child, but were %s", getChildren().size());
        return getChildren().get(0);
    }

    /**
     * Returns this node's children. For "container" nodes like arrays, collections, and maps,
     * the children will be nodes representing array/collection element or map key/value.
     * For other nodes, the children are based on the {@link #getTargetClass()} fields.
     * <p>
     * A node (including container nodes) may not have children in case of cyclic relationships.
     * An empty list would be returned to break the cycle.
     *
     * @return this node's children or an empty list if none
     */
    public List<Node> getChildren() {
        return children;
    }

    public void setChildren(final List<Node> children) {
        this.children = children;
    }

    /**
     * This method is used for detecting cycles. If this node
     * is equal to any of its ancestors, then there is a cycle.
     *
     * @return {@code true} if this node has an ancestor equal to it,
     * {@code false} otherwise.
     */
    public boolean hasAncestorEqualToSelf() {
        Node ancestor = parent;

        while (ancestor != null) {
            if (ancestor.equals(this)) {
                return true;
            }

            // Partial equals() check when dealing with the root:
            // ignore the field since the root doesn't have one
            if (ancestor.getParent() == null) {
                return Objects.equals(ancestor.getTargetClass(), targetClass)
                        && Objects.equals(ancestor.getType(), type);
            }

            ancestor = ancestor.getParent();
        }

        return false;
    }

    public int getDepth() {
        return depth;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Node other = (Node) o;

        return this.getTargetClass().equals(other.getTargetClass())
                && Objects.equals(this.getType(), other.getType())
                && Objects.equals(this.getField(), other.getField());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTargetClass(), getType(), getField());
    }

    @Override
    public String toString() {
        final String nodeName = field == null
                ? Format.withoutPackage(targetClass)
                : Format.withoutPackage(parent.targetClass) + '.' + field.getName();

        //noinspection StringBufferReplaceableByString
        return new StringBuilder().append("Node[")
                .append(nodeName)
                .append(", depth=").append(depth)
                .append(", #chn=").append(children.size())
                .append(", ").append(Format.withoutPackage(type))
                .append(']')
                .toString();
    }

    public static final class Builder {
        private NodeContext nodeContext;
        private Type type;
        private Class<?> rawType;
        private Class<?> targetClass;
        private Field field;
        private Node parent;
        private List<Node> children;
        private NodeKind nodeKind;
        private Map<Type, Type> additionalTypeMap = Collections.emptyMap();

        private Builder() {
        }

        public Builder nodeContext(final NodeContext nodeContext) {
            this.nodeContext = nodeContext;
            return this;
        }

        public Builder type(final Type type) {
            this.type = type;
            return this;
        }

        public Builder rawType(final Class<?> rawType) {
            this.rawType = rawType;
            return this;
        }

        public Builder targetClass(final Class<?> targetClass) {
            this.targetClass = targetClass;
            return this;
        }

        public Builder field(@Nullable final Field field) {
            this.field = field;
            return this;
        }

        public Builder parent(@Nullable final Node parent) {
            this.parent = parent;
            return this;
        }

        public Builder children(final List<Node> children) {
            this.children = children;
            return this;
        }

        public Builder nodeKind(final NodeKind nodeKind) {
            this.nodeKind = nodeKind;
            return this;
        }

        public Builder additionalTypeMap(final Map<Type, Type> additionalTypeMap) {
            this.additionalTypeMap = additionalTypeMap;
            return this;
        }

        public Node build() {
            return new Node(this);
        }
    }
}
