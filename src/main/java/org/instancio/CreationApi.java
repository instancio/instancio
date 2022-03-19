package org.instancio;

import org.instancio.generator.Generator;

/**
 * Instancio API for defining model specs and generating instances of a class.
 *
 * @param <T> type being created
 */
public interface CreationApi<T> {

    /**
     * Creates a new instance of a class and populates it with data.
     * <p>
     * Example:
     * <pre>{@code
     *     Person person = Instancio.of(Person.class).create();
     * }</pre>
     * <p>
     * The returned object will have all its fields populated with random data,
     * including collection and array fields.
     *
     * @return a fully populated object
     */
    T create();

    /**
     * Creates a model containing all the information for populating a class.
     * <p>
     * The model can be useful when class population needs to be customised
     * and the customisations need to be re-used in different parts of the code.
     * <p>
     * Example:
     * <pre>{@code
     *     Model<Person> personModel = Instancio.of(Person.class)
     *             .with("fullName", () -> "Jane Doe")
     *             .toModel();
     *
     *     // Re-use the model to create instances of Person class
     *     // without duplicating the model's details
     *     Person person = Instancio.of(personModel).create();
     * }</pre>
     * <p>
     * Since the internal data of the model is not part of the public API,
     * this interface does not contain any methods.
     *
     * @return a model containing all the details
     */
    Model<T> toModel();

    /**
     * Specifies that a class should be ignored.
     * Instancio will not assign or create values of the given type.
     * <p>
     * Example:
     * <pre>{@code
     *     Person person = Instancio.of(Person.class)
     *             .ignore(String.class)
     *             .create();
     * }</pre>
     * <p>
     * will create a fully populated person, but will ignore all string fields.
     * The value of all string fields will be either {@code null} or have a default
     * value (if they have one).
     *
     * @param klass to ignore
     * @return API builder reference
     */
    CreationApi<T> ignore(Class<?> klass);

    /**
     * Specifies that a field should be ignored.
     * Instancio will not assign or create values to the specified field.
     * <p>
     * Example:
     * <pre>{@code
     *     Person person = Instancio.of(Person.class)
     *             .ignore("isActive")
     *             .create();
     * }</pre>
     * <p>
     * will create a fully populated person with the {@code isActive} field left as is.
     * Therefore, if {@code isActive} was initialised to a default value, the returned
     * person instance will have {@code isActive} field with the default value.
     *
     * @param field to ignore
     * @return API builder reference
     */
    CreationApi<T> ignore(String field);

    /**
     * Specifies that a field of the given class should be ignored.
     * Instancio will not assign or create values to the specified field.
     * <p>
     * Example:
     * <pre>{@code
     *     Person person = Instancio.of(Person.class)
     *             .ignore(Address.class, "unit")
     *             .ignore(Address.class, "street")
     *             .create();
     * }</pre>
     * <p>
     * will create a fully populated person but {@code unit} and {@code street}
     * of all Address instances will be ignored.
     *
     * @param klass declaring the field
     * @param field to ignore
     * @return API builder reference
     */
    CreationApi<T> ignore(Class<?> klass, String field);

    /**
     * Specifies that fields of the given type can be assigned a {@code null} value.
     * By default, Instancio assigns non-null values. If marked as nullable,
     * Instancio will randomly assign either a null or non-null value to fields of this type.
     * <p>
     * Example:
     * <pre>{@code
     *     Person person = Instancio.of(Person.class)
     *             .withNullable(String.class)
     *             .create();
     * }</pre>
     *
     * @param klass that is nullable
     * @return API builder reference
     * @see #withNullable(String)
     * @see #withNullable(Class, String)
     */
    CreationApi<T> withNullable(Class<?> klass);

    /**
     * Specifies that the field can be assigned a {@code null} value.
     * By default, Instancio assigns non-null values. If marked as nullable,
     * Instancio will randomly assign either a null or non-null value to the field.
     * <p>
     * Example:
     * <pre>{@code
     *     Person person = Instancio.of(Person.class)
     *         .withNullable("gender")
     *         .create();
     * }</pre>
     *
     * @param field that is nullable
     * @return API builder reference
     * @see #withNullable(Class)
     * @see #withNullable(Class, String)
     */
    CreationApi<T> withNullable(String field);

