/*
 * Copyright 2022-2023 the original author or authors.
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

import org.instancio.internal.InstancioApiImpl;
import org.instancio.internal.InstancioOfClassApiImpl;
import org.instancio.internal.InstancioOfCollectionApiImpl;
import org.instancio.internal.InstancioOfMapApiImpl;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Instancio API for creating instances of a class.
 *
 * <h2>Usage</h2>
 *
 * <h3>Create and populate an instance of a class</h3>
 * Returns an object fully populated with random data with non-null values.
 *
 * <pre>{@code
 *     Person person = Instancio.create(Person.class);
 * }</pre>
 *
 * <h3>Customise object's values</h3>
 * Returns an object populated with random data and some specified fields' values customised.
 *
 * <pre>{@code
 *     Person person = Instancio.of(Person.class)
 *             .set(field("fullName"), "Homer Simpson") // Person.fullName
 *             .supply(all(LocalDateTime.class), () -> LocalDateTime.now())
 *             .generate(field(Phone.class, "number"), gen -> gen.text().pattern("(#d#d#d) #d#d#d-#d#d#d#d"))
 *             .create();
 * }</pre>
 *
 * <h3>Allow {@code null} values to be generated</h3>
 * Default behaviour is to populate every field with a non-null, random value.
 * Specifying nullable will randomly generate either {@code null} or an actual value.
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
 * Ignored fields will not be populated. Their values will be {@code null}
 * (unless they have a default value assigned).
 *
 * <pre>{@code
 *     Person person = Instancio.of(Person.class)
 *             .ignore(field("age"))
 *             .ignore(field(Address.class, "city"))
 *             .ignore(all(Date.class))
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
 *     // Use the model but override the name
 *     Person homer = Instancio.of(simpsons).set(field("name"), "Homer").create();
 *     Person marge = Instancio.of(simpsons).set(field("name"), "Marge").create();
 *
 *     // A model can also used to create another model.
 *     // This snippet creates a new model from the original model to include a new pet.
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
 * <h4>Option 1: using a {@link TypeToken}</h4>
 * <pre>{@code
 *     List<Person> person = Instancio.create(new TypeToken<List<Person>>() {}); // note the empty '{}' braces
 * }</pre>
 *
 * <h4>Option 2: using {@code withTypeParameters} to specify generic type arguments</h4>
 * <pre>{@code
 *     List<Person> person = Instancio.of(List.class).withTypeParameters(Person.class).create();
 * }</pre>
 * <p>
 * Note: the second approach will produce an "unchecked assignment" warning.
 *
 * @see InstancioApi
 * @see Select
 * @since 1.0.1
 */
public final class Instancio {

    private Instancio() {
        // non-instantiable
    }

    /**
     * Creates a fully-populated instance of given class.
     *
     * @param type to create
     * @param <T>  type
     * @return a fully-populated instance
     * @since 1.0.1
     */
    public static <T> T create(final Class<T> type) {
        return of(type).create();
    }

    /**
     * Creates an infinite stream of distinct, fully populated instances of given class.
     * <p>
     * Example:
     * <pre>{@code
     *     List<Person> persons = Instancio.stream(Person.class)
     *         .limit(5)
     *         .collect(Collectors.toList());
     * }</pre>
     *
     * @param type to create
     * @param <T>  type
     * @return an infinite stream of distinct, fully populated instances
     * @since 1.1.9
     */
    public static <T> Stream<T> stream(final Class<T> type) {
        return of(type).stream();
    }

    /**
     * Creates a fully-populated instance of type specified in the type token.
     * This method can be used to create generic classes.
     * <p>
     * Example: {@code List<Person> persons = Instancio.of(new TypeToken<List<Person>>(){}).create()}
     *
     * @param typeToken specifying the type to create
     * @param <T>       type
     * @return a fully-populated instance
     */
    public static <T> T create(final TypeTokenSupplier<T> typeToken) {
        return of(typeToken).create();
    }

    /**
     * Creates an infinite stream of distinct, fully populated instances of type specified in the type token.
     * <p>
     * Example:
     * <pre>{@code
     *     List<Pair<Integer, String>> pairs = Instancio.stream(new TypeToken<Pair<Integer, String>>() {})
     *         .limit(5)
     *         .collect(Collectors.toList());
     * }</pre>
     *
     * @param typeToken specifying the type to create
     * @param <T>       type
     * @return an infinite stream of distinct, fully populated instances
     * @since 1.1.9
     */
    public static <T> Stream<T> stream(final TypeTokenSupplier<T> typeToken) {
        return of(typeToken).stream();
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
     * @param type to create
     * @param <T>  type
     * @return API builder reference
     */
    public static <T> InstancioOfClassApi<T> of(final Class<T> type) {
        return new InstancioOfClassApiImpl<>(type);
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
     * @param typeToken specifying the type to create
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

    /**
     * Builder API for generating a {@link List} that allows customisation of generated values.
     *
     * @param elementType class to generate as list elements
     * @param <T>         element type
     * @return API builder reference
     * @since 2.0.0
     */
    @SuppressWarnings("all")
    public static <T> InstancioOfCollectionApi<List<T>> ofList(final Class<T> elementType) {
        return new InstancioOfCollectionApiImpl(List.class, elementType);
    }

    /**
     * Builder API for generating a {@link Set} that allows customisation of generated values.
     *
     * @param elementType class to generate as set elements
     * @param <T>         element type
     * @return API builder reference
     * @since 2.0.0
     */
    @SuppressWarnings("all")
    public static <T> InstancioOfCollectionApi<Set<T>> ofSet(final Class<T> elementType) {
        return new InstancioOfCollectionApiImpl(Set.class, elementType);
    }

    /**
     * Builder API for generating a {@link Map} that allowss customisation of generated values.
     *
     * @param keyType   class to generate as map keys
     * @param valueType class to generate as map values
     * @param <K>       key type
     * @param <V>       value type
     * @return API builder reference
     * @since 2.0.0
     */
    @SuppressWarnings("all")
    public static <K, V> InstancioOfCollectionApi<Map<K, V>> ofMap(
            final Class<K> keyType,
            final Class<V> valueType) {

        return new InstancioOfMapApiImpl(Map.class, keyType, valueType);
    }
}
