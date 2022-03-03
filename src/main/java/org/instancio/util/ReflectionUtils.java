package org.instancio.util;

import experimental.reflection.nodes.GenericType;
import org.instancio.Pair;
import org.instancio.exception.InstancioException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ReflectionUtils {
    private static final Logger LOG = LoggerFactory.getLogger(ReflectionUtils.class);

    private ReflectionUtils() {
    }

    public static List<Field> getDeclaredAndSuperclassFields(Class<?> klass) {
        List<Field> results = new ArrayList<>();

        while (klass != Object.class) {
            results.addAll(Arrays.asList(klass.getDeclaredFields()));
            klass = klass.getSuperclass();
        }
        return results;
    }

    public static Optional<Class<?>> getCollectionType(Field collectionField) {

        System.out.println(collectionField);

        final Type type = collectionField.getGenericType();
        if (type instanceof ParameterizedType) {
            final ParameterizedType pType = (ParameterizedType) type;
            // Type: { ParameterizedType, TypeVariable, GenericArrayType, WildCardType
            final Type[] typeArgs = pType.getActualTypeArguments();
            LOG.debug("typeArgs: {}", Arrays.toString(typeArgs));

            if (typeArgs[0] instanceof ParameterizedType) {
                final ParameterizedType nestedType = (ParameterizedType) typeArgs[0];
                final Type[] nestedTypeArgs = nestedType.getActualTypeArguments();


                return Optional.of((Class<?>) nestedTypeArgs[0]);
            }

            return Optional.of((Class<?>) typeArgs[0]);
        }
        return Optional.empty();
    }

    public static Deque<Class<?>> getParameterizedTypes(final Field field) {
        final Type type = field.getGenericType();
        return getParameterizedTypes(type);
    }

    public static Deque<Class<?>> getParameterizedTypes(final Class<?> klass, final String field) {
        final Field f = getField(klass, field);

        final Deque<Class<?>> parameterizedTypes = getParameterizedTypes(f.getGenericType());

        LOG.debug("---> klass {}", klass.getCanonicalName());

        LOG.debug("---> Field '{}' <{}>, type: {}, genSuperClass: {}",
                f.getName(), f.getGenericType(), f.getType(), f.getType().getGenericSuperclass());

        LOG.debug("---> Field '{}' pTypes: {}", f.getName(), parameterizedTypes);

        return parameterizedTypes;
    }

    public static Deque<Class<?>> getParameterizedTypes(final Type type) {
        final Deque<Class<?>> results = new ArrayDeque<>();

        for (Type next = type; next instanceof ParameterizedType; ) {
            final ParameterizedType pType = (ParameterizedType) next;
            //LOG.debug("pType: {}", pType.getTypeName());
            final Type[] typeArgs = pType.getActualTypeArguments();
            results.add((Class<?>) pType.getRawType());

            if ((typeArgs[0] instanceof TypeVariable)) {
                break;
            }
            if (!(typeArgs[0] instanceof ParameterizedType)) {
                results.add((Class<?>) typeArgs[0]);
            }

            next = typeArgs[0];
        }

        //LOG.debug("Parameterized types for '{}': {}", type, results);
        if (results.size() > 1) // NOTE checking not empty fails some test cases
            results.pollFirst(); // XXX hack...

        return results;
    }

    public static Map<GenericType, Class<?>> getTypeMap(final Field field) {
        if (field.getType().getTypeParameters().length == 0) {
            return Collections.emptyMap();
        }

        return getTypeMap(field.getType(), field.getGenericType());
    }

    public static Map<GenericType, Class<?>> getTypeMap(Class<?> declaringClass, final Type genericType) {

        LOG.debug("\n\n---- getTypeMap for\ndeclaringClass: {}\ngenericType: {}\n", declaringClass.getSimpleName(), genericType);
        final Map<GenericType, Class<?>> map = new HashMap<>();
        final TypeVariable<?>[] typeVars = declaringClass.getTypeParameters();

        if (genericType instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) genericType;
            Type[] typeArgs = pType.getActualTypeArguments();

            if (typeVars.length != typeArgs.length) {
                throw new RuntimeException("typeVars.length = " + typeVars.length + ", typeArgs.length = " + typeArgs.length
                        + "\n typeVars = " + Arrays.toString(typeVars)
                        + "\n typeArgs = " + Arrays.toString(typeArgs)
                );
            }

            LOG.debug("pType: {}", pType);
            LOG.debug("actualTypeArguments: {}", Arrays.toString(typeArgs));

            for (int i = 0; i < typeArgs.length; i++) {
                TypeVariable<?> tvar = typeVars[i];
                Type actualType = typeArgs[i];

                LOG.debug(" --> tvar: {}, actualType: {}", tvar, actualType);

                if (actualType instanceof TypeVariable) {
                    map.put(GenericType.with(declaringClass, tvar.getName()), null);
                } else if (actualType instanceof ParameterizedType) {
                    Class<?> c = (Class<?>) ((ParameterizedType) actualType).getRawType();
                    ParameterizedType nestedPType = (ParameterizedType) actualType;

                    map.put(GenericType.with(declaringClass, tvar.getName()), c);

                    map.putAll(getTypeMap(c, nestedPType));
                } else if (actualType instanceof Class) {
                    map.put(GenericType.with(declaringClass, tvar.getName()), (Class<?>) actualType);
                } else {
                    throw new RuntimeException("Handle type: " + actualType);
                }
            }
        } else {
            throw new UnsupportedOperationException("Else?");
        }

        return map;
    }

    public static Optional<Pair<Class<?>, Class<?>>> getMapType(Field mapField) {

        if (mapField.getGenericType() instanceof ParameterizedType) {
            final ParameterizedType pType = (ParameterizedType) mapField.getGenericType();
            // Type: { ParameterizedType, TypeVariable, GenericArrayType, WildCardType
            final Type[] typeArgs = pType.getActualTypeArguments();
            return Optional.of(Pair.of((Class<?>) typeArgs[0], (Class<?>) typeArgs[1]));
        }
        return Optional.empty();
    }

    public static void setField(Object target, Field field, Object value) {
        try {
            field.setAccessible(true);
            field.set(target, value);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex); // TODO
        }
    }

    public static Field getField(final Class<?> klass, final String fieldPath) throws RuntimeException {
        try {
            final String[] pathItems = fieldPath.split("\\.");
            Class<?> currentClass = klass;
            Field result = null;

            for (String pathItem : pathItems) {
                Field field = currentClass.getDeclaredField(pathItem);
                currentClass = field.getType();
                result = field;
            }

            return result;
        } catch (Exception ex) {
            throw new RuntimeException("Invalid field '" + fieldPath + "' for " + klass, ex);
        }
    }

    public static <T> T instantiate(Class<T> klass) {
        try {
            final Constructor<?>[] constructors = klass.getConstructors();

            for (Constructor<?> c : constructors) {
                if (c.getParameterCount() == 0) {
                    return (T) c.newInstance();
                }
            }

        } catch (Exception ex) {
            throw new InstancioException(String.format("Failed instantiating class '%s'", klass.getName()), ex);
        }
        throw new InstancioException(String.format("'%s' has no default constructor", klass.getName()));
    }

}

