/*
 * Copyright 2022-2024 the original author or authors.
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

import org.instancio.exception.InstancioApiException;
import org.instancio.generator.AfterGenerate;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorSpec;
import org.instancio.generators.Generators;
import org.instancio.settings.Keys;
import org.instancio.settings.SettingKey;
import org.instancio.settings.Settings;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Defines top-level operations supported by the API.
 *
 * @param <T> the type of object to create
 * @since 4.0.0
 */
interface InstancioOperations<T> {

    /**
     * Specifies that a class or field should be ignored.
     *
     * <p>Example:
     * <pre>{@code
     * Person person = Instancio.of(Person.class)
     *     .ignore(field(Phone::getPhoneNumber))
     *     .ignore(allStrings())
     *     .create();
     * }</pre>
     *
     * <p>will create a fully populated person, but will ignore the
     * {@code getPhoneNumber} field, and all strings.
     *
     * <h4>Precedence</h4>
     *
     * <p>This method has higher precedence than other API methods.
     * Once a target is ignored, no other selectors will apply.
     * For example, the following snippet will trigger an unused selector error
     * because {@code field(Phone::getNumber)} is redundant:
     *
     * <pre>{@code
     * Person person = Instancio.of(Person.class)
     *     .ignore(all(Phone.class))
     *     .set(field(Phone::getNumber), "123-45-56")
     *     .create();
     * }</pre>
     *
     * <h4>Usage with Java records</h4>
     *
     * <p>If {@code ignore()} targets one of the required arguments of a record
     * constructor, then a default value for the ignored type will be generated.
     *
     * <p>Example:
     * <pre>{@code
     * record PersonRecord(String name, int age) {}
     *
     * PersonRecord person = Instancio.of(PersonRecord.class)
     *     .ignore(allInts())
     *     .ignore(allStrings())
     *     .create();
     *
     * // will produce: PersonRecord[name=null, age=0]
     * }</pre>
     *
     * @param selector for fields and/or classes this method should be applied to
     * @return API builder reference
     * @since 4.0.0
     */
    InstancioOperations<T> ignore(TargetSelector selector);

    /**
     * Specifies that a field or class is nullable. By default, Instancio assigns
     * non-null values to fields. If marked as nullable, Instancio will generate either
     * a null or non-null value.
     *
     * <p>Example:
     * <pre>{@code
     * Person person = Instancio.of(Person.class)
     *     .withNullable(allStrings())
     *     .withNullable(field(Person::getAddress))
     *     .withNullable(fields().named("lastModified"))
     *     .create();
     * }</pre>
     *
     * <b>Note:</b> a type marked as nullable using this method is only nullable
     * when declared as a field, but <b>not</b> as a collection element,
     * or map key/value. For example, {@code withNullable(allStrings())}
     * will not generate nulls in a {@code List<String>}.
     *
     * @param selector for fields and/or classes this method should be applied to
     * @return API builder reference
     */
    InstancioOperations<T> withNullable(TargetSelector selector);

    /**
     * Sets a value to matching selector targets.
     *
     * <p>Example: if {@code Person} class contains a {@code List<Phone>}, the following
     * snippet will set all the country code of all phone instances to "+1".
     * <pre>{@code
     * Person person = Instancio.of(Person.class)
     *     .set(field(Phone::getCountryCode), "+1")
     *     .create();
     * }</pre>
     *
     * <p>Note: Instancio <b>will not</b></p>
     * <ul>
     *   <li>populate or modify objects supplied by this method</li>
     *   <li>apply other {@code set()}, {@code supply()}, or {@code generate()}}
     *       methods with matching selectors to the supplied object</li>
     *   <li>invoke {@code onComplete()} callbacks on supplied instances</li>
     * </ul>
     *
     * @param selector for fields and/or classes this method should be applied to
     * @param value    value to set
     * @param <V>      type of the value
     * @return API builder reference
     * @see #supply(TargetSelector, Supplier)
     * @since 4.0.0
     */
    <V> InstancioOperations<T> set(TargetSelector selector, V value);

