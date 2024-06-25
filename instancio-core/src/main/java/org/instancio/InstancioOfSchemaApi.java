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
import org.instancio.schema.DataSource;
import org.instancio.schema.Schema;
import org.instancio.schema.SchemaResource;
import org.instancio.settings.SettingKey;
import org.instancio.settings.Settings;

/**
 * An API for customising the properties of a {@link Schema}.
 *
 * @param <S> the type of schema
 * @since 5.0.0
 */
@ExperimentalApi
public interface InstancioOfSchemaApi<S extends Schema> extends InstancioWithSettingsApi {

    /**
     * Creates an instance of the schema.
     *
     * @return an instance of the schema
     * @since 5.0.0
     */
    @ExperimentalApi
    S create();

    /**
     * This method allows specifying an arbitrary
     * data source for a {@link Schema}.
     *
     * @param dataSource containing the data for the schema
     * @return API builder reference
     * @see SchemaResource
     * @since 5.0.0
     */
    @ExperimentalApi
    InstancioOfSchemaApi<S> withDataSource(DataSource dataSource);

    /**
     * Specifies the tag value of the records to fetch.
     *
     * @param tagValue of records to fetch
     * @return API builder reference
     * @since 5.0.0
     */
    @ExperimentalApi
    InstancioOfSchemaApi<S> withTagValue(String tagValue);

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    @ExperimentalApi
    InstancioOfSchemaApi<S> withSettings(Settings settings);

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    @ExperimentalApi
    <V> InstancioOfSchemaApi<S> withSetting(SettingKey<V> key, V value);
}
