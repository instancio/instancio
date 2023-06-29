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

import org.instancio.internal.util.Format;
import org.instancio.internal.util.Verify;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class InternalNode {

    private final NodeContext nodeContext;
    private final Type type;
    private final Class<?> rawType;
    private final Class<?> targetClass;
    private final Field field;
    private final InternalNode parent;
    private final TypeMap typeMap;
    private final NodeKind nodeKind;
    private final int depth;
    private final boolean cyclic;
    private List<InternalNode> children;
    private int hash;

    private InternalNode(final Builder builder) {
        nodeContext = builder.nodeContext;
        type = builder.type;
        rawType = builder.rawType;
        targetClass = builder.targetClass;
        field = builder.field;
        parent = builder.parent;
        children = builder.children == null ? Collections.emptyList() : Collections.unmodifiableList(builder.children);
        nodeKind = builder.nodeKind;
        typeMap = new TypeMap(type, nodeContext.getRootTypeMap(), builder.additionalTypeMap);
        depth = parent == null ? 0 : parent.depth + 1;
        cyclic = builder.cyclic;
    }

    public NodeKind getNodeKind() {
        return nodeKind;
    }

    public boolean is(final NodeKind nodeKind) {
        return this.nodeKind == nodeKind;
    }

    public boolean isIgnored() {
        return nodeKind == NodeKind.IGNORED;
    }

    public boolean isCyclic() {
        return cyclic;
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
        builder.cyclic = cyclic;
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

    public InternalNode getParent() {
        return parent;
    }

    public TypeMap getTypeMap() {
        return typeMap;
    }

    public InternalNode getOnlyChild() {
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
    public List<InternalNode> getChildren() {
        return children;
    }

    void setChildren(final List<InternalNode> children) {
        this.children = children;
    }

    /**
     * This method is used to determine if this is a cyclic node.
     */
    boolean hasAncestorWithSameTargetType() {
        InternalNode ancestor = parent;

        while (ancestor != null) {
            if ((nodeKind == NodeKind.DEFAULT || nodeKind == NodeKind.RECORD)
                    && Objects.equals(targetClass, ancestor.targetClass)
                    && Objects.equals(type, ancestor.type)) {
                return true;
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
        final InternalNode other = (InternalNode) o;

        return this.targetClass.equals(other.targetClass)
                && this.type.equals(other.type)
                && Objects.equals(this.field, other.field);
    }

    @Override
    public int hashCode() {
        if (hash == 0) {
            hash = computeHashCode();
        }
        return hash;
    }

    private int computeHashCode() {
        int result = type.hashCode();
        result = 31 * result + targetClass.hashCode();
        result = 31 * result + (field != null ? field.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        if (nodeKind == NodeKind.IGNORED) {
            return "Node[IGNORED]";
        }
        final String nodeName = field == null
                ? Format.withoutPackage(targetClass)
                : Format.withoutPackage(parent.targetClass) + '.' + field.getName();

        //noinspection StringBufferReplaceableByString
        return new StringBuilder().append("Node[")
                .append(nodeName)
                .append(", depth=").append(depth)
                .append(", type=").append(Format.withoutPackage(type))
                .append(']')
                .toString();
    }

    public String toDisplayString() {
        if (nodeKind == NodeKind.IGNORED) {
            return "ignored";
        }

        final StringBuilder sb = new StringBuilder();

        final String nodeName = field == null
                ? Format.withoutPackage(type)
                : Format.withoutPackage(parent.targetClass) + '.' + field.getName();

        if (field == null) {
            sb.append("class ").append(nodeName);
        } else {
            sb.append("field ").append(nodeName);
        }

        return sb.toString();
    }

    public static final class Builder {
        private NodeContext nodeContext;
        private Type type;
        private Class<?> rawType;
        private Class<?> targetClass;
        private Field field;
        private InternalNode parent;
        private List<InternalNode> children;
        private NodeKind nodeKind;
        private boolean cyclic;
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

        public Builder parent(@Nullable final InternalNode parent) {
            this.parent = parent;
            return this;
        }

        public Builder children(final List<InternalNode> children) {
            this.children = children;
            return this;
        }

        public Builder nodeKind(final NodeKind nodeKind) {
            this.nodeKind = nodeKind;
            return this;
        }

        public Builder cyclic() {
            this.cyclic = true;
            return this;
        }

        public Builder additionalTypeMap(final Map<Type, Type> additionalTypeMap) {
            this.additionalTypeMap = additionalTypeMap;
            return this;
        }

        public InternalNode build() {
            return new InternalNode(this);
        }
    }
}
