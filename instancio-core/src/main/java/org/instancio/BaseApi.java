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

import org.instancio.documentation.ExperimentalApi;
import org.instancio.exception.InstancioApiException;
import org.instancio.feed.Feed;
import org.instancio.feed.FeedProvider;
import org.instancio.generator.AfterGenerate;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorSpec;
import org.instancio.generators.Generators;
import org.instancio.settings.Keys;
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
interface BaseApi<T> {

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
    BaseApi<T> ignore(TargetSelector selector);

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
    BaseApi<T> withNullable(TargetSelector selector);

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
    <V> BaseApi<T> set(TargetSelector selector, V value);

    /**
     * Applies given {@code model} to the specified {@code selector}.
     *
     * <p>For example, given the following classes and {@link Model}:
     *
     * <pre>{@code
     * record Foo(String value) {}
     * record Container(Foo fooA, Foo fooB) {}
     *
     * Model<Foo> fooModel = Instancio.of(Foo.class)
     *     .set(field(Foo::value), "foo")
     *     .toModel();
     * }</pre>
     *
     * <p>The model can be applied to a specific {@code Foo} field declared
     * by the {@code Container}:
     *
     * <pre>{@code
     * Container container = Instancio.of(Container.class)
     *     .setModel(field(Container::fooA), fooModel)
     *     .create();
     * }</pre>
     *
     * <p>Alternatively, to apply the model to all instances of {@code Foo}:
     *
     * <pre>{@code
     * Container container = Instancio.of(Container.class)
     *     .setModel(all(Foo.class), fooModel)
     *     .create();
     * }</pre>
     *
     * <p><b>Note:</b> the following properties of the supplied model
     * are <b>not</b> applied to the target object:
     *
     * <ul>
     *   <li>{@link Settings}</li>
     *   <li>{@code lenient()} mode</li>
     *   <li>custom seed value</li>
     * </ul>
     *
     * <p>See the
     * <a href="https://www.instancio.org/user-guide/#using-setmodel">user guide</a>
     * for further details.
     *
     * @param selector to which the model will be applied to
     * @param model    to apply to the given selector's target
     * @param <V>      the type of object this model represents
     * @return API builder reference
     * @since 4.4.0
     */
    @ExperimentalApi
    <V> BaseApi<T> setModel(TargetSelector selector, Model<V> model);

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
    <V> BaseApi<T> supply(TargetSelector selector, Supplier<V> supplier);

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
    <V> BaseApi<T> supply(TargetSelector selector, Generator<V> generator);

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
    <V> BaseApi<T> generate(TargetSelector selector, GeneratorSpecProvider<V> gen);

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
    <V> BaseApi<T> generate(TargetSelector selector, GeneratorSpec<V> spec);

    /**
     * A callback that gets invoked after the root object has been fully populated.
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
     * OnCompleteCallback<String> callback = (String value) -> {
     *     // do something with the value
     * };
     *
     * Person person = Instancio.of(Person.class)
     *     .set(field(Phone::getCountryCode), "+1")
     *      // The callback will not be invoked because
     *      // Phone.countryCode value was provided via set() method
     *     .onComplete(field(Phone::getCountryCode), callback)
     *     .create();
     * }</pre>
     *
     * @param selector for fields and/or classes this method should be applied to
     * @param callback to invoke after object has been populated
     * @param <V>      type of object handled by the callback
     * @return API builder reference
     * @since 4.0.0
     */
    <V> BaseApi<T> onComplete(TargetSelector selector, OnCompleteCallback<V> callback);

