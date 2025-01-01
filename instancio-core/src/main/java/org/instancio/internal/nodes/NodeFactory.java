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

import org.instancio.exception.InstancioException;
import org.instancio.internal.context.ModelContext;
import org.instancio.internal.util.ObjectUtils;
import org.instancio.internal.util.ReflectionUtils;
import org.instancio.internal.util.TypeUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * Class for creating a node hierarchy for a given {@link Type}.
 */
public final class NodeFactory {
    private static final Logger LOG = LoggerFactory.getLogger(NodeFactory.class);

    private final DeclaredAndInheritedMemberCollector memberCollector;
    private final ModelContext modelContext;
    private final NodeCreator nodeCreator;
    private final TypeHelper typeHelper;
    private final OriginSelectorValidator originSelectorValidator;
    private final InternalFeedSpecHandler feedSpecHandler;

    public NodeFactory(final ModelContext modelContext) {
        this.modelContext = modelContext;
        this.nodeCreator = new NodeCreator(modelContext);
        this.typeHelper = new TypeHelper(modelContext.getRootType());
        this.originSelectorValidator = new OriginSelectorValidator(modelContext);
        this.memberCollector = new DeclaredAndInheritedMemberCollector(modelContext.getSettings());
        this.feedSpecHandler = DefaultFeedSpecHandler.create(modelContext);
    }

    public InternalNode createRootNode(final Type type) {
        final InternalNode root = nodeCreator.createNode(type, /* parent = */ null);

        final Queue<InternalNode> nodeQueue = new ArrayDeque<>();
        nodeQueue.offer(root);

        while (!nodeQueue.isEmpty()) {
            final InternalNode node = nodeQueue.poll();

            if (node.isCyclic() || !node.getChildren().isEmpty()) {
                continue;
            }

            originSelectorValidator.checkNode(node);

            final List<InternalNode> children = createChildren(node);
            node.setChildren(children);
            nodeQueue.addAll(children);
            // must be done after children have been set since
            // these are applied to child nodes of POJOs or records
            feedSpecHandler.applyFeedSpecs(node);
        }
        return root;
    }

    /**
     * Creates children for the given node.
     * Returned children will not have children of their own
     * unless the node was created by the {@link PredefinedNodeCreator}.
     *
     * @param node to create children for
     * @return child nodes (without children), or an empty list if none.
     */
    @NotNull
    private List<InternalNode> createChildren(@NotNull final InternalNode node) {
        if (node.isIgnored()) {
            return Collections.emptyList();
        }

        if (node.getDepth() >= modelContext.getMaxDepth()) {
            LOG.trace("Maximum depth ({}) reached {}", modelContext.getMaxDepth(), node);
            return Collections.emptyList();
        }

        final List<InternalNode> children;

        final Type type = node.getType();
        if (type instanceof Class) {
            // do not reflect on JDK classes
            children = node.is(NodeKind.JDK)
                    ? Collections.emptyList()
                    : createChildrenOfClass(node);
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

    private List<InternalNode> createChildrenOfClass(final InternalNode node) {
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
        return createChildrenFromMembers(node);
    }

    private List<InternalNode> createChildrenOfParameterizedType(final InternalNode node) {
        final ParameterizedType type = (ParameterizedType) node.getType();

        return node.isContainer()
                ? createContainerNodeChildren(node, type.getActualTypeArguments())
                : createChildrenFromMembers(node);
    }

    private List<InternalNode> createChildrenOfGenericArrayNode(final InternalNode node) {
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
    private List<InternalNode> createContainerNodeChildren(final InternalNode parent, final Type... types) {
        final List<InternalNode> results = new ArrayList<>(types.length);
        for (Type type : types) {
            final InternalNode node = nodeCreator.createNode(type, parent);
            if (node != null) {
                results.add(node);
            }
        }
        return results;
    }

    /**
     * Creates children nodes in the following order:
     *
     * <ol>
     *   <li>Fields (with matching setters, if any)</li>
     *   <li>Unmatched setters (if any)</li>
     * </ol>
     *
     * <p>The order ensures that fields will be populated before unmatched setters are invoked.
     *
     * @param node for which to create children
     * @return a list of children
     */
    private List<InternalNode> createChildrenFromMembers(final InternalNode node) {
        final ClassData classData = memberCollector.getClassData(node);
        final List<InternalNode> children = new ArrayList<>();

        // Add fields
        for (MemberPair memberPair : classData.getMemberPairs()) {
            final Field field = memberPair.getField();
            final Method setter = memberPair.getSetter();

            // Since field is available, prefer field type over method
            // parameter type to create the child node.
            final Type type = ObjectUtils.defaultIfNull(field.getGenericType(), field.getType());
            final InternalNode child = nodeCreator.createNode(type, field, setter, node);

            if (child != null) {
                children.add(child);
            }
        }

        // Add setter methods without a corresponding field
        for (Method method : classData.getUnmatchedSetters()) {
            final Type type = ReflectionUtils.getSetMethodParameterType(method);
            final InternalNode child = nodeCreator.createNode(type, /* field = */ null, method, node);

            if (child != null) {
                children.add(child);
            }
        }

        return children;
    }
}
