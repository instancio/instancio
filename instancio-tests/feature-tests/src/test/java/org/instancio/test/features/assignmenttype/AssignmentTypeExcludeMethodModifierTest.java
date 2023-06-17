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
import org.instancio.internal.util.SystemProperties;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.AssignmentType;
import org.instancio.settings.Keys;
import org.instancio.settings.MethodModifier;
import org.instancio.settings.OnSetMethodError;
import org.instancio.settings.OnSetMethodNotFound;
import org.instancio.settings.Settings;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag(Feature.ASSIGNMENT_TYPE)
@ExtendWith(InstancioExtension.class)
@DisabledIfSystemProperty(named = SystemProperties.ASSIGNMENT_TYPE, matches = "FIELD")
class AssignmentTypeExcludeMethodModifierTest {

    @WithSettings
    private final Settings settings = Settings.create()
            .set(Keys.ASSIGNMENT_TYPE, AssignmentType.METHOD)
            .set(Keys.ON_SET_METHOD_NOT_FOUND, OnSetMethodNotFound.FAIL)
            .set(Keys.ON_SET_METHOD_ERROR, OnSetMethodError.FAIL);

    @Test
    @DisplayName("Default behaviour: should invoke all setters regardless of modifiers")
    void shouldInvokeAllSettersByDefault() {
        final Pojo result = Instancio.create(Pojo.class);

        assertThat(result.foo).isNotNull();
        assertThat(result.bar).isNotNull();
        assertThat(result.baz).isNotNull();
    }

    @Test
    @DisplayName("Should exclude private and package-private setters")
    void excludePrivateAndPackagePrivateSetters() {
        final int exclusions = MethodModifier.PRIVATE | MethodModifier.PACKAGE_PRIVATE;

        final Pojo result = Instancio.of(Pojo.class)
                .withSettings(Settings.create()
                        .set(Keys.SETTER_EXCLUDE_MODIFIER, exclusions))
                .create();

        assertThat(result.foo).isNotNull();
        assertThat(result.bar).isNull();
        assertThat(result.baz).isNull();
    }

    private static class Pojo {
        private String foo;
        private String bar;
        private String baz;

        public void setFoo(final String foo) {
            this.foo = foo;
        }

        // used via reflection
        @SuppressWarnings("unused")
        private void setBar(final String bar) {
            this.bar = bar;
        }

        // used via reflection
        @SuppressWarnings("unused")
        void setBaz(final String baz) {
            this.baz = baz;
        }
    }
}
