package org.instancio.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Collects declared, non-static fields.
 */
public class DeclaredFieldsCollector implements FieldCollector {

    @Override
    public List<Field> getFields(Class<?> klass) {
        final List<Field> collected = new ArrayList<>();

        for (Field field : klass.getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers())) {
                collected.add(field);
            }
        }
        return collected;
    }
}
