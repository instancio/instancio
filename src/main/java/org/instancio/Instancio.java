package org.instancio;

import org.instancio.internal.ClassInstancioApiImpl;
import org.instancio.internal.InstancioApiImpl;

/**
 * Instancio API for creating instances of a class.
 *
 * <h2>Usage</h2>
 *
 * <h3>Create and populate an instance of a class</h3>
 * Returns an object fully populated with random data with no {@code null} values.
 *
 * <pre>{@code
 *     Person person = Instancio.of(Person.class).create();
 * }</pre>
 *
 * <h3>Specify custom value generators for fields</h3>
 * Returns an object populated with random data except the specified fields using custom generators.
 *
 * <pre>{@code
 *     Person person = Instancio.of(Person.class)
 *             .with(field("fullName"), () -> "Homer Simpson") // Person.name
 *             .with(field(Address.class, "phoneNumber"), () -> new PhoneNumber("+1", "123-45-67"))
 *             .create();
 * }</pre>
 *
 * <h3>Allow {@code null} values to be generated</h3>
 * Default behaviour is to populate every field with a non-null, random value.
 * Specifying nullable will randomly generate either a {@code null} value or an actual value.
 *
 * <pre>{@code
 *     Person person = Instancio.of(Person.class)
 *             .withNullable(field("gender")) // Person.gender is nullable
 *             .withNullable(field(Address.class, "street")) // Address.street is nullable
 *             .withNullable(all(Date.class)) // all dates are nullable
 *             .create();
 * }</pre>
 *
 * <h3>Ignore certain fields or classes</h3>
 * Ignored field will not be populated. Ignored fields values will be {@code null}
 * (unless they have a default value).
 *
 * <pre>{@code
 *     Person person = Instancio.of(Person.class)
 *             .ignore(field("age")) // Person.age will be ignored
 *             .ignore(field(Address.class, "city")) // Address.city will be ignored
 *             .ignore(all(Date.class)) // all dates will be ignored
 *             .create();
 * }</pre>
 *
 * <h3>Creating instances of a class from a {@code Model}</h3>
 * Class specifications can also be saved as a {@code Model} using the {@code toModel()} method.
 * Then instances of a class can be generated from the model. Models can be useful for:
 *
 * <ul>
 *   <li>reducing code duplication by re-using the same model in different parts of the code</li>
 *   <li>acting as a prototype for creating specialised instances of a class
 *       by overriding model specifications</li>
 * </ul>
 *
 * <pre>{@code
 *     // Create a model
 *     Model<Person> simpsons = Instancio.of(Person.class)
 *             .with(all(Address.class), () -> new Address("742 Evergreen Terrace", "Springfield", "US"))
 *             .with(field("pets"), () -> List.of(
 *                          new Pet(PetType.CAT, "Snowball"),
 *                          new Pet(PetType.DOG, "Santa's Little Helper"))
 *             //... other specs
 *             .toModel();
 *
 *     // Use the above model as is
 *     Person person = Instancio.of(simpsons).create();
 *
 *     // Use the model but override the name field generator
 *     Person homer = Instancio.of(simpsons).with(field("name"), () -> "Homer").create();
 *     Person marge = Instancio.of(simpsons).with(field("name"), () -> "Marge").create();
 *
 *     // A model can also used to create another model.
 *     // This snippet overrides the original model to include a new pet.
 *     Model<Person> withNewPet = Instancio.of(simpsons)
 *             .with(field("pets"), () -> List.of(
 *                          new Pet(PetType.PIG, "Plopper"),
 *                          new Pet(PetType.CAT, "Snowball"),
 *                          new Pet(PetType.DOG, "Santa's Little Helper"))
 *             .toModel();
 *
 * }</pre>
 *
 * <h3>Creating generic classes</h3>
 * There are two ways to create generic class instances.
 *
 * <h4>Option 1: using {@code withTypeParameters} to specify generic type arguments</h4>
 * <pre>{@code
 *     List<Person> person = Instancio.of(List.class).withTypeParameters(Person.class).create();
 * }</pre>
 * <p>
 * This will create a list of persons however it will generate an "unchecked assignment" warning.
 *
 * <h4>Option 2: using a {@link TypeToken}</h4>
 * <pre>{@code
 *     List<Person> person = Instancio.of(new TypeToken<List<Person>>() {}).create(); // note the empty '{}' braces
 * }</pre>
 * <p>
 * This will not generate a warning, though the syntax is slightly more awkward.
 */
public class Instancio {

    private Instancio() {
        // non-instantiable
    }

    public static <T> InstancioOfClassApi<T> of(Class<T> klass) {
        return new ClassInstancioApiImpl<>(klass);
    }

    public static <T> InstancioApi<T> of(TypeTokenSupplier<T> typeToken) {
        return new InstancioApiImpl<>(typeToken);
    }

    public static <T> InstancioApi<T> of(Model<T> model) {
        return new InstancioApiImpl<>(model);
    }
}
