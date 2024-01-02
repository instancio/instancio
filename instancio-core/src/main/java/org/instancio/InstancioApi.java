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
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorSpec;
import org.instancio.settings.Settings;

import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Instancio API for generating instances of a class populated with random data.
 *
 * @param <T> type to create
 * @since 1.0.1
 */
public interface InstancioApi<T> extends
        InstancioOperations<T>,
        LenientMode<T>,
        VerboseMode<T> {

    /**
     * Creates a new instance of a class and populates it with data.
     * <p>
     * Example:
     * <pre>{@code
     * Person person = Instancio.of(Person.class).create();
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
     * Creates an infinite stream of distinct, fully populated object instances.
     *
     * <p>Example:
     * <pre>{@code
     * List<Person> persons = Instancio.of(Person.class)
     *     .generate(field(Person::getAge), gen -> gen.ints().range(18, 100))
     *     .stream()
     *     .limit(5)
     *     .collect(Collectors.toList());
     * }</pre>
     *
     * <p><b>Warning:</b> {@code limit()} must be called to avoid an infinite loop.
     *
     * <p>All objects in the stream are independent of each other.
     *
     * @return an infinite stream of distinct object instances
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
     * Model<Person> simpsons = Instancio.of(Person.class)
     *     .set(field(Person::getLastName), "Simpson")
     *     .set(field(Address::getCity), "Springfield")
     *     .generate(field(Person::getAge), gen -> gen.ints().range(40, 50))
     *     .toModel();
     *
     * Person homer = Instancio.of(simpsons)
     *     .set(field(Person::getFirstName), "Homer")
     *     .set(all(Gender.class), Gender.MALE)
     *     .create();
     *
     * Person marge = Instancio.of(simpsons)
     *     .set(field(Person::getFirstName), "Marge")
     *     .set(all(Gender.class), Gender.FEMALE)
     *     .create();
     * }</pre>
     *
     * @return a model that can be used as a template for creating objects
     * @since 1.0.1
     */
    Model<T> toModel();

    /**
     * {@inheritDoc}
     *
     * @since 1.0.1
     */
    @Override
    InstancioApi<T> ignore(TargetSelector selector);

    /**
     * {@inheritDoc}
     *
     * @since 1.0.1
     */
    @Override
    InstancioApi<T> withNullable(TargetSelector selector);

    /**
     * {@inheritDoc}
     *
     * @since 1.2.3
     */
    @Override
    <V> InstancioApi<T> set(TargetSelector selector, V value);

    /**
     * {@inheritDoc}
     *
     * @since 1.0.1
     */
    @Override
    <V> InstancioApi<T> supply(TargetSelector selector, Supplier<V> supplier);

    /**
     * {@inheritDoc}
     *
     * @since 1.0.1
     */
    @Override
    <V> InstancioApi<T> supply(TargetSelector selector, Generator<V> generator);

    /**
     * {@inheritDoc}
     *
     * @since 2.2.0
     */
    @Override
    <V> InstancioApi<T> generate(TargetSelector selector, GeneratorSpecProvider<V> gen);

    /**
     * {@inheritDoc}
     *
     * @since 2.6.0
     */
    @Override
    @ExperimentalApi
    <V> InstancioApi<T> generate(TargetSelector selector, GeneratorSpec<V> spec);

    /**
     * {@inheritDoc}
     *
     * @since 1.0.4
     */
    @Override
    <V> InstancioApi<T> onComplete(TargetSelector selector, OnCompleteCallback<V> callback);

    /**
     * {@inheritDoc}
     *
     * @since 1.4.0
     */
    @Override
    InstancioApi<T> subtype(TargetSelector selector, Class<?> subtype);

    /**
     * {@inheritDoc}
     *
     * @since 3.0.0
     */
    @Override
    @ExperimentalApi
    InstancioApi<T> assign(Assignment... assignments);

    /**
     * {@inheritDoc}
     *
     * @since 2.9.0
     */
    @Override
    InstancioApi<T> withMaxDepth(int maxDepth);

    /**
     * {@inheritDoc}
     *
     * @since 1.0.1
     */
    @Override
    InstancioApi<T> withSettings(Settings settings);

    /**
     * {@inheritDoc}
     *
     * @since 1.0.1
     */
    @Override
    InstancioApi<T> withSeed(long seed);

    /**
     * {@inheritDoc}
     *
     * @since 1.4.1
     */
    @Override
    InstancioApi<T> lenient();

    /**
     * {@inheritDoc}
     *
     * @since 3.0.0
     */
    @Override
    @ExperimentalApi
    InstancioApi<T> verbose();
}