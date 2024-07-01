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
package org.instancio.internal.feed.csv;

import org.instancio.feed.DataFormat;
import org.instancio.feed.DataSource;
import org.instancio.feed.Feed;
import org.instancio.internal.feed.DataLoader;
import org.instancio.internal.feed.DataStore;
import org.instancio.internal.feed.InternalFeed;
import org.instancio.internal.feed.InternalFeedContext;
import org.instancio.internal.feed.ResourceHandler;
import org.instancio.internal.feed.datasource.CacheableDataSource;
import org.instancio.internal.feed.datasource.FileDataSource;
import org.instancio.internal.feed.datasource.ResourceDataSource;
import org.instancio.internal.feed.datasource.StringDataSource;
import org.instancio.internal.util.ErrorMessageUtils;
import org.instancio.internal.util.Fail;
import org.instancio.settings.Keys;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CsvResourceHandler implements ResourceHandler {

    private static final Map<Object, DataStore<?>> DATA_STORE_MAP = new HashMap<>();

    @Override
    public InternalFeed createFeed(final InternalFeedContext<?> feedContext) {
        final Class<?> feedClass = feedContext.getFeedClass();
        final Feed.Source feedSource = feedClass.getDeclaredAnnotation(Feed.Source.class);
        final String tagProperty = getTagProperty(feedContext, feedSource);

        final DataSource dataSource = getDataSource(feedContext, feedSource, feedClass, tagProperty);
        final DataLoader<?> dataLoader = getDataLoader(feedContext.getDataFormat());
        final DataStore<?> dataStore = loadData(dataLoader, dataSource, tagProperty);

        return new CsvFeed(feedContext, dataStore);
    }

    @SuppressWarnings("unchecked")
    private static DataStore<?> loadData(
            final DataLoader<?> dataLoader,
            final DataSource dataSource,
            final String tagProperty) {

        final Object cacheKey = dataSource instanceof CacheableDataSource
                ? ((CacheableDataSource) dataSource).getKey()
                : null;

        if (cacheKey == null) {
            final List<?> data = dataLoader.load(dataSource);
            return new CsvDataStore((List<String[]>) data, tagProperty);
        }

        DataStore<?> dataStore = DATA_STORE_MAP.get(cacheKey);

        if (dataStore == null) {
            final List<?> data = dataLoader.load(dataSource);
            dataStore = new CsvDataStore((List<String[]>) data, tagProperty);
            DATA_STORE_MAP.put(cacheKey, dataStore);
        }
        return dataStore;
    }

    private DataLoader<?> getDataLoader(@Nullable final DataFormat dataFormat) {
        final InternalCsvDataFormat format = dataFormat == null
                ? InternalCsvDataFormat.defaults()
                : (InternalCsvDataFormat) dataFormat;

        return new CsvDataLoader(format);
    }

    private static String getTagProperty(
            final InternalFeedContext<?> feedContext,
            @Nullable final Feed.Source feedSource) {

        final String tagKey = feedContext.getSettings().get(Keys.FEED_TAG_KEY);

        final String tagProperty;
        if (!tagKey.equals(Keys.FEED_TAG_KEY.defaultValue())) {
            tagProperty = tagKey;
        } else if (feedSource != null && !feedSource.tagKey().isEmpty()) {
            tagProperty = feedSource.tagKey();
        } else {
            tagProperty = Keys.FEED_TAG_KEY.defaultValue();
        }
        return tagProperty;
    }

    @NotNull
    private static DataSource getDataSource(
            final InternalFeedContext<?> feedContext,
            final Feed.Source feedSource,
            final Class<?> feedClass,
            final String tagProperty) {

        // sources of data in order of precedence
        if (feedSource != null && !feedSource.data().isEmpty()) { // inline data
            final String cacheKey = feedClass.getName() + ":" + tagProperty;
            return new CacheableDataSource(new StringDataSource(feedSource.data()), cacheKey);
        }
        if (feedContext.getDataSource() != null) {
            return feedContext.getDataSource();
        }
        if (feedSource != null && !feedSource.path().isEmpty()) {
            final String cacheKey = feedSource.path() + ":" + tagProperty;
            return new CacheableDataSource(new FileDataSource(Paths.get(feedSource.path())), cacheKey);
        }
        if (feedSource != null && !feedSource.name().isEmpty()) {
            final String cacheKey = feedSource.name() + ":" + tagProperty;
            return new CacheableDataSource(new ResourceDataSource(feedSource.name()), cacheKey);
        }
        throw Fail.withUsageError(ErrorMessageUtils.feedWithoutDataSource(feedClass));
    }
}
