package org.instancio;

import org.instancio.generator.ValueGenerator;

/**
 *
 * @param <T> object type being created
 * @param <C> creation settings type
 */
// TODO cleanup/simplify
public interface CreationSettingsAPI<T, C extends CreationSettingsAPI<T, C>> {

  CreationSettingsAPI<T, C> ignore(String... fields);

  <V> CreationSettingsAPI<T, C> with(String field, ValueGenerator<V> generator);

  <V> CreationSettingsAPI<T, C> with(Class<V> klass, ValueGenerator<V> generator);

  CreationSettingsAPI<T, C> withNullable(String field);

  // TODO support for allowing null values by Class (what about subtypes of given class)?
  // CreationSettingsAPI<T, C> withNullable(Class<?> klass);

  CreationSettingsAPI<T, C> withType(Class<?>... types);
}
