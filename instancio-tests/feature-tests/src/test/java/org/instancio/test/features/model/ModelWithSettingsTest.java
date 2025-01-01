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
package org.instancio.test.features.model;

import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.pojo.basic.SupportedNumericTypes;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag({Feature.MODEL, Feature.SETTINGS})
@ExtendWith(InstancioExtension.class)
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

    @Test
    void verifyModelSettingsMerge() {
        final int stringLength = 10;
        final Settings settings1 = Settings.create()
                .set(Keys.STRING_NULLABLE, true);

        final Settings settings2 = Settings.create()
                .set(Keys.STRING_ALLOW_EMPTY, true);

        final Settings settings3 = Settings.create()
                .set(Keys.STRING_MIN_LENGTH, stringLength)
                .set(Keys.STRING_MAX_LENGTH, stringLength);

        final Model<StringHolder> model = Instancio.of(StringHolder.class)
                .withSettings(settings1)
                .withSettings(settings2)
                .withSettings(settings3)
                .toModel();

        final Set<String> results = Instancio.of(model)
                .withSettings(Settings.create().set(Keys.STRING_MAX_LENGTH, stringLength + 1))
                .stream()
                .limit(500)
                .map(StringHolder::getValue)
                .collect(Collectors.toSet());

        assertThat(results)
                .contains("", null)
                .filteredOn(Objects::nonNull)
                .anyMatch(s -> s.length() == stringLength)
                .anyMatch(s -> s.length() == stringLength + 1);
    }
}
