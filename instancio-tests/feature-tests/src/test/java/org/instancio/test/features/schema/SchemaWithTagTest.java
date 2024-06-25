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

import lombok.AllArgsConstructor;
import lombok.Data;
import org.instancio.Instancio;
import org.instancio.internal.util.CollectionUtils;
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
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

@FeatureTag({Feature.SCHEMA, Feature.VALUE_SPEC, Feature.GENERATE})
@ExtendWith(InstancioExtension.class)
class SchemaWithTagTest {

    @WithSettings
    private final Settings settings = Settings.create()
            .set(Keys.SCHEMA_DATA_ACCESS, SchemaDataAccess.RANDOM);

    @SchemaResource(path = "data/DataSpecWithTag.csv")
    private interface SampleSchema extends Schema {
        SchemaSpec<String> id();

        SchemaSpec<String> field1();

        SchemaSpec<String> field2();
    }

    @Data
    @AllArgsConstructor
    private static final class Entry {
        private final String id, field1, field2;
    }

    private static final Set<Entry> EN_ENTRIES = CollectionUtils.asSet(
            new Entry("101", "D_101", "F_101"),
            new Entry("102", "D_102", "F_102"),
            new Entry("103", "D_103", "F_103"));

    private static final Set<Entry> RU_ENTRIES = CollectionUtils.asSet(
            new Entry("201", "Д_201", "Ф_201"),
            new Entry("202", "Д_202", "Ф_202"),
            new Entry("203", "Д_203", "Ф_203"));

    private static final Set<Entry> ALL_ENTRIES = Stream
            .concat(EN_ENTRIES.stream(), RU_ENTRIES.stream())
            .collect(toSet());

    private static Stream<Arguments> tagArgs() {
        return Stream.of(
                Arguments.of("EN", EN_ENTRIES),
                Arguments.of("RU", RU_ENTRIES),
                Arguments.of(null, ALL_ENTRIES));
    }

    @Nested
    class UsingGenerateTest {

        @WithSettings
        private final Settings settings = Settings.create()
                .set(Keys.SCHEMA_DATA_ACCESS, SchemaDataAccess.RANDOM);

        @MethodSource("org.instancio.test.features.schema.SchemaWithTagTest#tagArgs")
        @ParameterizedTest
        void ofSet(final String tag, final Set<Entry> expectedEntries) {
            final SampleSchema result = Instancio.ofSchema(SampleSchema.class)
                    .withTagValue(tag)
                    .create();

            final Set<Entry> results = Instancio.ofSet(Entry.class)
                    .size(expectedEntries.size())
                    .generate(field(Entry::getId), result.id())
                    .generate(field(Entry::getField1), result.field1())
                    .generate(field(Entry::getField2), result.field2())
                    .create();

            assertThat(results).isEqualTo(expectedEntries);
        }

        @MethodSource("org.instancio.test.features.schema.SchemaWithTagTest#tagArgs")
        @ParameterizedTest
        void ofStream(final String tag, final Set<Entry> expectedEntries) {
            final SampleSchema result = Instancio.ofSchema(SampleSchema.class)
                    .withTagValue(tag)
                    .create();

            final Set<Entry> results = Instancio.of(Entry.class)
                    .generate(field(Entry::getId), result.id())
                    .generate(field(Entry::getField1), result.field1())
                    .generate(field(Entry::getField2), result.field2())
                    .stream()
                    .limit(Constants.SAMPLE_SIZE_DDD)
                    .collect(toSet());

            assertThat(results).isEqualTo(expectedEntries);
        }
    }

    @Nested
    class UsingSchemaSpecTest {

        @WithSettings
        private final Settings settings = Settings.create()
                .set(Keys.SCHEMA_DATA_ACCESS, SchemaDataAccess.RANDOM);

        @MethodSource("org.instancio.test.features.schema.SchemaWithTagTest#tagArgs")
        @ParameterizedTest
        void newSpecInstancePerObject(final String tag, final Set<Entry> expectedEntries) {
            final Set<Entry> results = Stream.generate(() -> {
                        // create each entry using a new Spec instance
                        final SampleSchema spec = Instancio.ofSchema(SampleSchema.class)
                                .withTagValue(tag)
                                .create();

                        return createAndAssertEntry(spec);
                    })
                    .limit(Constants.SAMPLE_SIZE_DDD)
                    .collect(toSet());

            assertThat(results).isEqualTo(expectedEntries);
        }

        @MethodSource("org.instancio.test.features.schema.SchemaWithTagTest#tagArgs")
        @ParameterizedTest
        void reusingSpecInstance(final String tag, final Set<Entry> expectedEntries) {
            // create each entry using the same Spec instance
            final SampleSchema spec = Instancio.ofSchema(SampleSchema.class)
                    .withTagValue(tag)
                    .create();

            final Set<Entry> results = Stream.generate(() -> createAndAssertEntry(spec))
                    .limit(Constants.SAMPLE_SIZE_DDD)
                    .collect(toSet());

            assertThat(results).isEqualTo(expectedEntries);
        }
    }

    private static Entry createAndAssertEntry(final SampleSchema spec) {
        final Entry entry = new Entry(spec.id().get(), spec.field1().get(), spec.field2().get());

        assertThat(entry.field1).endsWith(entry.id);
        assertThat(entry.field2).endsWith(entry.id);
        return entry;
    }

    /**
     * Verify using an arbitrary property (id in this case) as the tag key.
     */
    @ValueSource(strings = {"101", "102", "103", "201", "202", "203"})
    @ParameterizedTest
    void usingCustomTagKey(final String id) {
        // Run this first to load a schema with the default tag key.
        // This is to ensure that caching logic takes tag key into account
        // when multiple tag keys are used for the same schema.
        final SampleSchema schemaWithDefaultTagKey = Instancio.ofSchema(SampleSchema.class)
                .withTagValue("EN")
                .create();

        assertThat(schemaWithDefaultTagKey.field1().get()).matches("D_\\d{3}");

        // actual test
        final SampleSchema schema = Instancio.ofSchema(SampleSchema.class)
                .withSetting(Keys.SCHEMA_TAG_KEY, "id")
                .withTagValue(id)
                .create();

        for (int i = 0; i < Constants.SAMPLE_SIZE_D; i++) {
            assertThat(schema.field1().get()).endsWith(id);
            assertThat(schema.field2().get()).endsWith(id);
        }
    }
}