    /**
     * Specifies that a field of the given class can be assigned a {@code null} value.
     * By default, Instancio assigns non-null values. If marked as nullable,
     * Instancio will randomly assign either a null or non-null value to the field.
     * <p>
     * Example:
     * <pre>{@code
     *     Person person = Instancio.of(Person.class)
     *             .withNullable(Address.class, "postalCode")
     *             .create();
     * }</pre>
     *
     * @param klass declaring the field
     * @param field that is nullable
     * @return API builder reference
     * @see #withNullable(String)
     * @see #withNullable(Class)
     */
    CreationApi<T> withNullable(Class<?> klass, String field);

    /**
     * Maps an interface or base class to the given subclass.
     * The {@code subClass} must either extend or implement the {@code baseClass}.
     * <p>
     * For example, by default Instancio will assign an {@link java.util.ArrayList}
     * to a {@link java.util.List} field. If an alternative implementation is
     * required, this method allows to specify it:
     * <p>
     * <pre>{@code
     *     Person person = Instancio.of(Person.class)
     *             .map(List.class, Vector.class)
     *             .create();
     * }</pre>
     * <p>
     * will assign all {@code List} fields instances of {@code Vector}.
     *
     * @param baseClass interface or base class
     * @param subClass  subtype of the {@code baseClass}
     * @return API builder reference
     */
    CreationApi<T> map(Class<?> baseClass, Class<?> subClass);

    /**
     * Specifies a custom generator for the given field.
     * <p>
     * Example:
     * <pre>{@code
     *     Person person = Instancio.of(Person.class)
     *             .with("address", () -> new Address("123 Main St", "Springfield", "US"))
     *             .create();
     * }</pre>
     * <p>
     * will create a new instance of the address and assign it to the address field.
     * <p>
     * When a custom generated is supplied, Instancio will not modify the created instance
     * any in way (if the {@code Address} class has other fields, they will not be populated).
     *
     * @param field     to bind the generator to
     * @param generator supplying a value for the field
     * @param <V>       type of the value to create
     * @return API builder reference
     * @see #with(Class, String, Generator)
     * @see #with(Class, Generator)
     */
    <V> CreationApi<T> with(String field, Generator<V> generator);

    /**
     * Specifies a custom generator for the field of the given class.
     * <p>
     * Example:
     * <pre>{@code
     *     Person person = Instancio.of(Person.class)
     *             .with(Address.class, "phoneNumbers", () -> List.of(
     *                 new PhoneNumber("+1", "123-45-67"),
     *                 new PhoneNumber("+1", "345-67-89")))
     *             .create();
     * }</pre>
     * <p>
     * will create a new instance of a list containing the phone numbers and assign it to the
     * {@code phoneNumbers} field of the {@code Address} class.
     * <p>
     * When a custom generated is supplied, Instancio will not modify the created instance
     * any in way (if the {@code PhoneNumber} class has other fields, they will not be populated).
     *
     * @param klass     declaring the field
     * @param field     to bind the generator to
     * @param generator supplying a value for the field
     * @param <V>       type of the value to create
     * @return API builder reference
     * @see #with(String, Generator)
     * @see #with(Class, Generator)
     */
    <V> CreationApi<T> with(Class<?> klass, String field, Generator<V> generator);

    /**
     * Specifies a custom generator for the given type.
     * <p>
     * Example:
     * <pre>{@code
     *     Person person = Instancio.of(Person.class)
     *             .with(PhoneNumber.class, () -> new PhoneNumber("+1", "123-45-67"))
     *             .create();
     * }</pre>
     * <p>
     * will create a new instance of {@code LocalDateTime} for each
     * {@code LocalDateTime} field.
     * <p>
     * When a custom generated is supplied, Instancio will not modify the created instance
     * any in way (if the {@code PhoneNumber} class has other fields, they will not be populated).
     *
     * @param klass     to bind the generator to
     * @param generator supplying the value
     * @param <V>       type of the value to create
     * @return API builder reference
     * @see #with(String, Generator)
     * @see #with(Class, String, Generator)
     */
    <V> CreationApi<T> with(Class<V> klass, Generator<V> generator);

}
