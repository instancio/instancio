package org.instancio.model;

import org.instancio.util.Verify;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collection;
import java.util.Map;

public class NodeFactory {

    private static final Logger LOG = LoggerFactory.getLogger(NodeFactory.class);

    /**
     * @param nodeContext
     * @param klass       of the node.
     *                    <li>For {@link CollectionNode}s this will be List.class, Set.class, etc</li>
     *                    <li>For {@link MapNode} this will be Map.class</li>
     *                    <li>For {@link ClassNode} this will be any other class</li>
     * @param genericType
     * @param field
     * @param parent
     * @return
     */
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

            ((CollectionNode) result).getElementNode().getChildren(); // TODO delete
        } else if (Map.class.isAssignableFrom(klass)) {
            result = createMapNode(nodeContext, klass, genericType, field, parent);
        } else {
            //result = createClassNode(nodeContext, klass, genericType, field, parent);
            result = new ClassNode(nodeContext, field, klass, genericType, parent);
        }

        LOG.debug("Created node: {}", result);

        result.getChildren(); // TODO delete
        return result;
    }

    private Node createArrayNode(NodeContext nodeContext, Class<?> klass, Type genericType, Field field, Node parent) {
        LOG.debug("Getting array component as child node: {}", field.getType());

        Node result = null;

        if (field.getGenericType() instanceof GenericArrayType) {
            final GenericArrayType arrayType = (GenericArrayType) field.getGenericType();
            Type compType = arrayType.getGenericComponentType();
            if (compType instanceof TypeVariable) {
                final Class<?> rawType = nodeContext.getRootTypeMap().get(compType);
                //result = new ClassNode(nodeContext, rawType, null, this);
                result = this.createNode(nodeContext, rawType, null, field, parent);

            } else if (compType instanceof ParameterizedType) {
                ParameterizedType pType = (ParameterizedType) compType;

//                result = new ClassNode(nodeContext, (Class<?>) pType.getRawType(), pType, this);
                result = this.createNode(nodeContext, (Class<?>) pType.getRawType(), pType, field, parent);
            }
        } else {
            //Node node = new ClassNode(nodeContext, field.getType().getComponentType(), null, this);
            result = this.createNode(nodeContext, field.getType().getComponentType(), null, field, parent);
        }

        return new ArrayNode(nodeContext, field, klass, genericType,
                Verify.notNull(result, "Result is null"),
                parent);


    }

    private Node createCollectionNode(NodeContext nodeContext, Class<?> rawClass, Type genericType, Field field, Node parent) {

        //LOG.debug("Getting collection element as child node: {}", parent.getKlass());

        Node result = null;

        if (genericType instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) genericType;

            final Type[] actualTypeArgs = pType.getActualTypeArguments();
            final TypeVariable<?>[] typeVars = rawClass.getTypeParameters();

            Node elementNode = null;

            // will only loop once since Collection<E> has only one type variable
            for (int i = 0; i < actualTypeArgs.length; i++) {
                final Type actualTypeArg = actualTypeArgs[i];
                final TypeVariable<?> typeVar = typeVars[i];
                LOG.debug("actualTypeArg {}: {}, typeVar: {}", actualTypeArg.getClass().getSimpleName(), actualTypeArg, typeVar);

                if (actualTypeArg instanceof Class) {
                    // key/value have no field
                    elementNode = this.createNode(nodeContext, (Class<?>) actualTypeArg, null, field, parent);

                } else if (actualTypeArg instanceof ParameterizedType) {
                    ParameterizedType actualPType = (ParameterizedType) actualTypeArg;
                    Class<?> actualRawType = (Class<?>) actualPType.getRawType();

                    // collection elements have no field
                    // XXX passing field.. because element node could be another collection node.. if we don't pass the field, it will be lost
                    elementNode = this.createNode(nodeContext, actualRawType, actualPType, field, parent);
                } else if (actualTypeArg instanceof TypeVariable) {
                    Type mappedType = parent.getTypeMap().get(actualTypeArg);
                    LOG.debug("actualTypeArg '{}' mpapped to '{}'", ((TypeVariable<?>) actualTypeArg).getName(), mappedType);

                    if (mappedType == null) {
                        mappedType = nodeContext.getRootTypeMap().get(actualTypeArg);
                    }

                    if (mappedType instanceof Class) {
                        elementNode = this.createNode(nodeContext, (Class<?>) mappedType, null, field, parent);
                    } else if (mappedType instanceof ParameterizedType) {
                        Class rawType = (Class) ((ParameterizedType) mappedType).getRawType();
                        elementNode = this.createNode(nodeContext, rawType, mappedType, field, parent);
                    }
                }
            }

            if (elementNode != null) {
                Class<?> rawType = (Class<?>) pType.getRawType(); // Map.class and Map<> pType
                result = new CollectionNode(nodeContext, field, rawType, pType, elementNode, parent);
            } else {
                LOG.warn("Could not resolve Collection element type.");
            }
        }

        return result;
    }


    private Node createMapNode(
            NodeContext nodeContext,
            Class<?> rawClass,  // Map.class
            Type genericType,   // Map<Foo<String>, List<Bar<X>>>
            //Map<TypeVariable<?>, Type> typeMap,
            Field field, // Map<K,V> someField; // unless root node or the map is nested inside another collection or map
            Node parent) {

        //LOG.debug("Getting collection element as child node: {}", parent.getKlass());

        Node result = null;

        if (genericType instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) genericType;

            final Type[] actualTypeArgs = pType.getActualTypeArguments();
            final TypeVariable<?>[] typeVars = rawClass.getTypeParameters();

            Node keyNode = null;
            Node valueNode = null;

            for (int i = 0; i < actualTypeArgs.length; i++) {
                final Type actualTypeArg = actualTypeArgs[i];
                final TypeVariable<?> typeVar = typeVars[i];
                LOG.debug("actualTypeArg {}: {}, typeVar: {}", actualTypeArg.getClass().getSimpleName(), actualTypeArg, typeVar);
                Node node = null;

                if (actualTypeArg instanceof Class) {
                    // key/value have no field
                    node = this.createNode(nodeContext, (Class<?>) actualTypeArg, null, null, parent);

                } else if (actualTypeArg instanceof ParameterizedType) {
                    ParameterizedType actualPType = (ParameterizedType) actualTypeArg;
                    Class<?> actualRawType = (Class<?>) actualPType.getRawType();

                    // key/value have no field
                    node = this.createNode(nodeContext, actualRawType, actualPType, null, parent);
                } else if (actualTypeArg instanceof TypeVariable) {
                    Type mappedType = parent.getTypeMap().get(actualTypeArg);
                    if (mappedType == null) {
                        mappedType = nodeContext.getRootTypeMap().get(actualTypeArg);
                    }
                    LOG.debug("actualTypeArg '{}' mpapped to '{}'", ((TypeVariable<?>) actualTypeArg).getName(), mappedType);
                    if (mappedType instanceof Class) {
                        node = this.createNode(nodeContext, (Class<?>) mappedType, null, field, parent);
                    }
                }

                Verify.notNull(node, "Failed creating node. Args:"
                                + "\n -> rawType: %s"
                                + "\n -> genericType: %s"
                                + "\n -> field: %s"
                                + "\n -> actualTypeArg: %s"
                                + "\n -> typeVar: %s",
                        rawClass, genericType, field, actualTypeArg, typeVar);

                if (typeVar.getName().equals("K")) {
                    keyNode = node;
                } else {
                    valueNode = node;
                }
            }

            if (keyNode != null && valueNode != null) {
                Class<?> rawType = (Class<?>) pType.getRawType(); // Map.class and Map<> pType
                result = new MapNode(nodeContext, field, rawType, pType, keyNode, valueNode, parent);
            } else {
                LOG.warn("Could not resolve Map key/value types.\nKey: {}\nValue:{}", keyNode, valueNode);
            }
        }

        return result;
    }

    // TODO delete
