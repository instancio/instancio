/*
 * Copyright 2022-2026 the original author or authors.
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
package org.instancio.kotlin

import java.util.stream.Stream
import kotlin.reflect.javaType
import kotlin.reflect.typeOf
import org.instancio.Instancio
import org.instancio.InstancioApi
import org.instancio.InstancioCartesianProductApi
import org.instancio.InstancioClassApi
import org.instancio.InstancioCollectionsApi
import org.instancio.InstancioFeedApi
import org.instancio.InstancioGenApi
import org.instancio.InstancioObjectApi
import org.instancio.Model
import org.instancio.TypeTokenSupplier
import org.instancio.documentation.ExperimentalApi
import org.instancio.feed.Feed

/**
 * Instancio Kotlin API for creating instances of a class.
 *
 * @since 6.0.0
 */
object KInstancio {

    /**
     * Creates an instance of the specified type.
     *
     * ```kotlin
     * val person = KInstancio.create<Person>()
     * ```
     *
     * @param T the type of object
     * @return an object of the specified type
     * @since 6.0.0
     */
    @ExperimentalApi
    inline fun <reified T> create(): T {
        val javaType = typeOf<T>().javaType
        return Instancio.create(TypeTokenSupplier { javaType })
    }

    /**
     * Creates a blank object of the specified type.
     *
     * The created object will have the following properties:
     *
     * - value fields (strings, numbers, dates, etc.) are `null`
     * - arrays, collections, and maps are empty
     * - nested POJOs are blank
     *
     * For example, assuming the following POJO:
     *
     * ```kotlin
     * class Person {
     *     var name: String? = null
     *     var dateOfBirth: LocalDate? = null
     *     var phoneNumbers: List<Phone> = emptyList()
     *     var address: Address? = null
     * }
     * ```
     *
     * Creating a blank `Person` will produce:
     *
     * ```kotlin
     * val person = KInstancio.createBlank<Person>()
     *
     * // Output:
     * // Person[
     * //   name=null,
     * //   dateOfBirth=null,
     * //   phoneNumbers=[] // empty List
     * //   address=Address[street=null, city=null, country=null] // blank nested POJO
     * // ]
     * ```
     *
     * @param T the type of object
     * @return a blank object of the specified type
     * @since 6.0.0
     */
    @ExperimentalApi
    inline fun <reified T> createBlank(): T {
        return Instancio.createBlank(T::class.java)
    }

    /**
     * Creates a `List` of random size.
     *
     * Unless configured otherwise, the generated size will be between
     * `Keys.COLLECTION_MIN_SIZE` and `Keys.COLLECTION_MAX_SIZE`, inclusive.
     *
     * To create a list of a specific size, use `ofList`.
     *
     * ```kotlin
     * val persons = KInstancio.createList<Person>()
     * ```
     *
     * @param T element type
     * @return a list of randomly generated objects
     * @since 6.0.0
     */
    @ExperimentalApi
    inline fun <reified T> createList(): List<T> {
        return ofList<T>().create()
    }

    /**
     * Creates a `Set` of random size.
     *
     * Unless configured otherwise, the generated size will be between
     * `Keys.COLLECTION_MIN_SIZE` and `Keys.COLLECTION_MAX_SIZE`, inclusive.
     *
     * To create a set of a specific size, use `ofSet`.
     *
     * ```kotlin
     * val persons = KInstancio.createSet<Person>()
     * ```
     *
     * @param T element type
     * @return a set of randomly generated objects
     * @since 6.0.0
     */
    @ExperimentalApi
    inline fun <reified T> createSet(): Set<T> {
        return ofSet<T>().create()
    }

    /**
     * Creates a `Map` of random size.
     *
     * Unless configured otherwise, the generated size will be between
     * `Keys.MAP_MIN_SIZE` and `Keys.MAP_MAX_SIZE`, inclusive.
     *
     * To create a map of a specific size, use `ofMap`.
     *
     * ```kotlin
     * val map = KInstancio.createMap<UUID, Person>()
     * ```
     *
     * @param K key type
     * @param V value type
     * @return a map of randomly generated key-value pairs
     * @since 6.0.0
     */
    @ExperimentalApi
    inline fun <reified K, reified V> createMap(): Map<K, V> {
        return ofMap<K, V>().create()
    }

