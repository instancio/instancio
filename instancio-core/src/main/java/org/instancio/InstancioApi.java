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
     * @param target to ignore
     * @return API builder reference
     */
    InstancioApi<T> ignore(Binding target);

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
     * @param target that is nullable
     * @return API builder reference
     */
    InstancioApi<T> withNullable(Binding target);

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
     * For supplying random values, see {@link #supply(Binding, Generator)}.
     *
     * @param target   binding
     * @param supplier for the target's value
     * @param <V>      type of the value to generate
     * @return API builder reference
     * @see #supply(Binding, Generator)
     */
    <V> InstancioApi<T> supply(Binding target, Supplier<V> supplier);

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
     * @param target    binding
     * @param generator for supplying the target's value
     * @param <V>       type of the value to generate
     * @return API builder reference
     */
    <V> InstancioApi<T> supply(Binding target, Generator<V> generator);

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
     * @param target binding
     * @param gen    provider of built-in generators
     * @param <V>    type of the value to generate
     * @param <S>    generator spec type
     * @return API builder reference
     */
    <V, S extends GeneratorSpec<V>> InstancioApi<T> generate(Binding target, Function<Generators, S> gen);

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
     * @param target   binding
     * @param callback to invoke after object has been populated
     * @param <V>      type of object handled by the callback
     * @return API builder reference
     */
    <V> InstancioApi<T> onComplete(Binding target, OnCompleteCallback<V> callback);

    /**
     * Maps target field or class to the given subclass.
     * <p>
     * For example, by default Instancio will assign an {@link java.util.ArrayList}
     * to a {@link java.util.List} field. If an alternative implementation is
     * required, this method allows to specify it:
     *
     * <pre>{@code
     *     Person person = Instancio.of(Person.class)
     *             .map(all(List.class), Vector.class)
     *             .create();
     * }</pre>
     * <p>
     * will assign all {@code List}s to {@code Vector}s.
     *
     * @param target  binding
     * @param subtype of the type {@code target} binding
     * @return API builder reference
     */
    InstancioApi<T> map(Binding target, Class<?> subtype);

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
