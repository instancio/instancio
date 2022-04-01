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

import org.instancio.util.TypeUtils;
import org.instancio.util.Verify;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Collection;
import java.util.Map;

public class NodeFactory {
    private static final Logger LOG = LoggerFactory.getLogger(NodeFactory.class);

    private final NodeContext nodeContext;

    public NodeFactory(final NodeContext nodeContext) {
        this.nodeContext = nodeContext;
    }

    public Node createRootNode(final Class<?> klass, @Nullable final Type genericType) {
        return createNode(klass, genericType, null, null);
    }

    public Node createNode(final Class<?> klass,
                           @Nullable final Type genericType,
                           @Nullable final Field field,
                           final Node parent) {

        final Node result;

        if (klass.isArray() || genericType instanceof GenericArrayType) {
            result = createArrayNode(klass, genericType, field, parent);
        } else if (Collection.class.isAssignableFrom(klass)) {
            result = createCollectionNode(klass, genericType, field, parent);
        } else if (Map.class.isAssignableFrom(klass)) {
            result = createMapNode(klass, genericType, field, parent);
        } else {
            result = createClassNode(klass, genericType, field, parent);
        }

        if (nodeContext.isUnvisited(result)) {
            // mark as visited before invoking getChildren() to avoid stack overflow
            nodeContext.visited(result);
        }

        LOG.trace("Created node: {}", result);
        return result;
    }

    private Node createArrayNode(
            final Class<?> arrayClass,
            @Nullable final Type arrayGenericType,
            @Nullable final Field field,
            final Node parent) {

        Class<?> compRawType = field != null && field.getType().getComponentType() != null
                ? field.getType().getComponentType()
                : arrayClass.getComponentType();

        Type compGenericType = compRawType;

        if (arrayGenericType instanceof GenericArrayType) {
            compGenericType = ((GenericArrayType) arrayGenericType).getGenericComponentType();
        }
        Class<?> actualArrayClass = arrayClass;

        if (compGenericType instanceof TypeVariable) {
            compGenericType = resolveTypeVariable(compGenericType, parent);
            compRawType = TypeUtils.getRawType(compGenericType);

            if (arrayClass == Object[].class) {
                actualArrayClass = Array.newInstance(compRawType, 0).getClass();
            }
        }

        if (compRawType == null && compGenericType != null) {
            compRawType = TypeUtils.getRawType(compGenericType);
        }

        Verify.notNull(compRawType, "Component type is null: %s, %s", arrayClass, arrayGenericType);

        final Node elementNode = compGenericType instanceof Class
                ? createNode(compRawType, compRawType, null, parent)
                : createNode(compRawType, compGenericType, null, parent);

        return new ArrayNode(nodeContext, actualArrayClass, Verify.notNull(elementNode), field, arrayGenericType, parent);
    }

    private Node createCollectionNode(
            final Class<?> klass,
            @Nullable final Type genericType,
            @Nullable final Field field,
            final Node parent) {

        if (genericType instanceof ParameterizedType) {
            final Type[] typeArgs = TypeUtils.getTypeArguments(genericType);
            final Node elementNode = createElementNode(parent, typeArgs[0]);
            return new CollectionNode(nodeContext, klass, elementNode, field, genericType, parent);
        }

        // collection without type: 'List list'
        if (genericType instanceof Class) {
            final TypeVariable<?> typeVariable = klass.getTypeParameters()[0];
            final Class<?> mappedType = nodeContext.getRootTypeMap().getOrDefault(typeVariable, Object.class);
            final Node elementNode = new ClassNode(nodeContext, mappedType, null, null, parent);
            return new CollectionNode(nodeContext, klass, elementNode, field, klass, parent);
        }

        throw new IllegalStateException(String.format(
                "Unable to create a CollectionNode for class: %s, generic type: %s", klass.getName(), genericType));
    }

