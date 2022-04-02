/*
 * Copyright 2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
 *             .supply(field("fullName"), () -> "Homer Simpson") // Person.name
 *             .supply(field(Address.class, "phoneNumber"), () -> new PhoneNumber("+1", "123-45-67"))
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
 *             .supply(all(Address.class), () -> new Address("742 Evergreen Terrace", "Springfield", "US"))
 *             .supply(field("pets"), () -> List.of(
 *                          new Pet(PetType.CAT, "Snowball"),
 *                          new Pet(PetType.DOG, "Santa's Little Helper"))
 *             //... other specs
 *             .toModel();
 *
 *     // Use the above model as is
 *     Person person = Instancio.of(simpsons).create();
 *
 *     // Use the model but override the name field generator
 *     Person homer = Instancio.of(simpsons).supply(field("name"), () -> "Homer").create();
 *     Person marge = Instancio.of(simpsons).supply(field("name"), () -> "Marge").create();
 *
 *     // A model can also used to create another model.
 *     // This snippet overrides the original model to include a new pet.
 *     Model<Person> withNewPet = Instancio.of(simpsons)
 *             .supply(field("pets"), () -> List.of(
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

    /**
     * Creates a fully-populated instance of given class.
     *
     * @param klass to create
     * @param <T>   type
     * @return a fully-populated instance
     */
    public static <T> T create(final Class<T> klass) {
        return of(klass).create();
    }

    /**
     * Creates a fully-populated instance of type specified in the type token.
     * This method can be used to create generic classes.
     * <p>
     * Example: {@code List<Person> persons = Instancio.of(new TypeToken<List<Person>>(){}).create()}
     *
     * @param typeToken containing type to create
     * @param <T>       type
     * @return a fully-populated instance
     */
    public static <T> T create(final TypeTokenSupplier<T> typeToken) {
        return of(typeToken).create();
    }

    /**
     * Creates a populated instance of a class represented by the given model.
     * <p>
     * See the {@link Model} class on how to create models.
     *
     * @param model specifying generation parameters of the object to create
     * @param <T>   type
     * @return a populated instance
     * @see Model
     */
    public static <T> T create(final Model<T> model) {
        return of(model).create();
    }

    /**
     * Builder version of {@link #create(Class)} that allows customisation of generated values.
     *
     * <pre>{@code
     *     Person person = Instancio.of(Person.class)
     *         .generate(allInts(), gen -> gen.ints().min(1).max(99))
     *         .supply(all(Address.class), () -> new Address("742 Evergreen Terrace", "Springfield", "US"))
     *         .supply(field("pets"), () -> List.of(
     *                             new Pet(PetType.CAT, "Snowball"),
     *                             new Pet(PetType.DOG, "Santa's Little Helper")))
     *         .create();
     * }</pre>
     *
     * @param klass to create
     * @param <T>   type
     * @return API builder reference
     */
    public static <T> InstancioOfClassApi<T> of(final Class<T> klass) {
        return new ClassInstancioApiImpl<>(klass);
    }

    /**
     * Builder version of {@link #create(TypeTokenSupplier)} that allows customisation of generated values.
     *
     * <pre>{@code
     *     List<Person> persons = Instancio.of(new TypeToken<List<Person>>(){})
     *         .generate(allInts(), gen -> gen.ints().min(1).max(99))
     *         .supply(all(Address.class), () -> new Address("742 Evergreen Terrace", "Springfield", "US"))
     *         .supply(field("pets"), () -> List.of(
     *                             new Pet(PetType.CAT, "Snowball"),
     *                             new Pet(PetType.DOG, "Santa's Little Helper")))
     *         .create();
     * }</pre>
     *
     * @param typeToken specifying details of type being created
     * @param <T>       type
     * @return API builder reference
     */
    public static <T> InstancioApi<T> of(final TypeTokenSupplier<T> typeToken) {
        return new InstancioApiImpl<>(typeToken);
    }

    /**
     * Builder version of {@link #create(Model)} that allows overriding of generation
     * parameters of an existing model.
     *
     * <pre>{@code
     *     Model<Person> personModel = Instancio.of(Person.class)
     *         .generate(allInts(), gen -> gen.ints().min(1).max(99))
     *         .supply(all(Address.class), () -> new Address("742 Evergreen Terrace", "Springfield", "US"))
     *         .supply(field("pets"), () -> List.of(
     *                             new Pet(PetType.CAT, "Snowball"),
     *                             new Pet(PetType.DOG, "Santa's Little Helper")))
     *         .toModel();
     *
     *     // Use the existing model and add/override generation parameters
     *     Person simpsonKid = Instancio.of(personModel)
     *         .generate(field("fullName"), gen -> gen.oneOf("Lisa Simpson", "Bart Simpson"))
     *         .create();
     * }</pre>
     *
     * @param model specifying generation parameters of the object to create
     * @param <T>   type
     * @return API builder reference
     */
    public static <T> InstancioApi<T> of(final Model<T> model) {
        return new InstancioApiImpl<>(model);
    }
}
