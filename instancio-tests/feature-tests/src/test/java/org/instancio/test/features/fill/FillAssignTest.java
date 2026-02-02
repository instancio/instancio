/*
 * Copyright 2022-2026 the original author or authors.
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
package org.instancio.test.features.fill;

import org.instancio.Assign;
import org.instancio.Instancio;
import org.instancio.InstancioObjectApi;
import org.instancio.exception.UnresolvedAssignmentException;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.FillType;
import org.instancio.test.support.pojo.misc.StringsAbc;
import org.instancio.test.support.pojo.misc.StringsDef;
import org.instancio.test.support.pojo.misc.StringsGhi;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@FeatureTag({Feature.ASSIGN, Feature.FILL})
@ExtendWith(InstancioExtension.class)
class FillAssignTest {

    @Nested
    class GeneratedFieldTest {

        @EnumSource(value = FillType.class, names = {"POPULATE_NULLS", "POPULATE_NULLS_AND_DEFAULT_PRIMITIVES"})
        @ParameterizedTest
        @DisplayName("Overwrite initialised field with a GENERATED field value")
        void assignValueOfGeneratedField(final FillType fillType) {
            final String presetValueE = "E-value";

            // Initialise field e
            final StringsAbc object = StringsAbc.builder()
                    .def(StringsDef.builder()
                            .e(presetValueE)
                            .build())
                    .build();

            // Overwrite field e via assign() with field h (generated value)
            Instancio.ofObject(object)
                    .withFillType(fillType)
                    .assign(Assign.valueOf(StringsGhi::getH).to(StringsDef::getE))
                    .fill();

            assertThat(object.getDef().getE())
                    .isNotBlank()
                    .isNotEqualTo(presetValueE)
                    .isEqualTo(object.getDef().getGhi().getH());
        }

        @Test
        @DisplayName("Using APPLY_SELECTORS")
        void assignValueOfGeneratedField_applySelectors() {
            final String presetValueE = "E-value";

            // Initialise field e
            final StringsAbc object = StringsAbc.builder()
                    .def(StringsDef.builder()
                            .e(presetValueE)
                            .build())
                    .build();

            // Overwrite field e via assign() with field h.
            // However, since we're using APPLY_SELECTORS, field h
            // was not generated, therefore we get unresolved assignment error
            final InstancioObjectApi<StringsAbc> api = Instancio.ofObject(object)
                    .withFillType(FillType.APPLY_SELECTORS)
                    .assign(Assign.valueOf(StringsGhi::getH).to(StringsDef::getE));

            assertThatThrownBy(api::fill)
                    .isExactlyInstanceOf(UnresolvedAssignmentException.class)
                    .hasMessageContaining("from [field(StringsGhi, \"h\")] to [field(StringsDef, \"e\")]");
        }
    }

    @EnumSource(FillType.class)
    @ParameterizedTest
    @DisplayName("Overwrite initialised field with another INITIALISED field value")
    void assignValueOfPresetField(final FillType fillType) {
        final String presetValueE = "E-value";
        final String presetValueH = "H-value";

        // Initialise fields e and h
        final StringsAbc object = StringsAbc.builder()
                .def(StringsDef.builder()
                        .e(presetValueE)
                        .ghi(StringsGhi.builder()
                                .h(presetValueH)
                                .build())
                        .build())
                .build();

        // Overwrite field e via assign() with field h (preset value)
        Instancio.ofObject(object)
                .withFillType(fillType)
                .assign(Assign.valueOf(StringsGhi::getH).to(StringsDef::getE))
                .fill();

        assertThat(object.getDef().getE())
                .isEqualTo(object.getDef().getGhi().getH())
                .isEqualTo(presetValueH);
    }
}
