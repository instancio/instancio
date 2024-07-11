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
package org.instancio.feed;

import org.instancio.documentation.ExperimentalApi;
import org.instancio.settings.FeedDataAccess;
import org.instancio.settings.FeedDataEndAction;
import org.instancio.settings.FeedFormatType;
import org.instancio.settings.Keys;

/**
 * Interface for configuring {@link Feed} instances.
 *
 * @see Feed
 * @since 5.0.0
 */
@ExperimentalApi
public interface FeedOperations {

    /**
     * Specifies the data format options of a {@link Feed}, for example:
     *
     * <pre>{@code
     * SampleFeed feed = Instancio.ofFeed(SampleFeed.class)
     *     .formatOptions(format -> format.csv()
     *         .commentPrefix("#")
     *         .delimiter('|')
     *         .trimValues(true))
     *     .create();
     * }</pre>
     *
     * @param provider of format options
     * @return API builder reference
     * @since 5.0.0
     */
    @ExperimentalApi
    FeedOperations formatOptions(FormatOptionsProvider provider);

    /**
     * Specifies the data format type for the feed,
     * such as CSV (by default) or JSON, for example:
     *
     * <pre>{@code
     * SampleFeed feed = Instancio.ofFeed(SampleFeed.class)
     *     .formatType(FeedFormatType.JSON)
     *     .create();
     * }</pre>
     *
     * @param feedFormatType the data format type
     * @return API builder reference
     * @since 5.0.0
     */
    @ExperimentalApi
    FeedOperations formatType(FeedFormatType feedFormatType);

    /**
     * Specifies the data access mode for the feed (sequential or random).
     *
     * <p>The default is determined by the {@link Keys#FEED_DATA_ACCESS}
     * setting value.
     *
     * @param feedDataAccess the data access mode
     * @return API builder reference
     * @since 5.0.0
     */
    @ExperimentalApi
    FeedOperations dataAccess(FeedDataAccess feedDataAccess);

    /**
     * Specifies the action to take when the end of the data feed is reached.
     *
     * @param feedDataEndAction the strategy for handling end-of-data
     * @return API builder reference
     * @since 5.0.0
     */
    @ExperimentalApi
    FeedOperations onDataEnd(FeedDataEndAction feedDataEndAction);

    /**
     * Specifies the tag key for filtering records in the data feed.
     *
     * @param tagKey the key used to tag records
     * @return API builder reference
     * @since 5.0.0
     */
    @ExperimentalApi
    FeedOperations withTagKey(String tagKey);

    /**
     * Specifies the tag value of the records to fetch.
     *
     * @param tagValue the value of the tag to filter records by
     * @return API builder reference
     * @since 5.0.0
     */
    @ExperimentalApi
    FeedOperations withTagValue(String tagValue);
}