    /**
     * Creates an infinite stream of instances of the specified type.
     *
     * ```kotlin
     * val persons = KInstancio.stream<Person>()
     *     .limit(5)
     *     .collect(Collectors.toList())
     * ```
     *
     * @param T the type of object
     * @return an infinite stream of objects of the specified type
     * @since 6.0.0
     */
    @ExperimentalApi
    inline fun <reified T> stream(): Stream<T> {
        return Instancio.stream { typeOf<T>().javaType }
    }

    /**
     * Creates an object populated using the given model.
     * If the object needs to be customised, use the `of(Model)` method.
     *
     * For an example of how to create a model, see `InstancioApi.toModel()`.
     *
     * @param T the type of object
     * @param model a model that will be used as a template for creating the object
     * @return an object created based on the model
     * @since 6.0.0
     */
    @ExperimentalApi
    fun <T> create(model: Model<T>): T {
        return Instancio.create(model)
    }

    /**
     * Creates an infinite stream of objects populated using the given model.
     *
     * For example, given the following model:
     *
     * ```kotlin
     * val model = KInstancio.of<Person>()
     *     .ignore(field(Person::getId))
     *     .generate(field(Person::getDateOfBirth)) { gen -> gen.temporal().localDate().past() }
     *     .toModel()
     * ```
     *
     * you can create a stream of objects as follows:
     *
     * ```kotlin
     * val persons = KInstancio.stream(model)
     *     .limit(5)
     *     .collect(Collectors.toList())
     * ```
     *
     * @param T the type of object
     * @param model that will be used to generate the objects
     * @return an infinite stream of objects created based on the model
     * @since 6.0.0
     */
    @ExperimentalApi
    fun <T> stream(model: Model<T>): Stream<T> {
        return Instancio.stream(model)
    }

    /**
     * Builder version of `create` that allows customisation of generated values.
     *
     * ```kotlin
     * val person = KInstancio.of<Person>()
     *     .generate(allInts()) { gen -> gen.ints().min(1).max(99) }
     *     .supply(all(Address::class.java)) { Address("742 Evergreen Terrace", "Springfield", "US") }
     *     .supply(field("pets")) { listOf(
     *         Pet(PetType.CAT, "Snowball"),
     *         Pet(PetType.DOG, "Santa's Little Helper"))
     *     }
     *     .create()
     * ```
     *
     * @param T the type of object
     * @return API builder reference
     * @since 6.0.0
     */
    @ExperimentalApi
    inline fun <reified T> of(): InstancioApi<T> {
        return Instancio.of { typeOf<T>().javaType }
    }

    /**
     * Builder version of `createBlank` that allows customisation of generated values.
     *
     * For example, assuming the following POJO:
     *
     * ```kotlin
     * class Person {
     *     var name: String? = null
     *     var dateOfBirth: LocalDate? = null
     *     var phoneNumbers: List<Phone> = emptyList()
     *     var address: Address? = null
     * }
     * ```
     *
     * The snippet below will create a blank `Person` with two initialised fields:
     *
     * ```kotlin
     * val person = KInstancio.ofBlank<Person>()
     *     .set(field(Address::getCountry), "Canada")
     *     .generate(field(Person::getDateOfBirth)) { gen -> gen.temporal().localDate().past() }
     *     .create()
     *
     * // Sample output:
     * // Person[
     * //   name=null,
     * //   dateOfBirth=1990-12-29,
     * //   phoneNumbers=[]
     * //   address=Address[street=null, city=null, country=Canada]
     * // ]
     * ```
     *
     * @param T the type of object
     * @return API builder reference
     * @since 6.0.0
     */
    @ExperimentalApi
    inline fun <reified T> ofBlank(): InstancioClassApi<T> {
        return Instancio.ofBlank(T::class.java)
    }

