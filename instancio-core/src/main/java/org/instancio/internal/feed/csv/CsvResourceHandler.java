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
package org.instancio.internal.feed.csv;

import org.instancio.feed.FormatOptionsProvider.FormatOptions;
import org.instancio.internal.feed.CachingDataLoader;
import org.instancio.internal.feed.DataLoader;
import org.instancio.internal.feed.DataStore;
import org.instancio.internal.feed.InternalFeed;
import org.instancio.internal.feed.InternalFeedContext;
import org.instancio.internal.feed.ResourceHandler;

import java.util.List;
import java.util.function.BiFunction;

public final class CsvResourceHandler implements ResourceHandler {

    private final CachingDataLoader cachingDataLoader = new CachingDataLoader();

    @Override
    public InternalFeed createFeed(final InternalFeedContext<?> feedContext) {
        final FormatOptions options = feedContext.getFormatOptions();
        final FormatOptions csvOptions = options == null
                ? InternalCsvFormatOptions.defaults(feedContext.getGeneratorContext().getSettings())
                : options;

        final DataLoader<?> dataLoader = new CsvDataLoader((InternalCsvFormatOptions) csvOptions);

        final BiFunction<String, List<?>, DataStore<?>> tagKeyToDataStoreMapper =
                (tagKey, data) -> new CsvDataStore(tagKey, (List<String[]>) data);

        final DataStore<?> dataStore = cachingDataLoader.loadData(
                feedContext, dataLoader, tagKeyToDataStoreMapper);

        return new CsvFeed(feedContext, (DataStore<String[]>) dataStore);
    }
}
