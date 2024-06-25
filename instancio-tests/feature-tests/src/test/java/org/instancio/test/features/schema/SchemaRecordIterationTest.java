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
import org.instancio.Random;
import org.instancio.generator.Generator;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.schema.CombinatorMethod;
import org.instancio.schema.CombinatorProvider;
import org.instancio.schema.DerivedSpec;
import org.instancio.schema.GeneratedSpec;
import org.instancio.schema.Schema;
import org.instancio.schema.SchemaResource;
import org.instancio.schema.SchemaSpec;
import org.instancio.schema.TemplateSpec;
import org.instancio.settings.Keys;
import org.instancio.settings.SchemaDataAccess;
import org.instancio.settings.Settings;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The schema should return values from the same record as long as
 * different specs {@code get()} methods are invoked.
 * Once any previously called spec {@code get()} methods is invoked again,
 * the schema should iterate to another record.
 */
@FeatureTag({Feature.SCHEMA, Feature.VALUE_SPEC})
@ExtendWith(InstancioExtension.class)
class SchemaRecordIterationTest {

    @WithSettings
    private final Settings settings = Settings.create()
            .set(Keys.SCHEMA_DATA_ACCESS, SchemaDataAccess.RANDOM);

    /**
     * This schema uses individual combinator providers.
     */
    @SchemaResource(path = "data/DataSpecWithTag.csv")
    private interface Sample extends Schema {

        SchemaSpec<Integer> id();

        SchemaSpec<String> field1();

        SchemaSpec<String> field2();

        @TemplateSpec("${field1}:${field2}")
        SchemaSpec<String> templateSpec();

        @DerivedSpec(fromSpec = {"id", "field1"}, by = Concatenator.class)
        SchemaSpec<String> derivedSpec();

        @GeneratedSpec(FixedValueGenerator.class)
        SchemaSpec<String> generatedSpec();
    }

    private static class Concatenator implements CombinatorProvider {
        @CombinatorMethod
        String concatenate(final Object x, final Object y) {
            return x + ":" + y;
        }
    }

    private static class FixedValueGenerator implements Generator<String> {
        @Override
        public String generate(final Random random) {
            return "fixed-value";
        }
    }

    @Test
    void shouldProduceValuesFromTheSameRecord_allSpecs() {
        final Sample schema = Instancio.ofSchema(Sample.class)
                .withTagValue("EN")
                .create();

        for (int i = 0; i < Constants.SAMPLE_SIZE_DDD; i++) {
            final Integer id = schema.id().get();

            assertThat(id).isIn(101, 102, 103);
            assertThat(schema.field1().get()).matches(String.format("D_%d", id));
            assertThat(schema.field2().get()).matches(String.format("F_%d", id));
            assertThat(schema.templateSpec().get()).matches(String.format("D_%d:F_%d", id, id));
            assertThat(schema.derivedSpec().get()).matches(String.format("%d:D_%d", id, id));
            assertThat(schema.generatedSpec().get()).isEqualTo("fixed-value");
        }
    }

    @Test
    void shouldProduceValuesFromTheSameRecord_compositeSpecs() {
        final Sample schema = Instancio.ofSchema(Sample.class)
                .withTagValue("EN")
                .create();

        for (int i = 0; i < Constants.SAMPLE_SIZE_DDD; i++) {
            final Integer id = schema.id().get();

            assertThat(id).isIn(101, 102, 103);
            assertThat(schema.templateSpec().get()).matches(String.format("D_%d:F_%d", id, id));
            assertThat(schema.derivedSpec().get()).matches(String.format("%d:D_%d", id, id));
        }
    }
}
