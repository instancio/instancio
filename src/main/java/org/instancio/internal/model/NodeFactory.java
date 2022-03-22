package org.instancio.internal.model;

import org.instancio.util.ObjectUtils;
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
import java.util.Collection;
import java.util.Map;

// TODO this class needs refactoring
public class NodeFactory {

    private static final Logger LOG = LoggerFactory.getLogger(NodeFactory.class);

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

        Node result;

        if (klass.isArray()) {
            result = createArrayNode(nodeContext, klass, genericType, field, parent);
        } else if (Collection.class.isAssignableFrom(klass)) {
            result = createCollectionNode(nodeContext, klass, genericType, field, parent);
        } else if (Map.class.isAssignableFrom(klass)) {
            result = createMapNode(nodeContext, klass, genericType, field, parent);
        } else {
            //result = new ClassNode(nodeContext, klass, field, genericType, parent);
            result = createClassNode(nodeContext, klass, genericType, field, parent);
        }


        LOG.debug("Created node: {}", result);
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
            final Class<?> klass,
            @Nullable final Type genericType,
            @Nullable final Field field,
            @Nullable final Node parent) {

        Node elementNode = null;

        if (field != null) {
            if (field.getGenericType() instanceof GenericArrayType) {
                final GenericArrayType arrayType = (GenericArrayType) field.getGenericType();
                final Type compType = arrayType.getGenericComponentType();

                if (compType instanceof TypeVariable) {
                    final Class<?> rawType = (Class<?>) ObjectUtils.defaultIfNull(
                            nodeContext.getRootTypeMap().get(compType),
                            parent == null ? null : parent.getTypeMap().get(compType)
                    );

                    Verify.notNull(rawType, "Failed resolving array component type from type variable: '%s'", compType);
                    elementNode = this.createNode(nodeContext, rawType, null, null, parent);

                } else if (compType instanceof ParameterizedType) {
                    ParameterizedType pType = (ParameterizedType) compType;

                    elementNode = this.createNode(nodeContext, (Class<?>) pType.getRawType(), pType, null, parent);
                }
            } else {
                elementNode = this.createNode(nodeContext, field.getType().getComponentType(), null, null, parent);
            }
        } else {
            elementNode = this.createNode(nodeContext, klass.getComponentType(), null, null, parent);
        }

        return new ArrayNode(nodeContext, klass, Verify.notNull(elementNode), field, genericType, parent);
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

            Node elementNode = null;

            // no field value added to element nodes since elements are added via Collection.add(obj) method
            // will only loop once since Collection<E> has only one type variable

            Verify.isTrue(actualTypeArgs.length == 1, "Expected only 1 type arg");

            for (int i = 0; i < actualTypeArgs.length; i++) {
                final Type actualTypeArg = actualTypeArgs[i];

                if (actualTypeArg instanceof Class) {

                    elementNode = this.createNode(nodeContext, (Class<?>) actualTypeArg, null, null, parent);

                } else if (actualTypeArg instanceof ParameterizedType) {
                    ParameterizedType actualPType = (ParameterizedType) actualTypeArg;
                    Class<?> actualRawType = (Class<?>) actualPType.getRawType();
                    elementNode = this.createNode(nodeContext, actualRawType, actualPType, null, parent);
                } else if (actualTypeArg instanceof TypeVariable) {
                    Type mappedType = parent == null ? null : parent.resolveTypeVariable((TypeVariable<?>) actualTypeArg);

                    if (mappedType == null) {
                        mappedType = nodeContext.getRootTypeMap().get(actualTypeArg);
                    }

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

                }
            }

            if (elementNode != null) {

                // XXX handle with this properly
                if (field != null) {
                    final Type fieldGenericType = field.getGenericType();
                    final Type passedOnType;

                    if (fieldGenericType instanceof TypeVariable) {
                        final Type mappedType = parent.getTypeMap().get(fieldGenericType);

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
            ParameterizedType pType = (ParameterizedType) genericType;

            final Type[] actualTypeArgs = pType.getActualTypeArguments();
            final TypeVariable<?>[] typeVars = rawClass.getTypeParameters();

            Node keyNode = null;
            Node valueNode = null;

            // field is null for key and value nodes since values are added via Map.put(key,val)
            for (int i = 0; i < actualTypeArgs.length; i++) {
                final Type actualTypeArg = actualTypeArgs[i];
                final TypeVariable<?> typeVar = typeVars[i];

                Node node = null;

                if (actualTypeArg instanceof Class) {
                    node = this.createNode(nodeContext, (Class<?>) actualTypeArg, null, null, parent);

                } else if (actualTypeArg instanceof ParameterizedType) {
                    ParameterizedType actualPType = (ParameterizedType) actualTypeArg;
                    Class<?> actualRawType = (Class<?>) actualPType.getRawType();

                    node = this.createNode(nodeContext, actualRawType, actualPType, null, parent);
                } else if (actualTypeArg instanceof TypeVariable) {
                    Type mappedType = parent.getTypeMap().get(actualTypeArg);
                    if (mappedType == null) {
                        mappedType = nodeContext.getRootTypeMap().get(actualTypeArg);
                    }

                    if (mappedType instanceof Class) {
                        node = this.createNode(nodeContext, (Class<?>) mappedType, null, null, parent);
                    }
                }

                Verify.notNull(node, "Failed creating node. Args:"
                                + "\n -> rawType: %s"
                                + "\n -> genericType: %s"
                                + "\n -> field: %s"
                                + "\n -> actualTypeArg: %s"
                                + "\n -> typeVar: %s",
                        rawClass, genericType, field, actualTypeArg, typeVar);

                if (typeVar.getName().equals("K")) { // TODO hardcoded
                    keyNode = node;
                } else {
                    valueNode = node;
                }
            }

            if (keyNode != null && valueNode != null) {
                Class<?> mapClass = (Class<?>) pType.getRawType();
                result = new MapNode(nodeContext, mapClass, keyNode, valueNode, field, pType, parent);
            } else {
                LOG.debug("Could not resolve Map key/value types.\nKey: {}\nValue:{}", keyNode, valueNode);
            }
        } else if (genericType instanceof Class) { // collection without type specified... 'Map map'
            // TODO refactor
            final TypeVariable<?> keyTypeVariable = Map.class.getTypeParameters()[0];
            final TypeVariable<?> valueTypeVariable = Map.class.getTypeParameters()[1];

            final Class<?> keyClass = nodeContext.getRootTypeMap().getOrDefault(keyTypeVariable, Object.class);
            final Class<?> valueClass = nodeContext.getRootTypeMap().getOrDefault(valueTypeVariable, Object.class);

            Node keyNode = new ClassNode(nodeContext, keyClass, null, null, parent);
            Node valueNode = new ClassNode(nodeContext, valueClass, null, null, parent);
            result = new MapNode(nodeContext, rawClass, keyNode, valueNode, field, rawClass, parent);
        }

        return Verify.notNull(result);
    }

    private Node createClassNode(final NodeContext nodeContext,
                                 final Class<?> klass,
                                 final @Nullable Type genericType,
                                 final @Nullable Field field,
                                 final @Nullable Node parent) {

        if (genericType == null || (field != null && field.getGenericType() instanceof Class))
            return new ClassNode(nodeContext, klass, field, null, parent);

        if (klass != Object.class)
            return new ClassNode(nodeContext, klass, field, genericType, parent);

        if (genericType instanceof TypeVariable) {
            Type mappedType = parent.getTypeMap().getOrDefault(genericType, genericType);

            Node ancestor = parent;
            while ((mappedType == null || !nodeContext.getRootTypeMap().containsKey(mappedType)) && ancestor != null) {
                mappedType = ancestor.getTypeMap().getOrDefault(mappedType, mappedType);

                if (mappedType instanceof Class || mappedType instanceof ParameterizedType)
                    break;

                ancestor = ancestor.getParent();
            }

            if (mappedType instanceof Class) {
                return new ClassNode(nodeContext, (Class<?>) mappedType, field, mappedType, parent);
            }
            if (nodeContext.getRootTypeMap().containsKey(mappedType)) {
                Class<?> rawType = nodeContext.getRootTypeMap().get(mappedType);
                return new ClassNode(nodeContext, rawType, field, mappedType, parent);
            }
        } else if (genericType instanceof ParameterizedType) {

            if (field != null) {
                final Type fieldGenericType = field.getGenericType();
                final Type mappedType = parent.getTypeMap().getOrDefault(fieldGenericType, fieldGenericType);
                if (mappedType instanceof Class) {
                    return new ClassNode(nodeContext, (Class<?>) mappedType, field, null, parent);
                }
                if (parent.getRootTypeMap().containsKey(mappedType)) {

                    final Class<?> rawType = nodeContext.getRootTypeMap().get(mappedType);
                    return new ClassNode(nodeContext, rawType, field, null, parent);
                }
            }
        } else if (genericType instanceof Class) {
            return new ClassNode(nodeContext, (Class<?>) genericType, field, null, parent);
        }

        throw new IllegalStateException("Error creating a class node for klass: " + klass.getName() + ", type: " + genericType);
    }

}
