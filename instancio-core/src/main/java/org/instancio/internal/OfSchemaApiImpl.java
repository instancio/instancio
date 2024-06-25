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
package org.instancio.internal;

import org.instancio.InstancioOfSchemaApi;
import org.instancio.internal.schema.SchemaContext;
import org.instancio.internal.schema.SchemaProxy;
import org.instancio.schema.DataSource;
import org.instancio.schema.Schema;
import org.instancio.settings.SettingKey;
import org.instancio.settings.Settings;

public final class OfSchemaApiImpl<S extends Schema> implements InstancioOfSchemaApi<S> {
    private final SchemaContext.Builder<S> contextBuilder;

    public OfSchemaApiImpl(final Class<S> schemaClass) {
        this.contextBuilder = SchemaContext.builder(schemaClass);
    }

    @Override
    public InstancioOfSchemaApi<S> withDataSource(final DataSource dataSource) {
        contextBuilder.withDataSource(dataSource);
        return this;
    }

    @Override
    public InstancioOfSchemaApi<S> withTagValue(final String tagValue) {
        contextBuilder.withTagValue(tagValue);
        return this;
    }

    @Override
    public InstancioOfSchemaApi<S> withSettings(final Settings settings) {
        contextBuilder.withSettings(settings);
        return this;
    }

    @Override
    public <V> InstancioOfSchemaApi<S> withSetting(final SettingKey<V> key, final V value) {
        contextBuilder.withSetting(key, value);
        return this;
    }

    @Override
    public S create() {
        final SchemaContext<S> context = contextBuilder.build();
        return SchemaProxy.forClass(context);
    }

}