    /**
     * Builder version of `create(Model)` that allows overriding generation
     * parameters of an existing model.
     *
     * ```kotlin
     * val personModel = KInstancio.of<Person>()
     *     .generate(allInts()) { gen -> gen.ints().min(1).max(99) }
     *     .supply(all(Address::class.java)) { Address("742 Evergreen Terrace", "Springfield", "US") }
     *     .toModel()
     *
     * // Use the existing model and add/override generation parameters
     * val simpsonKid = KInstancio.of(personModel)
     *     .generate(field("fullName")) { gen -> gen.oneOf("Lisa Simpson", "Bart Simpson") }
     *     .create()
     * ```
     *
     * @param T the type of object
     * @param model specifying generation parameters of the object to create
     * @return API builder reference
     * @since 6.0.0
     */
    @ExperimentalApi
    fun <T> of(model: Model<T>): InstancioApi<T> {
        return Instancio.of(model)
    }

    /**
     * Fills the fields of the given object with randomly generated values,
     * preserving existing non-null and non-default values.
     *
     * For more details, including customisation options and usage constraints,
     * refer to the `ofObject` method.
     *
     * @param T the type of the object
     * @param obj the object whose fields should be populated with random values
     * @since 6.0.0
     */
    @ExperimentalApi
    fun <T> fill(obj: T) {
        Instancio.fill(obj)
    }

    /**
     * A builder API for populating fields of the given object with randomly
     * generated values.
     *
     * By default, only fields that are `null` or primitive fields with default
     * values will be populated, while existing non-null and non-default primitive
     * values will remain unchanged.
     *
     * For example, given the following class:
     *
     * ```kotlin
     * class Person {
     *     var name: String? = null
     *     var email: String? = null
     *     var dateOfBirth: LocalDate? = null
     * }
     * ```
     *
     * A `Person` instance can be populated as follows:
     *
     * ```kotlin
     * // Given a person with some initialised fields
     * val person = Person().apply {
     *     dateOfBirth = LocalDate.of(1980, 12, 31)
     * }
     *
     * // Populate the rest of the object
     * KInstancio.ofObject(person)
     *     .generate(field(Person::getEmail)) { gen -> gen.net().email() }
     *     .fill()
     *
     * // Sample output:
     * // Person[name=VCNSOU, email=fphna@mph.org, dateOfBirth=1980-12-31]
     * ```
     *
     * @param T the type of the object
     * @param obj the object whose fields should be populated with random values
     * @return API builder reference
     * @since 6.0.0
     */
    @ExperimentalApi
    fun <T> ofObject(obj: T): InstancioObjectApi<T> {
        return Instancio.ofObject(obj)
    }

    /**
     * Generates the Cartesian product based on the values specified via the
     * `with()` method. The Cartesian product is returned as a `List`
     * in lexicographical order.
     *
     * ```kotlin
     * data class Widget(val type: String, val num: Int)
     *
     * val results = KInstancio.ofCartesianProduct<Widget>()
     *     .with(field(Widget::type), "FOO", "BAR", "BAZ")
     *     .with(field(Widget::num), 1, 2, 3)
     *     .create()
     * ```
     *
     * This will produce the following list of `Widget` objects:
     *
     * ```
     * [Widget[type=FOO, num=1],
     *  Widget[type=FOO, num=2],
     *  Widget[type=FOO, num=3],
     *  Widget[type=BAR, num=1],
     *  Widget[type=BAR, num=2],
     *  Widget[type=BAR, num=3],
     *  Widget[type=BAZ, num=1],
     *  Widget[type=BAZ, num=2],
     *  Widget[type=BAZ, num=3]]
     * ```
     *
     * @param T the type of object
     * @return API builder reference
     * @since 6.0.0
     */
    @ExperimentalApi
    inline fun <reified T> ofCartesianProduct(): InstancioCartesianProductApi<T> {
        return Instancio.ofCartesianProduct { typeOf<T>().javaType }
    }

    /**
     * Generates the Cartesian product based on the values specified via the
     * `with()` method. The Cartesian product is returned as a `List`
     * in lexicographical order.
     *
     * See `ofCartesianProduct()` for an example.
     *
     * @param T the type of object
     * @param model specifying generation parameters of the object to create
     * @return API builder reference
     * @since 6.0.0
     */
    @ExperimentalApi
    fun <T> ofCartesianProduct(model: Model<T>): InstancioCartesianProductApi<T> {
        return Instancio.ofCartesianProduct(model)
    }

