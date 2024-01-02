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
import org.instancio.InstancioApi;
import org.instancio.exception.InstancioApiException;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.AssignmentType;
import org.instancio.settings.Keys;
import org.instancio.settings.OnSetMethodError;
import org.instancio.settings.OnSetMethodNotFound;
import org.instancio.settings.OnSetMethodUnmatched;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.assignment.OverloadedSettersPojo;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.tags.RunWith;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.field;

@RunWith.MethodAssignmentOnly
@FeatureTag({Feature.ASSIGNMENT_TYPE, Feature.ASSIGNMENT_TYPE_METHOD})
@ExtendWith(InstancioExtension.class)
class OverloadedSettersTest {

    @WithSettings
    private final Settings settings = Settings.create()
            .set(Keys.ASSIGNMENT_TYPE, AssignmentType.METHOD)
            .set(Keys.ON_SET_METHOD_NOT_FOUND, OnSetMethodNotFound.IGNORE)
            .set(Keys.ON_SET_METHOD_ERROR, OnSetMethodError.FAIL);

    /**
     * Should use the setter that matches the field type among the overloaded setter methods.
     * All other setters should NOT be invoked since {@link Keys#ON_SET_METHOD_UNMATCHED}
     * is set to {@link OnSetMethodUnmatched#IGNORE}.
     */
    @Test
    void shouldInvokeExpectedSetterOnly() {
        final OverloadedSettersPojo result = Instancio.of(OverloadedSettersPojo.class)
                .withSettings(Settings.create().set(Keys.ON_SET_METHOD_UNMATCHED, OnSetMethodUnmatched.IGNORE))
                .ignore(field("isViaSetter_theOne"))
                .create();

        assertThat(result.getTheOne()).isNotNull();

        assertThat(result.isSetViaMethod())
                .as("Value should be assigned via setter method")
                .isTrue();
    }

    @Test
    void shouldInvokeAllSetters() {
        final InstancioApi<OverloadedSettersPojo> api = Instancio.of(OverloadedSettersPojo.class)
                .withSettings(Settings.create().set(Keys.ON_SET_METHOD_UNMATCHED, OnSetMethodUnmatched.INVOKE))
                .ignore(field("isViaSetter_theOne"));

        assertThatThrownBy(api::create)
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("Throwing exception because:")
                .hasMessageContaining("Keys.ASSIGNMENT_TYPE = AssignmentType.METHOD")
                .rootCause()
                .isExactlyInstanceOf(UnsupportedOperationException.class) // thrown by the setter
                .hasMessage("This method throws an exception!");
    }
}
