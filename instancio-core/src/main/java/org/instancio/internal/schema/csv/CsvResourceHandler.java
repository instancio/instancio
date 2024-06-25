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
package org.instancio.internal.schema.csv;

import org.instancio.internal.schema.AbstractSchemaDataProvider;
import org.instancio.internal.schema.DataLoader;
import org.instancio.internal.schema.DataStore;
import org.instancio.internal.schema.ResourceHandler;
import org.instancio.internal.schema.SchemaContext;
import org.instancio.internal.schema.datasource.CacheableDataSource;
import org.instancio.internal.schema.datasource.FileDataSource;
import org.instancio.internal.schema.datasource.StringDataSource;
import org.instancio.internal.util.ErrorMessageUtils;
import org.instancio.internal.util.Fail;
import org.instancio.schema.SchemaResource;
import org.instancio.settings.Keys;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CsvResourceHandler implements ResourceHandler {

    private static final Map<Object, DataStore<?>> DATA_STORE_MAP = new HashMap<>();

    private final DataLoader<?> dataLoader;

    public CsvResourceHandler(final DataLoader<?> dataLoader) {
        this.dataLoader = dataLoader;
    }

    @Override
    @SuppressWarnings("unchecked")
    public AbstractSchemaDataProvider createDataSpecGenerator(final SchemaContext<?> schemaContext) {
        final Class<?> schemaClass = schemaContext.getSchemaClass();

        final SchemaResource schemaResource = schemaClass.getDeclaredAnnotation(SchemaResource.class);
        final String tagProperty = getTagProperty(schemaContext, schemaResource);
        final CacheableDataSource dataSource = getDataSource(
                schemaContext, schemaResource, schemaClass, tagProperty);

        DataStore<?> dataStore;
        if (dataSource.getCacheKey() == null) {
            final List<?> data = dataLoader.load(dataSource);
            dataStore = new CsvDataStore((List<String[]>) data, tagProperty);
        } else {
            dataStore = DATA_STORE_MAP.get(dataSource.getCacheKey());

            if (dataStore == null) {
                final List<?> data = dataLoader.load(dataSource);
                dataStore = new CsvDataStore((List<String[]>) data, tagProperty);
                DATA_STORE_MAP.put(dataSource.getCacheKey(), dataStore);
            }
        }

        return new CsvDataProvider(schemaContext, dataStore);
    }

    private static String getTagProperty(final SchemaContext<?> schemaContext, final SchemaResource schemaResource) {
        final String tagKey = schemaContext.getGeneratorContext().getSettings().get(Keys.SCHEMA_TAG_KEY);

        final String tagProperty;
        if (!tagKey.equals(Keys.SCHEMA_TAG_KEY.defaultValue())) {
            tagProperty = tagKey;
        } else if (schemaResource != null && !schemaResource.tagKey().isEmpty()) {
            tagProperty = schemaResource.tagKey();
        } else {
            tagProperty = Keys.SCHEMA_TAG_KEY.defaultValue();
        }
        return tagProperty;
    }

    @NotNull
    private static CacheableDataSource getDataSource(
            final SchemaContext<?> schemaContext,
            final SchemaResource schemaResource,
            final Class<?> schemaClass,
            final String tagProperty) {

        final CacheableDataSource dataSource;

        // sources of data in order of precedence
        if (schemaResource != null && !schemaResource.data().isEmpty()) { // inline data
            final String cacheKey = schemaClass.getName() + ":" + tagProperty;
            dataSource = new CacheableDataSource(new StringDataSource(schemaResource.data()), cacheKey);
        } else if (schemaContext.getDataSource() != null) {
            dataSource = new CacheableDataSource(schemaContext.getDataSource(), null);
        } else if (schemaResource != null && !schemaResource.path().isEmpty()) {
            final String cacheKey = schemaResource.path() + ":" + tagProperty;
            dataSource = new CacheableDataSource(new FileDataSource(schemaResource.path()), cacheKey);
        } else {
            throw Fail.withUsageError(ErrorMessageUtils.schemaWithoutDataSource(schemaClass));
        }
        return dataSource;
    }
}
