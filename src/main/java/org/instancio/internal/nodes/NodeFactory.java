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

// TODO this class needs refactoring
public class NodeFactory {

    private static final Logger LOG = LoggerFactory.getLogger(NodeFactory.class);
    private static final String MAP_KEY_TYPE_VARIABLE = "K";

    public Node createRootNode(final NodeContext nodeContext,
                               final Class<?> klass,
                               @Nullable final Type genericType) {

        return createNode(nodeContext, klass, genericType, null, null);
    }

    public Node createNode(final NodeContext nodeContext,
                           final Class<?> klass,
                           @Nullable final Type genericType,
                           @Nullable final Field field,
                           @Nullable final Node parent) {

        final Node result;

        if (klass.isArray() || genericType instanceof GenericArrayType) {
            result = createArrayNode(nodeContext, klass, genericType, field, parent);
        } else if (Collection.class.isAssignableFrom(klass)) {
            result = createCollectionNode(nodeContext, klass, genericType, field, parent);
        } else if (Map.class.isAssignableFrom(klass)) {
            result = createMapNode(nodeContext, klass, genericType, field, parent);
        } else {
            result = createClassNode(nodeContext, klass, genericType, field, parent);
        }

        LOG.trace("Created node: {}", result);
        if (nodeContext.isUnvisited(result)) {
            // mark is visited before invoking getChildren() to avoid stack overflow
            nodeContext.visited(result);

            result.getChildren(); // TODO delete
//            ((CollectionNode) result).getElementNode().getChildren(); // TODO delete
        }
        return result;
    }

    private Node createArrayNode(
            final NodeContext nodeContext,
            final Class<?> arrayClass,
            @Nullable final Type arrayGenericType,
            @Nullable final Field field,
            @Nullable final Node parent) {

        Node elementNode;
        Class<?> compRawType = field != null && field.getType().getComponentType() != null
                ? field.getType().getComponentType()
                : arrayClass.getComponentType();

        Type compGenericType = compRawType;

        if (arrayGenericType instanceof GenericArrayType) {
            compGenericType = ((GenericArrayType) arrayGenericType).getGenericComponentType();
        }

        if (compRawType == null && compGenericType != null) {
            compRawType = TypeUtils.getRawType(compGenericType);
        }

        Verify.notNull(compRawType, "Component type is null. klass: %s, genericType: %s", arrayClass, arrayGenericType);

        if (compGenericType instanceof GenericArrayType) {
            final GenericArrayType arrayType = (GenericArrayType) compGenericType;
            final Type compType = arrayType.getGenericComponentType();
            elementNode = _createNode(nodeContext, field, parent, compType);
        } else if (compGenericType instanceof Class) {
            elementNode = this.createNode(nodeContext, compRawType, compRawType, null, parent);
        } else {
            elementNode = this.createNode(nodeContext, compRawType, compGenericType, null, parent);
        }

        return new ArrayNode(nodeContext, arrayClass, Verify.notNull(elementNode), field, arrayGenericType, parent);
    }

    private Node createCollectionNode(
            final NodeContext nodeContext,
            final Class<?> rawClass,
            @Nullable final Type genericType,
            @Nullable final Field field,
            @Nullable final Node parent) {

        Node result = null;

        if (genericType instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) genericType;

            final Type[] actualTypeArgs = pType.getActualTypeArguments();

            Verify.state(actualTypeArgs.length == 1,
                    "List should have one type argument: %s", actualTypeArgs.length);

            // no field value added to element nodes since elements are added via Collection.add(obj) method
            Node elementNode = _createNode(nodeContext, null, parent, actualTypeArgs[0]);

            if (elementNode != null) {

                // XXX handle with this properly
                if (field != null) {
                    final Type fieldGenericType = field.getGenericType();
                    final Type passedOnType;

                    if (fieldGenericType instanceof TypeVariable) {
                        // NOTE: replaced by 'resolve' method
                        // final Type mappedType = parent.getTypeMap().get(fieldGenericType);
                        final Type mappedType = resolveTypeVariable(nodeContext, (TypeVariable<?>) fieldGenericType, parent);

                        passedOnType = mappedType instanceof ParameterizedType
                                ? ((ParameterizedType) mappedType).getRawType()
                                : (Class<?>) mappedType;

                    } else if (fieldGenericType instanceof ParameterizedType) {
                        passedOnType = ((ParameterizedType) fieldGenericType).getRawType();
                    } else if (fieldGenericType instanceof Class) {
                        passedOnType = fieldGenericType;
                    } else {
                        throw new IllegalStateException("Failed resolving collection type");
                    }

                    result = new CollectionNode(nodeContext, (Class<?>) passedOnType, elementNode, field, fieldGenericType, parent);
                } else {
                    Class<?> rawType = (Class<?>) pType.getRawType(); // Collection.class and Collection<> pType
                    result = new CollectionNode(nodeContext, rawType, elementNode, field, pType, parent);
                }
            } else {
                LOG.warn("Could not resolve Collection element type.");
            }
        } else if (genericType instanceof Class) { // collection without type specified... 'List list'

            final TypeVariable<?> typeVariable = rawClass.getTypeParameters()[0];
            final Class<?> mappedType = nodeContext.getRootTypeMap().getOrDefault(typeVariable, Object.class);

            Node elementNode = new ClassNode(nodeContext, mappedType, null, null, parent);
            result = new CollectionNode(nodeContext, rawClass, elementNode, field, rawClass, parent);
        }

