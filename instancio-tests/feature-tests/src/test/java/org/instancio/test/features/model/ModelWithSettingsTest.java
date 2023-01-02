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
package org.instancio.test.features.model;

import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.basic.SupportedNumericTypes;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag({Feature.MODEL, Feature.SETTINGS})
class ModelWithSettingsTest {
    private static final int INT_MIN = 1234;
    private static final int INT_MAX = 1235;

    @Test
    void verifyModelRetainsSettings() {
        final Model<SupportedNumericTypes> model = Instancio.of(SupportedNumericTypes.class)
                .withSettings(Settings.create()
                        .set(Keys.INTEGER_MIN, INT_MIN)
                        .set(Keys.INTEGER_MAX, INT_MAX))
                .toModel();

        final SupportedNumericTypes result = Instancio.create(model);
        assertThat(result.getPrimitiveInt()).isBetween(INT_MIN, INT_MAX);
    }

    @Test
    void verifyModelSettingsOverride() {
        final long expectedLong = -123L;
        final Model<SupportedNumericTypes> model = Instancio.of(SupportedNumericTypes.class)
                .withSettings(Settings.create()
                        .set(Keys.LONG_MIN, expectedLong)
                        .set(Keys.LONG_MAX, expectedLong)
                        .set(Keys.INTEGER_MIN, INT_MIN)
                        .set(Keys.INTEGER_MAX, INT_MAX))
                .toModel();

        final int newIntMin = 5678;
        final int newIntMax = 5679;
        final SupportedNumericTypes result = Instancio.of(model)
                .withSettings(Settings.create()
                        .set(Keys.INTEGER_MIN, newIntMin)
                        .set(Keys.INTEGER_MAX, newIntMax))
                .create();

        assertThat(result.getLongWrapper()).isEqualTo(expectedLong);
        assertThat(result.getPrimitiveLong()).isEqualTo(expectedLong);
        assertThat(result.getPrimitiveInt()).isBetween(newIntMin, newIntMax);
    }
}
