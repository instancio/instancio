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
package org.instancio.test.features.assignmenttype;

import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.exception.InstancioApiException;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.AssignmentType;
import org.instancio.settings.Keys;
import org.instancio.settings.OnSetMethodError;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.misc.SetterErrorPojo;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.tags.RunWith;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.allInts;

@FeatureTag({Feature.ASSIGNMENT_TYPE, Feature.ASSIGNMENT_TYPE_METHOD})
@ExtendWith(InstancioExtension.class)
@RunWith.MethodAssignmentOnly
class OnSetMethodErrorTest {

    @WithSettings
    private final Settings settings = Settings.create()
            .set(Keys.ASSIGNMENT_TYPE, AssignmentType.METHOD);

    @Test
    void assignViaField() {
        final SetterErrorPojo result = Instancio.of(SetterErrorPojo.class)
                .withSettings(Settings.create()
                        .set(Keys.ON_SET_METHOD_ERROR, OnSetMethodError.ASSIGN_FIELD))
                .create();

        assertThat(result.getValue()).isNotZero();
    }

    @Test
    void ignoreError() {
        final SetterErrorPojo result = Instancio.of(SetterErrorPojo.class)
                .withSettings(Settings.create()
                        .set(Keys.ON_SET_METHOD_ERROR, OnSetMethodError.IGNORE))
                .create();

        assertThat(result.getValue()).isZero();
    }

    @Test
    void failOnError() {
        final InstancioApi<SetterErrorPojo> api = Instancio.of(SetterErrorPojo.class)
                .set(allInts(), 123)
                .withSettings(Settings.create()
                        .set(Keys.ON_SET_METHOD_ERROR, OnSetMethodError.FAIL));

        assertThatThrownBy(api::create)
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasRootCauseExactlyInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining("Method invocation failed");
    }
}
