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

import org.instancio.FeedApi;
import org.instancio.feed.DataSource;
import org.instancio.feed.DataSourceProvider;
import org.instancio.feed.DataSourceProvider.DataSourceFactory;
import org.instancio.feed.Feed;
import org.instancio.feed.FormatOptionsProvider;
import org.instancio.feed.FormatOptionsProvider.FormatOptions;
import org.instancio.feed.FormatOptionsProvider.FormatOptionsFactory;
import org.instancio.generator.GeneratorContext;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.RandomHelper;
import org.instancio.internal.feed.csv.InternalCsvFormatOptions;
import org.instancio.internal.feed.datasource.CacheableDataSource;
import org.instancio.internal.feed.datasource.FileDataSource;
import org.instancio.internal.feed.datasource.ResourceDataSource;
import org.instancio.internal.feed.datasource.StringDataSource;
import org.instancio.internal.util.ErrorMessageUtils;
import org.instancio.internal.util.Fail;
import org.instancio.settings.FeedDataAccess;
import org.instancio.settings.FeedDataEndAction;
import org.instancio.settings.FeedFormatType;
import org.instancio.settings.Keys;
import org.instancio.settings.SettingKey;
import org.instancio.settings.Settings;
import org.instancio.support.Global;
import org.instancio.support.ThreadLocalSettings;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Paths;
import java.util.function.Function;

public final class InternalFeedContext<F extends Feed> {

    private final Class<F> feedClass;
    private final GeneratorContext generatorContext;
    private final String tagKey;
    private final String tagValue;
    private final DataSource dataSource;
    private final FormatOptions formatOptions;
    private final FeedFormatType feedFormatType;
    private final FeedDataAccess feedDataAccess;
    private final FeedDataEndAction feedDataEndAction;

    private InternalFeedContext(final Builder<F> builder) {
        this.feedClass = builder.feedClass;
        this.generatorContext = resolveGeneratorContext(builder);

        final Settings settings = generatorContext.getSettings();
        final Feed.Source feedSource = builder.feedClass.getDeclaredAnnotation(Feed.Source.class);
        this.tagKey = resolveTagKey(settings, feedClass, builder.tagKey);
        this.tagValue = builder.tagValue != null ? builder.tagValue : settings.get(Keys.FEED_TAG_VALUE);
        this.dataSource = resolveDataSource(feedSource, builder.feedClass, builder.dataSource, tagKey);
        this.formatOptions = builder.formatOptions != null
                ? builder.formatOptions.apply(settings)
                : null; //NOPMD
        this.feedFormatType = resolveDataFormatType(settings, feedClass, builder.feedFormatType);
        this.feedDataAccess = resolveFeedDataAccess(settings, feedClass, builder.feedDataAccess);
        this.feedDataEndAction = builder.feedDataEndAction != null
                ? builder.feedDataEndAction
                : settings.get(Keys.FEED_DATA_END_ACTION);
    }

    private static FeedFormatType resolveDataFormatType(
            final Settings settings,
            final Class<?> feedClass,
            final FeedFormatType feedFormatType) {

        if (feedFormatType != null) {
            return feedFormatType;
        }
        final Feed.FormatType formatType = feedClass.getDeclaredAnnotation(Feed.FormatType.class);
        if (formatType != null) {
            return formatType.value();
        }
        return settings.get(Keys.FEED_FORMAT_TYPE);
    }

    private static FeedDataAccess resolveFeedDataAccess(
            final Settings settings,
            final Class<?> feedClass,
            final FeedDataAccess feedDataAccess) {

        if (feedDataAccess != null) {
            return feedDataAccess;
        }

        final Feed.DataAccess dataAccessAnnotation = feedClass.getDeclaredAnnotation(Feed.DataAccess.class);
        return dataAccessAnnotation != null
                ? dataAccessAnnotation.value()
                : settings.get(Keys.FEED_DATA_ACCESS);
    }

    private static GeneratorContext resolveGeneratorContext(final Builder<?> builder) {
        if (builder.generatorContext != null) {
            return builder.generatorContext;
        }
        final Settings settings = Global.getPropertiesFileSettings()
                .merge(ThreadLocalSettings.getInstance().get())
                .merge(builder.settings)
                .lock();

        return new GeneratorContext(
                settings, RandomHelper.resolveRandom(settings.get(Keys.SEED), null));
    }

    private static String resolveTagKey(
            final Settings settings,
            final Class<?> feedClass,
            final String tagKey) {

        if (tagKey != null) {
            return tagKey;
        }
        final Feed.TagKey tagKeyAnnotation = feedClass.getDeclaredAnnotation(Feed.TagKey.class);
        if (tagKeyAnnotation != null) {
            return tagKeyAnnotation.value();
        }
        return settings.get(Keys.FEED_TAG_KEY);
    }

