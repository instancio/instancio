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
package org.instancio.test.features.assignmenttype;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.AssignmentType;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.assignment.SetterStylePojo;
import org.instancio.test.support.pojo.assignment.SetterStyleSet;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.tags.RunWith;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatObject;
import static org.instancio.Select.fields;

@FeatureTag(Feature.ASSIGNMENT_TYPE)
@RunWith.FieldAssignmentOnly
@ExtendWith(InstancioExtension.class)
class AssignmentTypeFieldTest {

    @Test
    void fieldAssignmentShouldNotUseSetters() {
        final SetterStyleSet result = Instancio.of(SetterStyleSet.class)
                .withSettings(Settings.create().set(Keys.ASSIGNMENT_TYPE, AssignmentType.FIELD))
                // These are used for verification; we don't want to generate values for these flags
                .ignore(fields().matching("viaSetter.*"))
                .create();

        assertResult(result);
    }

    private static void assertResult(final SetterStylePojo result) {
        assertThatObject(result).hasNoNullFieldsOrProperties();

        assertThat(result.getPrimitiveInt()).isPositive();
        assertThat(result.getIsBooleanWrapper()).isNotNull();
        assertThat(result.getNoIsPrefixBooleanPropertyWrapper()).isNotNull();

        assertThat(result.isViaSetter_primitiveInt()).isFalse();
        assertThat(result.isViaSetter_integerWrapper()).isFalse();
        assertThat(result.isViaSetter_string()).isFalse();
        assertThat(result.isViaSetter_isBooleanProperty()).isFalse();
        assertThat(result.isViaSetter_isBooleanWrapper()).isFalse();
        assertThat(result.isViaSetter_noIsPrefixBooleanProperty()).isFalse();
        assertThat(result.isViaSetter_noIsPrefixBooleanPropertyWrapper()).isFalse();
    }
}
