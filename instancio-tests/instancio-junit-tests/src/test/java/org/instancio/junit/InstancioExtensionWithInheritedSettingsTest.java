/*
 * Copyright 2022-2026 the original author or authors.
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
package org.instancio.junit;

import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies that {@code @WithSettings} declared by a superclass or an interface
 * is inherited by subclasses, and merged with the subclass settings.
 */
class InstancioExtensionWithInheritedSettingsTest {

    @ExtendWith(InstancioExtension.class)
    abstract static class BaseTest {

        @WithSettings
        private final Settings settings = Settings.create()
                .set(Keys.INTEGER_MIN, -1)
                .set(Keys.INTEGER_MAX, -1)
                .set(Keys.STRING_MIN_LENGTH, 5)
                .set(Keys.STRING_MAX_LENGTH, 5);
    }

    @Nested
    class InheritSettingsTest extends BaseTest {

        private @Given int intField;
        private @Given String stringField;

        @Test
        void shouldInheritSettingsFromSuperclass(@Given final int intParam, @Given final String stringParam) {
            assertThat(intParam).isEqualTo(-1);
            assertThat(intField).isEqualTo(-1);
            assertThat(stringParam).hasSize(5);
            assertThat(stringField).hasSize(5);
        }
    }

    @Nested
    class MergeSettingsTest extends BaseTest {

        @WithSettings
        private final Settings settings = Settings.create()
                .set(Keys.STRING_MIN_LENGTH, 15)
                .set(Keys.STRING_MAX_LENGTH, 15);

        private @Given int intField;
        private @Given String stringField;

        @Test
        void subclassSettingsShouldBeOverlaidOnSuperclassSettings(
                @Given final int intParam, @Given final String stringParam) {

            // not set by the subclass: inherited from the superclass
            assertThat(intParam).isEqualTo(-1);
            assertThat(intField).isEqualTo(-1);
            // set by both: the subclass wins
            assertThat(stringParam).hasSize(15);
            assertThat(stringField).hasSize(15);
        }
    }

    @ExtendWith(InstancioExtension.class)
    interface SettingsMixin {

        @WithSettings
        Settings settings = Settings.create()
                .set(Keys.LONG_MIN, -7L)
                .set(Keys.LONG_MAX, -7L);
    }

    @Nested
    class InheritSettingsFromInterfaceTest implements SettingsMixin {

        private @Given long longField;

        @Test
        void shouldInheritSettingsFromInterface(@Given final long longParam) {
            assertThat(longParam).isEqualTo(-7L);
            assertThat(longField).isEqualTo(-7L);
        }
    }

    @Nested
    class InheritSettingsFromSuperclassAndInterfaceTest extends BaseTest implements SettingsMixin {

        @Test
        void shouldMergeSuperclassAndInterfaceSettings(
                @Given final int intParam, @Given final long longParam, @Given final String stringParam) {

            assertThat(intParam).isEqualTo(-1);
            assertThat(longParam).isEqualTo(-7L);
            assertThat(stringParam).hasSize(5);
        }
    }
}
