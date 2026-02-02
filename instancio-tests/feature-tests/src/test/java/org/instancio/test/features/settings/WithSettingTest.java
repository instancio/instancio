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
package org.instancio.test.features.settings;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.misc.StringAndPrimitiveFields;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.test.support.asserts.ReflectionAssert.assertThatObject;

@FeatureTag({Feature.SETTINGS, Feature.WITH_SETTINGS_ANNOTATION})
@ExtendWith(InstancioExtension.class)
class WithSettingTest {

    @WithSettings
    private static final Settings settings = Settings.create()
            .set(Keys.STRING_MIN_LENGTH, 9)
            .set(Keys.STRING_MIN_LENGTH, 9);

    @Test
    void withSetting() {
        final StringAndPrimitiveFields result = Instancio.of(StringAndPrimitiveFields.class)
                .withSetting(Keys.STRING_MIN_LENGTH, 2)
                .withSetting(Keys.STRING_MAX_LENGTH, 2)
                .withSetting(Keys.INTEGER_MIN, -1)
                .withSetting(Keys.INTEGER_MAX, -1)
                .create();

        assertThat(result.getOne()).hasSize(2);
        assertThat(result.getTwo()).hasSize(2);
        assertThatObject(result).hasAllFieldsOfTypeEqualTo(int.class, -1);
    }
}
