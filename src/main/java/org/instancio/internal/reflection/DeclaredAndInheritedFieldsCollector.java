package org.instancio.internal.reflection;

import org.instancio.util.Verify;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Collects declared and super class fields, excluding static fields.
 */
public class DeclaredAndInheritedFieldsCollector implements FieldCollector {

    @Override
    public List<Field> getFields(final Class<?> klass) {
        Class<?> next = Verify.notNull(klass, "Class is null");

        final List<Field> collected = new ArrayList<>();
        while (shouldCollectFrom(next)) {
            for (Field field : next.getDeclaredFields()) {
                if (!Modifier.isStatic(field.getModifiers())) {
                    collected.add(field);
                }
            }
            next = next.getSuperclass();
        }

        return collected;
    }

    private boolean shouldCollectFrom(Class<?> next) {
        if (next == null || next.isInterface() || next.isArray() || next == Object.class) {
            return false;
        }

        final Package pkg = next.getPackage();
        return pkg != null && !pkg.getName().startsWith("java");
    }
}
