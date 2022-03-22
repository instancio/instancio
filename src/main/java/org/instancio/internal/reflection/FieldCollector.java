package org.instancio.internal.reflection;

import java.lang.reflect.Field;
import java.util.List;

/**
 * An interface for collecting fields from classes.
 */
public interface FieldCollector {

    List<Field> getFields(Class<?> klass);
}