    private Node createMapNode(
            final Class<?> klass,
            @Nullable final Type genericType,
            @Nullable final Field field,
            final Node parent) {

        if (genericType instanceof ParameterizedType) {
            final Type[] typeArgs = TypeUtils.getTypeArguments(genericType);
            final Node keyNode = createElementNode(parent, typeArgs[0]);
            final Node valueNode = createElementNode(parent, typeArgs[1]);
            final Class<?> mapClass = TypeUtils.getRawType(genericType);
            return new MapNode(nodeContext, mapClass, keyNode, valueNode, field, genericType, parent);
        }

        // map without type: 'Map map'
        if (genericType instanceof Class) {
            final TypeVariable<?> keyTypeVariable = Map.class.getTypeParameters()[0];
            final TypeVariable<?> valueTypeVariable = Map.class.getTypeParameters()[1];

            final Class<?> keyClass = nodeContext.getRootTypeMap().getOrDefault(keyTypeVariable, Object.class);
            final Class<?> valueClass = nodeContext.getRootTypeMap().getOrDefault(valueTypeVariable, Object.class);

            final Node keyNode = new ClassNode(nodeContext, keyClass, null, null, parent);
            final Node valueNode = new ClassNode(nodeContext, valueClass, null, null, parent);
            return new MapNode(nodeContext, klass, keyNode, valueNode, field, klass, parent);
        }

        throw new IllegalStateException(String.format(
                "Unable to create a MapNode for class: %s, generic type: %s", klass.getName(), genericType));
    }

    private Node createClassNode(
            final Class<?> klass,
            final @Nullable Type genericType,
            final @Nullable Field field,
            Node parent) {

        if (genericType == null || klass != Object.class || (field != null && field.getGenericType() instanceof Class)) {
            return new ClassNode(nodeContext, klass, field, genericType, parent);
        }

        if (genericType instanceof TypeVariable) {
            Type mappedType = resolveTypeVariable(genericType, parent);

            if (mappedType instanceof Class) {
                final Class<?> rawType = (Class<?>) mappedType;
                return rawType.isArray()
                        ? createArrayNode(rawType, null, field, parent)
                        : new ClassNode(nodeContext, rawType, field, mappedType, parent);
            }
            if (mappedType instanceof ParameterizedType) {
                return createNode(TypeUtils.getRawType(mappedType), mappedType, field, parent);
            }
            if (mappedType instanceof GenericArrayType) {
                return createArrayNode(TypeUtils.getArrayClass(mappedType), mappedType, field, parent);
            }
        }

        throw new IllegalStateException(String.format(
                "Error creating a class node for klass: %s, type: %s", klass.getName(), genericType));
    }

    // Note: field is null for collection/map/array element nodes as they are set via add()/put()/array[i]
    private Node createElementNode(final Node parent, final Type elementType) {
        final Type type = elementType instanceof TypeVariable
                ? resolveTypeVariable(elementType, parent)
                : elementType;

        Node elementNode = null;

        if (type instanceof Class) {
            elementNode = createNode((Class<?>) type, null, null, parent);
        } else if (type instanceof ParameterizedType || type instanceof GenericArrayType) {
            elementNode = createNode(TypeUtils.getRawType(type), type, null, parent);
        } else if (type instanceof WildcardType) {
            final WildcardType wildcardType = (WildcardType) type;
            final Type[] upperBounds = wildcardType.getUpperBounds();
            final Type upperBound = upperBounds[0];
            if (upperBound instanceof Class) {
                elementNode = createNode((Class<?>) upperBound, null, null, parent);
            } else if (upperBound instanceof ParameterizedType) {
                elementNode = createNode(TypeUtils.getRawType(upperBound), upperBound, null, parent);
            } else {
                throw new UnsupportedOperationException("Unsupported upper bound type: " + upperBound.getClass());
            }
        }

        return Verify.notNull(elementNode, "Null element node");
    }


    private Type resolveTypeVariable(final Type typeVariable, final Node parent) {
        Verify.isTrue(typeVariable instanceof TypeVariable, "Expected a type variable: %s", typeVariable.getClass());

        Type mappedType = parent.getTypeMap().getOrDefault(typeVariable, typeVariable);
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
        return mappedType;
    }
}
