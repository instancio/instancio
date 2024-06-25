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
import org.instancio.schema.TemplateSpec;
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
class SchemaWithTemplateSpecTest {

    @WithSettings
    private final Settings settings = Settings.create()
            .set(Keys.SCHEMA_DATA_ACCESS, SchemaDataAccess.RANDOM);

    @SchemaResource(path = "data/SchemaTemplateSpec.csv")
    private interface SampleSchema extends Schema {

        @TemplateSpec("${number} ${stringA}")
        SchemaSpec<String> derivedString();

        @TemplateSpec("${stringA} ${stringA} ${stringA}")
        SchemaSpec<String> repeatingDerivedString();

        @TemplateSpec("[${derivedString}] ${stringB}")
        SchemaSpec<String> derivedDerivedString();
    }

    @Test
    void derived() {
        final SampleSchema result = Instancio.createSchema(SampleSchema.class);

        final Set<String> results = Stream.generate(() -> result.derivedString().get())
                .limit(Constants.SAMPLE_SIZE_DD)
                .collect(Collectors.toSet());

        assertThat(results).containsOnly("1 a1", "2 a2", "3 a3");
    }

    @Test
    void repeatingDerivedString() {
        final SampleSchema result = Instancio.createSchema(SampleSchema.class);

        final Set<String> results = Stream.generate(() -> result.repeatingDerivedString().get())
                .limit(Constants.SAMPLE_SIZE_DD)
                .collect(Collectors.toSet());

        assertThat(results).containsOnly("a1 a1 a1", "a2 a2 a2", "a3 a3 a3");
    }

    @Test
    void derivedDerivedString() {
        final SampleSchema result = Instancio.createSchema(SampleSchema.class);

        final Set<String> results = Stream.generate(() -> result.derivedDerivedString().get())
                .limit(Constants.SAMPLE_SIZE_DD)
                .collect(Collectors.toSet());

        assertThat(results).containsOnly("[3 a3] b3", "[2 a2] b2", "[1 a1] b1");
    }

    @Test
    void nullableDerived() {
        final SampleSchema result = Instancio.createSchema(SampleSchema.class);

        final Set<String> results = Stream.generate(() -> result.derivedDerivedString().nullable().get())
                .limit(Constants.SAMPLE_SIZE_DD)
                .collect(Collectors.toSet());

        assertThat(results).containsOnly(null, "[3 a3] b3", "[2 a2] b2", "[1 a1] b1");
    }
}
