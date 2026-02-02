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
import org.instancio.test.pojo.beanvalidation.EmailBV;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.test.support.util.Constants.SAMPLE_SIZE_DD;

@FeatureTag(Feature.BEAN_VALIDATION)
@ExtendWith(InstancioExtension.class)
class EmailBVTest {

    private static final String DEFAULT_EMAIL_PATTERN = "\\w+@\\w+\\.(com|edu|org|net)";
    private static final int EMAIL_ADDRESS_MIN = 7;
    private static final int EMAIL_ADDRESS_MAX = 24;

    @RepeatedTest(SAMPLE_SIZE_DD)
    void onString() {
        final EmailBV.OnString result = Instancio.create(EmailBV.OnString.class);
        assertThat(result.getEmail())
                .hasSizeBetween(EMAIL_ADDRESS_MIN, EMAIL_ADDRESS_MAX)
                .matches(DEFAULT_EMAIL_PATTERN);
    }

    @RepeatedTest(SAMPLE_SIZE_DD)
    void onCharSequence() {
        final EmailBV.OnCharSequence result = Instancio.create(EmailBV.OnCharSequence.class);
        assertThat(result.getEmail())
                .hasSizeBetween(EMAIL_ADDRESS_MIN, EMAIL_ADDRESS_MAX)
                .matches(DEFAULT_EMAIL_PATTERN);
    }
}