    /**
     * Filters generated values using given {@code predicate}.
     * If a value is rejected, a new value will be generated,
     * which will also be tested against the {@code predicate}.
     * If no value is accepted after {@link Keys#MAX_GENERATION_ATTEMPTS},
     * an exception will be thrown.
     *
     * <p>A simple example is to generate a list of even numbers:
     * <pre>{@code
     * List<Integer> evenNumbers = Instancio.ofList(Integer.class)
     *     .filter(allInts(), (Integer i) -> i % 2 == 0)
     *     .create();
     * }</pre>
     *
     * <p>Note that customising objects using this method is less efficient
     * than {@link #generate(TargetSelector, GeneratorSpecProvider)}.
     * The latter should be preferred where possible.
     *
     * @param selector  for fields and/or classes this method should be applied to
     * @param predicate that must be satisfied by the generated value
     * @param <V>       the type of object the predicate is evaluated against
     * @return API builder reference
     * @see Keys#MAX_GENERATION_ATTEMPTS
     * @since 4.6.0
     */
    @ExperimentalApi
    <V> BaseApi<T> filter(TargetSelector selector, FilterPredicate<V> predicate);

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
    BaseApi<T> subtype(TargetSelector selector, Class<?> subtype);

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
    BaseApi<T> assign(Assignment... assignments);

    /**
     * Applies the provided {@code feed} to the specified {@code selector}.
     * The {@code selector} targets must be POJOs or Java {@code record}s.
     * Properties from the {@code feed} will be automatically mapped to the selected objects.
     *
     * <p>For example, we can generate instances of the following record:
     *
     * <pre>{@code
     * record Person(String firstName, String lastName, String fullName,
     *               int age, String username, String email) {}
     * }</pre>
     *
     * <p>using data from a CSV file (formatted for readability):
     *
     * <pre>
     * firstName, lastName, age, username
     * John,      Doe,      24,  john_doe
     * Alice,     Smith,    55,  alice_s
     * # more entries...
     * </pre>
     *
     * <p>by defining the following feed:
     *
     * <pre>{@code
     * @Feed.Source(resource = "persons.csv")
     * interface PersonFeed extends Feed {
     *
     *     @TemplateSpec("${firstName} ${lastName}")
     *     FeedSpec<String> fullName();
     *
     *     @GeneratedSpec(CustomEmailGenerator.class)
     *     FeedSpec<String> email();
     * }
     * }</pre>
     *
     * <p>and applying the feed using the {@code all(Person.class)} selector:
     *
     * <pre>{@code
     * Feed personFeed = Instancio.createFeed(PersonFeed.class);
     *
     * List<Person> persons = Instancio.ofList(Person.class)
     *     .applyFeed(all(Person.class), personFeed)
     *     .create();
     * }</pre>
     *
     * <p>Data from the CSV will be mapped to {@code Person} fields
     * by matching field names to property names in the data file.
     *
     * <ul>
     *   <li>Note that {@code PersonFeed} does not need to declare
     *       {@code firstName()} and {@code lastName()} methods
     *       (they are mapped automatically).</li>
     *   <li>{@code fullName()} and {@code email()} can also
     *       be mapped to the {@code Person} object, even though
     *       these are generated and not present in the data file.</li>
     * </ul>
     *
     * @param selector for fields and/or classes this method should be applied to
     * @param feed     the feed to apply to the selector
     * @return API builder reference
     * @see Instancio#createFeed(Class)
     * @see #applyFeed(TargetSelector, FeedProvider)
     * @since 5.0.0
     */
    @ExperimentalApi
    BaseApi<T> applyFeed(TargetSelector selector, Feed feed);

