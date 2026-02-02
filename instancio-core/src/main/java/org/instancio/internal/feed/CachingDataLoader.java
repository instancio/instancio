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
package org.instancio.internal.feed;

import org.instancio.exception.InstancioTerminatingException;
import org.instancio.feed.DataSource;
import org.instancio.internal.feed.datasource.CacheableDataSource;
import org.instancio.internal.util.Fail;
import org.instancio.internal.util.Sonar;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

import static org.instancio.internal.util.ErrorMessageUtils.feedDataSourceIoErrorMessage;

public class CachingDataLoader {

    private static final Map<Object, DataStore<?>> CACHE = new ConcurrentHashMap<>();

    @NotNull
    @SuppressWarnings(Sonar.GENERIC_WILDCARD_IN_RETURN)
    public DataStore<?> loadData(
            final InternalFeedContext<?> feedContext,
            final DataLoader<?> dataLoader,
            final BiFunction<String, List<?>, DataStore<?>> tagKeyToDataStoreMapper) {

        final DataSource dataSource = feedContext.getDataSource();
        final String tagKey = feedContext.getTagKey();

        final Object cacheKey = dataSource instanceof CacheableDataSource cacheableDataSource
                ? cacheableDataSource.getKey()
                : null;

        if (cacheKey == null) {
            final List<?> data = tryLoad(dataLoader, dataSource);
            return tagKeyToDataStoreMapper.apply(tagKey, data);
        }

        return CACHE.computeIfAbsent(cacheKey, mapKey -> {
            final List<?> data = tryLoad(dataLoader, dataSource);
            return tagKeyToDataStoreMapper.apply(tagKey, data);
        });
    }

    private static List<?> tryLoad(final DataLoader<?> dataLoader, final DataSource dataSource) {
        try {
            return (List<?>) dataLoader.load(dataSource);
        } catch (InstancioTerminatingException ex) {
            throw ex;
        } catch (Exception ex) {
            throw Fail.withUsageError(feedDataSourceIoErrorMessage(dataSource, ex), ex);
        }
    }
}
