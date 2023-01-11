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

import org.instancio.generator.AfterGenerate;
import org.instancio.generator.Generator;
import org.instancio.generators.Generators;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;

import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Instancio API for generating instances of a class populated with random data.
 *
 * @param <T> type to create
 * @since 1.0.1
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
     * @since 1.0.1
     */
    T create();

    /**
     * Returns a {@link Result} containing the created object and seed value
     * used to generate its values. The seed value can be used to reproduce
     * the same object again.
     *
     * @return result containing the created object
     * @since 1.5.1
     */
    Result<T> asResult();

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
     * @since 1.1.9
     */
    Stream<T> stream();

    /**
     * Creates a model containing all the information for populating a class.
     * <p>
     * The model can be useful when class population needs to be customised
     * and the customisations need to be re-used in different parts of the code.
     *
     * <p>Example:
     * <pre>{@code
     *   Model<Person> simpsons = Instancio.of(Person.class)
     *       .set(field(Person::getLastName), "Simpson")
     *       .set(field(Address::getCity), "Springfield")
     *       .generate(field(Person::getAge), gen -> gen.ints().range(40, 50))
     *       .toModel();
     *
     *   Person homer = Instancio.of(simpsons)
     *       .set(field(Person::getFirstName), "Homer")
     *       .set(all(Gender.class), Gender.MALE)
     *       .create();
     *
     *   Person marge = Instancio.of(simpsons)
     *       .set(field(Person::getFirstName), "Marge")
     *       .set(all(Gender.class), Gender.FEMALE)
     *       .create();
     * }</pre>
     *
     * @return a model that can be used as a template for creating objects
     * @since 1.0.1
     */
    Model<T> toModel();

    /**
     * Specifies that a class or field should be ignored.
     * <p>
     * Example:
     * <pre>{@code
     *   Person person = Instancio.of(Person.class)
     *       .ignore(field(Person::getPets))
     *       .ignore(field(Address::getPhoneNumbers))
     *       .ignore(allStrings())
     *       .create();
     * }</pre>
     * <p>
     * will create a fully populated person, but will ignore the {@code address} field
     * and all string fields.
     *
     * @param selector for fields and/or classes this method should be applied to
     * @return API builder reference
     */
    InstancioApi<T> ignore(TargetSelector selector);

    /**
     * Specifies that a field or class is nullable. By default, Instancio assigns
     * non-null values. If marked as nullable, Instancio will generate either
     * a null or non-null value.
     *
     * <p>Example:
     * <pre>{@code
     *     Person person = Instancio.of(Person.class)
     *             .withNullable(allStrings())
     *             .withNullable(field(Person::getAddress))
     *             .withNullable(fields().named("lastModified"))
     *             .create();
     * }</pre>
     *
     * @param selector for fields and/or classes this method should be applied to
     * @return API builder reference
     */
    InstancioApi<T> withNullable(TargetSelector selector);

    /**
     * Sets a value to matching selector targets.
     *
     * <p>Example: if {@code Person} class contains a {@code List<Phone>}, the following
     * snippet will set all the country code of all phone instances to "+1".
     * <pre>{@code
     *     Person person = Instancio.of(Person.class)
     *             .set(field(Phone::getCountryCode), "+1")
     *             .create();
     * }</pre>
     *
     * @param selector for fields and/or classes this method should be applied to
     * @param value    value to set
     * @param <V>      type of the value
     * @return API builder reference
     * @see #supply(TargetSelector, Supplier)
     */
    <V> InstancioApi<T> set(TargetSelector selector, V value);

    /**
     * Supplies an object using a {@link Supplier}.
     *
     * <p>Example:
     * <pre>{@code
     *     Person person = Instancio.of(Person.class)
     *             .supply(all(LocalDateTime.class), () -> LocalDateTime.now())
     *             .supply(field(Address::getPhoneNumbers), () -> List.of(
     *                 new PhoneNumber("+1", "123-45-67"),
     *                 new PhoneNumber("+1", "345-67-89")))
     *             .create();
     * }</pre>
     * <p>Note: Instancio <b>will not</b></p>
     * <ul>
     *   <li>populate or modify objects supplied by this method</li>
     *   <li>apply other {@code set()}, {@code supply()}, or {@code generate()}}
     *       methods with matching selectors to the supplied object</li>
     * </ul>
     *
     * <p>If you require the supplied object to be populated and/or selectors
     * to be applied, use the {@link #supply(TargetSelector, Generator)} method instead.
     *
     * @param selector for fields and/or classes this method should be applied to
     * @param supplier providing the value for given selector
     * @param <V>      type of the value to generate
     * @return API builder reference
     * @see #supply(TargetSelector, Generator)
     */
    <V> InstancioApi<T> supply(TargetSelector selector, Supplier<V> supplier);

    /**
     * Supplies an object using a {@link Generator} to matching selector targets.
     * By default, Instancio will populate uninitialised fields of the supplied
     * object. This includes fields with {@code null} or default primitive values.
     *
     * <p>This method supports the following use cases.
     *
     * <h4>Generate random objects</h4>
     *
     * <p>This method provides an instance of {@link Random} that can be used
     * to randomise generated objects. For example, if Instancio did not support
     * creation of {@code java.time.Year}, it could be generated as follows:
     *
     * <pre>{@code
     *   List<Year> years = Instancio.ofList(Year.class)
     *         .supply(all(Year.class), random -> Year.of(random.intRange(1900, 2000)))
     *         .create();
     * }</pre>
     *
     * <h4>Provide a partially initialised instance</h4>
     *
     * <p>In some cases, an object may need to be created in a certain state or
     * instantiated using a specific constructor to be in a valid state.
     * A partially initialised instance can be supplied using this method, and
     * Instancio will populate remaining fields that are {@code null}:
     *
     * <pre>{@code
     *   Person person = Instancio.of(Person.class)
     *       .supply(field(Person::getAddress), random -> new Address("Springfield", "USA"))
     *       .create();
     * }</pre>
     *
     * <p>This behaviour is controlled by the {@link AfterGenerate} hint specified
     * by {@link Generator#hints()}. Refer to the {@link Generator#hints()} Javadoc
     * for details, or <a href="https://www.instancio.org/user-guide/#custom-generators">
     * Custom Generators</a> section of the user guide.
     *
     * @param selector  for fields and/or classes this method should be applied to
     * @param generator that will provide the values
     * @param <V>       type of the value to generate
     * @return API builder reference
     * @see Generator
     * @see AfterGenerate
     * @see Keys#AFTER_GENERATE_HINT
     */
    <V> InstancioApi<T> supply(TargetSelector selector, Generator<V> generator);

    /**
     * Customises values using built-in generators provided by the {@code gen}
     * parameter, of type {@link Generators}.
     *
     * <p>Example:
     * <pre>{@code
     *   Person person = Instancio.of(Person.class)
     *       .generate(field(Person::getAge), gen -> gen.ints().range(18, 100))
     *       .generate(all(LocalDate.class), gen -> gen.temporal().localDate().past())
     *       .generate(field(Address::getPhoneNumbers), gen -> gen.collection().size(5))
     *       .generate(field(Address::getCity), gen -> gen.oneOf("Burnaby", "Vancouver", "Richmond"))
     *       .create();
     * }</pre>
     *
     * @param selector for fields and/or classes this method should be applied to
     * @param gen      provider of customisable built-in generators (also known as specs)
     * @param <V>      type of object to generate
     * @return API builder reference
     * @see Generators
     */
    <V> InstancioApi<T> generate(TargetSelector selector, GeneratorSpecProvider<V> gen);

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
     * @param selector for fields and/or classes this method should be applied to
     * @param callback to invoke after object has been populated
     * @param <V>      type of object handled by the callback
     * @return API builder reference
     * @since 1.0.4
     */
    <V> InstancioApi<T> onComplete(TargetSelector selector, OnCompleteCallback<V> callback);

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
     *             .subtype(all(AbstractWidget.class), ConcreteWidget.class)
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
     *             .subtype(all(List.class), LinkedList.class)
     *             .create();
     * }</pre>
     * <p>
     * will use the {@code LinkedList} implementation for all {@code List}s.
     *
     * @param selector for fields and/or classes this method should be applied to
     * @param subtype  to map the selector to
     * @return API builder reference
     * @since 1.4.0
     */
    InstancioApi<T> subtype(TargetSelector selector, Class<?> subtype);

    /**
     * Override default {@link Settings} for generating values.
     * The {@link Settings} class supports various parameters, such as
     * collection sizes, string lengths, numeric ranges, and so on.
     * For a list of overridable settings, refer to the {@link Keys} class.
     *
     * @param settings to use
     * @return API builder reference
     * @see Keys
     * @since 1.0.1
     */
    InstancioApi<T> withSettings(Settings settings);

    /**
     * Sets the seed value for the random number generator. If the seed is not specified,
     * a random seed will be used. Specifying the seed is useful for reproducing test results.
     * By specifying the seed value, the same random data will be generated again.
     *
     * <p>
     * Example:
     * <pre>{@code
     *     // Generates a different UUID each time
     *     UUID result = Instancio.create(UUID.class);
     *
     *     // Generates the same UUID each time
     *     UUID result = Instancio.of(UUID.class)
     *             .withSeed(1234)
     *             .create();
     * }</pre>
     *
     * @param seed for the random number generator
     * @return API builder reference
     * @since 1.0.1
     */
    InstancioApi<T> withSeed(long seed);

    /**
     * Disables strict mode in which unused selectors trigger an error.
     * In lenient mode unused selectors are simply ignored.
     * <p>
     * This method is a shorthand for:
     *
     * <pre>{@code
     *     Example example = Instancio.of(Example.class)
     *         .withSettings(Settings.create().set(Keys.MODE, Mode.LENIENT))
     *         .create();
     * }</pre>
     *
     * @return API builder reference
     * @since 1.4.1
     */
    InstancioApi<T> lenient();
}