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
import org.instancio.schema.Schema;
import org.instancio.schema.SchemaResource;
import org.instancio.schema.SchemaSpec;
import org.instancio.settings.Keys;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@FeatureTag({Feature.SCHEMA, Feature.VALUE_SPEC})
@ExtendWith(InstancioExtension.class)
class SchemaUnsupportedTagTest {

    @SchemaResource(path = "data/DataSpecWithTag.csv")
    private interface SampleSchema extends Schema {
        SchemaSpec<String> field1();
    }

    @ValueSource(strings = {"NONE", "INVALID_TAG"})
    @ParameterizedTest
    void unsupportedTag(final String tagValue) {
        final SampleSchema result = Instancio.ofSchema(SampleSchema.class)
                .withSetting(Keys.SCHEMA_TAG_VALUE, tagValue)
                .create();

        assertThatThrownBy(() -> result.field1().get())
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("no data found with tag value: '%s'", tagValue);
    }
}
