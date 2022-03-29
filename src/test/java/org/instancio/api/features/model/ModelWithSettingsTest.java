/*
 *  Copyright 2022 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.instancio.api.features.model;

import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.pojo.basic.SupportedNumericTypes;
import org.instancio.settings.Setting;
import org.instancio.settings.Settings;
import org.instancio.testsupport.tags.SettingsTag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ModelWithSettingsTest {
    private static final int INT_MIN = 1234;
    private static final int INT_MAX = 1235;

    @Test
    @SettingsTag
    void verifyModelRetainsSettings() {
        final Model<SupportedNumericTypes> model = Instancio.of(SupportedNumericTypes.class)
                .withSettings(Settings.create()
                        .set(Setting.INTEGER_MIN, INT_MIN)
                        .set(Setting.INTEGER_MAX, INT_MAX))
                .toModel();

        final SupportedNumericTypes result = Instancio.create(model);
        assertThat(result.getPrimitiveInt()).isBetween(INT_MIN, INT_MAX);
    }

    @Test
    @SettingsTag
    void verifyModelSettingsOverride() {
        final Model<SupportedNumericTypes> model = Instancio.of(SupportedNumericTypes.class)
                .withSettings(Settings.create()
                        .set(Setting.INTEGER_MIN, INT_MIN)
                        .set(Setting.INTEGER_MAX, INT_MAX))
                .toModel();

        final int newIntMin = 5678;
        final int newIntMax = 5679;
        final SupportedNumericTypes result = Instancio.of(model)
                .withSettings(Settings.create()
                        .set(Setting.INTEGER_MIN, newIntMin)
                        .set(Setting.INTEGER_MAX, newIntMax))
                .create();

        assertThat(result.getPrimitiveInt()).isBetween(newIntMin, newIntMax);
    }
}
