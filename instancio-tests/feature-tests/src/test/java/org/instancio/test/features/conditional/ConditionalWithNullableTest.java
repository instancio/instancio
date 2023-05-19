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
package org.instancio.test.features.conditional;

import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.misc.StringsAbc;
import org.instancio.test.support.pojo.misc.StringsGhi;
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
import static org.instancio.Select.field;
import static org.instancio.Select.valueOf;

@FeatureTag({
        Feature.CONDITIONAL,
        Feature.MODEL,
        Feature.WITH_NULLABLE
})
@ExtendWith(InstancioExtension.class)
class ConditionalWithNullableTest {

    private static final String A_VAL = "A";
    private static final String B_VAL = "B";
    private static final String G_VAL = "G";
    private static final int LIST_SIZE = 200;

    /**
     * Use case: given two fields, 'b' and 'g', one of them must be null, but not both.
     */
    @Nested
    class OneOfTwoFieldsIsNullTopDownTest {

        private final Model<List<StringsAbc>> model = Instancio.ofList(StringsAbc.class)
                .size(LIST_SIZE)
                // origin = b, destination = g
                .when(valueOf(StringsAbc::getB).is(B_VAL).set(field(StringsGhi::getG), null))
                .when(valueOf(StringsAbc::getB).is(null).set(field(StringsGhi::getG), G_VAL))
                .toModel();

        @RepeatedTest(Constants.SAMPLE_SIZE_DD)
        void withNullableOrigin() {
            final List<StringsAbc> results = Instancio.of(model)
                    .withNullable(field(StringsAbc::getB))
                    .set(field(StringsAbc::getB), B_VAL)
                    .create();

            assertEither_B_or_G_isNullButNotBoth(results);
        }

        @RepeatedTest(Constants.SAMPLE_SIZE_DD)
        void nullableOriginViaGeneratorSpec() {
            final List<StringsAbc> results = Instancio.of(model)
                    .generate(field(StringsAbc::getB), gen -> gen.string().prefix(B_VAL).length(0).nullable())
                    .create();

            assertEither_B_or_G_isNullButNotBoth(results);
        }
    }

    /**
     * Use case: given two fields, 'b' and 'g', one of them must be null, but not both.
     */
    @Nested
    class OneOfTwoFieldsIsNullBottomUpTest {

        private final Model<List<StringsAbc>> model = Instancio.ofList(StringsAbc.class)
                .size(LIST_SIZE)
                // origin = g, destination = b
                .when(valueOf(StringsGhi::getG).is(G_VAL).set(field(StringsAbc::getB), null))
                .when(valueOf(StringsGhi::getG).is(null).set(field(StringsAbc::getB), B_VAL))
                .toModel();

        @RepeatedTest(Constants.SAMPLE_SIZE_DD)
        void withNullableOrigin() {
            final List<StringsAbc> results = Instancio.of(model)
                    .withNullable(field(StringsGhi::getG))
                    .set(field(StringsGhi::getG), G_VAL)
                    .create();

            assertEither_B_or_G_isNullButNotBoth(results);
        }

        @RepeatedTest(Constants.SAMPLE_SIZE_DD)
        void nullableOriginViaGeneratorSpec() {
            final List<StringsAbc> results = Instancio.of(model)
                    .generate(field(StringsGhi::getG), gen -> gen.string().prefix(G_VAL).length(0).nullable())
                    .create();

            assertEither_B_or_G_isNullButNotBoth(results);
        }
    }

    private void assertEither_B_or_G_isNullButNotBoth(final List<StringsAbc> results) {
        assertThat(results).extracting(o -> o.def.ghi.g).containsOnly(null, G_VAL);
        assertThat(results).extracting(StringsAbc::getB).containsOnly(null, B_VAL);

        assertThat(results)
                .hasSize(LIST_SIZE)
                .filteredOn(obj -> Objects.equals(obj.b, null))
                .allMatch(obj -> Objects.equals(obj.def.ghi.g, G_VAL));

        assertThat(results)
                .filteredOn(obj -> Objects.equals(obj.b, B_VAL))
                .allMatch(obj -> Objects.equals(obj.def.ghi.g, null));
    }

    @Test
    void withNullableDestination() {
        final List<StringsAbc> result = Instancio.ofList(StringsAbc.class)
                .size(LIST_SIZE)
                .withNullable(field(StringsAbc::getB))
                .set(field(StringsAbc::getA), A_VAL)
                .when(valueOf(StringsAbc::getA).is(A_VAL).set(field(StringsAbc::getB), B_VAL))
                .create();

        assertThat(result).extracting(StringsAbc::getA).containsOnly(A_VAL);
        assertThat(result).extracting(StringsAbc::getB).containsOnly(null, B_VAL);
    }
}
