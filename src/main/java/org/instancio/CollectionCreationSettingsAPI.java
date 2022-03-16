package org.instancio;

import org.instancio.generator.ValueGenerator;

import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollectionCreationSettingsAPI<T, C extends CreationSettingsAPI<T, C>>
        extends AbstractCreationSettingsAPI<T, C> {

    CollectionCreationSettingsAPI(Class<T> klass) {
        super(klass);
    }

    @Override
    public CollectionCreationSettingsAPI<T, C> ignore(String... fields) {
        super.ignore(fields);
        return this;
    }

    @Override
    public <V> CollectionCreationSettingsAPI<T, C> with(String field, ValueGenerator<V> generator) {
        super.with(field, generator);
        return this;
    }

    @Override
    public <V> CollectionCreationSettingsAPI<T, C> with(Class<V> klass, ValueGenerator<V> generator) {
        super.with(klass, generator);
        return this;
    }

    @Override
    public CollectionCreationSettingsAPI<T, C> withNullable(String fieldName) {
        super.withNullable(fieldName);
        return this;
    }

    @Override
    public CollectionCreationSettingsAPI<T, C> withType(Class<?>... types) {
        super.withType(types);
        return this;
    }

    public List<T> create() {
        Map<TypeVariable<?>, Class<?>> rootTypeMap = new HashMap<>(); // TODO - populate

        InstancioContext context = new InstancioContext(
                klass, ignored, nullables, fieldValueGenerators, classValueGenerators, rootTypeMap);
        InstancioDriver driver = new InstancioDriver(context);
        return driver.createList(klass);
    }

}
