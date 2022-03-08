package org.instancio.util;

import org.instancio.exception.InstancioException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class ReflectionUtils {
    private static final Logger LOG = LoggerFactory.getLogger(ReflectionUtils.class);

    private ReflectionUtils() {
        // non-instantiable
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

