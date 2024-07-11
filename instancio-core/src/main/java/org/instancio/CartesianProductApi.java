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
import org.instancio.feed.Feed;
import org.instancio.feed.FeedProvider;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorSpec;
import org.instancio.settings.SettingKey;
import org.instancio.settings.Settings;

import java.util.List;
import java.util.function.Supplier;

/**
 * Provides an API for generating the Cartesian product.
 *
 * <p>This class supports most of the Instancio API methods,
 * but provides additional methods for generating the Cartesian product:
 *
 * <ul>
 *   <li>{@link #with(TargetSelector, Object[])} for specifying product values
 *       (see the method's Javadoc for an example)</li>
 *   <li>{@link #create()} for obtaining the results as a list</li>
 * </ul>
 *
 * @param <T> type to create
 * @since 4.0.0
 */
@ExperimentalApi
public interface CartesianProductApi<T> extends
        InstancioOperations<T>,
        InstancioWithSettingsApi,
        LenientMode,
        VerboseMode {

    /**
     * Sets a range of values for generating the Cartesian product.
     * The results are returned as a list in lexicographical order
     * using the {@link #create()} method.
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
     * <p><b>Limitations</b>
     * <p>A selector passed to {@code with()} must match a single target.
     * For example, Cartesian product cannot be generated for collection elements:
     *
     * <pre>{@code
     * record Widget(String type, int num) {}
     * record Container(List<Widget> widgets) {}
     *
     * List<Container> results = Instancio.ofCartesianProduct(Container.class)
     *     .with(field(Widget::type), "FOO", "BAR", "BAZ")
     *     .with(field(Widget::num), 1, 2, 3)
     *     .create();
     * }</pre>
     *
     * <p>The above will produce an error with a message:
     * <code>"no item is available to emit()"</code>.
     *
     * @param selector for fields and/or classes this method should be applied to
     * @param values   the range of values to generate
     * @param <V>      type of the value
     * @return API builder reference
     * @since 4.0.0
     */
    @ExperimentalApi
    @SuppressWarnings("unchecked")
    <V> CartesianProductApi<T> with(TargetSelector selector, V... values);

    /**
     * Returns he Cartesian product generated from values specified via
     * the {@link #with(TargetSelector, Object[])} method as a list.
     *
     * @return Cartesian product as a list
     * @since 4.0.0
     */
    @ExperimentalApi
    List<T> create();

    /**
     * {@inheritDoc}
     *
     * @since 4.0.0
     */
    @Override
    CartesianProductApi<T> ignore(TargetSelector selector);

    /**
     * {@inheritDoc}
     *
     * @since 4.0.0
     */
    @Override
    CartesianProductApi<T> withNullable(TargetSelector selector);

    /**
     * {@inheritDoc}
     *
     * @since 4.0.0
     */
    @Override
    <V> CartesianProductApi<T> set(TargetSelector selector, V value);

    /**
     * {@inheritDoc}
     *
     * @since 4.4.0
     */
    @Override
    @ExperimentalApi
    <V> CartesianProductApi<T> setModel(TargetSelector selector, Model<V> model);

    /**
     * {@inheritDoc}
     *
     * @since 4.0.0
     */
    @Override
    <V> CartesianProductApi<T> supply(TargetSelector selector, Supplier<V> supplier);

    /**
     * {@inheritDoc}
     *
     * @since 4.0.0
     */
    @Override
    <V> CartesianProductApi<T> supply(TargetSelector selector, Generator<V> generator);

    /**
     * {@inheritDoc}
     *
     * @since 4.0.0
     */
    @Override
    <V> CartesianProductApi<T> generate(TargetSelector selector, GeneratorSpecProvider<V> gen);

    /**
     * {@inheritDoc}
     *
     * @since 4.0.0
     */
    @Override
    @ExperimentalApi
    <V> CartesianProductApi<T> generate(TargetSelector selector, GeneratorSpec<V> spec);

    /**
     * {@inheritDoc}
     *
     * @since 4.0.0
     */
    @Override
    <V> CartesianProductApi<T> onComplete(TargetSelector selector, OnCompleteCallback<V> callback);

    /**
     * {@inheritDoc}
     *
     * @since 4.6.0
     */
    @Override
    @ExperimentalApi
    <V> CartesianProductApi<T> filter(TargetSelector selector, FilterPredicate<V> predicate);

    /**
     * {@inheritDoc}
     *
     * @since 4.7.0
     */
    @Override
    @ExperimentalApi
    CartesianProductApi<T> setBlank(TargetSelector selector);

    /**
     * {@inheritDoc}
     *
     * @since 4.0.0
     */
    @Override
    CartesianProductApi<T> subtype(TargetSelector selector, Class<?> subtype);

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    @ExperimentalApi
    CartesianProductApi<T> applyFeed(TargetSelector selector, Feed feed);

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    @ExperimentalApi
    CartesianProductApi<T> applyFeed(TargetSelector selector, FeedProvider provider);

    /**
     * {@inheritDoc}
     *
     * @since 4.8.0
     */
    @Override
    @ExperimentalApi
    CartesianProductApi<T> withUnique(TargetSelector selector);

    /**
     * {@inheritDoc}
     *
     * @since 4.0.0
     */
    @Override
    @ExperimentalApi
    CartesianProductApi<T> assign(Assignment... assignments);

    /**
     * {@inheritDoc}
     *
     * @since 4.0.0
     */
    @Override
    CartesianProductApi<T> withMaxDepth(int maxDepth);

    /**
     * {@inheritDoc}
     *
     * @since 4.3.1
     */
    @Override
    <V> CartesianProductApi<T> withSetting(SettingKey<V> key, V value);

    /**
     * {@inheritDoc}
     *
     * @since 4.0.0
     */
    @Override
    CartesianProductApi<T> withSettings(Settings settings);

    /**
     * {@inheritDoc}
     *
     * @since 4.0.0
     */
    @Override
    CartesianProductApi<T> withSeed(long seed);

    /**
     * {@inheritDoc}
     *
     * @since 4.0.0
     */
    @Override
    CartesianProductApi<T> lenient();

    /**
     * {@inheritDoc}
     *
     * @since 4.0.0
     */
    @Override
    CartesianProductApi<T> verbose();
}
