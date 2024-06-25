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
package org.instancio.test.features.schema;

import org.instancio.Instancio;
import org.instancio.internal.schema.datasource.FileDataSource;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.schema.Schema;
import org.instancio.schema.SchemaResource;
import org.instancio.schema.SchemaSpec;
import org.instancio.settings.Keys;
import org.instancio.settings.SchemaDataAccess;
import org.instancio.settings.Settings;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag({Feature.SCHEMA, Feature.VALUE_SPEC})
@ExtendWith(InstancioExtension.class)
class SchemaDataSourceTest {

    @WithSettings
    private final Settings settings = Settings.create()
            .set(Keys.SCHEMA_DATA_ACCESS, SchemaDataAccess.RANDOM);

    private interface SampleSchemaWithoutAnnotation extends Schema {
        SchemaSpec<Integer> id();

        SchemaSpec<String> value();
    }

    @SchemaResource(path = "data/SchemaExample.csv")
    private interface SchemaExample extends SampleSchemaWithoutAnnotation {
        @Override
        SchemaSpec<Integer> id();

        @Override
        SchemaSpec<String> value();
    }

    @Test
    void original() {
        final SchemaExample schema = Instancio.createSchema(SchemaExample.class);

        final Set<String> results = Stream.generate(() -> schema.id().get() + ":" + schema.value().get())
                .limit(100)
                .collect(Collectors.toSet());

        assertThat(results).containsOnly("1:value1", "2:value2", "3:value3");
    }

    @ValueSource(classes = {SampleSchemaWithoutAnnotation.class, SchemaExample.class})
    @ParameterizedTest
    void override(final Class<? extends SampleSchemaWithoutAnnotation> klass) {
        final SampleSchemaWithoutAnnotation schema = Instancio.ofSchema(klass)
                .withDataSource(new FileDataSource("data/DataSpecExampleOverride.csv"))
                .create();

        final Set<String> results = Stream.generate(() -> schema.id().get() + ":" + schema.value().get())
                .limit(100)
                .collect(Collectors.toSet());

        assertThat(results).containsOnly("1:override1", "2:override2", "3:override3");
    }
}
