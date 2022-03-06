package org.instancio;

import org.instancio.generator.ValueGenerator;

import java.lang.reflect.Field;
import java.lang.reflect.TypeVariable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

class InstancioContext {

    private final Class<?> klass;
    private final Set<Field> exclusions;
    private final Set<Field> nullables;
    private final Map<Field, ValueGenerator<?>> userSuppliedFieldValueGenerators;
    private final Map<Class<?>, ValueGenerator<?>> userSuppliedClassValueGenerators;
    private final Map<TypeVariable<?>, Class<?>> rootTypeMap;

    public InstancioContext(
            final Class<?> klass,
            final Set<Field> exclusions,
            final Set<Field> nullables,
            final Map<Field, ValueGenerator<?>> userSuppliedFieldValueGenerators,
            final Map<Class<?>, ValueGenerator<?>> userSuppliedClassValueGenerators,
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

    public Map<Field, ValueGenerator<?>> getUserSuppliedFieldValueGenerators() {
        return userSuppliedFieldValueGenerators;
    }

    public Map<Class<?>, ValueGenerator<?>> getUserSuppliedClassValueGenerators() {
        return userSuppliedClassValueGenerators;
    }

    public Map<TypeVariable<?>, Class<?>> getRootTypeMap() {
        return rootTypeMap;
    }
}
