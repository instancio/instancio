package org.instancio;

/**
 * A model containing all the information for populating a class that
 * can be obtained using {@code Instancio.of(Example.class).toModel()}.
 * <p>
 * The model can be useful when class population needs to be customised
 * and the customisations need to be re-used in different parts of the code.
 * <p>
 * Example:
 * <pre>{@code
 *     Model personModel = Instancio.of(Person.class)
 *         .supply(field("fullName"), () -> "Jane Doe")
 *         .toModel();
 *
 *     // Re-use the model to create instances of Person class
 *     // without duplicating model's details
 *     Person person = Instancio.of(personModel).create();
 * }</pre>
 *
 * <p>
 * Since the internal data of the model is not part of the public API,
 * this interface does not contain any methods.
 *
 * @param <T> type to be created by this model
 */
public interface Model<T> {
}
