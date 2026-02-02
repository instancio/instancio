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
package org.instancio.test.beanvalidation.spi.annotationprocessor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.annotations.StringSuffix;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(InstancioExtension.class)
class AnnotationProcessorWithBeanValidationDisabledBVTest {

    private static final int STRING_LENGTH_SETTING = 5;
    private static final String SUFFIX = "foo";

    @WithSettings
    private final Settings settings = Settings.create()
            .set(Keys.STRING_ALLOW_EMPTY, false)
            .set(Keys.STRING_NULLABLE, false)
            .set(Keys.STRING_MIN_LENGTH, STRING_LENGTH_SETTING)
            .set(Keys.STRING_MAX_LENGTH, STRING_LENGTH_SETTING)
            .set(Keys.BEAN_VALIDATION_ENABLED, false); // BV disabled!

    /**
     * If Bean Validation is disabled, BV annotations should be ignored,
     * but annotation processor should still handle custom annotations.
     */
    @RepeatedTest(Constants.SAMPLE_SIZE_D)
    void beanValidationDisabled() {
        class Pojo {
            @StringSuffix(SUFFIX)
            @NotNull
            @Size(max = 1) // ignored since BV is disabled
            String value;
        }

        final Pojo result = Instancio.create(Pojo.class);

        assertThat(result.value)
                .hasSize(STRING_LENGTH_SETTING + SUFFIX.length())
                .endsWith(SUFFIX);
    }
}
