package org.instancio;

import org.instancio.generator.ValueGenerator;

public interface CreationApi<T> {

    /**
     * Specifies that a class should be ignored.
     * Instancio will not  assign or create values of given types.
     *
     * @return CreationApi<T>
     */
    CreationApi<T> ignore(Class<?> klass);

    /**
     * Specifies that a field should be ignored.
     * Instancio will not assign a value to the specified field(s).
     *
     * @param field to ignore
     * @return the internal
     */
    CreationApi<T> ignore(String field);

    CreationApi<T> ignore(Class<?> klass, String field);

    CreationApi<T> withNullable(Class<?> klass);

    /**
     * Specifies that the field is nullable.
     * Instancio will randomly assign null to the given field.
     *
     * @param field that can be {@code null}
     * @return
     */
    CreationApi<T> withNullable(String field);

    /**
     * Specifies that all instances of the type are nullable.
     * Instancio will randomly assign null to the given type.
     *
     * @param klass
     * @return
     */
    CreationApi<T> withNullable(Class<?> klass, String field);

    /**
     * Map interface/base class to given class. The {@code to} class
     * must be a subtype of the {@code from} class.
     * <p>
     * For example, by default Instancio will assign an {@link java.util.ArrayList}
     * to a {@link java.util.List} field. If an alternative implementation is
     * required, the {@link #map(Class, Class)} method allows to specify it:
     * <p>
     * {@code map(List.class, Vector.class)}
     *
     * @param from class to map
     * @param to   target class, must be a subtype of {@code to}
     * @return
     */
    CreationApi<T> map(Class<?> from, Class<?> to);

    /**
     * Specifies a custom generator for the given field. When a generator
     * is specified, Instancio will set the value as is and will not modify
     * the supplied instance in any way.
     * <p>
     *
     * <pre>{@code
     *   // assign the amount to an instance created by the generator
     *   // Instancio will not modify the created amount instance
     *   with("total", () -> new Amount(500, "CAD"))
     *
     *   // assign the province field to a random value from the given choices
     *   with("province", oneOf("AB", "BC", "ON")
     * }</pre>
     *
     * @param field     to bind the generator to
     * @param generator supplying the value
     * @param <V>       type of the value to create
     * @return
     */
    <V> CreationApi<T> with(String field, ValueGenerator<V> generator);

    <V> CreationApi<T> with(Class<?> klass, String field, ValueGenerator<V> generator);

    /**
     * @param klass     to bind the generator to
     * @param generator supplying the value
     * @param <V>       type of the value to create
     * @return
     */
    <V> CreationApi<T> with(Class<V> klass, ValueGenerator<V> generator);

    Model<T> toModel();

    T create();
}
