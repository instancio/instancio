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
package org.instancio.internal.nodes;

import org.instancio.Node;
import org.instancio.internal.RootType;
import org.instancio.internal.util.CollectionUtils;
import org.instancio.internal.util.Format;
import org.instancio.internal.util.ObjectUtils;
import org.instancio.internal.util.Verify;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("PMD.GodClass")
public final class InternalNode implements Node {

    private final Type type;
    private final Class<?> rawType;
    private final Class<?> targetClass;
    private final Field field;
    private final Method setter;
    private final InternalNode parent;
    private final NodeKind nodeKind;
    private final boolean cyclic;
    private final NodeTypeMap nodeTypeMap;
    private List<InternalNode> children;
    private final int depth;
    private int hash;

    private InternalNode(final Builder builder) {
        type = builder.type;
        rawType = builder.rawType;
        targetClass = builder.targetClass;
        field = builder.field;
        setter = builder.setter;
        parent = builder.parent;
        nodeKind = builder.nodeKind;
        cyclic = builder.cyclic;
        nodeTypeMap = builder.nodeTypeMap;
        children = CollectionUtils.asUnmodifiableList(builder.children);
        depth = parent == null ? 0 : parent.depth + 1;
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
    @Override
    public Class<?> getTargetClass() {
        return targetClass;
    }

    /**
     * Returns a field associated with this node, or {@code null} if none.
     *
     * @return field, if present, or {@code null}
     */
    @Override
    public Field getField() {
        return field;
    }

    /**
     * Returns a setter associated with this node, or {@code null} if none.
     *
     * @return setter, if present, or {@code null}
     */
    @Override
    public Method getSetter() {
        return setter;
    }

    @Override
    public InternalNode getParent() {
        return parent;
    }

    public NodeTypeMap getTypeMap() {
        return nodeTypeMap;
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
            if ((nodeKind == NodeKind.POJO || nodeKind == NodeKind.RECORD)
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

        return this.depth == other.depth
                && this.targetClass.equals(other.targetClass)
                && this.type.equals(other.type)
                && Objects.equals(this.field, other.field)
                && Objects.equals(this.setter, other.setter);
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
        result = 31 * result + (setter != null ? setter.hashCode() : 0);
        result = 31 * result + depth;
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(50)
                .append("Node[");

        if (field == null && setter == null) {
            sb.append(Format.withoutPackage(targetClass));
        } else {
            if (field != null) {
                sb.append(Format.withoutPackage(parent.targetClass)).append('.').append(field.getName());
                if (setter != null) {
                    sb.append(", ");
                }
            }
            if (setter != null) {
                sb.append(Format.formatSetterMethod(setter));
            }
        }

        sb.append(", depth=").append(depth)
                .append(", type=").append(Format.withoutPackage(type));

        if (nodeKind == NodeKind.IGNORED) {
            sb.append(", IGNORED");
        }

        return sb.append(']').toString();
    }

    public String toDisplayString() {
        if (nodeKind == NodeKind.IGNORED) {
            return "ignored";
        }

        final StringBuilder sb = new StringBuilder(32);

        if (field == null && setter == null) {
            return sb.append("class ").append(Format.withoutPackage(type)).toString();
        }

        if (field != null) {
            sb.append("field ")
                    .append(Format.withoutPackage(field.getDeclaringClass()))
                    .append('.')
                    .append(field.getName());
        }
        if (setter != null) {
            if (field != null) {
                sb.append(", ");
            }
            sb.append("setter ")
                    .append(Format.withoutPackage(setter.getDeclaringClass()))
                    .append('.')
                    .append(setter.getName())
                    .append('(')
                    .append(setter.getParameterTypes()[0].getSimpleName())
                    .append(')');
        }

        return sb.toString();
    }

    public Builder toBuilder() {
        final Builder builder = new Builder(type, rawType, nodeTypeMap.getRootType());
        builder.targetClass = targetClass;
        builder.field = field;
        builder.setter = setter;
        builder.parent = parent;
        builder.children = children;
        builder.nodeKind = nodeKind;
        builder.cyclic = cyclic;
        builder.nodeTypeMap = nodeTypeMap;
        return builder;
    }

    public static Builder builder(
            final Type type,
            final Class<?> rawType,
            final RootType rootType) {

        return new Builder(type, rawType, rootType);
    }

    public static final class Builder {
        private final Type type;
        private final Class<?> rawType;
        private final RootType rootType;
        private Class<?> targetClass;
        private Field field;
        private Method setter;
        private InternalNode parent;
        private List<InternalNode> children;
        private NodeKind nodeKind;
        private boolean cyclic;
        private NodeTypeMap nodeTypeMap;
        private Map<Type, Type> additionalTypeMap = Collections.emptyMap();

        private Builder(
                final Type type,
                final Class<?> rawType,
                final RootType rootType) {

            this.type = type;
            this.rawType = rawType;
            this.rootType = rootType;
        }

        public Builder targetClass(final Class<?> targetClass) {
            this.targetClass = targetClass;
            return this;
        }

        public Builder member(@Nullable final Member member) {
            if (member instanceof Field) {
                return member((Field) member);
            }
            if (member instanceof Method) {
                return member((Method) member);
            }
            return this;
        }

        public Builder member(@Nullable final Field field) {
            this.field = field;
            return this;
        }

        public Builder member(@Nullable final Method setter) {
            this.setter = setter;
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
            targetClass = ObjectUtils.defaultIfNull(targetClass, rawType);
            nodeTypeMap = new NodeTypeMap(type, rootType, additionalTypeMap);
            return new InternalNode(this);
        }
    }
}