    /**
     * Builder API for generating a `List` that allows customising generated values.
     *
     * ```kotlin
     * val persons = KInstancio.ofList<Person>()
     *     .size(5)
     *     .create()
     * ```
     *
     * @param T element type
     * @return API builder reference
     * @since 6.0.0
     */
    @ExperimentalApi
    inline fun <reified T> ofList(): InstancioCollectionsApi<List<T>> {
        return Instancio.ofList { typeOf<T>().javaType }
    }

    /**
     * Builder API for generating a `List` using the specified model for list elements.
     *
     * @param T element type
     * @param elementModel a model for creating list elements
     * @return API builder reference
     * @since 6.0.0
     */
    @ExperimentalApi
    fun <T> ofList(elementModel: Model<T>): InstancioCollectionsApi<List<T>> {
        return Instancio.ofList(elementModel)
    }

    /**
     * Builder API for generating a `Set` that allows customisation of generated values.
     *
     * ```kotlin
     * val persons = KInstancio.ofSet<Person>()
     *     .size(5)
     *     .create()
     * ```
     *
     * @param T element type
     * @return API builder reference
     * @since 6.0.0
     */
    @ExperimentalApi
    inline fun <reified T> ofSet(): InstancioCollectionsApi<Set<T>> {
        return Instancio.ofSet { typeOf<T>().javaType }
    }

    /**
     * Builder API for generating a `Set` using the specified model for set elements.
     *
     * @param T element type
     * @param elementModel a model for creating set elements
     * @return API builder reference
     * @since 6.0.0
     */
    @ExperimentalApi
    fun <T> ofSet(elementModel: Model<T>): InstancioCollectionsApi<Set<T>> {
        return Instancio.ofSet(elementModel)
    }

    /**
     * Builder API for generating a `Map` that allows customisation of generated values.
     *
     * ```kotlin
     * val map = KInstancio.ofMap<UUID, Person>()
     *     .size(5)
     *     .create()
     * ```
     *
     * @param K key type
     * @param V value type
     * @return API builder reference
     * @since 6.0.0
     */
    @ExperimentalApi
    inline fun <reified K, reified V> ofMap(): InstancioCollectionsApi<Map<K, V>> {
        return Instancio.ofMap({ typeOf<K>().javaType }, { typeOf<V>().javaType })
    }

    /**
     * A shorthand API for generating simple value types,
     * such as strings, numbers, dates, etc.
     *
     * This API supports generating a single value using the `get()` method:
     *
     * ```kotlin
     * val url = KInstancio.gen().net().url().get()
     *
     * val randomChoice = KInstancio.gen().oneOf("foo", "bar", "baz").get()
     * ```
     *
     * as well as generating a list of values using the `list(int)` method:
     *
     * ```kotlin
     * val pastDates = KInstancio.gen().temporal().localDate().past().list(5)
     *
     * val uuids = KInstancio.gen().text().uuid().upperCase().withoutDashes().list(5)
     * ```
     *
     * Additionally, the API can generate an infinite stream of values:
     *
     * ```kotlin
     * val stream = KInstancio.gen().text().pattern("#C#C#C-#d#d#d")
     *     .stream()
     *     .limit(100) // limit must be called to avoid an infinite loop
     * ```
     *
     * @return API builder reference
     * @since 6.0.0
     */
    @ExperimentalApi
    fun gen(): InstancioGenApi {
        return Instancio.gen()
    }

    /**
     * Creates a feed of the specified type.
     *
     * @param F the type of feed
     * @return a feed instance of the specified type
     * @since 6.0.0
     */
    @ExperimentalApi
    inline fun <reified F : Feed> createFeed(): F {
        return Instancio.createFeed(F::class.java)
    }

    /**
     * Builder version of `createFeed` that allows customising the feed's properties.
     *
     * @param F the type of feed
     * @return API builder reference
     * @since 6.0.0
     */
    @ExperimentalApi
    inline fun <reified F : Feed> ofFeed(): InstancioFeedApi<F> {
        return Instancio.ofFeed(F::class.java)
    }
}
