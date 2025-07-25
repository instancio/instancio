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
import org.instancio.internal.ApiValidator;
import org.instancio.internal.context.ModelContext;
import org.instancio.internal.nodes.resolvers.NodeKindResolverFacade;
import org.instancio.internal.util.Format;
import org.instancio.internal.util.StringUtils;
import org.instancio.internal.util.TypeUtils;
import org.instancio.internal.util.Verify;
import org.instancio.settings.AssignmentType;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Helper class for creating nodes.
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
class NodeCreator {

    private static final Logger LOG = LoggerFactory.getLogger(NodeCreator.class);

    private final PredefinedNodeCreator predefinedNodeCreator;
    private final ModelContext modelContext;
    private final TypeHelper typeHelper;
    private final NodeKindResolverFacade nodeKindResolverFacade;
    private final SubtypeResolver subtypeResolver;
    private final List<Pattern> ignorePatterns;

    NodeCreator(final ModelContext modelContext) {
        this.modelContext = modelContext;
        this.typeHelper = new TypeHelper(modelContext.getRootType());
        this.nodeKindResolverFacade = new NodeKindResolverFacade(modelContext.getInternalServiceProviders());
        this.predefinedNodeCreator = new PredefinedNodeCreator(modelContext.getRootType(), nodeKindResolverFacade);
        this.subtypeResolver = new SubtypeResolver(modelContext);
        this.ignorePatterns = getIgnorePatterns(modelContext.getSettings());
    }

    private List<Pattern> getIgnorePatterns(final Settings settings) {
        final List<String> regexes = StringUtils.split(settings.get(Keys.IGNORE_FIELD_NAME_REGEXES));
        if (regexes.isEmpty()) {
            return Collections.emptyList();
        }

        final List<Pattern> patterns = new ArrayList<>(regexes.size());
        for (String regex : regexes) {
            patterns.add(Pattern.compile(regex));
        }
        return Collections.unmodifiableList(patterns);
    }


    @Nullable
    InternalNode createNode(@NotNull final Type type,
                            @Nullable final InternalNode parent) {

        return createNode(type, /* member = */ null, /* setter = */ null, parent);
    }

    /**
     * This method creates nodes without children, except nodes that are
     * created using the {@link PredefinedNodeCreator}, in which case
     * the returned node may have children.
     *
     * <p>By default, {@link AssignmentType#FIELD} is used.
     * This means that the nodes are created from fields only.
     * In this case, the {@code member} argument is always a field
     * and the {@code setter} argument is always {@code null}.
     *
     * <p>When {@link AssignmentType#METHOD} is used, in addition to fields,
     * we also extract setter methods. An attempt is made to match
     * each field to its corresponding setter.
     *
     * <p>When a match is found, the {@code member} argument is a field,
     * and the {@code setter} is the corresponding set method.
     *
     * <p>However, it is possible to encounter setters that do not match
     * field names based on assumed naming conventions. We also want to
     * create nodes from these leftover unmatched setters.
     * In such cases, the {@code member} argument is the unmatched setter
     * <b>method</b>, and the {@code setter} argument is {@code null}.
     *
     * <p>The above results in a node hierarchy where each node
     * may have a null field, setter, or both; for example:
     *
     * <pre>
     * | node.field | node.setter |
     * |------------+-------------+-----------------------------------------
     * | foo        | setFoo()    | field/setter matched
     * | null       | setFoo()    | unmatched setter
     * | foo        | null        | field without a setter (or setter did not match)
     * | null       | null        | a node whose properties are not read (e.g. Collection)
     * </pre>
     *
     * @param type   the type of the node
     * @param member either {@link Field} or setter {@link Method}
     * @param setter optional set method when {@code member} is a field
     * @param parent the parent node
     * @return created node
     */
    @Nullable
    InternalNode createNode(@NotNull final Type type,
                            @Nullable final Member member,
                            @Nullable final Method setter,
                            @Nullable final InternalNode parent) {

        Verify.notNull(type, "'type' is null");

        if (parent != null && parent.getDepth() >= modelContext.getMaxDepth()) {
            LOG.trace("Maximum depth ({}) reached {}", modelContext.getMaxDepth(), parent);
            return null;
        }

        if (shouldIgnoreMember(member)) {
            return null;
        }

        if (LOG.isTraceEnabled()) {
            LOG.trace("Creating node for: {}", Format.withoutPackage(type));
        }

        InternalNode node;
        final InternalNode template = predefinedNodeCreator.createFromTemplate(type, member, parent);

        if (template != null) {
            node = template;
        } else if (type instanceof Class) {
            node = fromClass((Class<?>) type, member, setter, parent);
        } else if (type instanceof ParameterizedType) {
            node = fromParameterizedType((ParameterizedType) type, member, setter, parent);
        } else if (type instanceof TypeVariable) {
            node = fromTypeVariable((TypeVariable<?>) type, member, setter, parent);
        } else if (type instanceof WildcardType) {
            node = fromWildcardType((WildcardType) type, member, setter, parent);
        } else if (type instanceof GenericArrayType) {
            node = fromGenericArrayNode((GenericArrayType) type, member, setter, parent);
        } else {
            throw new InstancioException("Unsupported type: " + type.getClass());
        }

        if (node != null && modelContext.isIgnored(node)) {
            node = node.toBuilder().nodeKind(NodeKind.IGNORED).build();
        }

        LOG.trace("Created node {} for type {}", node, type);
        return node;
    }

