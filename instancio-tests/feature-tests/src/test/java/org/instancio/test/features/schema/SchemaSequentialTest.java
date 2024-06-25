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
import org.instancio.settings.SchemaDataEndStrategy;
import org.instancio.settings.Settings;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@FeatureTag({Feature.SCHEMA, Feature.VALUE_SPEC})
@ExtendWith(InstancioExtension.class)
class SchemaSequentialTest {

    @SchemaResource(data = "id\n1\n2")
    private interface SampleSchema extends Schema {
        SchemaSpec<Integer> id();
    }

    @WithSettings
    private final Settings settings = Settings.create()
            .set(Keys.SCHEMA_DATA_ACCESS, SchemaDataAccess.SEQUENTIAL);

    @RepeatedTest(5)
    void sequential() {
        final SampleSchema result = Instancio.createSchema(SampleSchema.class);

        assertThat(result.id().get()).isEqualTo(1);
        assertThat(result.id().get()).isEqualTo(2);
    }

    @Test
    void insufficientItems_shouldThrowErrorByDefault() {
        final SampleSchema schema = Instancio.createSchema(SampleSchema.class);

        assertThat(schema.id().get()).isEqualTo(1);
        assertThat(schema.id().get()).isEqualTo(2);

        final SchemaSpec<Integer> idSpec = schema.id();

        assertThatThrownBy(idSpec::get)
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("reached end of data");
    }

    @Test
    void insufficientItems_recycle() {
        final SampleSchema result = Instancio.ofSchema(SampleSchema.class)
                .withSetting(Keys.SCHEMA_DATA_END_STRATEGY, SchemaDataEndStrategy.RECYCLE)
                .create();

        for (int i = 0; i < 10; i++) {
            assertThat(result.id().get()).isEqualTo(1);
            assertThat(result.id().get()).isEqualTo(2);
        }
    }
}
