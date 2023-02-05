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
import org.instancio.test.pojo.beanvalidation.StringLengthBV;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.test.support.util.Constants.SAMPLE_SIZE_DD;

@FeatureTag(Feature.BEAN_VALIDATION)
@ExtendWith(InstancioExtension.class)
class StringLengthBVTest {

    @RepeatedTest(SAMPLE_SIZE_DD)
    void withMinSize() {
        final StringLengthBV.WithMinSize result = Instancio.create(StringLengthBV.WithMinSize.class);
        assertThat(result.getValue()).hasSizeGreaterThanOrEqualTo(8);
    }

    @RepeatedTest(SAMPLE_SIZE_DD)
    void withMinSizeZeo() {
        final StringLengthBV.WithMinSizeZero result = Instancio.create(StringLengthBV.WithMinSizeZero.class);
        assertThat(result.getValue()).hasSizeBetween(0, Keys.STRING_MAX_LENGTH.defaultValue());
    }

    @RepeatedTest(SAMPLE_SIZE_DD)
    void withMaxSize() {
        final StringLengthBV.WithMaxSize result = Instancio.create(StringLengthBV.WithMaxSize.class);
        assertThat(result.getValue()).hasSizeLessThanOrEqualTo(1);

    }

    @RepeatedTest(SAMPLE_SIZE_DD)
    void withMaxSizeZero() {
        final StringLengthBV.WithMaxSizeZero result = Instancio.create(StringLengthBV.WithMaxSizeZero.class);
        assertThat(result.getValue()).isEmpty();
    }

    @RepeatedTest(SAMPLE_SIZE_DD)
    void withMinMaxSize() {
        final StringLengthBV.WithMinMaxSize result = Instancio.create(StringLengthBV.WithMinMaxSize.class);
        assertThat(result.getValue()).hasSizeBetween(19, 20);
    }

    @RepeatedTest(SAMPLE_SIZE_DD)
    void withMinMaxEqual() {
        final StringLengthBV.WithMinMaxEqual result = Instancio.create(StringLengthBV.WithMinMaxEqual.class);
        assertThat(result.getValue()).hasSize(5);
    }

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
    void annotationShouldNotAffectFieldsThatAreNotAnnotated() {
        final StringLengthBV.ThreeFieldsOneMin result = Instancio.create(StringLengthBV.ThreeFieldsOneMin.class);

        final int maxLengthFromProperties = 1000;

        assertThat(result.getS1()).hasSizeLessThanOrEqualTo(maxLengthFromProperties);
        assertThat(result.getS2()).hasSizeGreaterThanOrEqualTo(20);
        assertThat(result.getS3()).hasSizeLessThanOrEqualTo(maxLengthFromProperties);
    }
}
