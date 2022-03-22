package org.instancio.util;

import org.instancio.exception.InstancioApiException;
import org.instancio.exception.InstancioException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class ReflectionUtils {

    private ReflectionUtils() {
        // non-instantiable
    }

    @SuppressWarnings("java:S3011")
    public static void setField(Object target, Field field, Object value) {
        if (target == null) {
            return;
        }
        try {
            field.setAccessible(true);
            field.set(target, value);
        } catch (IllegalAccessException ex) {
            throw new InstancioException("Could not set value to the field: " + field, ex);
        }
    }

    public static Field getField(final Class<?> klass, final String fieldPath) {
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
            throw new InstancioApiException("Invalid field '" + fieldPath + "' for " + klass, ex);
        }
    }

    @SuppressWarnings("java:S3011")
    public static <T> T instantiate(Class<T> klass) {
        try {
            final Constructor<?>[] constructors = klass.getConstructors();

            for (Constructor<?> c : constructors) {
                if (c.getParameterCount() == 0) {
                    c.setAccessible(true);
                    return (T) c.newInstance();
                }
            }

        } catch (Exception ex) {
            throw new InstancioException(String.format("Failed instantiating class '%s'", klass.getName()), ex);
        }
        throw new InstancioException(String.format("'%s' has no default constructor", klass.getName()));
    }

}

