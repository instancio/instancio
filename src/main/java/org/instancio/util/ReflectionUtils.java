package org.instancio.util;

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
import java.util.Deque;
import java.util.List;
import java.util.Optional;

public class ReflectionUtils {
    private static final Logger LOG = LoggerFactory.getLogger(ReflectionUtils.class);

    private ReflectionUtils() {
        // non-instantiable
    }

    public static List<Field> getDeclaredAndSuperclassFields(Class<?> klass) {
        List<Field> results = new ArrayList<>();

        while (klass != Object.class) {
            results.addAll(Arrays.asList(klass.getDeclaredFields()));
            klass = klass.getSuperclass();
        }
        return results;
    }

    public static Deque<Class<?>> getParameterizedTypes(final Field field) {
        final Type type = field.getGenericType();
        return getParameterizedTypes(type);
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

