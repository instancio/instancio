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
package org.instancio.test.features.assignmenttype.method;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.AssignmentType;
import org.instancio.settings.Keys;
import org.instancio.settings.MethodModifier;
import org.instancio.settings.OnSetMethodNotFound;
import org.instancio.settings.OnSetMethodUnmatched;
import org.instancio.settings.SetterStyle;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.assignment.UnmatchedSettersPojo;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.tags.RunWith;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for unmatched setters with settings:
 *
 * <ul>
 *   <li>{@link Keys#SETTER_STYLE}</li>
 *   <li>{@link Keys#SETTER_EXCLUDE_MODIFIER}</li>
 * </ul>
 */
@RunWith.MethodAssignmentOnly
@FeatureTag({Feature.ASSIGNMENT_TYPE, Feature.ASSIGNMENT_TYPE_METHOD})
@ExtendWith(InstancioExtension.class)
class MethodAssignmentUnmatchedSettersTest {

    /**
     * {@link Keys#ON_SET_METHOD_UNMATCHED} should be false by default.
     */
    @Test
    void ignoreUnmatchedSettersDefault() {
        final UnmatchedSettersPojo result = Instancio.of(UnmatchedSettersPojo.class)
                .withSettings(Settings.create()
                        .set(Keys.ASSIGNMENT_TYPE, AssignmentType.METHOD)
                        .set(Keys.ON_SET_METHOD_NOT_FOUND, OnSetMethodNotFound.IGNORE))
                .create();

        assertThat(result.getInvokedSetters()).isEmpty();
        assertThat(result.getValues()).isEmpty();
    }

    @Nested
    @DisplayName("Setters methods with 'set' prefix")
    class SetterStyleSetTest {

        @WithSettings
        private final Settings settings = Settings.create()
                .set(Keys.ASSIGNMENT_TYPE, AssignmentType.METHOD)
                .set(Keys.ON_SET_METHOD_UNMATCHED, OnSetMethodUnmatched.INVOKE)
                .set(Keys.ON_SET_METHOD_NOT_FOUND, OnSetMethodNotFound.IGNORE);

        @Test
        void allSettersAreInvokedInDeclarationOrder() {
            final UnmatchedSettersPojo result = Instancio.create(UnmatchedSettersPojo.class);

            assertThat(result.getInvokedSetters()).containsExactlyInAnyOrder(
                    "setPublicFoo",
                    "setPackagePrivateFoo",
                    "setProtectedFoo",
                    "setPrivateFoo");

            assertThat(result.getValues()).hasSize(4);
        }

        @Test
        void onlyPublicSetterIsInvoked() {
            final int exclusions = MethodModifier.PACKAGE_PRIVATE
                    | MethodModifier.PROTECTED
                    | MethodModifier.PRIVATE;

            final UnmatchedSettersPojo result = Instancio.of(UnmatchedSettersPojo.class)
                    .withSettings(Settings.create().set(Keys.SETTER_EXCLUDE_MODIFIER, exclusions))
                    .create();

            assertThat(result.getInvokedSetters()).containsOnly("setPublicFoo");
            assertThat(result.getValues()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("Setters methods with 'with' prefix")
    class SetterStyleWithTest {

        @WithSettings
        private final Settings settings = Settings.create()
                .set(Keys.ASSIGNMENT_TYPE, AssignmentType.METHOD)
                .set(Keys.ON_SET_METHOD_UNMATCHED, OnSetMethodUnmatched.INVOKE)
                .set(Keys.ON_SET_METHOD_NOT_FOUND, OnSetMethodNotFound.IGNORE)
                .set(Keys.SETTER_STYLE, SetterStyle.WITH);

        @Test
        void allSettersAreInvokedInDeclarationOrder() {
            final UnmatchedSettersPojo result = Instancio.create(UnmatchedSettersPojo.class);

            assertThat(result.getInvokedSetters()).containsExactlyInAnyOrder(
                    "withPublicFoo",
                    "withPackagePrivateFoo",
                    "withProtectedFoo",
                    "withPrivateFoo");

            assertThat(result.getValues()).hasSize(4);
        }

        @Test
        void onlyPublicSetterIsInvoked() {
            final int exclusions = MethodModifier.PACKAGE_PRIVATE
                    | MethodModifier.PROTECTED
                    | MethodModifier.PRIVATE;

            final UnmatchedSettersPojo result = Instancio.of(UnmatchedSettersPojo.class)
                    .withSettings(Settings.create().set(Keys.SETTER_EXCLUDE_MODIFIER, exclusions))
                    .create();

            assertThat(result.getInvokedSetters()).containsOnly("withPublicFoo");
            assertThat(result.getValues()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("Setters methods without a prefix")
    class SetterStyleWithoutPrefixTest {

        @Test
        void noSettersAreInvoked() {
            final UnmatchedSettersPojo result = Instancio.of(UnmatchedSettersPojo.class)
                    .withSettings(Settings.create()
                            .set(Keys.ASSIGNMENT_TYPE, AssignmentType.METHOD)
                            .set(Keys.ON_SET_METHOD_NOT_FOUND, OnSetMethodNotFound.IGNORE)
                            .set(Keys.SETTER_STYLE, SetterStyle.PROPERTY))
                    .create();

            assertThat(result.getInvokedSetters()).isEmpty();
            assertThat(result.getValues()).isEmpty();
        }
    }

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class StaticSetterTest {

        @WithSettings
        private final Settings settings = Settings.create()
                .set(Keys.ASSIGNMENT_TYPE, AssignmentType.METHOD)
                .set(Keys.ON_SET_METHOD_UNMATCHED, OnSetMethodUnmatched.INVOKE)
                .set(Keys.ON_SET_METHOD_NOT_FOUND, OnSetMethodNotFound.IGNORE);

        @Test
        @Order(1)
        void shouldIgnoreStaticSetter() {
            Instancio.of(StaticSetter.class)
                    .withSettings(Settings.create()
                            .set(Keys.SETTER_EXCLUDE_MODIFIER, MethodModifier.STATIC))
                    .create();

            assertThat(StaticSetter.value).isNull();
        }

        @Test
        @Order(2)
        void shouldSetValueViaStaticSetter() {
            Instancio.create(StaticSetter.class);

            assertThat(StaticSetter.value).isNotBlank();
        }
    }

    private static class StaticSetter {
        static String value;

        static void setFoo(String s) {
            value = s;
        }
    }
}
