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

import org.instancio.exception.InstancioException;
import org.instancio.internal.reflection.DeclaredAndInheritedFieldsCollector;
import org.instancio.internal.reflection.FieldCollector;
import org.instancio.internal.util.ObjectUtils;
import org.instancio.internal.util.TypeUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * Class for creating a node hierarchy for a given {@link Type}.
 */
public final class NodeFactory {
    private static final Logger LOG = LoggerFactory.getLogger(NodeFactory.class);

    private final FieldCollector fieldCollector = new DeclaredAndInheritedFieldsCollector();
    private final NodeContext nodeContext;
    private final NodeCreator nodeCreator;
    private final TypeHelper typeHelper;

    public NodeFactory(final NodeContext nodeContext) {
        this.nodeContext = nodeContext;
        this.nodeCreator = new NodeCreator(nodeContext);
        this.typeHelper = new TypeHelper(nodeContext);
    }

    public Node createRootNode(final Type type) {
        final Node root = nodeCreator.createRootNodeWithoutChildren(type);

        // The queue contains nodes without children.
        // Children are populated after taking a node off the queue.
        final Queue<Node> childlessNodeQueue = new LinkedList<>();
        childlessNodeQueue.offer(root);

        while (!childlessNodeQueue.isEmpty()) {
            final Node node = childlessNodeQueue.poll();
            final List<Node> children = createChildlessChildren(node);
            node.setChildren(children);
            childlessNodeQueue.addAll(children);
        }
        return root;
    }

    /**
     * Creates children for the given node.
     * Returned children will not have of their own.
     *
     * @param node to create children for
     * @return child nodes (without children), or an empty list if none.
     */
    @NotNull
    private List<Node> createChildlessChildren(@NotNull final Node node) {
        if (node.getDepth() >= nodeContext.getMaxDepth()) {
            LOG.trace("Maximum depth ({}) reached {}", nodeContext.getMaxDepth(), node);
            return Collections.emptyList();
        }

        final List<Node> children;

        final Type type = node.getType();
        if (type instanceof Class) {
            children = createChildrenOfClass(node);
        } else if (type instanceof ParameterizedType) {
            children = createChildrenOfParameterizedType(node);
        } else if (type instanceof GenericArrayType) {
            children = createChildrenOfGenericArrayNode(node);
        } else {
            // should not be reachable
            throw new InstancioException("Unexpected node type: " + type);
        }
        return children;
    }

    private List<Node> createChildrenOfClass(final Node node) {
        final Class<?> targetClass = node.getTargetClass();

        if (node.isContainer()) {
            Type[] types = targetClass.isArray()
                    ? new Type[]{targetClass.getComponentType()}
                    : targetClass.getTypeParameters();

            // e.g. CustomMap extends HashMap<String, Long> - type parameters are empty
            // and need to be resolved from superclass
            if (types.length == 0) {
                types = TypeUtils.getGenericSuperclassTypeArguments(targetClass);
            }

            return createContainerNodeChildren(node, types);
        }
        return createChildrenFromFields(targetClass, node);
    }

    private List<Node> createChildrenOfParameterizedType(final Node node) {
        final ParameterizedType type = (ParameterizedType) node.getType();

        return node.isContainer()
                ? createContainerNodeChildren(node, type.getActualTypeArguments())
                : createChildrenFromFields(node.getTargetClass(), node);
    }

    private List<Node> createChildrenOfGenericArrayNode(final Node node) {
        final GenericArrayType type = (GenericArrayType) node.getType();
        Type gcType = type.getGenericComponentType();
        if (gcType instanceof TypeVariable) {
            gcType = typeHelper.resolveTypeVariable((TypeVariable<?>) gcType, node);
        }

        return createContainerNodeChildren(node, gcType);
    }

    /**
     * Creates children for a "container" node (that is an array, collection, or map).
     * Children of a container node have no 'field' property since their values
     * are not assigned via fields, but added via {@link Collection#add(Object)},
     * {@link Map#put(Object, Object)}, etc.
     *
     * @param parent of the children
     * @param types  children's types
     * @return a list of children, or an empty list if no children were created (e.g. to avoid cycles)
     */
    private List<Node> createContainerNodeChildren(final Node parent, final Type... types) {
        final List<Node> results = new ArrayList<>(types.length);
        for (Type type : types) {
            final Node node = nodeCreator.createNodeWithoutChildren(type, null, parent);
            if (node != null) {
                results.add(node);
            }
        }
        return results;
    }

    private List<Node> createChildrenFromFields(final Class<?> targetClass, final Node parent) {
        final List<Field> fields = fieldCollector.getFields(targetClass);
        final List<Node> list = new ArrayList<>(fields.size());

        for (Field f : fields) {
            final Type type = ObjectUtils.defaultIfNull(f.getGenericType(), f.getType());
            final Node node = nodeCreator.createNodeWithoutChildren(type, f, parent);
            if (node != null) {
                list.add(node);
            }
        }
        return list;
    }
}
