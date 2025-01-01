/*
 * Copyright 2022-2025 the original author or authors.
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
package org.instancio.test.features.generator.custom.populate;

import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.generator.AfterGenerate;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.misc.StringsAbc;
import org.instancio.test.support.pojo.misc.StringsDef;
import org.instancio.test.support.pojo.misc.StringsGhi;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.instancio.test.support.asserts.ReflectionAssert.assertThatObject;

@FeatureTag({Feature.AFTER_GENERATE, Feature.GENERATOR, Feature.MODEL})
@ExtendWith(InstancioExtension.class)
class PopulateNestedObjectTest {

    @Nested
    class PopulateWithoutOverwritingExistingValuesTest {
        /**
         * Given a shallow object, with nested objects that are {@code null}, should
         * populate fields of nested objects and apply selectors since default.
         */
        private void assertShouldPopulateNullNestedObjects(
                final AfterGenerate afterGenerate,
                final boolean overwriteExistingValues) {

            final StringsAbc objToPopulate = StringsAbc.builder().a("A").build();
            final Model<StringsAbc> model = PopulateHelper.populate(objToPopulate, afterGenerate);

            final StringsAbc result = Instancio.of(model)
                    .withSettings(Settings.create()
                            .set(Keys.OVERWRITE_EXISTING_VALUES, overwriteExistingValues))
                    .set(field(StringsAbc::getB), "B")
                    .set(field(StringsDef::getE), "E") // nested object property
                    .set(field(StringsGhi::getH), "H") // nested object property
                    .create();

            assertThat(result).isSameAs(objToPopulate);
            assertThat(result.getA()).isEqualTo("A");
            assertThat(result.getB()).isEqualTo("B");
            assertThat(result.getDef().getE()).isEqualTo("E");
            assertThat(result.getDef().getGhi().getH()).isEqualTo("H");
            assertThatObject(result).isFullyPopulated();
        }

        @Test
        void populateNulls() {
            assertShouldPopulateNullNestedObjects(AfterGenerate.POPULATE_NULLS, true);
            assertShouldPopulateNullNestedObjects(AfterGenerate.POPULATE_NULLS, false);
        }

        @Test
        void populateNullsAndDefaultPrimitives() {
            assertShouldPopulateNullNestedObjects(AfterGenerate.POPULATE_NULLS_AND_DEFAULT_PRIMITIVES, true);
            assertShouldPopulateNullNestedObjects(AfterGenerate.POPULATE_NULLS_AND_DEFAULT_PRIMITIVES, false);
        }

        @Test
        void populateAll() {
            assertShouldPopulateNullNestedObjects(AfterGenerate.POPULATE_ALL, false);
        }
    }

    /**
     * Given an object with nested objects that are <b>not</b> {@code null},
     * should populate fields of nested objects and apply selectors.
     */
    @EnumSource(value = AfterGenerate.class, names = {"POPULATE_NULLS", "POPULATE_NULLS_AND_DEFAULT_PRIMITIVES"})
    @ParameterizedTest
    void shouldPopulateInitialisedNestedObjects(final AfterGenerate afterGenerate) {
        final StringsAbc objToPopulate = StringsAbc.builder()
                .a("A")
                .def(StringsDef.builder()
                        .d("D")
                        .ghi(StringsGhi.builder().i("I").build())
                        .build())
                .build();

        final Model<StringsAbc> model = PopulateHelper.populate(objToPopulate, afterGenerate);

        final StringsAbc result = Instancio.of(model)
                .set(field(StringsAbc::getB), "B")
                .set(field(StringsDef::getE), "E")
                .set(field(StringsGhi::getH), "H")
                .create();

        assertThat(result).isSameAs(objToPopulate);
        assertThat(result.getA()).isEqualTo("A");
        assertThat(result.getB()).isEqualTo("B");
        assertThat(result.getDef().getD()).isEqualTo("D");
        assertThat(result.getDef().getE()).isEqualTo("E");
        assertThat(result.getDef().getGhi().getH()).isEqualTo("H");
        assertThat(result.getDef().getGhi().getI()).isEqualTo("I");
        assertThatObject(result).isFullyPopulated();
    }

}
