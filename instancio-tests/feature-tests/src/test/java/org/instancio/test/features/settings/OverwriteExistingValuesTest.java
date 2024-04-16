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
package org.instancio.test.features.settings;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.basic.ClassWithInitializedField;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.allInts;
import static org.instancio.Select.allStrings;

@FeatureTag({Feature.SETTINGS, Feature.OVERWRITE_EXISTING_VALUES})
@ExtendWith(InstancioExtension.class)
class OverwriteExistingValuesTest {

    private static final int OVERWRITE_INT = -1;
    private static final String OVERWRITE_STRING = "foo";

    /**
     * By default, {@link Keys#OVERWRITE_EXISTING_VALUES} should be {@code true}.
     */
    @Test
    void shouldOverwriteInitialisedValuesWithRandomValuesByDefault() {
        final ClassWithInitializedField result = Instancio.create(ClassWithInitializedField.class);

        assertThat(result.getIntValue()).isNotEqualTo(ClassWithInitializedField.DEFAULT_INT_FIELD_VALUE);
        assertThat(result.getStringValue()).isNotEqualTo(ClassWithInitializedField.DEFAULT_STRING_FIELD_VALUE);
    }

    @Test
    void shouldPreserveInitialisedValues() {
        final ClassWithInitializedField result = Instancio.of(ClassWithInitializedField.class)
                .withSettings(Settings.create().set(Keys.OVERWRITE_EXISTING_VALUES, false))
                .create();

        assertThat(result.getIntValue()).isEqualTo(ClassWithInitializedField.DEFAULT_INT_FIELD_VALUE);
        assertThat(result.getStringValue()).isEqualTo(ClassWithInitializedField.DEFAULT_STRING_FIELD_VALUE);
    }

    /**
     * Should be able to overwrite initialised fields using selectors
     * regardless of the {@link Keys#OVERWRITE_EXISTING_VALUES} setting.
     */
    @ValueSource(booleans = {true, false})
    @ParameterizedTest
    void shouldOverwriteInitialisedValuesUsingSelectors(final boolean overwriteExistingValues) {
        final ClassWithInitializedField result = Instancio.of(ClassWithInitializedField.class)
                .withSettings(Settings.create().set(Keys.OVERWRITE_EXISTING_VALUES, overwriteExistingValues))
                .set(allInts(), OVERWRITE_INT)
                .set(allStrings(), OVERWRITE_STRING)
                .create();

        assertThat(result.getIntValue()).isEqualTo(OVERWRITE_INT);
        assertThat(result.getStringValue()).isEqualTo(OVERWRITE_STRING);
    }
}
