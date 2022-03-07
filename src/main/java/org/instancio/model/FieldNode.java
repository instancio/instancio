package org.instancio.model;

import org.instancio.util.ObjectUtils;
import org.instancio.util.ReflectionUtils;
import org.instancio.util.Verify;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

/**
 * <pre>
 * To address:
 *  - confusion between "this.genericType" and "this.field.getGenericType()".
 *  -- When to use one vs the other?
 *  -- rename this.genericType
 *
 * </pre>
 */
public class FieldNode extends Node {
    private static final Logger LOG = LoggerFactory.getLogger(FieldNode.class);

    private static final String JAVA_PKG_PREFIX = "java";

    private final Field field;

    /**
     * Contains actual type of the field.
     * For generic classes {@code field.getType()} returns Object.
     */
    private final Class<?> actualFieldType;
    private final String[] typeVariables;
    private final Map<TypeVariable<?>, Type> typeMap;

    private final Class<?> classDeclaringTheTypeVariable; // e.g. List<E> => List.class (declares E)
    private final Type genericType;

    private final List<PType> nestedTypes = new ArrayList<>();


    public FieldNode(
            final NodeContext nodeContext,
            final Field field,
            Class<?> classDeclaringTheTypeVariable,
            final Type genericType,
            final FieldNode parentFieldNode) {

        super(nodeContext, field.getType(), genericType, parentFieldNode);

        this.field = Verify.notNull(field, "Field must not be null");

        if (classDeclaringTheTypeVariable == null) {
            this.actualFieldType = field.getType();
            classDeclaringTheTypeVariable = field.getType();
        } else {
            LOG.debug("Setting field node for '{}' to '{}'", field.getName(), classDeclaringTheTypeVariable);
            this.actualFieldType = classDeclaringTheTypeVariable;
        }

        this.typeVariables = getTypeVariables(field);
        this.classDeclaringTheTypeVariable = classDeclaringTheTypeVariable;
        this.genericType = genericType;
        this.typeMap = getTypeMap(classDeclaringTheTypeVariable, genericType);

        nodeContext.visited(field);
        setChildren(makeChildren());
    }

    public FieldNode(NodeContext nodeContext, Field field) {
        this(nodeContext, field, field.getType(), field.getGenericType(), /* parent = */ null); // TODO set parent
    }

    private List<Node> makeChildren() {
        if (Collection.class.isAssignableFrom(field.getType())) {
            return getCollectionElementTypeAsChildNode();
        }


        final Package fieldPackage = actualFieldType.getPackage();
        if (fieldPackage == null || fieldPackage.getName().startsWith(JAVA_PKG_PREFIX)) {
            return Collections.emptyList(); // Exclude JDK classes
        }

        return getDeclaredFieldsAsChildNodes();
    }

    // Collection element raw class + generic type (actual type arg)
    private List<Node> getCollectionElementTypeAsChildNode() {
        LOG.debug("Getting collection element as child node: {}", field.getType());

        final List<Node> childNodes = new ArrayList<>();

        //final Type genericType = field.getGenericType();
        if (genericType instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) genericType;

            final Type[] actualTypeArgs = pType.getActualTypeArguments();
            final TypeVariable<?>[] typeVars = field.getType().getTypeParameters();

            for (int i = 0; i < actualTypeArgs.length; i++) {
                final Type actualTypeArg = actualTypeArgs[i];
                final TypeVariable<?> typeVar = typeVars[i];
                LOG.debug("actualTypeArg {}: {}, typeVar: {}", actualTypeArg.getClass().getSimpleName(), actualTypeArg, typeVar);

                if (actualTypeArg instanceof ParameterizedType) {
                    ParameterizedType actualPType = (ParameterizedType) actualTypeArg;
                    Class<?> actualRawType = (Class<?>) actualPType.getRawType();

                    Node node = new ClassNode(getNodeContext(), actualRawType, actualPType, this);
                    childNodes.add(node);
                }

            }
        }

