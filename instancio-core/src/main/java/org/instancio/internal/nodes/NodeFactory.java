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

import org.instancio.TargetSelector;
import org.instancio.exception.InstancioException;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.reflection.DeclaredAndInheritedFieldsCollector;
import org.instancio.internal.reflection.FieldCollector;
import org.instancio.util.ServiceLoaders;
import org.instancio.spi.TypeResolver;
import org.instancio.util.Format;
import org.instancio.util.ObjectUtils;
import org.instancio.util.TypeUtils;
import org.instancio.util.Verify;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * Class for creating a node hierarchy for a given {@link Type}.
 */
@SuppressWarnings({"PMD.GodClass", "PMD.ExcessiveImports"})
public final class NodeFactory {
    private static final Logger LOG = LoggerFactory.getLogger(NodeFactory.class);

    private final FieldCollector fieldCollector = new DeclaredAndInheritedFieldsCollector();
    private final NodeContext nodeContext;
    private final List<TypeResolver> typeResolvers;

    public NodeFactory(final NodeContext nodeContext) {
        this.nodeContext = nodeContext;
        this.typeResolvers = ServiceLoaders.loadAll(TypeResolver.class);
    }

    public Node createRootNode(final Type type) {
        return createNode(type, null, null);
    }

    private Node createNode(final Type type, @Nullable final Field field, @Nullable final Node parent) {
        Verify.notNull(type, "'type' is null");

        if (LOG.isTraceEnabled()) {
            LOG.trace("Creating node for: {}", Format.withoutPackage(type));
        }

        final Node node;

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

        LOG.trace("Created node: {}", node);
        return node;
    }

    private Node fromWildcardType(final WildcardType type, @Nullable final Field field, @Nullable final Node parent) {
        return createNode(type.getUpperBounds()[0], field, parent);
    }

    private Node fromTypeVariable(final TypeVariable<?> type, @Nullable final Field field, @Nullable final Node parent) {
        final Type resolvedType = resolveTypeVariable(type, parent);

        if (resolvedType == null) {
            LOG.warn("Unable to resolve type variable '{}'. Parent: {}", type, parent);
            return null;
        }

        return createNode(resolvedType, field, parent);
    }

    /**
     * Checks if there is a user-supplied type provided via
     * {@link org.instancio.InstancioApi#subtype(TargetSelector, Class)}}
     * method or via generator {@code gen.collection().subtype(Class)}.
     * <p>
     * If above is not present, then checks if subtype is available using
     * {@link TypeResolver} SPI.
     *
     * @param node whose type to resolve
     * @return resolved class or an empty result if unresolved
     */
    private Optional<Class<?>> resolveSubtype(final Node node) {
        // User supplied subtype via API or generator
        final Optional<Class<?>> target = nodeContext.getUserSuppliedSubtype(node);
        if (target.isPresent()) {
            LOG.debug("Using subtype provided via API: {} -> {}", node.getRawType().getName(), target.get().getName());
            return target;
        }

        final Class<?> typeToResolve = node.getRawType();

        // Do not resolve subtypes via SPI for collection classes
        if (Collection.class.isAssignableFrom(typeToResolve) || Map.class.isAssignableFrom(typeToResolve)) {
            return Optional.empty();
        }

        LOG.debug("Resolving subtype via SPI for type '{}'", typeToResolve.getTypeName());

        for (TypeResolver resolver : typeResolvers) {
            final Optional<Class<?>> resolved = resolver.resolve(typeToResolve);
            if (resolved.isPresent()) {
                LOG.debug("Using subtype provided via '{}': {} -> {}",
                        resolver.getClass().getName(), node.getRawType().getName(), resolved.get().getName());
                return resolved;
            }
        }

        return Optional.empty();
    }

    private Node createNodeWithSubtypeMapping(final Type type, @Nullable final Field field, @Nullable final Node parent) {
        final Class<?> rawType = TypeUtils.getRawType(type);
        final Node node = Node.builder()
                .nodeContext(nodeContext)
                .type(type)
                .rawType(rawType)
                .targetClass(rawType)
                .field(field)
                .parent(parent)
                .build();

        final Optional<Class<?>> target = resolveSubtype(node);

        if (target.isPresent()) {
            final Class<?> targetClass = target.get();
            ApiValidator.validateSubtype(rawType, targetClass);

            if (LOG.isDebugEnabled()) {
                LOG.debug("Subtype mapping '{}' to '{}'", Format.withoutPackage(rawType), Format.withoutPackage(targetClass));
            }

            return node.toBuilder()
                    .targetClass(targetClass)
                    .additionalTypeMap(createTypeMapForSubtype(rawType, targetClass))
                    .build();
        }

        return node;
    }

    private Node fromClass(final Class<?> type, @Nullable final Field field, @Nullable final Node parent) {
        final Node node = createNodeWithSubtypeMapping(type, field, parent);
        if (node.hasAncestorEqualToSelf()) {
            return null;
        }

        final Class<?> targetClass = node.getTargetClass();

        if (isContainerClass(targetClass)) {
            final Type[] types = targetClass.isArray() ? new Type[]{targetClass.getComponentType()} : targetClass.getTypeParameters();
            final List<Node> children = createContainerNodeChildren(Arrays.stream(types), node);
            node.setChildren(children);
        } else {
            final List<Node> children = createChildrenFromFields(targetClass, node);
            node.setChildren(children);
        }
        return node;
    }

