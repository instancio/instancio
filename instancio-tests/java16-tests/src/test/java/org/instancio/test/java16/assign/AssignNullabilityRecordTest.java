/*
 * Copyright 2022-2023 the original author or authors.
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
package org.instancio.test.java16.assign;

import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.When;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.java16.record.StringsAbcRecord;
import org.instancio.test.support.java16.record.StringsDefRecord;
import org.instancio.test.support.java16.record.StringsGhiRecord;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Assign.given;
import static org.instancio.Assign.valueOf;
import static org.instancio.Select.all;
import static org.instancio.Select.field;
import static org.instancio.Select.root;

@FeatureTag({Feature.ASSIGN, Feature.WITH_NULLABLE})
@ExtendWith(InstancioExtension.class)
class AssignNullabilityRecordTest {

    private static final String B_VAL = "B";
    private static final String D_VAL = "D";
    private static final String G_VAL = "G";
    private static final int LIST_SIZE = 200;

    @Test
    void nullRoot() {
        final StringsAbcRecord result = Instancio.of(StringsAbcRecord.class)
                .set(root(), null)
                .assign(// should all be ignored and not raise an error
                        valueOf(StringsAbcRecord::a).to(StringsAbcRecord::b),
                        valueOf(StringsAbcRecord::b).to(StringsAbcRecord::a),
                        valueOf(StringsGhiRecord::g).to(StringsGhiRecord::i),
                        valueOf(StringsGhiRecord::i).to(StringsGhiRecord::g))
                .create();

        assertThat(result).isNull();
    }

    @Test
    void withNullableRecord() {
        final List<StringsAbcRecord> results = Instancio.ofList(StringsAbcRecord.class)
                .size(LIST_SIZE)
                .withNullable(all(StringsDefRecord.class))
                .assign(given(StringsAbcRecord::a)
                        .satisfies(obj -> true)
                        .set(field(StringsDefRecord::d), D_VAL))
                .create();

        assertThat(results)
                .hasSize(LIST_SIZE)
                .extracting(StringsAbcRecord::def)
                .containsNull();

        assertThat(results)
                .filteredOn(res -> res.def() != null)
                .extracting(res -> res.def().d())
                .containsOnly(D_VAL);
    }

    /**
     * When {@code SomeRecord} is set to null,
     * using {@code SomeRecord.someField} as an assignment <b>destination</b>
     * should not trigger any errors.
     */
    @Nested
    class RecordDestinationFieldIsIgnoredWhenRecordIsSetToNullTest {

        @Test
        void setNullRecordViaSet() {
            final StringsAbcRecord result = Instancio.of(StringsAbcRecord.class)
                    .set(all(StringsDefRecord.class), null)
                    .assign(valueOf(StringsAbcRecord::a).to(StringsDefRecord::d)) // ignored
                    .create();

            assertThat(result.def()).isNull();
        }

        @Test
        void setNullRecordViaAssignment() {
            final StringsAbcRecord result = Instancio.of(StringsAbcRecord.class)
                    .assign(given(StringsAbcRecord::a).satisfies(o -> true).set(all(StringsDefRecord.class), null))
                    .assign(valueOf(StringsAbcRecord::a).to(StringsDefRecord::d)) // ignored
                    .create();

            assertThat(result.def()).isNull();
        }
    }

    /**
     * When {@code SomeRecord} is set to null,
     * using {@code SomeRecord.someField} as an assignment <b>origin</b>
     * should not trigger any errors.
     */
    @Nested
    class RecordOriginFieldIsIgnoredWhenRecordIsSetToNullTest {

        @Test
        void setNullRecordViaSet() {
            final StringsAbcRecord result = Instancio.of(StringsAbcRecord.class)
                    .set(all(StringsDefRecord.class), null)
                    .assign(valueOf(StringsDefRecord::d).to(StringsGhiRecord::g))
                    .create();

            assertThat(result.def()).isNull();
        }

        @Test
        void setNullRecordViaAssignment() {
            final StringsAbcRecord result = Instancio.of(StringsAbcRecord.class)
                    .assign(given(StringsAbcRecord::a).satisfies(o -> true).set(all(StringsDefRecord.class), null))
                    .assign(valueOf(StringsDefRecord::d).to(StringsGhiRecord::g))
                    .create();

            assertThat(result.def()).isNull();
        }
    }

    /**
     * Use case: given two fields, 'b' and 'g', one of them must be null, but not both.
     */
    @Nested
    class OneOfTwoFieldsIsNullTopDownTest {

        private final Model<List<StringsAbcRecord>> model = Instancio.ofList(StringsAbcRecord.class)
                .size(LIST_SIZE)
                .assign(given(field(StringsAbcRecord::b), field(StringsGhiRecord::g))
                        .set(When.isNull(), G_VAL)
                        .set(When.isNotNull(), null))
                .toModel();

        @RepeatedTest(Constants.SAMPLE_SIZE_DD)
        void withNullableOrigin() {
            final List<StringsAbcRecord> results = Instancio.of(model)
                    .withNullable(field(StringsAbcRecord::b))
                    .set(field(StringsAbcRecord::b), B_VAL)
                    .create();

            assertEither_B_or_G_isNullButNotBoth(results);
        }

        @RepeatedTest(Constants.SAMPLE_SIZE_DD)
        void nullableOriginViaGeneratorSpec() {
            final List<StringsAbcRecord> results = Instancio.of(model)
                    .generate(field(StringsAbcRecord::b), gen -> gen.string().prefix(B_VAL).length(0).nullable())
                    .create();

            assertEither_B_or_G_isNullButNotBoth(results);
        }

        private void assertEither_B_or_G_isNullButNotBoth(final List<StringsAbcRecord> results) {
            assertThat(results).extracting(o -> o.def().ghi().g()).containsOnly(null, G_VAL);
            assertThat(results).extracting(StringsAbcRecord::b).containsOnly(null, B_VAL);

            assertThat(results)
                    .hasSize(LIST_SIZE)
                    .filteredOn(obj -> Objects.equals(obj.b(), null))
                    .allMatch(obj -> Objects.equals(obj.def().ghi().g(), G_VAL));

            assertThat(results)
                    .filteredOn(obj -> Objects.equals(obj.b(), B_VAL))
                    .allMatch(obj -> Objects.equals(obj.def().ghi().g(), null));
        }
    }
}
