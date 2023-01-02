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
package org.instancio.test.features.settings;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.basic.ClassWithInitializedField;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.allInts;
import static org.instancio.Select.allStrings;

@FeatureTag({Feature.SETTINGS, Feature.OVERWRITE_EXISTING_VALUES})
class OverwriteExistingValuesTest {

    @Nested
    class OverwriteExistingValuesEnabled {

        @Test
        void overwriteExistingValuesIsEnabledByDefault() {
            final ClassWithInitializedField result = Instancio.create(ClassWithInitializedField.class);
            assertThat(result.getIntValue()).isNotEqualTo(ClassWithInitializedField.DEFAULT_INT_FIELD_VALUE);
            assertThat(result.getStringValue()).isNotEqualTo(ClassWithInitializedField.DEFAULT_STRING_FIELD_VALUE);
        }

        @Test
        void overwriteInitialisedFieldUsingSelector() {
            final int overwriteInt = -1;
            final String overwriteString = "foo";

            final ClassWithInitializedField result = Instancio.of(ClassWithInitializedField.class)
                    .set(allInts(), overwriteInt)
                    .set(allStrings(), overwriteString)
                    .create();

            assertThat(result.getIntValue()).isEqualTo(overwriteInt);
            assertThat(result.getStringValue()).isEqualTo(overwriteString);
        }
    }

    @Nested
    @ExtendWith(InstancioExtension.class)
    class OverwriteExistingValuesDisabled {

        @WithSettings
        private final Settings settings = Settings.create()
                .set(Keys.OVERWRITE_EXISTING_VALUES, false);

        @Test
        void shouldNotOverwriteExistingValues() {
            final ClassWithInitializedField result = Instancio.create(ClassWithInitializedField.class);

            assertThat(result.getIntValue()).isEqualTo(ClassWithInitializedField.DEFAULT_INT_FIELD_VALUE);
            assertThat(result.getStringValue()).isEqualTo(ClassWithInitializedField.DEFAULT_STRING_FIELD_VALUE);
        }

        @Test
        void shouldNotOverwriteExistingValuesUsingSelectors() {
            final ClassWithInitializedField result = Instancio.of(ClassWithInitializedField.class)
                    .set(allInts(), -1)
                    .set(allStrings(), "foo")
                    .create();

            assertThat(result.getIntValue()).isEqualTo(ClassWithInitializedField.DEFAULT_INT_FIELD_VALUE);
            assertThat(result.getStringValue()).isEqualTo(ClassWithInitializedField.DEFAULT_STRING_FIELD_VALUE);
        }
    }
}
