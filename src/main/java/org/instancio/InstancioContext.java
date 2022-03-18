package org.instancio;

import org.instancio.generator.Generator;

import java.lang.reflect.Field;
import java.lang.reflect.TypeVariable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

class InstancioContext {

    private final Class<?> klass;
    private final Set<Field> exclusions;
    private final Set<Field> nullables;
    private final Map<Field, Generator<?>> userSuppliedFieldValueGenerators;
    private final Map<Class<?>, Generator<?>> userSuppliedClassValueGenerators;
    private final Map<TypeVariable<?>, Class<?>> rootTypeMap;

    public InstancioContext(
            final Class<?> klass,
            final Set<Field> exclusions,
            final Set<Field> nullables,
            final Map<Field, Generator<?>> userSuppliedFieldValueGenerators,
            final Map<Class<?>, Generator<?>> userSuppliedClassValueGenerators,
            final Map<TypeVariable<?>, Class<?>> rootTypeMap) {

        this.klass = klass;
        this.exclusions = Collections.unmodifiableSet(exclusions);
        this.nullables = Collections.unmodifiableSet(nullables);
        this.userSuppliedFieldValueGenerators = Collections.unmodifiableMap(userSuppliedFieldValueGenerators);
        this.userSuppliedClassValueGenerators = Collections.unmodifiableMap(userSuppliedClassValueGenerators);
        this.rootTypeMap = Collections.unmodifiableMap(rootTypeMap);
    }

    public Class<?> getKlass() {
        return klass;
    }

    public boolean isExcluded(final Field field) {
        return exclusions.contains(field);
    }

    public boolean isNullable(final Field field) {
        return nullables.contains(field);
    }

    public Map<Field, Generator<?>> getUserSuppliedFieldValueGenerators() {
        return userSuppliedFieldValueGenerators;
    }

    public Map<Class<?>, Generator<?>> getUserSuppliedClassValueGenerators() {
        return userSuppliedClassValueGenerators;
    }

    public Map<TypeVariable<?>, Class<?>> getRootTypeMap() {
        return rootTypeMap;
    }
}
