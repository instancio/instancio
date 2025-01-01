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
package org.instancio.internal;

import org.instancio.InstancioFeedApi;
import org.instancio.feed.DataSourceProvider;
import org.instancio.feed.Feed;
import org.instancio.feed.FormatOptionsProvider;
import org.instancio.internal.feed.InternalFeedContext;
import org.instancio.internal.feed.InternalFeedProxy;
import org.instancio.settings.FeedDataAccess;
import org.instancio.settings.FeedDataEndAction;
import org.instancio.settings.FeedFormatType;
import org.instancio.settings.SettingKey;
import org.instancio.settings.Settings;

public final class FeedApiImpl<F extends Feed> implements InstancioFeedApi<F> {

    private final InternalFeedContext.Builder<F> feedContextBuilder;

    public FeedApiImpl(final Class<F> feedClass) {
        this.feedContextBuilder = InternalFeedContext.builder(feedClass);
    }

    @Override
    public InstancioFeedApi<F> withDataSource(final DataSourceProvider provider) {
        feedContextBuilder.withDataSource(provider);
        return this;
    }

    @Override
    public InstancioFeedApi<F> formatOptions(final FormatOptionsProvider provider) {
        feedContextBuilder.formatOptions(provider);
        return this;
    }

    @Override
    public InstancioFeedApi<F> formatType(final FeedFormatType feedFormatType) {
        feedContextBuilder.formatType(feedFormatType);
        return this;
    }

    @Override
    public InstancioFeedApi<F> dataAccess(final FeedDataAccess feedDataAccess) {
        feedContextBuilder.dataAccess(feedDataAccess);
        return this;
    }

    @Override
    public InstancioFeedApi<F> onDataEnd(final FeedDataEndAction feedDataEndAction) {
        feedContextBuilder.onDataEnd(feedDataEndAction);
        return this;
    }

    @Override
    public InstancioFeedApi<F> withTagKey(final String tagKey) {
        feedContextBuilder.withTagKey(tagKey);
        return this;
    }

    @Override
    public InstancioFeedApi<F> withTagValue(final String tagValue) {
        feedContextBuilder.withTagValue(tagValue);
        return this;
    }

    @Override
    public <V> InstancioFeedApi<F> withSetting(final SettingKey<V> key, final V value) {
        feedContextBuilder.withSetting(key, value);
        return this;
    }

    @Override
    public InstancioFeedApi<F> withSettings(final Settings settings) {
        feedContextBuilder.withSettings(settings);
        return this;
    }

    @Override
    public F create() {
        final InternalFeedContext<F> context = feedContextBuilder.build();
        return InternalFeedProxy.forClass(context);
    }
}
