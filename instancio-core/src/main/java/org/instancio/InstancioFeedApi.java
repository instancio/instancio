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
import org.instancio.feed.DataFormatProvider;
import org.instancio.feed.DataSourceProvider;
import org.instancio.feed.Feed;
import org.instancio.settings.SettingKey;
import org.instancio.settings.Settings;

/**
 * An API for customising the properties of a {@link Feed}.
 *
 * @param <F> the type of feed
 * @since 5.0.0
 */
@ExperimentalApi
public interface InstancioFeedApi<F extends Feed> extends InstancioWithSettingsApi {

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
     * Specifies the data format of a {@link Feed}, for example:
     *
     * <pre>{@code
     * SampleFeed feed = Instancio.ofFeed(SampleFeed.class)
     *     .withDataFormat(format -> format.csv()
     *         .commentPrefix("#")
     *         .separatorChar('|')
     *         .trimValues(true))
     *     .create();
     * }</pre>
     *
     * @param provider of the data format
     * @return API builder reference
     * @since 5.0.0
     */
    @ExperimentalApi
    InstancioFeedApi<F> withDataFormat(DataFormatProvider provider);

    /**
     * Specifies the tag value of the records to fetch.
     *
     * @param tagValue of records to fetch
     * @return API builder reference
     * @since 5.0.0
     */
    @ExperimentalApi
    InstancioFeedApi<F> withTagValue(String tagValue);

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    @ExperimentalApi
    InstancioFeedApi<F> withSettings(Settings settings);

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    @ExperimentalApi
    <V> InstancioFeedApi<F> withSetting(SettingKey<V> key, V value);
}