    /**
     * Supplies an object using a {@link Supplier}.
     *
     * <p>Example:
     * <pre>{@code
     * Person person = Instancio.of(Person.class)
     *     .supply(all(LocalDateTime.class), () -> LocalDateTime.now())
     *     .supply(field(Address::getPhoneNumbers), () -> List.of(
     *         new PhoneNumber("+1", "123-45-67"),
     *         new PhoneNumber("+1", "345-67-89")))
     *     .create();
     * }</pre>
     *
     * <p>Note: Instancio <b>will not</b></p>
     * <ul>
     *   <li>populate or modify objects supplied by this method</li>
     *   <li>apply other {@code set()}, {@code supply()}, or {@code generate()}}
     *       methods with matching selectors to the supplied object</li>
     *   <li>invoke {@code onComplete()} callbacks on supplied instances</li>
     * </ul>
     *
     * <p>If you require the supplied object to be populated and/or selectors
     * to be applied, use the {@link #supply(TargetSelector, Generator)} method instead.
     *
     * @param selector for fields and/or classes this method should be applied to
     * @param supplier providing the value for given selector
     * @param <V>      type of the supplied value
     * @return API builder reference
     * @see #supply(TargetSelector, Generator)
     * @since 4.0.0
     */
    <V> InstancioOperations<T> supply(TargetSelector selector, Supplier<V> supplier);

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
     * List<Year> years = Instancio.ofList(Year.class)
     *     .supply(all(Year.class), random -> Year.of(random.intRange(1900, 2000)))
     *     .create();
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
     * Person person = Instancio.of(Person.class)
     *     .supply(field(Person::getAddress), random -> new Address("Springfield", "USA"))
     *     .create();
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
     * @since 4.0.0
     */
    <V> InstancioOperations<T> supply(TargetSelector selector, Generator<V> generator);

    /**
     * Customises values using built-in generators provided by the {@code gen}
     * parameter, of type {@link Generators}.
     *
     * <p>Example:
     * <pre>{@code
     * Person person = Instancio.of(Person.class)
     *     .generate(field(Person::getAge), gen -> gen.ints().range(18, 100))
     *     .generate(all(LocalDate.class), gen -> gen.temporal().localDate().past())
     *     .generate(field(Address::getPhoneNumbers), gen -> gen.collection().size(5))
     *     .generate(field(Address::getCity), gen -> gen.oneOf("Burnaby", "Vancouver", "Richmond"))
     *     .create();
     * }</pre>
     *
     * @param selector for fields and/or classes this method should be applied to
     * @param gen      provider of customisable built-in generators (also known as specs)
     * @param <V>      type of object to generate
     * @return API builder reference
     * @see #generate(TargetSelector, GeneratorSpec)
     * @see Generators
     */
    <V> InstancioOperations<T> generate(TargetSelector selector, GeneratorSpecProvider<V> gen);

    /**
     * Customises values using arbitrary generator specs.
     *
     * <p>Example:
     * <pre>{@code
     * Person person = Instancio.of(Person.class)
     *     .generate(field(Person::getAge), Instancio.ints().range(18, 100))
     *     .generate(all(LocalDate.class),  Instancio.temporal().localDate().past())
     *     .generate(field(Phone::getNumber),  MyCustomGenerators.phones().northAmerican())
     *     .create();
     * }</pre>
     *
     * @param selector for fields and/or classes this method should be applied to
     * @param spec     generator spec
     * @param <V>      type of object to generate
     * @return API builder reference
     * @see #generate(TargetSelector, GeneratorSpecProvider)
     * @since 4.0.0
     */
    <V> InstancioOperations<T> generate(TargetSelector selector, GeneratorSpec<V> spec);