    /**
     * Creates a feed and applies it to the specified {@code selector}.
     * The selector's target must be a POJO or a Java {@code record}.
     * Properties from the feed will be automatically mapped to the
     * selected object.
     *
     * <p>For example, given the following CSV file (formatted for readability):
     *
     * <pre>
     * firstName, lastName, age, username
     * John,      Doe,      24,  john_doe
     * Alice,     Smith,    55,  alice_s
     * # more entries...
     * </pre>
     *
     * <p>and a record with matching properties:
     *
     * <pre>{@code
     * class Person(String firstName, String lastName, int age, String username) {}
     * }</pre>
     *
     * <p>a feed can be applied as follows:
     *
     * <pre>{@code
     * List<Person> persons = Instancio.ofList(Person.class)
     *     .applyFeed(all(Person.class), feed -> feed.ofResource("data/persons.csv"))
     *     .create();
     *
     * // Output:
     * // [Person[firstName=John, lastName=Doe, age=24, username=john_doe],
     * //  Person[firstName=Alice, lastName=Smith, age=55, username=alice_s]]
     * }</pre>
     *
     * @param selector for fields and/or classes this method should be applied to
     * @param provider the provider API for specifying feed configuration
     * @return API builder reference
     * @see #applyFeed(TargetSelector, Feed)
     * @since 5.0.0
     */
    @ExperimentalApi
    BaseApi<T> applyFeed(TargetSelector selector, FeedProvider provider);

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
    BaseApi<T> withMaxDepth(int maxDepth);

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
    BaseApi<T> withSeed(long seed);

    /**
     * Specifies that a blank object should be generated for the selected target.
     *
     * <p>A blank object has the following properties:
     *
     * <ul>
     *   <li>value fields (strings, numbers, dates, etc) are {@code null}</li>
     *   <li>arrays, collections, and maps are empty</li>
     *   <li>nested POJOs are blank</li>
     * </ul>
     *
     * <p>Example:
     * <pre>{@code
     * Person person = Instancio.of(Person.class)
     *     .setBlank(field(Person::getAddress))
     *     .create();
     *
     * // Output:
     * // Person[
     * //   name="GNQTXA",
     * //   dateOfBirth=1988-04-09,
     * //   address=Address[street=null, city=null, country=null] // blank Address
     * //]
     * }</pre>
     *
     * @param selector for fields and/or classes this method should be applied to
     * @return API builder reference
     * @see Instancio#createBlank(Class)
     * @see Instancio#ofBlank(Class)
     * @since 4.7.0
     */
    @ExperimentalApi
    BaseApi<T> setBlank(TargetSelector selector);

    /**
     * Specifies that the given selector's target(s) should have unique values.
     *
     * <p>Example:
     * <pre>{@code
     * record Data(int foo, int bar) {}
     *
     * List<Data> results = Instancio.ofList(Data.class)
     *     .size(100)
     *     .withUnique(field(Data::foo))
     *     .create();
     * }</pre>
     *
     * <p>The above snippet generates a list of {@code Data} instances
     * with unique {@code foo} values. Note that values will be unique
     * across all targets that match the selector. For instance,
     * the following usages:
     *
     * <ul>
     *   <li>{@code withUnique(allInts())}</li>
     *   <li>{@code withUnique(all(field(Data::foo), field(Data::bar))}</li>
     * </ul>
     * <p>
     * would result in unique values for {@code foo} and {@code bar}
     * with no overlap (i.e. {@code foo} and {@code bar} are disjoint).
     * To generate unique values per field (with potential overlap),
     * the {@code withUnique()} method must be specified per field:
     *
     * <pre>{@code
     * List<Data> results = Instancio.ofList(Data.class)
     *     .size(100)
     *     .withUnique(field(Data::foo)) // e.g. { 601, 42, 573, ...}
     *     .withUnique(field(Data::bar)) // e.g. { 888, 251, 42, ...}
     *     .create();
     * }</pre>
     *
     * <p>If it is impossible to generate a sufficient number of
     * unique values after a certain number of attempts,
     * an exception will be thrown:
     *
     * <pre>{@code
     * List<Boolean> results = Instancio.ofList(Boolean.class)
     *     .size(10) // will fail as it's impossible to generate 10 unique booleans
     *     .withUnique(allBooleans())
     *     .create();
     * }</pre>
     *
     * @param selector for fields and/or classes this method should be applied to
     * @return API builder reference
     * @since 4.8.0
     */
    @ExperimentalApi
    BaseApi<T> withUnique(TargetSelector selector);
}
