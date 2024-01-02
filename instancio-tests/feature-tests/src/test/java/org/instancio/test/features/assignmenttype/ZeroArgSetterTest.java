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
package org.instancio.test.features.assignmenttype;

import org.instancio.Instancio;
import org.instancio.settings.AssignmentType;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.assignment.ZeroArgSetter;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag(Feature.ASSIGNMENT_TYPE)
class ZeroArgSetterTest {

    @Test
    @DisplayName("Method name that matches the setter but takes no arguments")
    void shouldFallbackToFieldAssignment() {
        final ZeroArgSetter result = Instancio.of(ZeroArgSetter.class)
                .withSettings(Settings.create()
                        .set(Keys.ASSIGNMENT_TYPE, AssignmentType.METHOD))
                .create();

        assertThat(result.getValue()).isNotNull();
    }

}
