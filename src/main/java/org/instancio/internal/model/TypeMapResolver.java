package org.instancio.internal.model;

import org.instancio.util.ObjectUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Helper class for mapping type variables to actual type arguments.
 */
public class TypeMapResolver {

    private final Map<Type, Type> typeMap = new HashMap<>();
    private final Map<TypeVariable<?>, Class<?>> rootTypeMap;
    private final Type genericType;

    public TypeMapResolver(final Map<TypeVariable<?>, Class<?>> rootTypeMap,
                           final Type genericType) {

        this.rootTypeMap = rootTypeMap;
        this.genericType = genericType;
        initTypeMap();
    }

    // TODO hide
    public Map<Type, Type> getTypeMap() {
        return typeMap;
    }

    public Type getActualType(Type t) {
        return typeMap.get(t);
    }

    public int size() {
        return typeMap.size();
    }

    /**
     * Some possible field declarations and their generic types:
     *
     * <pre>{@code
     *   Type                | Generic type class
     *   --------------------+-------------------
     *   int                 | Class
     *   Integer             | Class
     *   Item<Integer>       | ParameterizedType
     *   Item<T>             | ParameterizedType
     *   T                   | TypeVariable
     *   Item<?>             | WildcardType
     *   Item<? extends Foo> | WildcardType
     * }</pre>
     */
    private void initTypeMap() {
        if (genericType instanceof Class) {
//            final TypeVariable<?>[] typeVariables = ((Class<?>) genericType).getTypeParameters();
//            for (TypeVariable<?> typeVar : typeVariables) {
//                if (!typeMap.containsKey(typeVar))
//                    typeMap.put(typeVar, rootTypeMap.get(typeVar));
//            }

            return; // non-generic class; nothing to resolve
        }

        if (genericType instanceof TypeVariable && rootTypeMap.containsKey(genericType)) {
            final Class<?> mappedType = rootTypeMap.get(genericType);
            typeMap.put(genericType, mappedType);
            return;
        }

        if (genericType instanceof ParameterizedType) {
            final ParameterizedType pType = (ParameterizedType) genericType;
            final Class<?> rawType = (Class<?>) pType.getRawType();
            final TypeVariable<?>[] typeVars = rawType.getTypeParameters();
            final Type[] typeArgs = pType.getActualTypeArguments();

            for (int i = 0; i < typeArgs.length; i++) {
                final Type mappedType = resolveTypeMapping(typeArgs[i]);
                // Mapped type can be null when a type variable isn't in the root type map.
                // In this case with map type variable to type variable
                typeMap.put(typeVars[i], ObjectUtils.defaultIfNull(mappedType, typeArgs[i]));
            }
        }
    }

    private Type resolveTypeMapping(final Type typeArg) {
        if (typeArg instanceof TypeVariable) {
            return rootTypeMap.get(typeArg);
        } else if (typeArg instanceof ParameterizedType) {
            return typeArg;
        } else if (typeArg instanceof Class) {
            return typeArg;
        } else if (typeArg instanceof WildcardType) {
            WildcardType wType = (WildcardType) typeArg;
            return resolveTypeMapping(wType.getUpperBounds()[0]); // TODO multiple bounds
        }
        throw new UnsupportedOperationException("Unsupported type: " + typeArg.getClass());
    }

    @Override
    public String toString() {
        return new StringJoiner("\n - ", TypeMapResolver.class.getSimpleName() + "[", "]")
                .add("typeMap=" + typeMap)
                .add("rootTypeMap=" + rootTypeMap)
                .add("genericType=" + genericType)
                .toString();
    }
}
