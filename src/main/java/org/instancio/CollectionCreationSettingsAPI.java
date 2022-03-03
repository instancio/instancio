package org.instancio;

import org.instancio.generator.ValueGenerator;

import java.util.List;

public class CollectionCreationSettingsAPI<C, T extends CreationSettingsAPI<C, T>>
        extends AbstractCreationSettingsAPI<C, T> {

    CollectionCreationSettingsAPI(Class<C> klass) {
        super(klass);
    }

    @Override
    public CollectionCreationSettingsAPI<C, T> exclude(String... fields) {
        super.exclude(fields);
        return this;
    }

    @Override
    public <V> CollectionCreationSettingsAPI<C, T> with(String field, ValueGenerator<V> generator) {
        super.with(field, generator);
        return this;
    }

    @Override
    public <V> CollectionCreationSettingsAPI<C, T> with(Class<V> klass, ValueGenerator<V> generator) {
        super.with(klass, generator);
        return this;
    }

    @Override
    public CollectionCreationSettingsAPI<C, T> withNullable(String field) {
        super.withNullable(field);
        return this;
    }

    @Override
    public CollectionCreationSettingsAPI<C, T> withType(Class<?>... types) {
        super.withType(types);
        return this;
    }

    public List<C> create() {
        InstancioContext context = new InstancioContext(
                klass, exclusions, nullables, fieldValueGenerators, classValueGenerators, genericTypes);
        InstancioDriver driver = new InstancioDriver(context);
        return driver.createList(klass);
    }

}
