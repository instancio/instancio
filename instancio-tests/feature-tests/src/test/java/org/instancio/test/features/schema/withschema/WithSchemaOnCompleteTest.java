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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.instancio.Select.root;

@FeatureTag({Feature.SCHEMA, Feature.WITH_SCHEMA, Feature.ON_COMPLETE})
@ExtendWith(InstancioExtension.class)
class WithSchemaOnCompleteTest {

    @WithSettings
    private static final Settings settings = Settings.create()
            .set(Keys.SCHEMA_DATA_END_STRATEGY, SchemaDataEndStrategy.RECYCLE);

    @SchemaResource(data = "value\nfoo")
    private interface SampleSchema extends Schema {}

    @Test
    void onComplete() {
        final Schema schema = Instancio.createSchema(SampleSchema.class);

        final AtomicReference<String> callbackValue = new AtomicReference<>();

        final StringHolder result = Instancio.of(StringHolder.class)
                .withSchema(root(), schema)
                .onComplete(field(StringHolder::getValue), callbackValue::set)
                .create();

        assertThat(result.getValue())
                .isEqualTo(callbackValue.get())
                .isEqualTo("foo");
    }
}
