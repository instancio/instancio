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
import org.instancio.junit.Given;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.pojo.beanvalidation.StringLengthBV;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.util.HibernateValidatorUtil;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.test.support.util.Constants.SAMPLE_SIZE_DD;

@FeatureTag(Feature.BEAN_VALIDATION)
@ExtendWith(InstancioExtension.class)
class StringLengthBVTest {

    @WithSettings
    private final Settings settings = Settings.create()
            .set(Keys.STRING_MAX_LENGTH, 3);

    @RepeatedTest(SAMPLE_SIZE_DD)
    void withMinSize(@Given StringLengthBV.WithMinSize result) {
        HibernateValidatorUtil.assertValid(result);
    }

    @RepeatedTest(SAMPLE_SIZE_DD)
    void withMinSizeZero(@Given StringLengthBV.WithMinSizeZero result) {
        HibernateValidatorUtil.assertValid(result);
        assertThat(result.getValue()).hasSizeBetween(0, settings.get(Keys.STRING_MAX_LENGTH));
    }

    @RepeatedTest(SAMPLE_SIZE_DD)
    void withMaxSize(@Given StringLengthBV.WithMaxSize result) {
        HibernateValidatorUtil.assertValid(result);
    }

    @RepeatedTest(SAMPLE_SIZE_DD)
    void withMaxSizeZero(@Given StringLengthBV.WithMaxSizeZero result) {
        HibernateValidatorUtil.assertValid(result);
    }

    @RepeatedTest(SAMPLE_SIZE_DD)
    void withMinMaxSize(@Given StringLengthBV.WithMinMaxSize result) {
        HibernateValidatorUtil.assertValid(result);
    }

    @RepeatedTest(SAMPLE_SIZE_DD)
    void withMinMaxEqual(@Given StringLengthBV.WithMinMaxEqual result) {
        HibernateValidatorUtil.assertValid(result);
    }

    /**
     * This test can't be verified using Hibernate validator because
     * string field prefix is being appended, which results in a longer
     * string than allowed by the constraint.
     */
    @Test
    void withStringFieldPrefix() {
        final StringLengthBV.WithMinMaxEqual result = Instancio.of(StringLengthBV.WithMinMaxEqual.class)
                .withSettings(Settings.create().set(Keys.STRING_FIELD_PREFIX_ENABLED, true))
                .create();

        final String expectedPrefix = "value_";
        assertThat(result.getValue())
                .hasSize(5 + expectedPrefix.length())
                .startsWith(expectedPrefix);
    }

    @Test
    void annotationShouldNotAffectFieldsThatAreNotAnnotated(@Given StringLengthBV.ThreeFieldsOneMin result) {
        HibernateValidatorUtil.assertValid(result);

        final int maxLengthFromProperties = 1000;

        assertThat(result.getS1()).hasSizeLessThanOrEqualTo(maxLengthFromProperties);
        assertThat(result.getS2()).hasSizeGreaterThanOrEqualTo(20);
        assertThat(result.getS3()).hasSizeLessThanOrEqualTo(maxLengthFromProperties);
    }
}