    private InternalNode fromWildcardType(
            final WildcardType type,
            @Nullable final Member member,
            @Nullable final Method setter,
            @Nullable final InternalNode parent) {

        return createNode(type.getUpperBounds()[0], member, setter, parent);
    }

    private InternalNode fromTypeVariable(
            final TypeVariable<?> type,
            @Nullable final Member member,
            @Nullable final Method setter,
            @Nullable final InternalNode parent) {

        final Type resolvedType = typeHelper.resolveTypeVariable(type, parent);

        if (resolvedType == null) {
            LOG.warn("Unable to resolve type variable '{}'. Parent: {}", type, parent);
            return null;
        }
        return createNode(resolvedType, member, setter, parent);
    }

    private InternalNode.Builder builderTemplate(
            final Type type,
            final Class<?> rawType,
            final Member member) {

        return InternalNode.builder(type, rawType, modelContext.getRootType())
                .member(member);
    }

    private InternalNode createNodeWithSubtypeMapping(
            final Type type,
            @Nullable final Member member,
            @Nullable final Method setter,
            @Nullable final InternalNode parent) {

        final Class<?> rawType = TypeUtils.getRawType(type);

        InternalNode node = builderTemplate(type, rawType, member)
                .parent(parent)
                .nodeKind(getNodeKind(rawType))
                .member(setter)
                .build();

        final Class<?> targetClass = subtypeResolver.resolveSubtype(node).orElse(rawType);

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

    private InternalNode fromClass(
            final Class<?> type,
            @Nullable final Member member,
            @Nullable final Method setter,
            @Nullable final InternalNode parent) {

        final InternalNode node = createNodeWithSubtypeMapping(type, member, setter, parent);
        return node.hasAncestorWithSameTargetType() ? node.toBuilder().cyclic().build() : node;
    }

    private NodeKind getNodeKind(final Class<?> rawType) {
        return nodeKindResolverFacade.getNodeKind(rawType);
    }

    private InternalNode fromParameterizedType(
            final ParameterizedType type,
            @Nullable final Member member,
            @Nullable final Method setter,
            @Nullable final InternalNode parent) {

        final InternalNode node = createNodeWithSubtypeMapping(type, member, setter, parent);
        return node.hasAncestorWithSameTargetType() ? node.toBuilder().cyclic().build() : node;
    }

    private InternalNode fromGenericArrayNode(
            final GenericArrayType type,
            @Nullable final Member member,
            @Nullable final Method setter,
            @Nullable final InternalNode parent) {

        Type gcType = type.getGenericComponentType();
        if (gcType instanceof TypeVariable) {
            gcType = typeHelper.resolveTypeVariable((TypeVariable<?>) gcType, parent);
        }
        return createArrayNodeWithSubtypeMapping(type, gcType, member, setter, parent);
    }

    private InternalNode createArrayNodeWithSubtypeMapping(
            final Type arrayType,
            final Type genericComponentType,
            @Nullable final Member member,
            @Nullable final Method setter,
            @Nullable final InternalNode parent) {

        final Class<?> rawComponentType = TypeUtils.getRawType(genericComponentType);
        final Class<?> arrayClass = TypeUtils.getArrayClass(rawComponentType);
        final InternalNode node = builderTemplate(arrayType, arrayClass, member)
                .parent(parent)
                .nodeKind(NodeKind.ARRAY)
                .member(setter)
                .build();

        final Class<?> targetClass = subtypeResolver.resolveSubtype(node).orElse(rawComponentType);
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

    private boolean shouldIgnoreMember(final @Nullable Member member) {
        if (!ignorePatterns.isEmpty() && member instanceof Field) {
            final String fieldName = member.getName();
            for (Pattern p : ignorePatterns) {
                if (p.matcher(fieldName).matches()) {
                    return true;
                }
            }
        }

        return false;
    }
}
