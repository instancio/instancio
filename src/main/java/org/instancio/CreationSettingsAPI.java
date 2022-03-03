package org.instancio;

import org.instancio.generator.ValueGenerator;

// What is C?
public interface CreationSettingsAPI<C, T extends CreationSettingsAPI<C, T>> {

    CreationSettingsAPI<C, T> exclude(String... fields);

    <V> CreationSettingsAPI<C, T> with(String field, ValueGenerator<V> generator);

    <V> CreationSettingsAPI<C, T> with(Class<V> klass, ValueGenerator<V> generator);

    CreationSettingsAPI<C, T> withNullable(String field);

    CreationSettingsAPI<C, T> withType(Class<?>... types);
}
