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
package org.instancio.test.features.assignmenttype.method;

import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.exception.InstancioApiException;
import org.instancio.exception.UnusedSelectorException;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.AssignmentType;
import org.instancio.settings.Keys;
import org.instancio.settings.OnSetMethodError;
import org.instancio.settings.OnSetMethodNotFound;
import org.instancio.settings.OnSetMethodUnmatched;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.dynamic.DynPojoBase;
import org.instancio.test.support.pojo.dynamic.MixedPojo;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.tags.RunWith;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.field;
import static org.instancio.Select.setter;

@RunWith.MethodAssignmentOnly
@FeatureTag({Feature.ASSIGNMENT_TYPE, Feature.ASSIGNMENT_TYPE_METHOD})
@ExtendWith(InstancioExtension.class)
class MethodAssignmentMixedPojoTest {

    private static final Settings BASE_SETTINGS = Settings.create()
            .set(Keys.ASSIGNMENT_TYPE, AssignmentType.METHOD)
            .set(Keys.ON_SET_METHOD_ERROR, OnSetMethodError.FAIL)
            .set(Keys.ON_SET_METHOD_UNMATCHED, OnSetMethodUnmatched.INVOKE);

    /**
     * Field without setter should _not_ be populated.
     */
    @Nested
    class OnSetMethodNotFoundIgnoreTest {
        @WithSettings
        private final Settings settings = Settings.from(BASE_SETTINGS)
                .set(Keys.ON_SET_METHOD_NOT_FOUND, OnSetMethodNotFound.IGNORE);

        @Test
        void create() {
            final MixedPojo result = Instancio.create(MixedPojo.class);

            assertThat(result.getRegularField()).isNotBlank();
            assertThat(result.getRegularFieldWithNoSetter()).isNull();
            assertThat(result.getFoo()).isNotBlank();
            assertThat(result.getDynamicField()).isNotBlank();
            assertThat(result.getData())
                    .as("Expected size 1 because there's 1 dynamic field")
                    .hasSize(1);
        }

        @Test
        void withFieldSelector_getRegularField() {
            final MixedPojo result = Instancio.of(MixedPojo.class)
                    .set(field(MixedPojo::getRegularField), "foo")
                    .create();

            assertThat(result.getRegularField()).isEqualTo("foo");
        }

        @Test
        void withFieldSelector_getRegularFieldWithNoSetter() {
            final InstancioApi<MixedPojo> api = Instancio.of(MixedPojo.class)
                    .set(field(MixedPojo::getRegularFieldWithNoSetter), "foo")
                    .set(field("regularFieldWithNonMatchingSetter"), "bar");

            // Since OnSetMethodNotFound is IGNORE and there's no setter, the field selector is unused
            assertThatThrownBy(api::create)
                    .isExactlyInstanceOf(UnusedSelectorException.class)
                    .hasMessageContaining("regularFieldWithNoSetter")
                    .hasMessageContaining("regularFieldWithNonMatchingSetter");
        }
    }

    /**
     * Field without setter _should_ be populated.
     */
    @Nested
    class OnSetMethodNotFoundAssignFieldTest {
        @WithSettings
        private final Settings settings = Settings.from(BASE_SETTINGS)
                .set(Keys.ON_SET_METHOD_NOT_FOUND, OnSetMethodNotFound.ASSIGN_FIELD);

        @Test
        void create() {
            final MixedPojo result = Instancio.create(MixedPojo.class);

            assertThat(result.getRegularFieldWithNoSetter())
                    .as("Expected value to be assigned via field since OnSetMethodNotFound is ASSIGN_FIELD")
                    .isNotBlank();

            assertThat(result.getDynamicField()).isNotBlank();

            assertThat(result.getData())
                    .as("randomly generated map data + one dynamic field")
                    .hasSizeGreaterThan(Constants.MIN_SIZE);
        }

        @Test
        void ignoreDataField() {
            final MixedPojo result = Instancio.of(MixedPojo.class)
                    .ignore(field(DynPojoBase::getData))
                    .create();

            assertThat(result.getData())
                    .as("one dynamic field; no random data since field is ignore()'ed")
                    .hasSize(1);
        }

        @Test
        void withFieldSelector_getRegularField() {
            final MixedPojo result = Instancio.of(MixedPojo.class)
                    .set(field(MixedPojo::getRegularField), "foo")
                    .create();

            assertThat(result.getRegularField()).isEqualTo("foo");
        }

        @Test
        void withFieldSelector_getRegularFieldWithNoSetter() {
            final MixedPojo result = Instancio.of(MixedPojo.class)
                    .set(field(MixedPojo::getRegularFieldWithNoSetter), "foo")
                    .create();

            assertThat(result.getRegularFieldWithNoSetter()).isEqualTo("foo");
        }

        @RepeatedTest(10)
        void withFieldSelector_getRegularFieldWithNonMatchingSetter() {
            final MixedPojo result = Instancio.of(MixedPojo.class)
                    .set(field("regularFieldWithNonMatchingSetter"), "foo")
                    .create();

            // The value gets set twice since "regularFieldWithNonMatchingSetter"
            // and "setFoo()" belong to different nodes. As a result, the value is assigned
            //
            // - via 'regularFieldWithNonMatchingSetter' field (value is set to "foo")
            // - and then overwritten via 'setFoo()' to a random value
            assertThat(result.getFoo()).isNotEqualTo("foo");
            assertThat(result)
                    .hasFieldOrProperty("regularFieldWithNonMatchingSetter")
                    .isNotEqualTo("foo");
        }

        @Test
        void withFieldSelector_ignoringSetterMethod_getRegularFieldWithNonMatchingSetter() {
            // If the setter is ignored, the value assigned
            // via "regularFieldWithNonMatchingSetter" selector is retained
            final MixedPojo result = Instancio.of(MixedPojo.class)
                    .ignore(setter(MixedPojo::setFoo))
                    .set(field("regularFieldWithNonMatchingSetter"), "foo")
                    .create();

            assertThat(result.getFoo()).isEqualTo("foo");
            assertThat(result).hasFieldOrPropertyWithValue("regularFieldWithNonMatchingSetter", "foo");
        }

        @Test
        void withMethodSelector_getRegularFieldWithNonMatchingSetter() {
            final MixedPojo result = Instancio.of(MixedPojo.class)
                    // setFoo() sets the regularFieldWithNonMatchingSetter field
                    .set(setter(MixedPojo::setFoo), "foo")
                    .create();

            assertThat(result.getFoo()).isEqualTo("foo");
            assertThat(result).hasFieldOrPropertyWithValue("regularFieldWithNonMatchingSetter", "foo");
        }
    }

    @Nested
    class OnSetMethodNotFoundFailTest {
        @WithSettings
        private final Settings settings = Settings.from(BASE_SETTINGS)
                .set(Keys.ON_SET_METHOD_NOT_FOUND, OnSetMethodNotFound.FAIL);

        @Test
        void create() {
            assertThatThrownBy(() -> Instancio.create(MixedPojo.class))
                    .isExactlyInstanceOf(InstancioApiException.class)
                    .hasMessageContaining("regularFieldWithNoSetter"); // offending field

        }
    }
}