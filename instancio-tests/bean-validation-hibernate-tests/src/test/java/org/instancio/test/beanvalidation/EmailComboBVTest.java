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
package org.instancio.test.beanvalidation;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.pojo.beanvalidation.EmailComboBV;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.util.HibernateValidatorUtil;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.instancio.test.support.util.Constants.SAMPLE_SIZE_DD;

@FeatureTag(Feature.BEAN_VALIDATION)
@ExtendWith(InstancioExtension.class)
class EmailComboBVTest {

    @RepeatedTest(SAMPLE_SIZE_DD)
    void emailWithLength() {
        final EmailComboBV.EmailWithLength result = Instancio.create(EmailComboBV.EmailWithLength.class);

        HibernateValidatorUtil.assertValid(result);
    }

    @RepeatedTest(SAMPLE_SIZE_DD)
    void notNullEmailWithLength() {
        final EmailComboBV.NotNullEmailWithLength result = Instancio.of(EmailComboBV.NotNullEmailWithLength.class)
                .withSettings(Settings.create().set(Keys.STRING_NULLABLE, true))
                .create();

        HibernateValidatorUtil.assertValid(result);
    }
}
