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
import org.instancio.settings.OnSetMethodNotFound;
import org.instancio.settings.Settings;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.tags.RunWith;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.field;

@RunWith.MethodAssignmentOnly
@FeatureTag({Feature.ASSIGNMENT_TYPE, Feature.ASSIGNMENT_TYPE_METHOD})
@ExtendWith(InstancioExtension.class)
class OnSetMethodNotFoundTest {

    @SuppressWarnings("unused")
    private static class WithoutSetter {
        private int value;
    }

    private static class FinalFieldWithoutSetter {
        private final List<String> list = new ArrayList<>();
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
                .withSetting(Keys.ON_SET_METHOD_NOT_FOUND, OnSetMethodNotFound.ASSIGN_FIELD)
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
                .withSetting(Keys.ON_SET_METHOD_NOT_FOUND, OnSetMethodNotFound.FAIL);

        assertThatThrownBy(api::create)
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("Setter method could not be resolved for field");
    }

    /**
     * {@code final} fields cannot have setters,
     * so make sure not to fail in this case.
     */
    @Test
    void onSetMethodNotFoundWithFinalField() {
        final FinalFieldWithoutSetter result = Instancio.of(FinalFieldWithoutSetter.class)
                .withSetting(Keys.FAIL_ON_ERROR, true)
                .withSetting(Keys.ON_SET_METHOD_NOT_FOUND, OnSetMethodNotFound.FAIL)
                .create();

        assertThat(result.list).isEmpty();
    }

}