    /**
     * A callback that gets invoked after an object has been fully populated.
     *
     * <p>Example:
     * <pre>{@code
     * // Sets countryCode field on all instances of Phone to the specified value
     * Person person = Instancio.of(Person.class)
     *     .onComplete(all(Phone.class), (Phone phone) -> phone.setCountryCode("+1"))
     *     .create();
     * }</pre>
     *
     * <p><b>Note:</b> callbacks are never invoked on objects provided using:
     *
     * <ul>
     *   <li>{@link #set(TargetSelector, Object)}</li>
     *   <li>{@link #supply(TargetSelector, Supplier)}</li>
     * </ul>
     *
     * <pre>{@code
     * Person person = Instancio.of(Person.class)
     *     .set(field(Phone::getCountryCode), "+1")
     *     .onComplete(field(Phone::getCountryCode), ...) // will not be invoked!
     *     .create();
     * }</pre>
     *
     * @param selector for fields and/or classes this method should be applied to
     * @param callback to invoke after object has been populated
     * @param <V>      type of object handled by the callback
     * @return API builder reference
     * @since 4.0.0
     */
    <V> InstancioOperations<T> onComplete(TargetSelector selector, OnCompleteCallback<V> callback);

    /**
     * Maps target field or class to the given subtype. This can be used
     * in the following cases:
     *
     * <ol>
     *   <li>to specify an implementation for interfaces or abstract classes</li>
     *   <li>to override default implementations used by Instancio</li>
     * </ol>
     * <b>Specify an implementation for an abstract type</b>
     * <p>
     * When Instancio encounters an interface or an abstract type it is not aware of
     * (for example, that is not part of the JDK), it will not be able to instantiate it.
     * This method can be used to specify an implementation to use in such cases.
     * For example:
     *
     * <pre>{@code
     * WidgetContainer container = Instancio.of(WidgetContainer.class)
     *     .subtype(all(AbstractWidget.class), ConcreteWidget.class)
     *     .create();
     * }</pre>
     * <p>
     * <b>Override default implementations</b>
     * <p>
     * By default, Instancio uses certain defaults for collection classes, for example
     * {@link ArrayList} for {@link List}.
     * If an alternative implementation is required, this method allows to specify it:
     *
     * <pre>{@code
     * Person person = Instancio.of(Person.class)
     *     .subtype(all(List.class), LinkedList.class)
     *     .create();
     * }</pre>
     * <p>
     * will use the {@code LinkedList} implementation for all {@code List}s.
     *
     * @param selector for fields and/or classes this method should be applied to
     * @param subtype  to map the selector to
     * @return API builder reference
     * @since 4.0.0
     */
    InstancioOperations<T> subtype(TargetSelector selector, Class<?> subtype);

    /**
     * Generates values based on given assignments.
     * An {@link Assignment} can be created using one of the builder patterns
     * provided by the {@link Assign} class.
     *
     * <ul>
     *   <li>{@code Assign.valueOf(originSelector).to(destinationSelector)}</li>
     *   <li>{@code Assign.given(originSelector).satisfies(predicate).set(destinationSelector, value)}</li>
     *   <li>{@code Assign.given(originSelector, destinationSelector).set(predicate, value)}</li>
     * </ul>
     *
     * <p>For example, the following snippet uses
     * {@link Assign#given(TargetSelector, TargetSelector)} to create
     * an assignment that sets {@code Phone.countryCode} based on
     * the value of the {@code Address.country} field:
     *
     * <pre>{@code
     * Assignment assignment = Assign.given(field(Address::getCountry), field(Phone::getCountryCode))
     *     .set(When.isIn("Canada", "USA"), "+1")
     *     .set(When.is("Italy"), "+39")
     *     .set(When.is("Poland"), "+48")
     *     .set(When.is("Germany"), "+49");
     *
     * Person person = Instancio.of(Person.class)
     *     .generate(field(Address::getCountry), gen -> gen.oneOf("Canada", "USA", "Italy", "Poland", "Germany"))
     *     .assign(assignment)
     *     .create();
     * }</pre>
     *
     * <p>The above API allows specifying different values for a given
     * origin/destination pair. An alternative for creating a conditional
     * is provided by {@link Assign#given(TargetSelector)}. This method
     * allows specifying different destination selectors for a given origin:
     *
     * <pre>{@code
     * Assignment shippedOrderAssignment = Assign.given(Order::getStatus)
     *     .is(OrderStatus.SHIPPED)
     *     .supply(field(Order::getDeliveryDueDate), () -> LocalDate.now().plusDays(2));
     *
     * Assignment cancelledOrderAssignment = Assign.given(Order::getStatus)
     *      .is(OrderStatus.CANCELLED)
     *      .set(field(Order::getCancellationReason), "Shipping delays")
     *      .generate(field(Order::getCancellationDate), gen -> gen.temporal().instant().past());
     *
     * List<Order> orders = Instancio.ofList(Order.class)
     *     .generate(all(OrderStatus.class), gen -> gen.oneOf(OrderStatus.SHIPPED, OrderStatus.CANCELLED))
     *     .assign(shippedOrderAssignment, cancelledOrderAssignment)
     *     .create();
     * }</pre>
     *
     * <h4>Limitations of assignments</h4>
     *
     * <p>Using assignments has a few limitations to be aware of.
     *
     * <ul>
     *   <li>The origin selector must match a single target.
     *       It must not be a {@link SelectorGroup} created via
     *       {@link Select#all(GroupableSelector...)} or primitive/wrapper
     *       selector, such as {@link Select#allInts()}</li>
     *   <li>An assignment where the origin selector's target is within
     *       a collection element must have a destination selector
     *       within the same collection element.</li>
     *   <li>Circular assignments will produce an error.</li>
     * </ul>
     *
     * @param assignments one or more assignment expressions for setting values
     * @return API builder reference
     * @throws InstancioApiException if the origin selector of an assignment
     *                               matches more than one target, or the
     *                               assignments form a cycle
     * @see Assign
     * @since 4.0.0
     */
    InstancioOperations<T> assign(Assignment... assignments);