        return childNodes;
    }

    private List<Node> getDeclaredFieldsAsChildNodes() {
        final Field[] childFields = actualFieldType.getDeclaredFields();
        final List<Node> childNodes = new ArrayList<>();

        for (Field childField : childFields) {
            final Type typeParameter = childField.getGenericType();

            Class<?> resolvedTypeArg = null;
            Type typeArgument = typeMap.get(typeParameter);

            if (typeArgument instanceof TypeVariable) {
                resolvedTypeArg = getRootTypeMap().get(typeArgument);
            } else if (typeArgument instanceof Class) {
                resolvedTypeArg = (Class<?>) typeArgument;
            }

            final Optional<Type> classParameterizedTypePType = nestedTypes.stream()
                    .filter(pType -> pType.getRawType().equals(typeArgument)).findAny()
                    .map(PType::getParameterizedType);

            Type genericType = classParameterizedTypePType.orElse(childField.getGenericType());

            if (getNodeContext().isUnvisited(childField)) {
                FieldNode childNode = new FieldNode(getNodeContext(), childField, resolvedTypeArg, genericType, this);
                childNodes.add(childNode);
                getNodeContext().visited(childField);
            }

        }

        return childNodes;
    }

    public Field getField() {
        return field;
    }

    public String getFieldName() {
        return field.getName();
    }

    public Class<?> getActualFieldType() {
        // XXX this.genericType or field.getGenericType()?
        if (actualFieldType.equals(Object.class) && getRootTypeMap().containsKey(field.getGenericType())) {
            // Handle fields like:
            // class SomeClass<T> { T field; }
            return getRootTypeMap().get(field.getGenericType());
        }
        return actualFieldType;
    }

    public Class<?> getArrayType() {
        if (field.getGenericType() instanceof GenericArrayType) {
            final GenericArrayType arrayType = (GenericArrayType) field.getGenericType();
            Type compType = arrayType.getGenericComponentType();
            if (compType instanceof TypeVariable) {
                return getRootTypeMap().get(compType);
            }
        }
        return field.getType().getComponentType();
    }

    public Class<?> getCollectionType() {
        if (!Collection.class.isAssignableFrom(field.getType())) {
            throw new IllegalStateException("Not a collection field: " + field.getName());
        }

        // XXX can there be more than one type arg?
        final ParameterizedType genericType = (ParameterizedType) field.getGenericType();
        Type typeArgument = genericType.getActualTypeArguments()[0];

        Class<?> resolvedTypeArg = null;

        if (typeArgument instanceof TypeVariable) {
            final TypeVariable<?> collectionTypeParameter = field.getType().getTypeParameters()[0]; // E
            typeArgument = ObjectUtils.defaultIfNull(typeMap.get(collectionTypeParameter), typeArgument);
            resolvedTypeArg = getRootTypeMap().get(typeArgument);
        } else if (typeArgument instanceof Class) {
            resolvedTypeArg = (Class<?>) typeArgument;
        } else if (typeArgument instanceof ParameterizedType) {
            resolvedTypeArg = (Class<?>) ((ParameterizedType) typeArgument).getRawType();
        }

        return resolvedTypeArg;
    }


    /**
     * Returns type name of the declared field, e.g.
     *
     * <pre>{@code
     *   T someField;          // => T
     *   PType<L,R> PType;     // => org.example.PType<L, R>
     *   String str;           // => java.lang.String
     *   List<Phone> numbers;  // => java.util.List<org.example.Phone>
     * }</pre>
     */
    public String getTypeName() {

        return field.getGenericType().getTypeName();
    }

    public Map<TypeVariable<?>, Type> getTypeMap() {
        return typeMap;
    }

    public FieldNode getChildByTypeParameter(final String typeParameter) {
        return getFieldNodeStream()
                .filter(it -> Objects.equals(typeParameter, it.getField().getGenericType().getTypeName()))
                .findAny()
                .orElseThrow(() -> new NoSuchElementException("No child with type parameter: " + typeParameter));
    }

    public FieldNode getChildByFieldName(String name) {
        return getFieldNodeStream()
                .filter(it -> it.getFieldName().equals(name))
                .findAny()
                .orElseThrow(() -> new NoSuchElementException("Field with name '" + name + "' not found"));
    }

    private Stream<FieldNode> getFieldNodeStream() {
        return getChildren().stream()
                .filter(it -> it instanceof FieldNode)
                .map(it -> (FieldNode) it);
    }


    private static String[] getTypeVariables(Field field) {
        TypeVariable<?>[] typeParameters = field.getType().getTypeParameters();
        String[] typeVariables = new String[typeParameters.length];

        for (int i = 0; i < typeParameters.length; i++) {
            typeVariables[i] = typeParameters[i].getName();
        }
        return typeVariables;
    }

    private Map<TypeVariable<?>, Type> getTypeMap(Class<?> declaringClass, final Type genericType) {
        if (declaringClass == null) {
            return Collections.emptyMap();
        }

        final Map<TypeVariable<?>, Type> map = new HashMap<>();
        final TypeVariable<?>[] typeVars = declaringClass.getTypeParameters();

        if (genericType instanceof ParameterizedType) {

            ParameterizedType pType = (ParameterizedType) genericType;
            Type[] typeArgs = pType.getActualTypeArguments();

//            LOG.debug("pType: {}", pType);
//            LOG.debug("actualTypeArguments: {}", Arrays.toString(typeArgs));

            for (int i = 0; i < typeArgs.length; i++) {
                TypeVariable<?> tvar = typeVars[i];
                Type actualType = typeArgs[i];

                //LOG.debug(" --> tvar: {}, actualType: {}", tvar, actualType);
                // XXX typeMap should use Type objects as keys? what about user rootTypeMap?
                //  Problem is using strings can overwrite values when string keys are the same,
                //  even though they might represent different Type objects...
                //  Need to isolate this scenario and test it.
                if (actualType instanceof TypeVariable) {

                    map.put(tvar, actualType);

                    if (getRootTypeMap().containsKey(actualType)) {
                        map.put((TypeVariable<?>) actualType, getRootTypeMap().get(actualType));
                    }
                } else if (actualType instanceof ParameterizedType) {
                    Class<?> c = (Class<?>) ((ParameterizedType) actualType).getRawType();
                    ParameterizedType nestedPType = (ParameterizedType) actualType;

                    map.put(tvar, c);

                    nestedTypes.add(new PType(c, nestedPType));
                } else if (actualType instanceof Class) {
                    map.put(tvar, actualType);
                } else {
                    throw new IllegalStateException("Unhandled type: " + actualType);
                }
            }
        } else {
            LOG.debug("No generic info for declaringClass: {}, genericType: {}", declaringClass, genericType);
        }

        return map;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FieldNode)) return false;
        FieldNode fieldNode = (FieldNode) o;
        return field.equals(fieldNode.field) && actualFieldType.equals(fieldNode.actualFieldType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(field, actualFieldType);
    }

    @Override
    public String toString() {
        String s = "";
        s += "Field: " + field.getName() + " " + field.getType().getSimpleName() + ", actual type: " + actualFieldType.getSimpleName() + "\n"
                + " -> typeVars: " + Arrays.toString(typeVariables) + "\n"
                + " -> pTypes: " + ReflectionUtils.getParameterizedTypes(field) + "\n"
                + " -> typeName: " + getTypeName() + "\n"
                + " -> typeMap: " + typeMap + "\n"
                + " -> nestedTypes: " + nestedTypes + "\n"
                + " -> classDeclaringTheTypeVariable: " + classDeclaringTheTypeVariable + "\n";

        if (getChildren() != null)
            s += " -> children: " + getChildren().stream()
                    .map(it -> it instanceof FieldNode ? ((FieldNode) it).field.getName() : "ClassNode: " + it.getKlass())
                    .collect(joining(",")) + "\n";

        return s;
    }
}
