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
import org.instancio.test.support.pojo.misc.StringsAbc;
import org.instancio.test.support.pojo.misc.StringsDef;
import org.instancio.test.support.pojo.misc.StringsGhi;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Assign.given;
import static org.instancio.Select.field;
import static org.instancio.test.features.generator.custom.populate.PopulateHelper.populate;

@FeatureTag({
        Feature.AFTER_GENERATE,
        Feature.ASSIGN,
        Feature.GENERATOR,
        Feature.MODEL
})
@ExtendWith(InstancioExtension.class)
class PopulateAssignTest {

    @Nested
    class AssignWithCollectionsTest {
        private final Supplier<StringsAbc> supplier = () -> StringsAbc.builder()
                .a(Instancio.gen().oneOf("A1", "A2").get())
                .def(StringsDef.builder()
                        .f("F")
                        .build())
                .build();

        private Model<StringsAbc> modelWithAssignments(final Model<StringsAbc> baseModel) {
            return Instancio.of(baseModel)
                    .assign(given(StringsAbc::getA).is("A1")
                            .set(field(StringsDef::getE), "E1")
                            .set(field(StringsGhi::getH), "H1"))
                    .assign(given(StringsAbc::getA).is("A2")
                            .set(field(StringsDef::getE), "E2")
                            .set(field(StringsGhi::getH), "H2"))
                    .assign(given(StringsDef::getF).is("F").set(field(StringsAbc::getC), "C"))
                    .toModel();
        }

        @EnumSource(value = AfterGenerate.class, names = {"POPULATE_NULLS", "POPULATE_NULLS_AND_DEFAULT_PRIMITIVES"})
        @ParameterizedTest
        void stream(final AfterGenerate afterGenerate) {
            final Model<StringsAbc> baseModel = populate(supplier, afterGenerate);
            final Model<StringsAbc> modelWithAssignments = modelWithAssignments(baseModel);

            final List<StringsAbc> results = Instancio.of(modelWithAssignments)
                    .stream()
                    .limit(Constants.SAMPLE_SIZE_DD)
                    .collect(Collectors.toList());

            assertResults(results);
        }

        @EnumSource(value = AfterGenerate.class, names = {"POPULATE_NULLS", "POPULATE_NULLS_AND_DEFAULT_PRIMITIVES"})
        @ParameterizedTest
        void ofSet(final AfterGenerate afterGenerate) {
            final Model<StringsAbc> baseModel = populate(supplier, afterGenerate);
            final Model<StringsAbc> modelWithAssignments = modelWithAssignments(baseModel);

            final Set<StringsAbc> results = Instancio.ofSet(modelWithAssignments)
                    .size(Constants.SAMPLE_SIZE_DD)
                    .create();

            assertResults(results);
        }

        private void assertResults(final Collection<StringsAbc> results) {
            assertThat(results).hasSize(Constants.SAMPLE_SIZE_DD).allSatisfy(result -> {
                assertThat(result.getA()).isIn("A1", "A2");

                if (result.getA().equals("A1")) {
                    assertThat(result.getDef().getE()).isEqualTo("E1");
                    assertThat(result.getDef().getGhi().getH()).isEqualTo("H1");
                } else {
                    assertThat(result.getDef().getE()).isEqualTo("E2");
                    assertThat(result.getDef().getGhi().getH()).isEqualTo("H2");
                }

                assertThat(result.getC()).isEqualTo("C");
                assertThat(result.getDef().getF()).isEqualTo("F");
            });
        }
    }
}