    /**
     * Specifies the maximum depth for populating an object.
     * The root object is at depth zero. Children of the root
     * object are at depth 1, grandchildren at depth 2, and so on.
     *
     * <p>Instancio will populate values up to the maximum depth.
     * Beyond that, values will be {@code null} unless the maximum
     * depth is set to a higher value.
     *
     * <p>The default maximum depth is defined by {@link Keys#MAX_DEPTH}.
     *
     * <p><b>Note:</b> this method is a shorthand for:
     *
     * <pre>{@code
     * int maxDepth = 5;
     * Person person = Instancio.of(Person.class)
     *     .withSettings(Settings.create().set(Keys.MAX_DEPTH, maxDepth))
     *     .create();
     * }</pre>
     *
     * <p>If the maximum depth is specified using {@code Settings} <i>and</i>
     * this method, then this method takes precedence.
     *
     * @param maxDepth the maximum depth, must not be negative
     * @return API builder reference
     * @since 4.0.0
     */
    InstancioOperations<T> withMaxDepth(int maxDepth);

    /**
     * Override setting for the given {@code key} with the specified {@code value}.
     *
     * @param key   the setting key to override
     * @param value the setting value
     * @param <V>   the setting value type
     * @return API builder reference
     * @see Keys
     * @see #withSettings(Settings)
     * @since 4.3.1
     */
    <V> InstancioOperations<T> withSetting(SettingKey<V> key, V value);

    /**
     * Override default {@link Settings} for generating values.
     * The {@link Settings} class supports various parameters, such as
     * collection sizes, string lengths, numeric ranges, and so on.
     * For a list of overridable settings, refer to the {@link Keys} class.
     *
     * @param settings to use
     * @return API builder reference
     * @see Keys
     * @see #withSetting(SettingKey, Object)
     * @since 4.0.0
     */
    InstancioOperations<T> withSettings(Settings settings);

    /**
     * Sets the seed value for the random number generator. If the seed is not specified,
     * a random seed will be used. Specifying the seed is useful for reproducing test results.
     * By specifying the seed value, the same random data will be generated again.
     *
     * <p>
     * Example:
     * <pre>{@code
     * // Generates a different UUID each time
     * UUID result = Instancio.create(UUID.class);
     *
     * // Generates the same UUID each time
     * UUID result = Instancio.of(UUID.class)
     *     .withSeed(1234)
     *     .create();
     * }</pre>
     *
     * @param seed for the random number generator
     * @return API builder reference
     * @since 4.0.0
     */
    InstancioOperations<T> withSeed(long seed);
}
