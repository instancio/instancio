package org.instancio;

import java.util.function.Function;

/**
 * Instancio API for generating instances of a class populated with random data.
 *
 * @param <T> type being created
 */
public interface InstancioApi<T> {

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
     *             .supply(field("fullName"), () -> "Jane Doe")
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
     * Specifies that a class or field should be ignored.
     * <p>
     * Example:
     * <pre>{@code
     *     Person person = Instancio.of(Person.class)
     *             .ignore(field("pets"))  // Person.pets field
     *             .ignore(field(Address.class, "phoneNumbers")) // Address.phoneNumbers field
     *             .ignore(allStrings())
     *             .create();
     * }</pre>
     * <p>
     * will create a fully populated person, but will ignore the {@code address} field
     * and all string fields.
     *
     * @param target to ignore
     * @return API builder reference
     */
    InstancioApi<T> ignore(Binding target);

    /**
     * Specifies that a field or class is nullable. By default, Instancio assigns
     * non-null values. If marked as nullable, Instancio will randomly assign either
     * a null or non-null value to fields of this type.
     * <p>
     * Example:
     * <pre>{@code
     *     Person person = Instancio.of(Person.class)
     *             .withNullable(allStrings())
     *             .withNullable(field(Address.class))
     *             .create();
     * }</pre>
     *
     * @param target that is nullable
     * @return API builder reference
     */
    InstancioApi<T> withNullable(Binding target);

    /**
     * Supplies a value for a field or class using a custom generator.
     *
     * <p>
     * Examples.
     * <pre>{@code
     *     Person person = Instancio.of(Person.class)
     *             .supply(all(LocalDateTime.class), () -> LocalDateTime.now()) // set all dates to current time
     *             .supply(field("fullName"), () -> "Homer Simpson") // set Person.fullName
     *             .supply(field(Address.class, "phoneNumbers"), () -> List.of( // set Address.phoneNumbers
     *                 new PhoneNumber("+1", "123-45-67"),
     *                 new PhoneNumber("+1", "345-67-89")))
     *             .create();
     * }</pre>
     * <p>
     * Note: when a custom generator is supplied for a complex type like {@code PhoneNumber} in the above
     * example, Instancio will not modify the created instance in any way. If the {@code PhoneNumber} class
     * has other fields, they will be ignored.
     *
     * @param target    class or field
     * @param generator for supplying the target's value
     * @param <V>       type of the value to generate
     * @return API builder reference
     */
    <V> InstancioApi<T> supply(Binding target, Generator<V> generator);

    /**
     * Supplies a random value for a field or class using a built-in generator.
     * <p>
     * Example:
     * <pre>{@code
     *     Person person = Instancio.of(Person.class)
     *             .generate(field("age"), gen -> gen.ints().min(18).max(100))
     *             .generate(field("name"), gen -> gen.string().min(5).allowEmpty())
     *             .generate(field(Address.class, "phoneNumbers"), gen -> gen.collection().minSize(5))
     *             .generate(field(Address.class, "city"), gen -> gen.oneOf("Burnaby", "Vancouver", "Richmond"))
     *             .create();
     * }</pre>
     *
     * @param target class or field
     * @param gen    provider of built-in generators
     * @param <V>    type of the value to generate
     * @param <S>    generator spec type
     * @return API builder reference
     */
    <V, S extends GeneratorSpec<V>> InstancioApi<T> generate(Binding target, Function<Generators, S> gen);

    /**
     * Maps an interface or base class to the given subclass.
     * The {@code subClass} must either extend or implement the {@code baseClass}.
     * <p>
     * For example, by default Instancio will assign an {@link java.util.ArrayList}
     * to a {@link java.util.List} field. If an alternative implementation is
     * required, this method allows to specify it:
     *
     * <pre>{@code
     *     Person person = Instancio.of(Person.class)
     *             .map(List.class, Vector.class)
     *             .create();
     * }</pre>
     * <p>
     * will assign all {@code List}s to {@code Vector}s.
     *
     * @param baseClass interface or base class
     * @param subClass  subtype of the {@code baseClass}
     * @return API builder reference
     */
    InstancioApi<T> map(Class<?> baseClass, Class<?> subClass);

    /**
     * Set the seed value for the random number generator. If seed is not specified,
     * a random seed will be used. Specifying a seed is useful for reproducing test results.
     * By specifying the seed value, the same random data will be generated again.
     *
     * <p>
     * Example:
     * <pre>{@code
     *     // Generates a different UUID each time
     *     UUID result = Instancio.of(UUID.class).create();
     *
     *     // Generates the same UUID
     *     UUID result = Instancio.of(UUID.class)
     *             .withSeed(1234)
     *             .create();
     * }</pre>
     *
     * @param seed for the random number generator
     * @return API builder reference
     */
    InstancioApi<T> withSeed(int seed);
}