    private Node fromParameterizedType(final ParameterizedType type, @Nullable final Field field, @Nullable final Node parent) {
        final Node node = createNodeWithSubtypeMapping(type, field, parent);
        if (node.hasAncestorEqualToSelf()) {
            return null;
        }

        final Class<?> targetClass = node.getTargetClass();
        final List<Node> children = isContainerClass(targetClass)
                ? createContainerNodeChildren(Arrays.stream(type.getActualTypeArguments()), node)
                : createChildrenFromFields(targetClass, node);

        node.setChildren(children);
        return node;
    }

    private Node fromGenericArrayNode(final GenericArrayType type, @Nullable final Field field, @Nullable final Node parent) {
        Type gcType = type.getGenericComponentType();

        if (gcType instanceof TypeVariable) {
            gcType = resolveTypeVariable((TypeVariable<?>) gcType, parent);
        }

        final Class<?> rawType = TypeUtils.getArrayClass(gcType);
        final Node node = Node.builder()
                .nodeContext(nodeContext)
                .type(type)
                .targetClass(rawType)
                .rawType(rawType)
                .field(field)
                .parent(parent)
                .build();

        final List<Node> children = createContainerNodeChildren(Stream.of(gcType), node);
        node.setChildren(children);
        return node;
    }

    /**
     * Creates children for a "container" node (that is an array, collection, or map).
     * Children of a container node have no 'field' property since their values
     * are not assigned via fields, but added via {@link Collection#add(Object)},
     * {@link Map#put(Object, Object)}, etc.
     *
     * @param types  a stream of children's types
     * @param parent of the children
     * @return a list of children, or an empty list if no children were created (e.g. to avoid cycles)
     */
    private List<Node> createContainerNodeChildren(final Stream<Type> types, final Node parent) {
        return types.map(type -> createNode(type, null, parent))
                .filter(Objects::nonNull)
                .collect(toList());
    }

    private List<Node> createChildrenFromFields(final Class<?> targetClass, final Node parent) {
        return fieldCollector.getFields(targetClass)
                .stream()
                .map(f -> createNode(ObjectUtils.defaultIfNull(f.getGenericType(), f.getType()), f, parent))
                .filter(Objects::nonNull)
                .collect(toList());
    }

    private static boolean isContainerClass(final Class<?> targetClass) {
        return targetClass.isArray()
                || targetClass == Optional.class
                || Collection.class.isAssignableFrom(targetClass)
                || Map.class.isAssignableFrom(targetClass);
    }

    private Type resolveTypeVariable(final TypeVariable<?> typeVar, @Nullable final Node parent) {
        Type mappedType = parent == null ? typeVar : parent.getTypeMap().getOrDefault(typeVar, typeVar);
        Node ancestor = parent;

        while ((mappedType == null || mappedType instanceof TypeVariable) && ancestor != null) {
            Type rootTypeMapping = nodeContext.getRootTypeMap().get(mappedType);
            if (rootTypeMapping != null) {
                return rootTypeMapping;
            }

            mappedType = ancestor.getTypeMap().getOrDefault(mappedType, mappedType);

            if (mappedType instanceof Class || mappedType instanceof ParameterizedType) {
                break;
            }

            ancestor = ancestor.getParent();
        }
        return mappedType == typeVar ? null : mappedType; // NOPMD
    }


    /**
     * A "subtype type map" is required for performing type substitutions of parameterized types.
     * For example, a subtype may declare a type variable that maps to a type variable declared
     * by the super type. This method provides the "bridge" mapping that allows resolving the actual
     * type parameters.
     * <p>
     * For example, given the following classes:
     *
     * <pre>{@code
     *     interface Supertype<A> {}
     *     class Subtype<B> implements Supertype<B>
     * }</pre>
     * <p>
     * the method returns a map of {@code {B -> A}}
     * <p>
     * NOTE: in its current form, this method only handles the most basic use cases.
     *
     * @param supertype base class
     * @param subtype   of the base class
     * @return additional type mappings that might help resolve type variables
     */
    private static Map<Type, Type> createTypeMapForSubtype(final Class<?> supertype, final Class<?> subtype) {
        if (supertype.equals(subtype)) {
            return Collections.emptyMap();
        }

        final Map<Type, Type> typeMap = new HashMap<>();
        final TypeVariable<?>[] subtypeParams = subtype.getTypeParameters();
        final TypeVariable<?>[] supertypeParams = supertype.getTypeParameters();

        if (subtypeParams.length == supertypeParams.length) {
            for (int i = 0; i < subtypeParams.length; i++) {
                typeMap.put(subtypeParams[i], supertypeParams[i]);
            }
        }

        // If subtype has a generic superclass, add its type variables and type arguments to the type map
        if (subtype.getGenericSuperclass() instanceof ParameterizedType) {
            final ParameterizedType genericSuperclass = (ParameterizedType) subtype.getGenericSuperclass();
            final Class<?> rawSuperclassType = TypeUtils.getRawType(genericSuperclass);
            final TypeVariable<?>[] typeVars = rawSuperclassType.getTypeParameters();
            final Type[] typeArgs = genericSuperclass.getActualTypeArguments();

            if (typeVars.length == typeArgs.length) {
                for (int i = 0; i < typeVars.length; i++) {
                    typeMap.put(typeVars[i], typeArgs[i]);
                }
            }
        }

        return typeMap;
    }
}
