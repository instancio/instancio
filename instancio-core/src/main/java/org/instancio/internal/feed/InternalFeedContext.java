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
package org.instancio.internal.feed;

import org.instancio.feed.DataFormat;
import org.instancio.feed.DataFormatProvider;
import org.instancio.feed.DataFormatProvider.DataFormatFactory;
import org.instancio.feed.DataSource;
import org.instancio.feed.DataSourceProvider;
import org.instancio.feed.DataSourceProvider.DataSourceFactory;
import org.instancio.feed.Feed;
import org.instancio.generator.GeneratorContext;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.RandomHelper;
import org.instancio.internal.feed.csv.InternalCsvDataFormat;
import org.instancio.settings.FeedDataAccess;
import org.instancio.settings.Keys;
import org.instancio.settings.SettingKey;
import org.instancio.settings.Settings;
import org.instancio.support.Global;
import org.instancio.support.ThreadLocalSettings;

public final class InternalFeedContext<F extends Feed> {
    private final Class<F> feedClass;
    private final DataSource dataSource;
    private final DataFormat dataFormat;
    private final FeedDataAccess feedDataAccess;
    private final GeneratorContext generatorContext;
    private final Settings settings;

    public InternalFeedContext(final Builder<F> builder) {
        this.feedClass = builder.feedClass;
        this.dataSource = builder.dataSource;
        this.dataFormat = builder.dataFormat;
        this.settings = Global.getPropertiesFileSettings()
                .merge(ThreadLocalSettings.getInstance().get())
                .merge(builder.settings)
                .lock();

        this.generatorContext = new GeneratorContext(
                settings, RandomHelper.resolveRandom(settings.get(Keys.SEED), null));

        final Feed.DataAccess dataAccessAnnotation = feedClass.getDeclaredAnnotation(Feed.DataAccess.class);
        this.feedDataAccess = dataAccessAnnotation != null
                ? dataAccessAnnotation.value()
                : settings.get(Keys.FEED_DATA_ACCESS);
    }

    public Class<F> getFeedClass() {
        return feedClass;
    }

    public DataFormat getDataFormat() {
        return dataFormat;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public Settings getSettings() {
        return settings;
    }

    public GeneratorContext getGeneratorContext() {
        return generatorContext;
    }

    public FeedDataAccess getFeedDataAccess() {
        return feedDataAccess;
    }

    public static <F extends Feed> Builder<F> builder(final Class<F> feedClass) {
        return new Builder<>(feedClass);
    }

    @SuppressWarnings("UnusedReturnValue")
    public static final class Builder<F extends Feed> {
        private static final DataSourceFactory DATA_SOURCE_FACTORY = new DataSourceFactory() {};
        private static final DataFormatFactory DATA_FORMAT_FACTORY = new DataFormatFactory() {};

        private final Class<F> feedClass;
        private DataSource dataSource;
        private DataFormat dataFormat;
        private Settings settings;

        private Builder(final Class<F> feedClass) {
            ApiValidator.isTrue(feedClass != null && feedClass.isInterface(),
                    "Feed must be an interface, but got: %s", feedClass);
            this.feedClass = feedClass;
        }

        public Builder<F> withTagValue(final String tagValue) {
            return withSetting(Keys.FEED_TAG_VALUE, tagValue);
        }

        public Builder<F> withDataSource(final DataSourceProvider provider) {
            ApiValidator.notNull(provider, "'dataSourceProvider' must not be null");
            this.dataSource = provider.get(DATA_SOURCE_FACTORY);
            return this;
        }

        public Builder<F> withDataFormat(final DataFormatProvider provider) {
            ApiValidator.notNull(provider, "'dataFormatProvider' must not be null");
            InternalCsvDataFormat.Builder builder = (InternalCsvDataFormat.Builder)
                    provider.get(DATA_FORMAT_FACTORY);
            this.dataFormat = builder.build();
            return this;
        }

        public <V> Builder<F> withSetting(final SettingKey<V> key, final V value) {
            if (settings == null) {
                settings = Settings.create();
            } else if (settings.isLocked()) {
                settings = Settings.from(settings);
            }
            settings.set(key, value);
            return this;
        }

        public Builder<F> withSettings(final Settings arg) {
            ApiValidator.notNull(arg, "'settings' must not be null");

            if (settings == null) {
                settings = Settings.from(arg);
            } else {
                settings = settings.merge(arg);
            }
            return this;
        }

        public InternalFeedContext<F> build() {
            return new InternalFeedContext<>(this);
        }
    }
}
