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

import org.instancio.generator.GeneratorSpec;
import org.instancio.settings.Settings;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

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
     * Creates an infinite stream of distinct, fully populated objects.
     * <p>
     * Example:
     * <pre>{@code
     *     List<Person> persons = Instancio.of(Person.class)
     *         .stream()
     *         .limit(5)
     *         .collect(Collectors.toList());
     * }</pre>
     *
     * @return an infinite stream of distinct, populated objects
     */
    Stream<T> stream();

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
     * the {@link Model} interface does not contain any methods.
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
     * @param selectors for fields and/or classes this method should be applied to
     * @return API builder reference
     */
    InstancioApi<T> ignore(SelectorGroup selectors);

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
     * @param selectors for fields and/or classes this method should be applied to
     * @return API builder reference
     */
    InstancioApi<T> withNullable(SelectorGroup selectors);

    /**
     * Sets a value for a field or class.
     * <p>
     * Example: if a {@code Person} has a {@code List<PhoneNumber>}, the following
     * will set all generated phone numbers' country codes to "+1".
     * <pre>{@code
     *     Person person = Instancio.of(Person.class)
     *             .supply(field(PhoneNumber.class, "phoneNumbers"), "+1")
     *             .create();
     * }</pre>
     * <p>
     * For supplying random values, see {@link #supply(SelectorGroup, Generator)}.
     *
     * @param selectors for fields and/or classes this method should be applied to
     * @param value     value to set
     * @param <V>       type of the value
     * @return API builder reference
     * @see #supply(SelectorGroup, Supplier)
     */
    <V> InstancioApi<T> set(SelectorGroup selectors, V value);

    /**
     * Supplies a <b>non-random</b> value for a field or class using a {@link Supplier}.
     * <p>
     * Example:
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
     * Note: Instancio will not modify the supplied instance in any way. If the {@code PhoneNumber} class
     * has other fields, they will be ignored.
     * <p>
     * For supplying random values, see {@link #supply(SelectorGroup, Generator)}.
     *
     * @param selectors for fields and/or classes this method should be applied to
     * @param supplier  providing the value for given selectors
     * @param <V>       type of the value to generate
     * @return API builder reference
     * @see #supply(SelectorGroup, Generator)
     */
    <V> InstancioApi<T> supply(SelectorGroup selectors, Supplier<V> supplier);

    /**
     * Supplies a randomised value for a field or class using a custom {@link Generator}.
     * <p>
     * Example:
     * <pre>{@code
     *     Person person = Instancio.of(Person.class)
     *             .supply(field(Address.class, "phoneNumbers"), random -> List.of(
     *                     // Generate phone numbers with a random country code, either US or Mexico
     *                     new PhoneNumber(random.from("+1", "+52"), "123-55-66"),
     *                     new PhoneNumber(random.from("+1", "+52"), "123-77-88")))
     *             .create();
     * }</pre>
     * <p>
     * Note: Instancio will not modify the supplied instance in any way. If the {@code PhoneNumber} class
     * has other fields, they will be ignored.
     *
     * @param selectors for fields and/or classes this method should be applied to
     * @param generator that will provide the values
     * @param <V>       type of the value to generate
     * @return API builder reference
     */
    <V> InstancioApi<T> supply(SelectorGroup selectors, Generator<V> generator);

    /**
     * Generates a random value for a field or class using a built-in generator.
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
     * @param selectors for fields and/or classes this method should be applied to
     * @param gen       provider of built-in generators
     * @param <V>       type of the value to generate
     * @param <S>       generator spec type
     * @return API builder reference
     */
    <V, S extends GeneratorSpec<V>> InstancioApi<T> generate(SelectorGroup selectors, Function<Generators, S> gen);

    /**
     * A callback that gets invoked after an object has been fully populated.
     * <p>
     * Example:
     * <pre>{@code
     *     // Sets countryCode field on all instances of Phone to the specified value
     *     Person person = Instancio.of(Person.class)
     *             .onComplete(all(Phone.class), (Phone phone) -> phone.setCountryCode("+1"))
     *             .create();
     * }</pre>
     *
     * @param selectors for fields and/or classes this method should be applied to
     * @param callback  to invoke after object has been populated
     * @param <V>       type of object handled by the callback
     * @return API builder reference
     */
    <V> InstancioApi<T> onComplete(SelectorGroup selectors, OnCompleteCallback<V> callback);

    /**
     * Maps target field or class to the given subtype. This can be used
     * in the following cases:
     *
     * <ol>
     *     <li>to specify an implementation for interfaces or abstract classes</li>
     *     <li>to override default implementations used by Instancio</li>
     * </ol>
     * <b>Specify an implementation for an abstract type</b>
     * <p>
     * When Instancio encounters an interface or an abstract type it is not aware of
     * (for example, that is not part of the JDK), it will not be able to instantiate it.
     * This method can be used to specify an implementation to use in such cases.
     * For example:
     *
     * <pre>{@code
     *     WidgetContainer container = Instancio.of(WidgetContainer.class)
     *             .map(all(AbstractWidget.class), ConcreteWidget.class)
     *             .create();
     * }</pre>
     * <p>
     * <b>Override default implementations</b>
     * <p>
     * By default, Instancio uses certain defaults for collection classes, for example
     * {@link java.util.ArrayList} for {@link java.util.List}.
     * If an alternative implementation is required, this method allows to specify it:
     *
     * <pre>{@code
     *     Person person = Instancio.of(Person.class)
     *             .map(all(List.class), LinkedList.class)
     *             .create();
     * }</pre>
     * <p>
     * will use the {@code LinkedList} implementation for all {@code List}s.
     *
     * @param selectors for fields and/or classes this method should be applied to
     * @param subtype   to map the selectors to
     * @return API builder reference
     */
    InstancioApi<T> map(SelectorGroup selectors, Class<?> subtype);

    /**
     * Override default settings for generated values.
     * Settings include collection sizes, string lengths, numeric ranges, etc.
     *
     * @param settings to use
     * @return API builder reference
     */
    InstancioApi<T> withSettings(Settings settings);

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