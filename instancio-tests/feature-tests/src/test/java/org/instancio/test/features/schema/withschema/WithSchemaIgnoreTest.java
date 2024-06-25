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
import org.instancio.test.support.conditions.Conditions;
import org.instancio.test.support.pojo.misc.StringFields;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.field;
import static org.instancio.Select.root;

@FeatureTag({Feature.SCHEMA, Feature.WITH_SCHEMA, Feature.IGNORE})
@ExtendWith(InstancioExtension.class)
class WithSchemaIgnoreTest {

    @WithSettings
    private static final Settings settings = Settings.create()
            .set(Keys.SCHEMA_DATA_END_STRATEGY, SchemaDataEndStrategy.RECYCLE);

    @SchemaResource(data = "one,two\nfoo,bar")
    private interface SampleSchema extends Schema {}

    @Test
    void withIgnoredField() {
        final Schema schema = Instancio.createSchema(SampleSchema.class);

        final StringFields result = Instancio.of(StringFields.class)
                .withSchema(root(), schema)
                .ignore(field(StringFields::getOne))
                .create();

        assertThat(result.getOne()).isNull();
        assertThat(result.getTwo()).isEqualTo("bar");
        assertThat(result.getThree()).is(Conditions.RANDOM_STRING);
        assertThat(result.getFour()).is(Conditions.RANDOM_STRING);
    }

    @Test
    void withIgnoredPojo() {
        class Container {
            StringFields pojo1, pojo2;
        }

        final Schema schema = Instancio.createSchema(SampleSchema.class);

        final Container result = Instancio.of(Container.class)
                .withSchema(all(StringFields.class), schema)
                .ignore(field("pojo2"))
                .ignore(field(StringFields::getTwo))
                .create();

        assertThat(result.pojo1.getOne()).isEqualTo("foo");
        assertThat(result.pojo1.getTwo()).isNull();
        assertThat(result.pojo1.getThree()).is(Conditions.RANDOM_STRING);
        assertThat(result.pojo1.getFour()).is(Conditions.RANDOM_STRING);

        assertThat(result.pojo2).isNull();
    }

}
