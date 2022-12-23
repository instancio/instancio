/*
 * Copyright 2022 the original author or authors.
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
package org.instancio.test.features.assignment;

import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.assignment.AssignmentType;
import org.instancio.assignment.OnSetMethodError;
import org.instancio.exception.InstancioApiException;
import org.instancio.internal.util.SystemProperties;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.allInts;

@FeatureTag(Feature.ASSIGNMENT)
@ExtendWith(InstancioExtension.class)
@DisabledIfSystemProperty(named = SystemProperties.ASSIGNMENT_TYPE, matches = "FIELD")
class OnSetMethodErrorTest {

    @SuppressWarnings("unused")
    private static class SetterErrorPojo {
        private int value;

        private void setValue(final int value) {
            throw new UnsupportedOperationException("setter error");
        }
    }

    @WithSettings
    private final Settings settings = Settings.create()
            .set(Keys.ASSIGNMENT_TYPE, AssignmentType.METHOD);

    @Test
    void assignViaField() {
        final SetterErrorPojo result = Instancio.of(SetterErrorPojo.class)
                .withSettings(Settings.create()
                        .set(Keys.ON_SET_METHOD_ERROR, OnSetMethodError.ASSIGN_FIELD))
                .create();

        assertThat(result.value).isNotZero();
    }

    @Test
    void ignoreError() {
        final SetterErrorPojo result = Instancio.of(SetterErrorPojo.class)
                .withSettings(Settings.create()
                        .set(Keys.ON_SET_METHOD_ERROR, OnSetMethodError.IGNORE))
                .create();

        assertThat(result.value).isZero();
    }

    @Test
    void failOnError() {
        final int expectedValue = 123;
        final InstancioApi<SetterErrorPojo> api = Instancio.of(SetterErrorPojo.class)
                .set(allInts(), expectedValue)
                .withSettings(Settings.create()
                        .set(Keys.ON_SET_METHOD_ERROR, OnSetMethodError.FAIL));

        assertThatThrownBy(api::create)
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasRootCauseExactlyInstanceOf(UnsupportedOperationException.class)
                .hasMessage(String.format("%n" +
                        "Throwing exception because:%n" +
                        " -> Keys.ASSIGNMENT_TYPE = AssignmentType.METHOD%n" +
                        " -> Keys.ON_SET_METHOD_ERROR = OnSetMethodError.FAIL%n" +
                        "%n" +
                        "Method invocation failed:%n" +
                        " -> Method: OnSetMethodErrorTest$SetterErrorPojo.setValue(int)%n" +
                        " -> Argument type:  Integer%n" +
                        " -> Argument value: " + expectedValue + "%n" +
                        "%n" +
                        "Root cause: %n" +
                        " -> java.lang.UnsupportedOperationException: setter error%n" +
                        "%n" +
                        "To resolve the error, consider one of the following:%n" +
                        " -> Address the root cause that triggered the exception%n" +
                        " -> Update Keys.ON_SET_METHOD_ERROR setting to%n" +
                        "    -> OnSetMethodError.ASSIGN_FIELD to assign value via field%n" +
                        "    -> OnSetMethodError.IGNORE to leave value uninitialised%n"));
    }
}
