/*
 * Copyright 2022-2025 the original author or authors.
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

import org.instancio.documentation.ExperimentalApi;
import org.instancio.feed.Feed;
import org.instancio.internal.ApiImpl;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.CartesianProductApiImpl;
import org.instancio.internal.ClassApiImpl;
import org.instancio.internal.CollectionsApiImpl;
import org.instancio.internal.FeedApiImpl;
import org.instancio.internal.GenApiImpl;
import org.instancio.internal.MapApiImpl;
import org.instancio.settings.FillType;
import org.instancio.settings.Keys;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static org.instancio.internal.util.TypeUtils.cast;

/**
 * Instancio API for creating instances of a class.
 *
 * <h2>Usage</h2>
 *
 * <h3>Create and populate an instance of a class</h3>
 * Returns an object fully populated with random data with non-null values.
 *
 * <pre>{@code
 * Person person = Instancio.create(Person.class);
 * }</pre>
 *
 * <h3>Customise object's values</h3>
 * Returns an object populated with random data and some specified fields' values customised.
 *
 * <pre>{@code
 * // Customise specific fields using set(), supply(), or generate()
 * Person person = Instancio.of(Person.class)
 *     .set(field(Person::getFullName), "Homer Simpson")
 *     .supply(all(LocalDateTime.class), () -> LocalDateTime.now())
 *     .generate(field(Phone::getNumber), gen -> gen.text().pattern("(#d#d#d) #d#d#d-#d#d#d#d"))
 *     .create();
 * }</pre>
 *
 * <h3>Allow {@code null} values to be generated</h3>
 * By default, Instancio populates every field with non-null values.
 * Specifying fields as nullable allows them to be randomly assigned
 * either {@code null} or non-null values.
 *
 * <pre>{@code
 * Person person = Instancio.of(Person.class)
 *     .withNullable(field(Person::getDateOfBirth))
 *     .withNullable(all(Gender.class))
 *     .withNullable(allStrings())
 *     .create();
 * }</pre>
 *
 * <h3>Ignore certain fields or classes</h3>
 * Ignored fields will not be populated. Their values will be {@code null}
 * (unless they have a default value assigned).
 *
 * <pre>{@code
 * Person person = Instancio.of(Person.class)
 *     .ignore(fields().named("id"))  // Person.id, Address.id, etc.
 *     .ignore(all(LocalDateTime.class))
 *     .create();
 * }</pre>
 *
 * <h3>Creating instances of a class from a {@code Model}</h3>
 * Parameters from the Instancio builder API can be saved
 * as a {@link Model} object using the {@link InstancioApi#toModel()} method.
 * Objects can subsequently be generated based on this model.
 * Models are useful for:
 *
 * <ul>
 *   <li>Serving as prototypes for creating customised instances of a class,
 *       allowing model parameters to be overridden.</li>
 *   <li>Reducing code duplication by reusing models across different
 *       parts of the codebase.</li>
 * </ul>
 *
 * <pre>{@code
 * // Create a reusable model to standardise certain values
 * Model<Person> simpsons = Instancio.of(Person.class)
 *     .supply(all(Address.class), () -> new Address("742 Evergreen Terrace", "Springfield", "US"))
 *     .supply(field(Person::getPets), () -> List.of(
 *                  new Pet(PetType.CAT, "Snowball"),
 *                  new Pet(PetType.DOG, "Santa's Little Helper"))
 *     // Additional specifications...
 *     .toModel();
 *
 * // The toModel() method allows you to save the builder configurations inside
 * // a reusable template. You can then generate objects from the model directly
 * // or modify it to override certain values, e g. use the above model as is:
 * Person person = Instancio.create(simpsons);
 *
 * // Use the model but override the name:
 * Person homer = Instancio.of(simpsons).set(field(Person::getName), "Homer").create();
 * Person marge = Instancio.of(simpsons).set(field(Person::getName), "Marge").create();
 *
 * // A model can also used to create another model.
 * // This snippet creates a new model from the original model to include a new pet.
 * Model<Person> withNewPet = Instancio.of(simpsons)
 *     .supply(field(Person::getPets), () -> List.of(
 *                  new Pet(PetType.PIG, "Plopper"),
 *                  new Pet(PetType.CAT, "Snowball"),
 *                  new Pet(PetType.DOG, "Santa's Little Helper"))
 *     .toModel();
 * }</pre>
 *
 * <h3>Creating generic classes</h3>
 * You can create instances of generic types using two approaches.
 *
 * <h4>Option 1: using a {@link TypeToken}</h4>
 * <pre>{@code
 * Pair<Apple, Banana> pairOfFruits = Instancio.create(new TypeToken<Pair<Apple, Banana>>() {}); // note the empty '{}' braces
 * }</pre>
 *
 * <p>Creates a {@code Pair} object with specific type arguments
 * ({@code Apple} and {@code Banana}) using {@code TypeToken}.
 *
 * <h4>Option 2: using {@code withTypeParameters} to specify the type arguments</h4>
 *
 * <p>This approach allows arbitrary type parameters to be specified at runtime.
 * However, using this method may result in an "unchecked assignment" warning.
 *
 * <pre>{@code
 * Pair<Apple, Banana> pairOfFruits = Instancio.of(Pair.class)
 *     .withTypeParameters(Apple.class, Banana.class)
 *     .create();
 * }</pre>
 *
 * @see InstancioApi
 * @see Select
 * @since 1.0.1
 */