        return Verify.notNull(result, "Unable to create a CollectionNode for class: " + rawClass.getName()
                + ", generic type: " + genericType);
    }


    // field referencing the map such as {@code Map<K,V> someField},
    // or {@code null} if it's a root node or the map is nested inside another collection or map
    private Node createMapNode(
            final NodeContext nodeContext,
            final Class<?> rawClass,
            @Nullable final Type genericType,
            @Nullable final Field field,
            @Nullable final Node parent) {

        Node result = null;

        if (genericType instanceof ParameterizedType) {
            final ParameterizedType pType = (ParameterizedType) genericType;
            final Type[] actualTypeArgs = pType.getActualTypeArguments();

            // field is null for key and value nodes since values are added via Map.put(key,val)
            final Node keyNode = _createNode(nodeContext, null, parent, actualTypeArgs[0]);
            final Node valueNode = _createNode(nodeContext, null, parent, actualTypeArgs[1]);
            final Class<?> mapClass = (Class<?>) pType.getRawType();

            result = new MapNode(nodeContext, mapClass, keyNode, valueNode, field, pType, parent);
        } else if (genericType instanceof Class) { // collection without type specified... 'Map map'
            final TypeVariable<?> keyTypeVariable = Map.class.getTypeParameters()[0];
            final TypeVariable<?> valueTypeVariable = Map.class.getTypeParameters()[1];

            final Class<?> keyClass = nodeContext.getRootTypeMap().getOrDefault(keyTypeVariable, Object.class);
            final Class<?> valueClass = nodeContext.getRootTypeMap().getOrDefault(valueTypeVariable, Object.class);

            final Node keyNode = new ClassNode(nodeContext, keyClass, null, null, parent);
            final Node valueNode = new ClassNode(nodeContext, valueClass, null, null, parent);
            result = new MapNode(nodeContext, rawClass, keyNode, valueNode, field, rawClass, parent);
        }

        return Verify.notNull(result, "Failed creating MapNode for rawType '%s', genericType '%s'",
                rawClass.getName(), genericType);
    }

    private Node createClassNode(final NodeContext nodeContext,
                                 final Class<?> klass,
                                 final @Nullable Type genericType,
                                 final @Nullable Field field,
                                 final @Nullable Node parent) {

        if (genericType == null || klass != Object.class || (field != null && field.getGenericType() instanceof Class))
            return new ClassNode(nodeContext, klass, field, genericType, parent);


        if (genericType instanceof TypeVariable) {
            Type mappedType = resolveTypeVariable(nodeContext, (TypeVariable<?>) genericType, parent);

            if (mappedType instanceof Class) {
                return new ClassNode(nodeContext, (Class<?>) mappedType, field, mappedType, parent);
            }
            if (mappedType instanceof ParameterizedType) {
                Class<?> rawType = (Class<?>) ((ParameterizedType) mappedType).getRawType();
                return this.createNode(nodeContext, rawType, mappedType, field, parent);
            }
            if (mappedType instanceof GenericArrayType) {
                final GenericArrayType arrayType = (GenericArrayType) mappedType;
                final Type compType = arrayType.getGenericComponentType();
                final Class<?> rawType = TypeUtils.getRawType(compType);
                final Class<?> arrayClass = Array.newInstance(rawType, 0).getClass();

                //return this.createNode(nodeContext, arrayClass, compType, field, parent);

                return this.createArrayNode(nodeContext, arrayClass, arrayType, field, parent);

            }
            if (nodeContext.getRootTypeMap().containsKey(mappedType)) {
                Class<?> rawType = nodeContext.getRootTypeMap().get(mappedType);
                return new ClassNode(nodeContext, rawType, field, mappedType, parent);
            }
        } else if (genericType instanceof ParameterizedType) {
            throw new RuntimeException("Unused branch"); // TODO cleanup
//            if (field != null) {
//                final Type fieldGenericType = field.getGenericType();
//                final Type mappedType = parent.getTypeMap().getOrDefault(fieldGenericType, fieldGenericType);
//                if (mappedType instanceof Class) {
//                    return new ClassNode(nodeContext, (Class<?>) mappedType, field, null, parent);
//                }
//                if (nodeContext.getRootTypeMap().containsKey(mappedType)) {
//
//                    final Class<?> rawType = nodeContext.getRootTypeMap().get(mappedType);
//                    return new ClassNode(nodeContext, rawType, field, null, parent);
//                }
//            }
        } else if (genericType instanceof Class) {
            throw new RuntimeException("Unused branch"); // TODO cleanup
            //return new ClassNode(nodeContext, (Class<?>) genericType, field, null, parent);
        }

        throw new IllegalStateException("Error creating a class node for klass: " + klass.getName() + ", type: " + genericType);
    }

    private Node _createNode(final NodeContext nodeContext,
                             @Nullable final Field field,
                             @Nullable final Node parent,
                             final Type actualTypeArg) {

        Node elementNode = null;
        if (actualTypeArg instanceof Class) {

            elementNode = this.createNode(nodeContext, (Class<?>) actualTypeArg, null, null, parent);

        } else if (actualTypeArg instanceof ParameterizedType) {
            ParameterizedType actualPType = (ParameterizedType) actualTypeArg;
            Class<?> actualRawType = (Class<?>) actualPType.getRawType();
            elementNode = this.createNode(nodeContext, actualRawType, actualPType, null, parent);
        } else if (actualTypeArg instanceof TypeVariable) {
            Type mappedType = resolveTypeVariable(nodeContext, (TypeVariable<?>) actualTypeArg, parent);

            if (mappedType instanceof Class) {
                elementNode = this.createNode(nodeContext, (Class<?>) mappedType, null, null, parent);
            } else if (mappedType instanceof ParameterizedType) {
                Class<?> rawType = (Class<?>) ((ParameterizedType) mappedType).getRawType();
                elementNode = this.createNode(nodeContext, rawType, mappedType, null, parent);
            }
        } else if (actualTypeArg instanceof WildcardType) {
            final WildcardType wildcardType = (WildcardType) actualTypeArg;
            final Type[] upperBounds = wildcardType.getUpperBounds();
            final Type upperBound = upperBounds[0];
            if (upperBound instanceof Class) {
                elementNode = this.createNode(nodeContext, (Class<?>) upperBound, null, null, parent);
            } else if (upperBound instanceof ParameterizedType) {
                final ParameterizedType upperPType = (ParameterizedType) upperBound;
                elementNode = this.createNode(nodeContext, (Class<?>) upperPType.getRawType(), upperPType, null, parent);
            } else {
                throw new UnsupportedOperationException("Unsupported upper bound type: " + upperBound.getClass());
            }

        } else if (actualTypeArg instanceof GenericArrayType) {
            Class<?> rawType = TypeUtils.getRawType(actualTypeArg);
            elementNode = this.createNode(nodeContext, rawType, actualTypeArg, field, parent);
        }
        return elementNode;
    }


    private Type resolveTypeVariable(final NodeContext nodeContext, final TypeVariable<?> typeVariable, final Node parent) {
        Type mappedType = parent.getTypeMap().getOrDefault(typeVariable, typeVariable);

        Node ancestor = parent;
        while ((mappedType == null || !nodeContext.getRootTypeMap().containsKey(mappedType)) && ancestor != null) {
            mappedType = ancestor.getTypeMap().getOrDefault(mappedType, mappedType);

            if (mappedType instanceof Class || mappedType instanceof ParameterizedType) {
                break;
            }

            ancestor = ancestor.getParent();
        }
        return mappedType;
    }

    // TODO delete
//    private Type resolveTypeVariable2(final NodeContext nodeContext, @Nullable final TypeVariable<?> typeVariable, final Node parent) {
//        Type mappedType = parent == null ? null : parent.resolveTypeVariable(typeVariable);
//
//        if (mappedType == null) {
//            mappedType = nodeContext.getRootTypeMap().get(typeVariable);
//        }
//        return mappedType;
//    }

}
