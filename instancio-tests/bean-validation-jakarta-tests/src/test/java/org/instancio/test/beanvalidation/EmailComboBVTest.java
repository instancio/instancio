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
package org.instancio.test.beanvalidation;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.pojo.beanvalidation.EmailComboBV;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.test.support.util.Constants.SAMPLE_SIZE_DD;

@FeatureTag(Feature.BEAN_VALIDATION)
@ExtendWith(InstancioExtension.class)
class EmailComboBVTest {

    private static final String EMAIL_PATTERN = "\\w+@\\w+\\.\\p{Lower}{3}";

    @RepeatedTest(SAMPLE_SIZE_DD)
    void emailWithSize() {
        final EmailComboBV.EmailWithSize result = Instancio.create(EmailComboBV.EmailWithSize.class);

        assertThat(result.getEmailThenSize())
                .matches(EMAIL_PATTERN)
                .hasSizeBetween(7, 12);

        assertThat(result.getSizeThenEmail())
                .matches(EMAIL_PATTERN)
                .hasSize(10);
    }

    @RepeatedTest(SAMPLE_SIZE_DD)
    void notNullEmailWithSize() {
        final EmailComboBV.NotNullEmailWithSize result = Instancio.of(EmailComboBV.NotNullEmailWithSize.class)
                .withSettings(Settings.create().set(Keys.STRING_NULLABLE, true))
                .create();

        assertThat(result.getValue())
                .isNotNull()
                .matches(EMAIL_PATTERN)
                .hasSizeBetween(15, 20);
    }
}
