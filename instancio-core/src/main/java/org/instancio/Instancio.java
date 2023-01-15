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

import java.util.Collection;
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
 *             .set(field(Person::getFullName), "Homer Simpson")
 *             .supply(all(LocalDateTime.class), () -> LocalDateTime.now())
 *             .generate(field(Phone::getNumber), gen -> gen.text().pattern("(#d#d#d) #d#d#d-#d#d#d#d"))
 *             .create();
 * }</pre>
 *
 * <h3>Allow {@code null} values to be generated</h3>
 * Default behaviour is to populate every field with a non-null value.
 * Specifying nullable will randomly generate either {@code null} or non-null value.
 *
 * <pre>{@code
 *     Person person = Instancio.of(Person.class)
 *             .withNullable(field(Person::getDateOfBirth))
 *             .withNullable(all(Gender.class))
 *             .withNullable(allStrings())
 *             .create();
 * }</pre>
 *
 * <h3>Ignore certain fields or classes</h3>
 * Ignored fields will not be populated. Their values will be {@code null}
 * (unless they have a default value assigned).
 *
 * <pre>{@code
 *     Person person = Instancio.of(Person.class)
 *             .ignore(fields().named("id"))  // Person.id, Address.id, etc.
 *             .ignore(all(LocalDateTime.class))
 *             .create();
 * }</pre>
 *
 * <h3>Creating instances of a class from a {@code Model}</h3>
 * Instancio builder API parameters can be saved as a {@link Model} object using
 * the {@link InstancioApi#toModel()} method. Objects can then be generated based
 * on the model. Models can be useful:
 *
 * <ul>
 *   <li>as a prototype for creating customised instances of a class
 *       by overriding model parameters</li>
 *   <li>reducing code duplication by re-using the same model in different
 *       parts of the code</li>
 * </ul>
 *
 * <pre>{@code
 *     // Create a model
 *     Model<Person> simpsons = Instancio.of(Person.class)
 *             .supply(all(Address.class), () -> new Address("742 Evergreen Terrace", "Springfield", "US"))
 *             .supply(field(Person::getPets), () -> List.of(
 *                          new Pet(PetType.CAT, "Snowball"),
 *                          new Pet(PetType.DOG, "Santa's Little Helper"))
 *             //... other specs
 *             .toModel();
 *
 *     // Use the above model as is
 *     Person person = Instancio.create(simpsons);
 *
 *     // Use the model but override the name
 *     Person homer = Instancio.of(simpsons).set(field(Person::getName), "Homer").create();
 *     Person marge = Instancio.of(simpsons).set(field(Person::getName), "Marge").create();
 *
 *     // A model can also used to create another model.
 *     // This snippet creates a new model from the original model to include a new pet.
 *     Model<Person> withNewPet = Instancio.of(simpsons)
 *             .supply(field(Person::getPets), () -> List.of(
 *                          new Pet(PetType.PIG, "Plopper"),
 *                          new Pet(PetType.CAT, "Snowball"),
 *                          new Pet(PetType.DOG, "Santa's Little Helper"))
 *             .toModel();
 * }</pre>
 *
 * <h3>Creating generic classes</h3>
 * There are two options for creating instances of a generic type.
 *
 * <h4>Option 1: using a {@link TypeToken}</h4>
 * <pre>{@code
 *     Pair<Apple, Banana> pairOfFruits = Instancio.create(new TypeToken<Pair<Apple, Banana>>() {}); // note the empty '{}' braces
 * }</pre>
 *
 * <h4>Option 2: using {@code withTypeParameters} to specify the type arguments</h4>
 * <pre>{@code
 *     Pair<Apple, Banana> pairOfFruits = Instancio.of(Pair.class)
 *             .withTypeParameters(Apple.class, Banana.class)
 *             .create();
 * }</pre>
 * <p>
 * The second approach allows specifying arbitrary type parameters at runtime,
 * however using this method will produce an "unchecked assignment" warning.
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
     * Creates an instance of the specified class.
     *
     * @param type to create
     * @param <T>  the type of object
     * @return an object of the specified type
     * @since 1.0.1
     */
    public static <T> T create(final Class<T> type) {
        return of(type).create();
    }

    /**
     * Creates an infinite stream of instances of the specified class.
     * <p>
     * Example:
     * <pre>{@code
     *     List<Person> persons = Instancio.stream(Person.class)
     *         .limit(5)
     *         .collect(Collectors.toList());
     * }</pre>
     *
     * @param type to create
     * @param <T>  the type of object
     * @return an infinite stream of objects of the specified type
     * @since 1.1.9
     */
    public static <T> Stream<T> stream(final Class<T> type) {
        return of(type).stream();
    }

    /**
     * Creates an object of type specified by the type token.
     * This method can be used for creating instances of generic types.
     * <p>
     * Example:
     * <pre>{@code
     *   Pair<UUID, Person> pair = Instancio.create(new TypeToken<Pair<UUID, Person>>(){});
     * }</pre>
     *
     * @param typeToken specifying the type to create
     * @param <T>       the type of object
     * @return an object of the specified type
     */
    public static <T> T create(final TypeTokenSupplier<T> typeToken) {
        return of(typeToken).create();
    }

    /**
     * Creates an infinite stream of objects of type specified by the type token.
     * This method can be used for creating streams of generic types.
     * <p>
     * Example:
     * <pre>{@code
     *   List<Pair<Integer, String>> pairs = Instancio.stream(new TypeToken<Pair<Integer, String>>() {})
     *       .limit(5)
     *       .collect(Collectors.toList());
     * }</pre>
     *
     * @param typeToken specifying the type to create
     * @param <T>       the type of object
     * @return an infinite stream of objects of the specified type
     * @since 1.1.9
     */
    public static <T> Stream<T> stream(final TypeTokenSupplier<T> typeToken) {
        return of(typeToken).stream();
    }

    /**
     * Creates an object populated using the given model.
     * If the object needs to be customised, use the {@link #of(Model)} method.
     * <p>
     * For an example of how to create a model, see {@link InstancioApi#toModel()}.
     *
     * @param model a model that will be used as a template for creating the object
     * @param <T>   the type of object
     * @return an object created based on the model
     * @see InstancioApi#toModel()
     * @see #of(Model)
     * @see #stream(Model)
     */
    public static <T> T create(final Model<T> model) {
        return of(model).create();
    }

    /**
     * Creates an infinite stream of objects populated using the given model.
     * <p>
     * Example:
     * <pre>{@code
     *     Model<Person> model = Instancio.of(Person.class)
     *         .ignore(field(Person::getId))
     *         .generate(field(Person::dateOfBirth), gen -> gen.temporal().localDate().past())
     *         .toModel();
     *
     *     List<Person> persons = Instancio.stream(model)
     *         .limit(5)
     *         .collect(Collectors.toList());
     * }</pre>
     *
     * @param model that will be used to generate the objects
     * @param <T>   the type of object
     * @return an infinite stream of objects created based on the model
     * @see #create(Model)
     * @see #of(Model)
     * @since 2.4.0
     */
    public static <T> Stream<T> stream(final Model<T> model) {
        return of(model).stream();
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
     * @param <T>  the type of object
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
     * @param <T>       the type of object
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
     * @param <T>   the type of object
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
     * Builder API for generating a {@link List} using the specified model for list elements.
     *
     * @param elementModel a model for creating list elements
     * @param <T>          element type
     * @return API builder reference
     * @since 2.5.0
     */
    public static <T> InstancioOfCollectionApi<List<T>> ofList(final Model<T> elementModel) {
        return InstancioOfCollectionApiImpl.fromElementModel(cast(List.class), elementModel);
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
     * Builder API for generating a {@link Set} using the specified model for list elements.
     *
     * @param elementModel a model for creating set elements
     * @param <T>          element type
     * @return API builder reference
     * @since 2.5.0
     */
    public static <T> InstancioOfCollectionApi<Set<T>> ofSet(final Model<T> elementModel) {
        return InstancioOfCollectionApiImpl.fromElementModel(cast(Set.class), elementModel);
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

    @SuppressWarnings("unchecked")
    private static <T, C extends Collection<T>> Class<C> cast(final Class<?> collectionClass) {
        return (Class<C>) collectionClass;
    }
}