@SuppressWarnings("PMD.CouplingBetweenObjects")
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
     * Creates a blank object of the specified class.
     *
     * <p>The created object will have the following properties:
     *
     * <ul>
     *   <li>value fields (strings, numbers, dates, etc) are {@code null}</li>
     *   <li>arrays, collections, and maps are empty</li>
     *   <li>nested POJOs are blank</li>
     * </ul>
     *
     * <p>For example, assuming the following POJO:
     *
     * <pre>{@code
     * class Person {
     *     String name;
     *     LocalDate dateOfBirth;
     *     List<Phone> phoneNumbers;
     *     Address address;
     * }
     * }</pre>
     *
     * <p>Creating a blank {@code Person} object will produce:
     *
     * <pre>{@code
     * Person person = Instancio.createBlank(Person.class);
     *
     * // Output:
     * // Person[
     * //   name=null,
     * //   dateOfBirth=null,
     * //   phoneNumbers=[] // empty List
     * //   address=Address[street=null, city=null, country=null] // blank nested POJO
     * // ]
     * }</pre>
     *
     * @param type the type of blank object to create
     * @param <T>  the type of object
     * @return a blank object of the specified type
     * @see BaseApi#setBlank(TargetSelector)
     * @see #ofBlank(Class)
     * @since 4.7.0
     */
    @ExperimentalApi
    public static <T> T createBlank(final Class<T> type) {
        return ofBlank(type).create();
    }

    /**
     * Creates a {@link List} of random size.
     *
     * <p>Unless configured otherwise, the generated size will be between
     * {@link Keys#COLLECTION_MIN_SIZE} and {@link Keys#COLLECTION_MAX_SIZE},
     * inclusive.
     *
     * <p>To create a list of a specific size, use {@link #ofList(Class)}.
     *
     * @param elementType class to generate as list elements
     * @param <T>         element type
     * @return API builder reference
     * @since 3.0.1
     */
    public static <T> List<T> createList(final Class<T> elementType) {
        return ofList(elementType).create();
    }

    /**
     * Creates a {@link Set} of random size.
     *
     * <p>Unless configured otherwise, the generated size will be between
     * {@link Keys#COLLECTION_MIN_SIZE} and {@link Keys#COLLECTION_MAX_SIZE},
     * inclusive.
     *
     * <p>To create a {@code Set} of a specific size, use {@link #ofSet(Class)}.
     *
     * @param elementType class to generate as set elements
     * @param <T>         element type
     * @return API builder reference
     * @since 3.0.1
     */
    public static <T> Set<T> createSet(final Class<T> elementType) {
        return ofSet(elementType).create();
    }

    /**
     * Creates a {@link Map} of random size.
     *
     * <p>Unless configured otherwise, the generated size will be between
     * {@link Keys#MAP_MIN_SIZE} and {@link Keys#MAP_MAX_SIZE}, inclusive.
     *
     * <p>To create a {@code Map} of a specific size, use {@link #ofMap(Class, Class)}.
     *
     * @param keyType   class to generate as map keys
     * @param valueType class to generate as map values
     * @param <K>       key type
     * @param <V>       value type
     * @return API builder reference
     * @since 3.0.1
     */
    public static <K, V> Map<K, V> createMap(final Class<K> keyType, final Class<V> valueType) {
        return ofMap(keyType, valueType).create();
    }

    /**
     * Creates an infinite stream of instances of the specified class.
     * <p>
     * Example:
     * <pre>{@code
     * List<Person> persons = Instancio.stream(Person.class)
     *     .limit(5)
     *     .collect(Collectors.toList());
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
     * Pair<UUID, Person> pair = Instancio.create(new TypeToken<Pair<UUID, Person>>(){});
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
     * List<Pair<Integer, String>> pairs = Instancio.stream(new TypeToken<Pair<Integer, String>>() {})
     *     .limit(5)
     *     .collect(Collectors.toList());
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
     *
     * <p>For example, given the following model:
     *
     * <pre>{@code
     * Model<Person> model = Instancio.of(Person.class)
     *     .ignore(field(Person::getId))
     *     .generate(field(Person::dateOfBirth), gen -> gen.temporal().localDate().past())
     *     .toModel();
     * }</pre>
     *
     * <p>you can create a stream of objects as follows:
     *
     * <pre>{@code
     * List<Person> persons = Instancio.stream(model)
     *     .limit(5)
     *     .collect(Collectors.toList());
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
     * Person person = Instancio.of(Person.class)
     *     .generate(allInts(), gen -> gen.ints().min(1).max(99))
     *     .supply(all(Address.class), () -> new Address("742 Evergreen Terrace", "Springfield", "US"))
     *     .supply(field("pets"), () -> List.of(
     *                         new Pet(PetType.CAT, "Snowball"),
     *                         new Pet(PetType.DOG, "Santa's Little Helper")))
     *     .create();
     * }</pre>
     *
     * @param type to create
     * @param <T>  the type of object
     * @return API builder reference
     */
    public static <T> InstancioClassApi<T> of(final Class<T> type) {
        return new ClassApiImpl<>(type);
    }

    /**
     * Builder version of the {@link #createBlank(Class)} method
     * that allows customisation of generated values.
     *
     * <p>For example, assuming the following POJO:
     *
     * <pre>{@code
     * class Person {
     *     String name;
     *     LocalDate dateOfBirth;
     *     List<Phone> phoneNumbers;
     *     Address address;
     * }
     * }</pre>
     *
     * <p>The snippet below will create a blank {@code Person} object
     * with two initialised fields:
     *
     * <pre>{@code
     * Person person = Instancio.ofBlank(Person.class)
     *     .set(field(Address::getCountry), "Canada")
     *     .generate(field(Person::getDateOfBirth), gen -> gen.temporal().localDate().past())
     *     .create()
     *
     * // Sample output:
     * // Person[
     * //   name=null,
     * //   dateOfBirth=1990-12-29,
     * //   phoneNumbers=[]
     * //   address=Address[street=null, city=null, country=Canada]
     * //]
     * }</pre>
     *
     * @param type the type of blank object to create
     * @param <T>  the type of object
     * @return API builder reference
     * @see BaseApi#setBlank(TargetSelector)
     * @since 4.7.0
     */
    @ExperimentalApi
    public static <T> InstancioClassApi<T> ofBlank(final Class<T> type) {
        final InstancioClassApi<T> api = new ClassApiImpl<>(type);
        api.setBlank(Select.root());
        return api;
    }

    /**
     * Builder version of {@link #create(TypeTokenSupplier)} that allows customisation of generated values.
     *
     * <pre>{@code
     * List<Person> persons = Instancio.of(new TypeToken<List<Person>>(){})
     *     .generate(allInts(), gen -> gen.ints().min(1).max(99))
     *     .supply(all(Address.class), () -> new Address("742 Evergreen Terrace", "Springfield", "US"))
     *     .supply(field("pets"), () -> List.of(
     *                         new Pet(PetType.CAT, "Snowball"),
     *                         new Pet(PetType.DOG, "Santa's Little Helper")))
     *     .create();
     * }</pre>
     *
     * @param typeToken specifying the type to create
     * @param <T>       the type of object
     * @return API builder reference
     */
    public static <T> InstancioApi<T> of(final TypeTokenSupplier<T> typeToken) {
        return new ApiImpl<>(typeToken);
    }

    /**
     * Builder version of {@link #create(Model)} that allows overriding of generation
     * parameters of an existing model.
     *
     * <pre>{@code
     * Model<Person> personModel = Instancio.of(Person.class)
     *     .generate(allInts(), gen -> gen.ints().min(1).max(99))
     *     .supply(all(Address.class), () -> new Address("742 Evergreen Terrace", "Springfield", "US"))
     *     .supply(field("pets"), () -> List.of(
     *                         new Pet(PetType.CAT, "Snowball"),
     *                         new Pet(PetType.DOG, "Santa's Little Helper")))
     *     .toModel();
     *
     * // Use the existing model and add/override generation parameters
     * Person simpsonKid = Instancio.of(personModel)
     *     .generate(field("fullName"), gen -> gen.oneOf("Lisa Simpson", "Bart Simpson"))
     *     .create();
     * }</pre>
     *
     * @param model specifying generation parameters of the object to create
     * @param <T>   the type of object
     * @return API builder reference
     */
    public static <T> InstancioApi<T> of(final Model<T> model) {
        return new ApiImpl<>(model);
    }

    /**
     * Fills the fields of the given object with randomly generated values,
     * preserving existing non-null and non-default values.
     *
     * <p>For more details, including customisation options and usage constraints,
     * refer to the {@link #ofObject(Object)} method.
     *
     * @param object the object whose fields should be populated with random values.
     * @param <T>    the type of the object
     * @see #ofObject(Object)
     * @see FillType
     * @since 5.3.0
     */
    @ExperimentalApi
    public static <T> void fill(final T object) {
        ofObject(object).fill();
    }

    /**
     * A builder API for populating fields of the given object with randomly
     * generated values.
     *
     * <p>By default, only fields that are {@code null} or primitive fields
     * with default values will be populated, while existing non-null
     * and non-default primitive values will remain unchanged.
     *
     * <p>For example, given the following class:
     *
     * <pre>{@code
     * class Person {
     *     private String name;
     *     private String email;
     *     private LocalDate dateOfBirth;
     *     // getters and setters
     * }
     * }</pre>
     *
     * <p>A {@code Person} instance can be populated as follows:
     *
     * <pre>{@code
     * // Given a person with some initialised fields
     * Person person = new Person();
     * person.setDateOfBirth(LocalDate.of(1980, 12, 31));
     *
     * // Populate the rest of the object
     * Instancio.ofObject(person)
     *     .generate(field(Person::getEmail), gen -> gen.net().email())
     *     .fill();
     *
     * // Sample output:
     * // Person[name=VCNSOU, email=fphna@mph.org, dateOfBirth=1980-12-31]
     * }</pre>
     *
     * <ul>
     *   <li>The {@code name} field which was {@code null}
     *       was populated with a random value.</li>
     *   <li>The {@code email} field was generated using
     *       the specified email generator.</li>
     *   <li>The {@code dateOfBirth} field retained the initialised value.</li>
     * </ul>
     *
     * <p>Note that by default, Instancio uses the
     * {@link FillType#POPULATE_NULLS_AND_DEFAULT_PRIMITIVES}
     * when populating objects.
     * The default fill type can be customised in two ways:
     *
     * <ul>
     *   <li>Using {@link InstancioObjectApi#withFillType(FillType)} method</li>
     *   <li>Via {@code Settings}, using the {@link Keys#FILL_TYPE} key</li>
     * </ul>
     *
     * <p>For example, when using {@link FillType#APPLY_SELECTORS},
     * the object will be modified only via selectors:
     *
     * <pre>{@code
     * Person person = new Person();
     * person.setDateOfBirth(LocalDate.of(1980, 12, 31));
     *
     * Instancio.ofObject(person)
     *     .withFillType(FillType.APPLY_SELECTORS)
     *     .generate(field(Person::getEmail), gen -> gen.net().email())
     *     .fill();
     *
     * // Sample output (note: the name field remains null):
     * // Person[name=null, email=xnb@mfk4.org, dateOfBirth=1980-12-31]
     * }</pre>
     *
     * <h4>Limitations</h4>
     *
     * <p>The input object must satisfy the following requirements:
     *
     * <ul>
     *   <li>Must not be a parameterized type except for
     *       {@link Collection} or {@link Map}</li>
     *   <li>Must not be an empty collection or map</li>
     * </ul>
     *
     * <p>Note: While this method can populate fields within elements
     * of a collection, it does not:
     *
     * <ul>
     *   <li>Add new elements to the collection.</li>
     *   <li>Replace {@code null} elements with non-null values.</li>
     * </ul>
     *
     * <p>As a result, initialised collections will retain their original size
     * unless overwritten with a new collection instance via a selector.
     *
     * @param object the object whose fields should be populated with random values
     * @param <T>    the type of the object
     * @return API builder reference
     * @see #fill(Object)
     * @see FillType
     * @since 5.3.0
     */
    @ExperimentalApi
    public static <T> InstancioObjectApi<T> ofObject(final T object) {
        ApiValidator.notNull(object, "object to populate must not be null");
        return new ApiImpl<>(object);
    }

    /**
     * Generates the Cartesian product based on the values specified via the
     * {@code with()} method. The Cartesian product is returned as
     * a {@link List} in lexicographical order.
     *
     * <p>Example:
     * <pre>{@code
     * record Widget(String type, int num) {}
     *
     * List<Widget> results = Instancio.ofCartesianProduct(Widget.class)
     *     .with(field(Widget::type), "FOO", "BAR", "BAZ")
     *     .with(field(Widget::num), 1, 2, 3)
     *     .create();
     * }</pre>
     *
     * <p>This will produce the following list of {@code Widget} objects:
     * <pre>
     * [Widget[type=FOO, num=1],
     *  Widget[type=FOO, num=2],
     *  Widget[type=FOO, num=3],
     *  Widget[type=BAR, num=1],
     *  Widget[type=BAR, num=2],
     *  Widget[type=BAR, num=3],
     *  Widget[type=BAZ, num=1],
     *  Widget[type=BAZ, num=2],
     *  Widget[type=BAZ, num=3]]
     * </pre>
     *
     * @param type to create
     * @param <T>  the type of object
     * @return API builder reference
     * @see #ofCartesianProduct(Model)
     * @see #ofCartesianProduct(TypeTokenSupplier)
     * @since 4.0.0
     */
    @ExperimentalApi
    public static <T> InstancioCartesianProductApi<T> ofCartesianProduct(final Class<T> type) {
        return new CartesianProductApiImpl<>(ApiValidator.validateOfCartesianProductElementType(type));
    }

    /**
     * Generates the Cartesian product based on the values specified via the
     * {@code with()} method. The Cartesian product is returned as
     * a {@link List} in lexicographical order.
     *
     * <p>See {@link #ofCartesianProduct(Class)} for an example.
     *
     * @param typeToken specifying the type to create
     * @param <T>       the type of object
     * @return API builder reference
     * @see #ofCartesianProduct(Class)
     * @see #ofCartesianProduct(Model)
     * @since 4.0.0
     */
    @ExperimentalApi
    public static <T> InstancioCartesianProductApi<T> ofCartesianProduct(final TypeTokenSupplier<T> typeToken) {
        return new CartesianProductApiImpl<>(typeToken);
    }

    /**
     * Generates the Cartesian product based on the values specified via the
     * {@code with()} method. The Cartesian product is returned as
     * a {@link List} in lexicographical order.
     *
     * <p>See {@link #ofCartesianProduct(Class)} for an example.
     *
     * @param model specifying generation parameters of the object to create
     * @param <T>   the type of object
     * @return API builder reference
     * @see #ofCartesianProduct(Class)
     * @see #ofCartesianProduct(TypeTokenSupplier)
     * @since 4.0.0
     */
    @ExperimentalApi
    public static <T> InstancioCartesianProductApi<T> ofCartesianProduct(final Model<T> model) {
        return new CartesianProductApiImpl<>(model);
    }

    /**
     * Builder API for generating a {@link List} that allows customising generated values.
     *
     * @param elementType class to generate as list elements
     * @param <T>         element type
     * @return API builder reference
     * @since 2.0.0
     */
    @SuppressWarnings("all")
    public static <T> InstancioCollectionsApi<List<T>> ofList(final Class<T> elementType) {
        return new CollectionsApiImpl(List.class, ApiValidator.validateOfListElementType(elementType));
    }

    /**
     * Builder API for generating a {@link List} using a type token.
     *
     * @param elementTypeToken specifying the element type
     * @param <T>              element type
     * @return API builder reference
     * @since 2.16.0
     */
    @SuppressWarnings("all")
    public static <T> InstancioCollectionsApi<List<T>> ofList(final TypeTokenSupplier<T> elementTypeToken) {
        return new CollectionsApiImpl(List.class, ApiValidator.validateTypeToken(elementTypeToken));
    }

    /**
     * Builder API for generating a {@link List} using the specified model for list elements.
     *
     * @param elementModel a model for creating list elements
     * @param <T>          element type
     * @return API builder reference
     * @since 2.5.0
     */
    public static <T> InstancioCollectionsApi<List<T>> ofList(final Model<T> elementModel) {
        return CollectionsApiImpl.fromElementModel(cast(List.class), elementModel);
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
    public static <T> InstancioCollectionsApi<Set<T>> ofSet(final Class<T> elementType) {
        return new CollectionsApiImpl(Set.class, ApiValidator.validateOfSetElementType(elementType));
    }

    /**
     * Builder API for generating a {@link Set} using a type token.
     *
     * @param elementTypeToken specifying the element type
     * @param <T>              element type
     * @return API builder reference
     * @since 2.16.0
     */
    @SuppressWarnings("all")
    public static <T> InstancioCollectionsApi<Set<T>> ofSet(final TypeTokenSupplier<T> elementTypeToken) {
        return new CollectionsApiImpl(Set.class, ApiValidator.validateTypeToken(elementTypeToken));
    }

    /**
     * Builder API for generating a {@link Set} using the specified model for list elements.
     *
     * @param elementModel a model for creating set elements
     * @param <T>          element type
     * @return API builder reference
     * @since 2.5.0
     */
    public static <T> InstancioCollectionsApi<Set<T>> ofSet(final Model<T> elementModel) {
        return CollectionsApiImpl.fromElementModel(cast(Set.class), elementModel);
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
    public static <K, V> InstancioCollectionsApi<Map<K, V>> ofMap(
            final Class<K> keyType,
            final Class<V> valueType) {

        return new MapApiImpl(Map.class,
                ApiValidator.validateOfMapKeyOrValueType(keyType),
                ApiValidator.validateOfMapKeyOrValueType(valueType));
    }

    /**
     * Builder API for generating a {@link Map} using type tokens.
     *
     * @param keyTypeToken   specifying the key type
     * @param valueTypeToken specifying the value type
     * @param <K>            key type
     * @param <V>            value type
     * @return API builder reference
     * @since 2.16.0
     */
    @SuppressWarnings("all")
    public static <K, V> InstancioCollectionsApi<Map<K, V>> ofMap(
            final TypeTokenSupplier<K> keyTypeToken,
            final TypeTokenSupplier<V> valueTypeToken) {

        return new MapApiImpl(Map.class,
                ApiValidator.validateTypeToken(keyTypeToken),
                ApiValidator.validateTypeToken(valueTypeToken));
    }

    /**
     * A shorthand API for generating simple value types,
     * such as strings, numbers, dates, etc.
     *
     * <p>This API supports generating a single value
     * using the {@code get()} method:
     *
     * <pre>{@code
     * URL url = Instancio.gen().net().url().get();
     *
     * String randomChoice = Instancio.gen().oneOf("foo", "bar", "baz").get();
     * }</pre>
     *
     * <p>as  well as generating a list of values
     * using the {@code list(int size)} method:
     *
     * <pre>{@code
     * List<LocalDate> pastDates = Instancio.gen().temporal().localDate().past().list(5);
     *
     * List<String> uuids = Instancio.gen().text().uuid().upperCase().withoutDashes().list(5);
     * }</pre>
     *
     * <p>Additionally, the API can generate an infinite stream of values,
     * for example a stream of strings in the {@code "ABC-123"} format:
     *
     * <pre>{@code
     * Stream<String> pastDates = Instancio.gen().text().pattern("#C#C#C-#d#d#d")
     *   .stream()
     *   .limit(100); // limit must be called to avoid an infinite loop
     * }</pre>
     *
     * @return API builder reference
     * @since 5.0.0
     */
    @ExperimentalApi
    public static InstancioGenApi gen() {
        return new GenApiImpl();
    }

    /**
     * Creates a feed of the specified type.
     *
     * @param type the class that defines a feed
     * @param <F>  the type of feed
     * @return API builder reference
     * @see #ofFeed(Class)
     * @since 5.0.0
     */
    @ExperimentalApi
    public static <F extends Feed> F createFeed(final Class<F> type) {
        return new FeedApiImpl<>(type).create();
    }

    /**
     * Builder version of {@link #createFeed(Class)}
     * that allows customising the feed's properties.
     *
     * @param type the class that defines a feed
     * @param <F>  the type of feed
     * @return API builder reference
     * @see #createFeed(Class)
     * @since 5.0.0
     */
    @ExperimentalApi
    public static <F extends Feed> InstancioFeedApi<F> ofFeed(final Class<F> type) {
        return new FeedApiImpl<>(type);
    }
}
