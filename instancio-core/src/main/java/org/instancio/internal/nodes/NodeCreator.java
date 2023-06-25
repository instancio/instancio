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

import org.instancio.exception.InstancioException;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.nodes.resolvers.NodeKindResolverFacade;
import org.instancio.internal.util.Format;
import org.instancio.internal.util.TypeUtils;
import org.instancio.internal.util.Verify;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Map;
import java.util.Optional;

/**
 * Helper class for creating a {@link InternalNode} without its children.
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
class NodeCreator {

    private static final Logger LOG = LoggerFactory.getLogger(NodeCreator.class);

    private final NodeContext nodeContext;
    private final TypeHelper typeHelper;
    private final NodeKindResolverFacade nodeKindResolverFacade;

    NodeCreator(final NodeContext nodeContext) {
        this.nodeContext = nodeContext;
        this.typeHelper = new TypeHelper(nodeContext);
        this.nodeKindResolverFacade = new NodeKindResolverFacade(nodeContext.getContainerFactories());
    }

    InternalNode createRootNodeWithoutChildren(final Type type) {
        return createNodeWithoutChildren(type, null, null);
    }

    @Nullable
    InternalNode createNodeWithoutChildren(final Type type, @Nullable final Field field, @Nullable final InternalNode parent) {
        Verify.notNull(type, "'type' is null");

        if (parent != null && parent.getDepth() >= nodeContext.getMaxDepth()) {
            LOG.trace("Maximum depth ({}) reached {}", nodeContext.getMaxDepth(), parent);
            return null;
        }

        if (LOG.isTraceEnabled()) {
            LOG.trace("Creating node for: {}", Format.withoutPackage(type));
        }

        final InternalNode node;

        if (type instanceof Class) {
            node = fromClass((Class<?>) type, field, parent);
        } else if (type instanceof ParameterizedType) {
            node = fromParameterizedType((ParameterizedType) type, field, parent);
        } else if (type instanceof TypeVariable) {
            node = fromTypeVariable((TypeVariable<?>) type, field, parent);
        } else if (type instanceof WildcardType) {
            node = fromWildcardType((WildcardType) type, field, parent);
        } else if (type instanceof GenericArrayType) {
            node = fromGenericArrayNode((GenericArrayType) type, field, parent);
        } else {
            throw new InstancioException("Unsupported type: " + type.getClass());
        }

        if (node != null && nodeContext.isIgnored(node)) {
            return node.toBuilder().nodeKind(NodeKind.IGNORED).build();
        }

        LOG.trace("Created node {} for type {}", node, type);
        return node;
    }

    private InternalNode fromWildcardType(final WildcardType type, @Nullable final Field field, @Nullable final InternalNode parent) {
        return createNodeWithoutChildren(type.getUpperBounds()[0], field, parent);
    }

    private InternalNode fromTypeVariable(final TypeVariable<?> type, @Nullable final Field field, @Nullable final InternalNode parent) {
        final Type resolvedType = typeHelper.resolveTypeVariable(type, parent);

        if (resolvedType == null) {
            LOG.warn("Unable to resolve type variable '{}'. Parent: {}", type, parent);
            return null;
        }
        return createNodeWithoutChildren(resolvedType, field, parent);
    }

    private Optional<Class<?>> resolveSubtype(final InternalNode node) {
        final Optional<Class<?>> subtype = nodeContext.getSubtype(node);

        if (subtype.isPresent()) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Resolved subtype: {} -> {}", node.getRawType().getName(), subtype.get().getName());
            }
            return subtype;
        }
        return Optional.ofNullable(resolveSubtypeFromAncestors(node));
    }

    private static Class<?> resolveSubtypeFromAncestors(final InternalNode node) {
        InternalNode next = node;
        while (next != null) {
            final Type actualType = next.getTypeMap().getActualType(node.getRawType());
            if (actualType != null) {
                return TypeUtils.getRawType(actualType);
            }
            next = next.getParent();
        }
        return null;
    }

    private InternalNode createNodeWithSubtypeMapping(
            final Type type,
            @Nullable final Field field,
            @Nullable final InternalNode parent) {

        final Class<?> rawType = TypeUtils.getRawType(type);

        InternalNode node = InternalNode.builder()
                .nodeContext(nodeContext)
                .type(type)
                .rawType(rawType)
                .targetClass(rawType)
                .field(field)
                .parent(parent)
                .nodeKind(getNodeKind(rawType))
                .build();

        final Class<?> targetClass = resolveSubtype(node).orElse(rawType);

        // Handle the case where: Child<T> extends Parent<T>
        // If the child node inherits a TypeVariable field declaration from
        // the parent, we need to map Parent.T -> Child.T to resolve the type variable
        final Map<Type, Type> genericSuperclassTypeMap = typeHelper.createSuperclassTypeMap(targetClass);

        if (!genericSuperclassTypeMap.isEmpty()) {
            node = node.toBuilder()
                    .additionalTypeMap(genericSuperclassTypeMap)
                    .build();
        }

        if (rawType != targetClass && !targetClass.isEnum() && !rawType.isPrimitive()) {
            ApiValidator.validateSubtype(rawType, targetClass);

            if (LOG.isDebugEnabled()) {
                LOG.debug("Subtype mapping '{}' to '{}'",
                        Format.withoutPackage(rawType),
                        Format.withoutPackage(targetClass));
            }

            // Re-evaluate node kind and type map
            return node.toBuilder()
                    .targetClass(targetClass)
                    .nodeKind(getNodeKind(targetClass))
                    .additionalTypeMap(typeHelper.createBridgeTypeMap(rawType, targetClass))
                    .build();
        }
        return node;
    }

    private InternalNode fromClass(final Class<?> type, @Nullable final Field field, @Nullable final InternalNode parent) {
        final InternalNode node = createNodeWithSubtypeMapping(type, field, parent);
        return node.hasAncestorEqualToSelf() ? null : node;
    }

    private NodeKind getNodeKind(final Class<?> rawType) {
        return nodeKindResolverFacade.getNodeKind(rawType);
    }

    private InternalNode fromParameterizedType(
            final ParameterizedType type,
            @Nullable final Field field,
            @Nullable final InternalNode parent) {

        final InternalNode node = createNodeWithSubtypeMapping(type, field, parent);
        return node.hasAncestorEqualToSelf() ? null : node;
    }

    private InternalNode fromGenericArrayNode(
            final GenericArrayType type,
            @Nullable final Field field,
            @Nullable final InternalNode parent) {

        Type gcType = type.getGenericComponentType();
        if (gcType instanceof TypeVariable) {
            gcType = typeHelper.resolveTypeVariable((TypeVariable<?>) gcType, parent);
        }
        return createArrayNodeWithSubtypeMapping(type, gcType, field, parent);
    }

    private InternalNode createArrayNodeWithSubtypeMapping(
            final Type arrayType,
            final Type genericComponentType,
            @Nullable final Field field,
            @Nullable final InternalNode parent) {

        final Class<?> rawComponentType = TypeUtils.getRawType(genericComponentType);
        final InternalNode node = InternalNode.builder()
                .nodeContext(nodeContext)
                .type(arrayType)
                .rawType(TypeUtils.getArrayClass(rawComponentType))
                .targetClass(TypeUtils.getArrayClass(rawComponentType))
                .field(field)
                .parent(parent)
                .nodeKind(NodeKind.ARRAY)
                .build();

        final Class<?> targetClass = resolveSubtype(node).orElse(rawComponentType);
        final Class<?> targetClassComponentType = targetClass.getComponentType();

        if (!rawComponentType.isPrimitive()
                && targetClassComponentType != null
                && rawComponentType != targetClassComponentType) {

            ApiValidator.validateSubtype(rawComponentType, targetClassComponentType);

            if (LOG.isDebugEnabled()) {
                LOG.debug("Subtype mapping '{}' to '{}'",
                        Format.withoutPackage(rawComponentType),
                        Format.withoutPackage(targetClass));
            }

            final Map<Type, Type> typeMapForSubtype = typeHelper.createBridgeTypeMap(rawComponentType, targetClassComponentType);

            // Map component type to match array subtype
            typeMapForSubtype.put(rawComponentType, targetClassComponentType);

            return node.toBuilder()
                    .targetClass(targetClass)
                    .additionalTypeMap(typeMapForSubtype)
                    .build();
        }
        return node;
    }

}
