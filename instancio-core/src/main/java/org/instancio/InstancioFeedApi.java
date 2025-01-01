/*
 * Copyright 2022-2025 the original author or authors.
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
import org.instancio.feed.DataSourceProvider;
import org.instancio.feed.Feed;
import org.instancio.feed.FormatOptionsProvider;
import org.instancio.settings.FeedDataAccess;
import org.instancio.settings.FeedDataEndAction;
import org.instancio.settings.FeedFormatType;
import org.instancio.settings.SettingKey;
import org.instancio.settings.Settings;

/**
 * An API for customising the properties of a {@link Feed}.
 *
 * @param <F> the type of feed
 * @since 5.0.0
 */
@ExperimentalApi
public interface InstancioFeedApi<F extends Feed> extends
        FeedApi,
        SettingsApi {

    /**
     * Creates an instance of the feed.
     *
     * @return an instance of the feed
     * @since 5.0.0
     */
    @ExperimentalApi
    F create();

    /**
     * Specifies the data source for a {@link Feed}, for example:
     *
     * <pre>{@code
     * SampleFeed feed = Instancio.ofFeed(SampleFeed.class)
     *     .withDataSource(source -> source.ofResource("data/sample.csv"))
     *     .create();
     * }</pre>
     *
     * @param provider of the data source
     * @return API builder reference
     * @see Feed.Source
     * @since 5.0.0
     */
    @ExperimentalApi
    InstancioFeedApi<F> withDataSource(DataSourceProvider provider);

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    @ExperimentalApi
    InstancioFeedApi<F> formatOptions(FormatOptionsProvider provider);

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    @ExperimentalApi
    InstancioFeedApi<F> formatType(FeedFormatType feedFormatType);

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    @ExperimentalApi
    InstancioFeedApi<F> withTagKey(String tagKey);

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    @ExperimentalApi
    InstancioFeedApi<F> withTagValue(String tagValue);

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    InstancioFeedApi<F> dataAccess(FeedDataAccess feedDataAccess);

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    InstancioFeedApi<F> onDataEnd(FeedDataEndAction feedDataEndAction);

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    @ExperimentalApi
    <V> InstancioFeedApi<F> withSetting(SettingKey<V> key, V value);

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    @ExperimentalApi
    InstancioFeedApi<F> withSettings(Settings settings);
}
