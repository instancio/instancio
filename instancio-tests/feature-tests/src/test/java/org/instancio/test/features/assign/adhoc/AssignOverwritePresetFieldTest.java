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
package org.instancio.test.features.assign.adhoc;

import lombok.Data;
import org.instancio.Assign;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.Keys;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag({Feature.ASSIGN, Feature.OVERWRITE_EXISTING_VALUES})
@ExtendWith(InstancioExtension.class)
class AssignOverwritePresetFieldTest {

    private static final String INITIAL_VALUE = "--A--";

    private static @Data class Pojo {
        private String a = INITIAL_VALUE;
        private String b;
    }

    @ValueSource(booleans = {true, false})
    @ParameterizedTest(name = "when Keys.OVERWRITE_EXISTING_VALUES set to {0}")
    @DisplayName("Should overwrite initialised field 'a' with the value generated for field 'b'")
    void overwriteInitializedFieldUsingAssignment(final boolean overwriteExistingValues) {
        final Pojo result = Instancio.of(Pojo.class)
                .withSetting(Keys.OVERWRITE_EXISTING_VALUES, overwriteExistingValues)
                .assign(Assign.valueOf(Pojo::getB).to(Pojo::getA))
                .create();

        assertThat(result.getA())
                .isEqualTo(result.getB())
                .isNotEqualTo(INITIAL_VALUE);
    }
}
