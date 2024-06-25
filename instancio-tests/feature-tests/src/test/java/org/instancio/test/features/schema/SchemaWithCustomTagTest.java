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
import org.instancio.exception.InstancioApiException;
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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@FeatureTag({Feature.SCHEMA, Feature.VALUE_SPEC})
@ExtendWith(InstancioExtension.class)
class SchemaWithCustomTagTest {

    @SchemaResource(data = "customTag,id\nfoo,1\nbar,2", tagKey = "customTag")
    private interface SampleSchema extends Schema {
        SchemaSpec<Integer> id();
    }

    @SchemaResource(data = "customTag,id\nfoo,1\nbar,2", tagKey = "")
    private interface SampleSchemaWithBlankTag extends Schema {
        SchemaSpec<Integer> id();
    }

    @Nested
    class SampleSchemaWithBlankTagTest {
        @WithSettings
        private final Settings settings = Settings.create()
                .set(Keys.SCHEMA_DATA_ACCESS, SchemaDataAccess.RANDOM);

        @Test
        void withTagBar() {
            final String tag = "bar";
            final SampleSchemaWithBlankTag schema = Instancio.ofSchema(SampleSchemaWithBlankTag.class)
                    .withTagValue(tag)
                    .create();

            assertThatThrownBy(() -> schema.id().get())
                    .isExactlyInstanceOf(InstancioApiException.class)
                    .hasMessageContaining("no data found with tag value: '%s'", tag);
        }

        @Test
        void withoutTag() {
            final List<Integer> results = Instancio.createSchema(SampleSchemaWithBlankTag.class).id().list(10);

            assertThat(results).containsOnly(1, 2);
        }
    }

    @Nested
    class SampleSchemaTest {
        @WithSettings
        private final Settings settings = Settings.create()
                .set(Keys.SCHEMA_DATA_ACCESS, SchemaDataAccess.RANDOM);

        @Test
        void withTagFoo() {
            final List<Integer> results = Instancio.ofSchema(SampleSchema.class)
                    .withTagValue("foo")
                    .create()
                    .id()
                    .list(10);

            assertThat(results).containsOnly(1);
        }

        @Test
        void withTagBar() {
            final List<Integer> results = Instancio.ofSchema(SampleSchema.class)
                    .withTagValue("bar")
                    .create()
                    .id()
                    .list(10);

            assertThat(results).containsOnly(2);
        }

        @Test
        void withoutTag() {
            final List<Integer> results = Instancio.createSchema(SampleSchema.class).id().list(10);

            assertThat(results).containsOnly(1, 2);
        }
    }
}
