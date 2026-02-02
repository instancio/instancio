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
package org.instancio.test.beanvalidation;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.pojo.beanvalidation.StringDigitsBV;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.test.support.util.Constants.SAMPLE_SIZE_D;

@FeatureTag(Feature.BEAN_VALIDATION)
@ExtendWith(InstancioExtension.class)
class BeanValidationDisabledBVTest {

    @WithSettings
    private final Settings settings = Settings.create()
            .set(Keys.BEAN_VALIDATION_ENABLED, false)
            .set(Keys.STRING_MIN_LENGTH, 1)
            .set(Keys.STRING_ALLOW_EMPTY, false)
            .set(Keys.STRING_NULLABLE, false);

    @RepeatedTest(SAMPLE_SIZE_D)
    void shouldIgnoreBeanValidationAnnotationsWhenDisabledViaSettings() {
        final StringDigitsBV.OnString result = Instancio.create(StringDigitsBV.OnString.class);

        assertThat(result.getS1()).isAlphabetic();
        assertThat(result.getS2()).isAlphabetic();
    }
}