//    private Node createClassNode(final NodeContext nodeContext,
//                                 final Class<?> klass,
//                                 final @Nullable Type genericType,
//                                 final @Nullable Field field,
//                                 final @Nullable Node parent) {
//
//        Node result = null;
//
//        if (genericType == null || genericType instanceof Class) {
//            return new ClassNode(nodeContext, field, klass, null, parent);
//        }
//
//        if (genericType instanceof ParameterizedType) {
//            ParameterizedType pType = (ParameterizedType) genericType;
//
//            final Type[] actualTypeArgs = pType.getActualTypeArguments();
//            final TypeVariable<?>[] typeVars = klass.getTypeParameters();
//
//            for (int i = 0; i < actualTypeArgs.length; i++) {
//                final Type actualTypeArg = actualTypeArgs[i];
//                final TypeVariable<?> typeVar = typeVars[i];
//                LOG.debug("actualTypeArg {}: {}, typeVar: {}", actualTypeArg.getClass().getSimpleName(), actualTypeArg, typeVar);
//
//                if (actualTypeArg instanceof Class) {
//                    // key/value have no field
//                    result = this.createNode(nodeContext, (Class<?>) actualTypeArg, null, null, parent);
//
//                } else if (actualTypeArg instanceof ParameterizedType) {
//                    ParameterizedType actualPType = (ParameterizedType) actualTypeArg;
//                    Class<?> actualRawType = (Class<?>) actualPType.getRawType();
//
//                    result = this.createNode(nodeContext, actualRawType, actualPType, null, parent);
//
//                } else if (actualTypeArg instanceof TypeVariable) {
//                    Type mappedType = parent.getTypeMap().get(actualTypeArg);
//                    LOG.debug("actualTypeArg '{}' mpapped to '{}'", ((TypeVariable<?>) actualTypeArg).getName(), mappedType);
//                    if (mappedType instanceof Class) {
//                        result = this.createNode(nodeContext, (Class<?>) mappedType, null, field, parent);
//                    }
//                }
//
//
//            }
//        }
//
//        return result;
//    }

}
