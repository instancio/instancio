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

import org.instancio.Instancio;
import org.instancio.internal.generator.domain.internet.EmailGenerator;
import org.instancio.internal.schema.datasource.StringDataSource;
import org.instancio.schema.DataSpec;
import org.instancio.schema.GeneratedSpec;
import org.instancio.schema.Schema;
import org.instancio.schema.SchemaResource;
import org.instancio.schema.SchemaSpec;
import org.instancio.schema.TemplateSpec;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class MappableSpecsResolverTest {

    private final MappableSpecsResolver resolver = new MappableSpecsResolver();

    @Test
    void withoutSubclass() {
        final Schema schema = Instancio.ofSchema(Schema.class)
                .withDataSource(new StringDataSource("id\n123"))
                .create();

        final Map<String, SchemaSpec<?>> results = resolver.getMappableSpecs(schema);

        assertThat(results).containsOnlyKeys("id");
    }

    @Test
    void withSingleProperty() {
        @SchemaResource(data = "id\n123")
        interface SampleSchema extends Schema {
            SchemaSpec<Integer> id();
        }

        final Schema schema = Instancio.createSchema(SampleSchema.class);

        final Map<String, SchemaSpec<?>> results = resolver.getMappableSpecs(schema);

        assertThat(results).containsKeys("id");
    }

    @Test
    void schemaWithDataSpec() {
        @SchemaResource(data = "__id__\n123")
        interface SampleSchema extends Schema {
            @DataSpec(propertyName = "__id__")
            SchemaSpec<Integer> id();
        }

        final Schema schema = Instancio.createSchema(SampleSchema.class);

        final Map<String, SchemaSpec<?>> results = resolver.getMappableSpecs(schema);

        assertThat(results).containsOnlyKeys("id");
    }

    @Test
    void withAdhocSpecs() {
        @SchemaResource(data = "givenName,surname,age,username,heightCm\nJohn,Doe,99,jdoe,178")
        interface SampleSchema extends Schema {
            @DataSpec(propertyName = "givenName")
            SchemaSpec<String> firstName();

            @DataSpec(propertyName = "surname")
            SchemaSpec<String> lastName();

            @TemplateSpec("${firstName} ${lastName}")
            SchemaSpec<String> fullName();

            SchemaSpec<Integer> age();

            @DataSpec(propertyName = "heightCm")
            SchemaSpec<Integer> height();

            @GeneratedSpec(EmailGenerator.class)
            SchemaSpec<String> email();
        }

        final Schema schema = Instancio.createSchema(SampleSchema.class);

        final Map<String, SchemaSpec<?>> results = resolver.getMappableSpecs(schema);

        assertThat(results).containsOnlyKeys(
                "firstName", "lastName", "fullName", "age", "height", "email", "username");
    }
}
