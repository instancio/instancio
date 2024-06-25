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
import org.instancio.schema.GeneratedSpec;
import org.instancio.schema.NullableSpec;
import org.instancio.schema.PostProcessor;
import org.instancio.schema.Schema;
import org.instancio.schema.SchemaResource;
import org.instancio.schema.SchemaSpec;
import org.instancio.schema.WithPostProcessor;
import org.instancio.settings.Keys;
import org.instancio.settings.SchemaDataAccess;
import org.instancio.settings.Settings;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag({Feature.SCHEMA, Feature.VALUE_SPEC})
@ExtendWith(InstancioExtension.class)
class SchemaGeneratedSpecTest {

    @WithSettings
    private final Settings settings = Settings.create()
            .set(Keys.SCHEMA_DATA_ACCESS, SchemaDataAccess.RANDOM);

    @SchemaResource(data = "id\n1")
    private interface SampleSchema extends Schema {
        SchemaSpec<Integer> id();

        @GeneratedSpec(CustomGenerator.class)
        SchemaSpec<Integer> value();

        @WithPostProcessor(NumberNegator.class)
        @GeneratedSpec(CustomGenerator.class)
        SchemaSpec<Integer> valueWithPostProcessor();

        @NullableSpec
        @GeneratedSpec(CustomGenerator.class)
        SchemaSpec<Integer> nullable();

        @NullableSpec
        @WithPostProcessor(NumberNegator.class)
        @GeneratedSpec(CustomGenerator.class)
        SchemaSpec<Integer> nullableWithPostProcessor();
    }

    private static class CustomGenerator implements Generator<Integer> {
        @Override
        public Integer generate(final Random random) {
            return 12345;
        }
    }

    private static class NumberNegator implements PostProcessor<Integer> {
        @Override
        public Integer process(final Integer input, final Random random) {
            return -input;
        }
    }

    @Test
    void generator() {
        final SampleSchema result = Instancio.createSchema(SampleSchema.class);

        assertThat(result.id().get()).isEqualTo(1);
        assertThat(result.value().get()).isEqualTo(12345);
        assertThat(result.valueWithPostProcessor().get()).isEqualTo(-12345);
    }

    @Test
    void nullable() {
        final SampleSchema schema = Instancio.createSchema(SampleSchema.class);

        final Set<Integer> results = Stream.generate(() -> schema.nullable().get())
                .limit(Constants.SAMPLE_SIZE_DD)
                .collect(Collectors.toSet());

        assertThat(results).containsExactly(null, 12345);
    }

    @Test
    void nullableValueWithPostProcessor() {
        final SampleSchema schema = Instancio.createSchema(SampleSchema.class);

        final Set<Integer> results = Stream.generate(() -> schema.nullableWithPostProcessor().get())
                .limit(Constants.SAMPLE_SIZE_DD)
                .collect(Collectors.toSet());

        assertThat(results).containsExactly(null, -12345);
    }
}
