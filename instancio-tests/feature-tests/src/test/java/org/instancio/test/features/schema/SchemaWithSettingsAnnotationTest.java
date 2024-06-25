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
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.schema.Schema;
import org.instancio.schema.SchemaResource;
import org.instancio.schema.SchemaSpec;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.allStrings;

/**
 * Verify data spec picks up settings specified using the {@link WithSettings} annotation.
 */
@FeatureTag({Feature.SCHEMA, Feature.VALUE_SPEC, Feature.WITH_SETTINGS_ANNOTATION})
@ExtendWith(InstancioExtension.class)
class SchemaWithSettingsAnnotationTest {

    private static final String RU_PREFIX = "Д";

    @SchemaResource(path = "data/DataSpecWithTag.csv")
    private interface SampleSchema extends Schema {
        SchemaSpec<String> field1(); // we only need one field, ignore other fields
    }

    @WithSettings
    private final Settings settings = Settings.create()
            .set(Keys.SCHEMA_TAG_VALUE, "RU");

    @RepeatedTest(5)
    void shouldUseTagFromAnnotatedSettings1() {
        final SampleSchema schema = Instancio.createSchema(SampleSchema.class);

        final String result = schema.field1().get();

        assertThat(result).startsWith(RU_PREFIX);
    }

    @RepeatedTest(5)
    void shouldUseTagFromAnnotatedSettings2() {
        final SampleSchema spec = Instancio.createSchema(SampleSchema.class);

        final String result = Instancio.of(String.class)
                .generate(allStrings(), spec.field1())
                .create();

        assertThat(result).startsWith(RU_PREFIX);
    }
}
