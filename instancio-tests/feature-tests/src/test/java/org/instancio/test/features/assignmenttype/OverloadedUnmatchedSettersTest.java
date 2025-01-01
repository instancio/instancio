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
import org.instancio.test.support.pojo.assignment.OverloadedUnmatchedSettersPojo;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.tags.RunWith;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.setter;

@RunWith.MethodAssignmentOnly
@FeatureTag({Feature.ASSIGNMENT_TYPE, Feature.ASSIGNMENT_TYPE_METHOD})
@ExtendWith(InstancioExtension.class)
class OverloadedUnmatchedSettersTest {

    @WithSettings
    private final Settings settings = Settings.create()
            .set(Keys.ASSIGNMENT_TYPE, AssignmentType.METHOD)
            .set(Keys.ON_SET_METHOD_UNMATCHED, OnSetMethodUnmatched.INVOKE)
            .set(Keys.ON_SET_METHOD_NOT_FOUND, OnSetMethodNotFound.IGNORE)
            .set(Keys.ON_SET_METHOD_ERROR, OnSetMethodError.FAIL);

    @Test
    void shouldInvokeSetterWithParameterMatchingSelector() {
        final OverloadedUnmatchedSettersPojo result = Instancio.of(OverloadedUnmatchedSettersPojo.class)
                .set(setter(OverloadedUnmatchedSettersPojo.class, "setValue", int.class), -1)
                .set(setter(OverloadedUnmatchedSettersPojo.class, "setValue", Integer.class), -2)
                .set(setter(OverloadedUnmatchedSettersPojo.class, "setValue", String.class), "foo")
                .set(setter(OverloadedUnmatchedSettersPojo.class, "setValue", long.class), -3L)
                .create();

        assertThat(result.getA()).isEqualTo("foo");
        assertThat(result.getB()).isEqualTo(-1);
        assertThat(result.getC()).isEqualTo(-2);
        assertThat(result.getD()).isEqualTo(-3);
    }

    /**
     * Tests using a selector without parameter type and overloaded setters.
     * Such usage is _not_ supported. These tests are for documentation only.
     *
     * <p>Ideally such cases should probably result in an exception.
     */
    @Nested
    class SelectorWithoutParameterTypeTest {

        @WithSettings
        private final Settings settings = Settings.create()
                .set(Keys.ASSIGNMENT_TYPE, AssignmentType.METHOD)
                .set(Keys.ON_SET_METHOD_UNMATCHED, OnSetMethodUnmatched.INVOKE)
                .set(Keys.ON_SET_METHOD_NOT_FOUND, OnSetMethodNotFound.IGNORE)
                .set(Keys.ON_SET_METHOD_ERROR, OnSetMethodError.FAIL);

        @FeatureTag(Feature.UNSUPPORTED)
        @Test
        void selectorDoesNotMatchCorrectMethod_OnSetMethodErrorFail() {
            final InstancioApi<OverloadedUnmatchedSettersPojo> api = Instancio.of(OverloadedUnmatchedSettersPojo.class)
                    .withSettings(Settings.create().set(Keys.ON_SET_METHOD_ERROR, OnSetMethodError.FAIL))
                    .set(setter(OverloadedUnmatchedSettersPojo.class, "setValue"), "foo");

            // Selector matches a setter with wrong parameter type.
            // As a result, method invocation fails and error is propagated due to OnSetMethodError.FAIL
            assertThatThrownBy(api::create)
                    .isExactlyInstanceOf(InstancioApiException.class)
                    .hasMessageContaining("Method invocation failed")
                    .hasMessageContaining("setValue(int)")
                    .hasMessageContaining("Provided argument value ..: \"foo\"");
        }

        @FeatureTag(Feature.UNSUPPORTED)
        @EnumSource(value = OnSetMethodError.class, mode = EnumSource.Mode.INCLUDE, names = {"IGNORE", "ASSIGN_FIELD"})
        @ParameterizedTest
        void selectorDoesNotMatchCorrectMethod(final OnSetMethodError onSetMethodError) {
            final OverloadedUnmatchedSettersPojo result = Instancio.of(OverloadedUnmatchedSettersPojo.class)
                    .withSettings(Settings.create().set(Keys.ON_SET_METHOD_ERROR, onSetMethodError))
                    .set(setter(OverloadedUnmatchedSettersPojo.class, "setValue"), "foo")
                    .create();

            // No error is thrown because OnSetMethodError is not FAIL,
            // but the results are unpredictable
            assertThat(result.getA()).isNotEqualTo("foo"); // value was not set!
            assertThat(result.getB()).isZero();
            assertThat(result.getC()).isPositive();
            assertThat(result.getD()).isPositive();
        }
    }
}
