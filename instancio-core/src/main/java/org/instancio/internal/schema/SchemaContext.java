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
package org.instancio.internal.schema;

import org.instancio.generator.GeneratorContext;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.RandomHelper;
import org.instancio.schema.DataSource;
import org.instancio.schema.Schema;
import org.instancio.settings.Keys;
import org.instancio.settings.SettingKey;
import org.instancio.settings.Settings;
import org.instancio.support.Global;
import org.instancio.support.ThreadLocalSettings;

public final class SchemaContext<S extends Schema> {
    private final Class<S> schemaClass;
    private final DataSource dataSource;
    private final GeneratorContext generatorContext;

    public SchemaContext(final Builder<S> builder) {
        this.schemaClass = builder.schemaClass;
        this.dataSource = builder.dataSource;

        final Settings settings = Global.getPropertiesFileSettings()
                .merge(ThreadLocalSettings.getInstance().get())
                .merge(builder.settings);

        this.generatorContext = new GeneratorContext(
                settings, RandomHelper.resolveRandom(settings.get(Keys.SEED), null));
    }

    public Class<S> getSchemaClass() {
        return schemaClass;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public GeneratorContext getGeneratorContext() {
        return generatorContext;
    }

    public static <S extends Schema> Builder<S> builder(final Class<S> dataSpec) {
        return new Builder<>(dataSpec);
    }

    @SuppressWarnings("UnusedReturnValue")
    public static final class Builder<S extends Schema> {
        private final Class<S> schemaClass;
        private DataSource dataSource;
        private Settings settings;

        private Builder(final Class<S> schemaClass) {
            ApiValidator.isTrue(schemaClass != null && schemaClass.isInterface(),
                    "Schema must be an interface, but got: %s", schemaClass);
            this.schemaClass = schemaClass;
        }

        public Builder<S> withTagValue(final String tagValue) {
            return withSetting(Keys.SCHEMA_TAG_VALUE, tagValue);
        }

        public Builder<S> withDataSource(final DataSource dataSource) {
            ApiValidator.notNull(dataSource, "'dataSource' must not be null");
            this.dataSource = dataSource;
            return this;
        }

        public <V> Builder<S> withSetting(final SettingKey<V> key, final V value) {
            if (settings == null) {
                settings = Settings.create();
            } else if (settings.isLocked()) {
                settings = Settings.from(settings);
            }
            settings.set(key, value);
            return this;
        }

        public Builder<S> withSettings(final Settings arg) {
            ApiValidator.notNull(arg, "'settings' must not be null");

            if (settings == null) {
                settings = Settings.from(arg);
            } else {
                settings = settings.merge(arg);
            }
            return this;
        }

        public SchemaContext<S> build() {
            return new SchemaContext<>(this);
        }
    }
}
