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
package org.instancio.test.features.schema.withschema;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.schema.Schema;
import org.instancio.schema.SchemaResource;
import org.instancio.settings.Keys;
import org.instancio.settings.SchemaDataEndStrategy;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.field;

@FeatureTag({Feature.SCHEMA, Feature.WITH_SCHEMA, Feature.WITH_NULLABLE})
@ExtendWith(InstancioExtension.class)
class WithSchemaWithNullableTest {

    @WithSettings
    private static final Settings settings = Settings.create()
            .set(Keys.SCHEMA_DATA_END_STRATEGY, SchemaDataEndStrategy.RECYCLE);

    @SchemaResource(data = "value\nfoo\nbar\nbaz")
    private interface SampleSchema extends Schema {}

    @Test
    void withNullable() {
        final Schema schema = Instancio.createSchema(SampleSchema.class);

        final List<StringHolder> result = Instancio.ofList(StringHolder.class)
                .size(Constants.SAMPLE_SIZE_DD)
                .withSchema(all(StringHolder.class), schema)
                .withNullable(field(StringHolder::getValue))
                .create();

        assertThat(result)
                .extracting(StringHolder::getValue)
                .containsOnly(null, "foo", "bar", "baz");
    }
}
