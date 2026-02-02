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
import org.instancio.settings.FillType;
import org.instancio.settings.Keys;
import org.instancio.settings.SettingKey;
import org.instancio.settings.Settings;

import java.util.function.Supplier;

/**
 * An API for manipulating and populating existing object instances.
 *
 * @param <T> the type of object being manipulated or populated
 * @since 5.3.0
 */
@ExperimentalApi
public interface InstancioObjectApi<T> extends
        BaseApi<T>,
        SettingsApi,
        LenientModeApi,
        VerboseModeApi {

    /**
     * Terminal method that triggers the population of
     * the target object with random values.
     *
     * @since 5.3.0
     */
    @ExperimentalApi
    void fill();

    /**
     * Sets the fill type for populating objects.
     * Refer to the {@link FillType} javadoc for details.
     *
     * @param fillType the fill type to use for field population
     * @return API builder reference
     * @see FillType
     * @see Keys#FILL_TYPE
     * @since 5.3.0
     */
    @ExperimentalApi
    InstancioObjectApi<T> withFillType(FillType fillType);

    /**
     * {@inheritDoc}
     *
     * @since 5.3.0
     */
    @Override
    InstancioObjectApi<T> ignore(TargetSelector selector);

    /**
     * {@inheritDoc}
     *
     * @since 5.3.0
     */
    @Override
    InstancioObjectApi<T> withNullable(TargetSelector selector);

    /**
     * {@inheritDoc}
     *
     * @since 5.3.0
     */
    @Override
    <V> InstancioObjectApi<T> set(TargetSelector selector, V value);

    /**
     * {@inheritDoc}
     *
     * @since 5.3.0
     */
    @Override
    @ExperimentalApi
    <V> InstancioObjectApi<T> setModel(TargetSelector selector, Model<V> model);

    /**
     * {@inheritDoc}
     *
     * @since 5.3.0
     */
    @Override
    <V> InstancioObjectApi<T> supply(TargetSelector selector, Supplier<V> supplier);

    /**
     * {@inheritDoc}
     *
     * @since 5.3.0
     */
    @Override
    <V> InstancioObjectApi<T> supply(TargetSelector selector, Generator<V> generator);

    /**
     * {@inheritDoc}
     *
     * @since 5.3.0
     */
    @Override
    <V> InstancioObjectApi<T> generate(TargetSelector selector, GeneratorSpecProvider<V> gen);

    /**
     * {@inheritDoc}
     *
     * @since 5.3.0
     */
    @Override
    @ExperimentalApi
    <V> InstancioObjectApi<T> generate(TargetSelector selector, GeneratorSpec<V> spec);

    /**
     * {@inheritDoc}
     *
     * @since 5.3.0
     */
    @Override
    <V> InstancioObjectApi<T> onComplete(TargetSelector selector, OnCompleteCallback<V> callback);

    /**
     * {@inheritDoc}
     *
     * @since 5.3.0
     */
    @Override
    @ExperimentalApi
    <V> InstancioObjectApi<T> filter(TargetSelector selector, FilterPredicate<V> predicate);

    /**
     * {@inheritDoc}
     *
     * @since 5.3.0
     */
    @Override
    @ExperimentalApi
    InstancioObjectApi<T> setBlank(TargetSelector selector);

    /**
     * {@inheritDoc}
     *
     * @since 5.3.0
     */
    @Override
    InstancioObjectApi<T> subtype(TargetSelector selector, Class<?> subtype);

    /**
     * {@inheritDoc}
     *
     * @since 5.3.0
     */
    @Override
    @ExperimentalApi
    InstancioObjectApi<T> assign(Assignment... assignments);

    /**
     * {@inheritDoc}
     *
     * @since 5.3.0
     */
    @Override
    InstancioObjectApi<T> withMaxDepth(int maxDepth);

    /**
     * {@inheritDoc}
     *
     * @since 5.3.0
     */
    @Override
    <V> InstancioObjectApi<T> withSetting(SettingKey<V> key, V value);

    /**
     * {@inheritDoc}
     *
     * @since 5.3.0
     */
    @Override
    InstancioObjectApi<T> withSettings(Settings settings);

    /**
     * {@inheritDoc}
     *
     * @since 5.3.0
     */
    @Override
    InstancioObjectApi<T> withSeed(long seed);

    /**
     * {@inheritDoc}
     *
     * @since 5.3.0
     */
    @Override
    InstancioObjectApi<T> lenient();

    /**
     * {@inheritDoc}
     *
     * @since 5.3.0
     */
    @Override
    @ExperimentalApi
    InstancioObjectApi<T> verbose();

    /**
     * {@inheritDoc}
     *
     * @since 5.3.0
     */
    @Override
    @ExperimentalApi
    InstancioObjectApi<T> applyFeed(TargetSelector selector, Feed feed);

    /**
     * {@inheritDoc}
     *
     * @since 5.3.0
     */
    @Override
    @ExperimentalApi
    InstancioObjectApi<T> applyFeed(TargetSelector selector, FeedProvider provider);

    /**
     * {@inheritDoc}
     *
     * @since 5.3.0
     */
    @Override
    @ExperimentalApi
    InstancioObjectApi<T> withUnique(TargetSelector selector);
}