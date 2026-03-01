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
package org.instancio;

import org.instancio.documentation.ExperimentalApi;
import org.instancio.feed.Feed;
import org.instancio.feed.FeedProvider;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorSpec;
import org.instancio.settings.SettingKey;
import org.instancio.settings.Settings;
import org.jspecify.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Instancio API for generating instances of a class populated with random data.
 *
 * @param <T> type to create
 * @since 1.0.1
 */
public interface InstancioApi<T> extends
        BaseApi<T>,
        SettingsApi,
        LenientModeApi,
        VerboseModeApi {

    /**
     * Creates a new instance of a class and populates it with data.
     *
     * <p>Example:
     * <pre>{@code
     * Person person = Instancio.of(Person.class)
     *     // snip...
     *     .create();
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
     * <p>Example:
     * <pre>{@code
     * Result<Person> result = Instancio.of(Person.class)
     *     .set(field(Person::getName), "Homer Simpson")
     *     .asResult();
     *
     * Person person = result.get();
     * long seed = result.getSeed();
     * }</pre>
     *
     * @return result containing the created object
     * @since 1.5.1
     */
    Result<T> asResult();

    /**
     * A convenience method for mapping the result (as returned by the
     * {@link #create()} method) to another object using a {@code function}.
     *
     * <p>For example, this method can be used to return the result
     * as JSON or other formats:
     *
     * <pre>{@code
     * String json = Instancio.of(Person.class).as(json());
     * }</pre>
     * <p>
     * where {@code json()} is a user-defined method implemented
     * using the preferred library, e.g. using Jackson:
     *
     * <pre>{@code
     * public static <T> Function<T, String> json() {
     *     return result -> new ObjectMapper()
     *             .writerWithDefaultPrettyPrinter()
     *             .writeValueAsString(result);
     * }
     * }</pre>
     *
     * @param function the function for mapping the result
     * @param <R>      the type of object the result is mapped to
     * @return the object returned by applying the mapping function to the result
     * @since 4.8.0
     */
    @ExperimentalApi
    default <R extends @Nullable Object> R as(Function<@Nullable T, R> function) {
        return function.apply(create());
    }

    /**
     * Creates an infinite {@link Stream} of objects. Ensure {@code limit()}
     * is called on the returned stream to prevent an infinite loop.
     *
     * <p>Each object is generated separately when using this method.
     * For instance, if a stateful generator like a sequence generator
     * is used, the sequence will restart from one for each object:
     *
     * <pre>{@code
     * List<Person> persons = Instancio.of(Person.class)
     *     .generate(field(Person::getId), gen -> gen.longSeq())
     *     .stream()
     *     .limit(3)
     *     .collect(Collectors.toList());
     *
     * // Output:
     * // [Person[id=1], Person[id=1], Person[id=1]]
     * }</pre>
     *
     * <p>Compared to using {@code ofList()}, where the sequence is continuous:
     *
     * <pre>{@code
     * List<Person> persons = Instancio.ofList(Person.class)
     *     .size(3)
     *     .generate(field(Person::getId), gen -> gen.longSeq())
     *     .create();
     *
     * // Output:
     * // [Person[id=1], Person[id=2], Person[id=3]]
     * }</pre>
     *
     * @return an infinite stream of object instances
     * @since 1.1.9
     */
    Stream<T> stream();

    /**
     * Creates a model containing generation parameters for creating an object
     * A model acts as a template for creating objects or other models.
     *
     * <p>The example below illustrates how to create a reusable model
     * with predefined attributes, which serves as a template for generating
     * objects with base properties:
     *
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
     * <p>For more information, see:
     *
     * <ul>
     *   <li>The <a href="https://www.instancio.org/user-guide/#using-models">
     *       Using Models</a> section of the user guide</li>
     *   <li><a href="https://www.instancio.org/articles/creating-object-templates-using-models/">
     *     Creating Object Templates Using Models</a> article at instancio.org</li>
     * </ul>
     *
     * @return a model that can be used as a template for creating objects
     * @see #setModel(TargetSelector, Model)
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
    <V extends @Nullable Object> InstancioApi<T> set(TargetSelector selector, V value);

    /**
     * {@inheritDoc}
     *
     * @since 4.4.0
     */
    @Override
    @ExperimentalApi
    <V> InstancioApi<T> setModel(TargetSelector selector, Model<V> model);

    /**
     * {@inheritDoc}
     *
     * @since 1.0.1
     */
    @Override
    <V extends @Nullable Object> InstancioApi<T> supply(TargetSelector selector, Supplier<V> supplier);

    /**
     * {@inheritDoc}
     *
     * @since 1.0.1
     */
    @Override
    <V extends @Nullable Object> InstancioApi<T> supply(TargetSelector selector, Generator<V> generator);

    /**
     * {@inheritDoc}
     *
     * @since 2.2.0
     */
    @Override
    <V extends @Nullable Object> InstancioApi<T> generate(TargetSelector selector, GeneratorSpecProvider<V> gen);

    /**
     * {@inheritDoc}
     *
     * @since 2.6.0
     */
    @Override
    @ExperimentalApi
    <V extends @Nullable Object> InstancioApi<T> generate(TargetSelector selector, GeneratorSpec<V> spec);

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
     * @since 4.6.0
     */
    @Override
    @ExperimentalApi
    <V> InstancioApi<T> filter(TargetSelector selector, FilterPredicate<V> predicate);

    /**
     * {@inheritDoc}
     *
     * @since 4.7.0
     */
    @Override
    @ExperimentalApi
    InstancioApi<T> setBlank(TargetSelector selector);

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
     * @since 4.3.1
     */
    @Override
    <V extends @Nullable Object> InstancioApi<T> withSetting(SettingKey<V> key, V value);

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

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    @ExperimentalApi
    InstancioApi<T> applyFeed(TargetSelector selector, Feed feed);

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    @ExperimentalApi
    InstancioApi<T> applyFeed(TargetSelector selector, FeedProvider provider);

    /**
     * {@inheritDoc}
     *
     * @since 4.8.0
     */
    @Override
    @ExperimentalApi
    InstancioApi<T> withUnique(TargetSelector selector);
}