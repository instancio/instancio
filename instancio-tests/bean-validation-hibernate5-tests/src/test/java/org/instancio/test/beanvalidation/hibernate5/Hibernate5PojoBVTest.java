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
package org.instancio.test.beanvalidation.hibernate5;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.validator.constraints.CreditCardNumber;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.URL;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.beanvalidation.hibernate5.util.Hibernate5ValidatorUtil;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@FeatureTag(Feature.BEAN_VALIDATION)
@ExtendWith(InstancioExtension.class)
class Hibernate5PojoBVTest {

    @Test
    void hibernate5AndJavax() {
        final Hibernate5PojoBV result = Instancio.of(Hibernate5PojoBV.class)
                .withSettings(Settings.create().set(Keys.BEAN_VALIDATION_ENABLED, true))
                .create();

        Hibernate5ValidatorUtil.assertValid(result);
    }

    private static class Hibernate5PojoBV {

        @NotNull
        @Length(min = 1, max = 2)
        private String length;

        @Digits(integer = 5, fraction = 2)
        private String digits;

        @Range(min = 1, max = 10)
        private int range;

        @URL
        @NotBlank
        private String url;

        @CreditCardNumber
        private String creditCard;

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
        }
    }
}