    private static DataSource resolveDataSource(
            final Feed.Source feedSource,
            final Class<?> feedClass,
            final DataSource dataSource,
            final String tagKey) {

        // sources of data in order of precedence
        if (dataSource != null) {
            if (dataSource instanceof ResourceDataSource || dataSource instanceof FileDataSource) {
                final String cacheKey = dataSource.getName() + ":" + tagKey;
                return new CacheableDataSource(dataSource, cacheKey);
            }
            return dataSource;
        }
        if (feedSource != null) {
            if (!feedSource.string().isEmpty()) {
                // inline data - use the annotated feed class for caching
                final String cacheKey = feedClass.getName() + ":" + tagKey;
                return new CacheableDataSource(new StringDataSource(feedSource.string()), cacheKey);
            }
            if (!feedSource.file().isEmpty()) {
                final String cacheKey = feedSource.file() + ":" + tagKey;
                return new CacheableDataSource(new FileDataSource(Paths.get(feedSource.file())), cacheKey);
            }
            if (!feedSource.resource().isEmpty()) {
                final String cacheKey = feedSource.resource() + ":" + tagKey;
                return new CacheableDataSource(new ResourceDataSource(feedSource.resource()), cacheKey);
            }
        }
        throw Fail.withUsageError(ErrorMessageUtils.feedWithoutDataSource(feedClass));
    }

    public Class<F> getFeedClass() {
        return feedClass;
    }

    public String getTagKey() {
        return tagKey;
    }

    public String getTagValue() {
        return tagValue;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    @Nullable
    public FormatOptions getFormatOptions() {
        return formatOptions;
    }

    public GeneratorContext getGeneratorContext() {
        return generatorContext;
    }

    public FeedDataAccess getFeedDataAccess() {
        return feedDataAccess;
    }

    public FeedDataEndAction getFeedDataEndStrategy() {
        return feedDataEndAction;
    }

    public FeedFormatType getDataFormatType() {
        return feedFormatType;
    }

    public static <F extends Feed> Builder<F> builder(final Class<F> feedClass) {
        return new Builder<>(feedClass);
    }

    public static <F extends Feed> Builder<F> builder(final DataSourceProvider dataSourceProvider) {
        return builder((Class<F>) Feed.class).withDataSource(dataSourceProvider);
    }

    @SuppressWarnings("UnusedReturnValue")
    public static final class Builder<F extends Feed> implements FeedApi {
        private static final DataSourceFactory DATA_SOURCE_FACTORY = new DataSourceFactory() {};
        private static final FormatOptionsFactory DATA_FORMAT_FACTORY = new FormatOptionsFactory() {};

        private final Class<F> feedClass;
        private GeneratorContext generatorContext;
        private String tagKey;
        private String tagValue;
        private DataSource dataSource;
        private Function<Settings, FormatOptions> formatOptions;
        private FeedFormatType feedFormatType;
        private FeedDataAccess feedDataAccess;
        private FeedDataEndAction feedDataEndAction;
        private Settings settings;

        private Builder(final Class<F> feedClass) {
            ApiValidator.isTrue(feedClass != null && feedClass.isInterface(),
                    "Feed must be an interface, but got: %s", feedClass);
            this.feedClass = feedClass;
        }

        public Builder<F> withGeneratorContext(final GeneratorContext generatorContext) {
            this.generatorContext = generatorContext;
            return this;
        }

        @Override
        public Builder<F> withTagKey(final String tagKey) {
            this.tagKey = tagKey;
            return this;
        }

        @Override
        public Builder<F> withTagValue(final String tagValue) {
            this.tagValue = tagValue;
            return this;
        }

        public Builder<F> withDataSource(final DataSourceProvider provider) {
            ApiValidator.notNull(provider, "'dataSourceProvider' must not be null");
            this.dataSource = provider.get(DATA_SOURCE_FACTORY);
            return this;
        }

        @Override
        public Builder<F> formatOptions(final FormatOptionsProvider provider) {
            ApiValidator.notNull(provider, "'formatOptionsProvider' must not be null");
            this.formatOptions = useSettings -> {
                // Safe to cast without a check because currently,
                // only CSV format options are supported
                final InternalCsvFormatOptions.Builder builder = (InternalCsvFormatOptions.Builder)
                        provider.get(DATA_FORMAT_FACTORY);

                return builder.build(useSettings);
            };
            return this;
        }

        @Override
        public Builder<F> formatType(final FeedFormatType feedFormatType) {
            this.feedFormatType = feedFormatType;
            return this;
        }

        @Override
        public Builder<F> dataAccess(final FeedDataAccess feedDataAccess) {
            this.feedDataAccess = feedDataAccess;
            return this;
        }

        @Override
        public Builder<F> onDataEnd(final FeedDataEndAction feedDataEndAction) {
            this.feedDataEndAction = feedDataEndAction;
            return this;
        }

        public <V> Builder<F> withSetting(final SettingKey<V> key, final V value) {
            if (settings == null) {
                settings = Settings.create();
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
