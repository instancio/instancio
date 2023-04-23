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
package org.instancio.test.features.assignment;

import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.assignment.AssignmentType;
import org.instancio.assignment.OnSetMethodNotFound;
import org.instancio.exception.InstancioApiException;
import org.instancio.internal.util.SystemProperties;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.field;

@FeatureTag(Feature.ASSIGNMENT)
@ExtendWith(InstancioExtension.class)
@DisabledIfSystemProperty(named = SystemProperties.ASSIGNMENT_TYPE, matches = "FIELD")
class OnSetMethodNotFoundTest {

    @SuppressWarnings("unused")
    private static class WithoutSetter {
        private int value;
    }

    private static class BooleanWithIsMethod {
        private Boolean is;
        private boolean populatedViaSetter;

        public void is(final Boolean is) {
            this.is = is;
            populatedViaSetter = true;
        }
    }

    @WithSettings
    private final Settings settings = Settings.create()
            .set(Keys.ASSIGNMENT_TYPE, AssignmentType.METHOD);

    @Test
    @DisplayName("Ignore boolean setter with signature: 'is(boolean b)' or 'is(Boolean b)'")
    void methodIsShouldBeIgnored() {
        final BooleanWithIsMethod result = Instancio.of(BooleanWithIsMethod.class)
                .ignore(field("populatedViaSetter"))
                .create();

        assertThat(result.is).isNotNull();
        assertThat(result.populatedViaSetter).isFalse();
    }

    @Test
    void assignViaField() {
        final WithoutSetter result = Instancio.of(WithoutSetter.class)
                .withSettings(Settings.create()
                        .set(Keys.ON_SET_METHOD_NOT_FOUND, OnSetMethodNotFound.ASSIGN_FIELD))
                .create();

        assertThat(result.value).isNotZero();
    }

    @Test
    void ignoreError() {
        final WithoutSetter result = Instancio.of(WithoutSetter.class)
                .withSettings(Settings.create()
                        .set(Keys.ON_SET_METHOD_NOT_FOUND, OnSetMethodNotFound.IGNORE))
                .create();

        assertThat(result.value).isZero();
    }

    @Test
    void failOnError() {
        final InstancioApi<WithoutSetter> api = Instancio.of(WithoutSetter.class)
                .withSettings(Settings.create()
                        .set(Keys.ON_SET_METHOD_NOT_FOUND, OnSetMethodNotFound.FAIL));

        assertThatThrownBy(api::create)
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("Setter method could not be resolved for field");
    }
}
