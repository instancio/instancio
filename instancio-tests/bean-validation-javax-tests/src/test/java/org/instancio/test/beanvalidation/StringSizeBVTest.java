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
package org.instancio.test.beanvalidation;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.pojo.beanvalidation.StringSizeBV;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag(Feature.BEAN_VALIDATION)
@ExtendWith(InstancioExtension.class)
class StringSizeBVTest {

    // Note: override custom properties to default string lengths for this test
    @WithSettings
    private final Settings settings = Settings.create()
            .set(Keys.STRING_MIN_LENGTH, Keys.STRING_MIN_LENGTH.defaultValue())
            .set(Keys.STRING_MAX_LENGTH, Keys.STRING_MAX_LENGTH.defaultValue());

    @Test
    void withMinSize() {
        final StringSizeBV.WithMinSize result = Instancio.create(StringSizeBV.WithMinSize.class);
        assertThat(result.getValue()).hasSizeGreaterThanOrEqualTo(8);
    }

    @Test
    void withMinSizeZeo() {
        final StringSizeBV.WithMinSizeZero result = Instancio.create(StringSizeBV.WithMinSizeZero.class);
        assertThat(result.getValue()).hasSizeBetween(0, Keys.STRING_MAX_LENGTH.defaultValue());
    }

    @Test
    void withMaxSize() {
        final StringSizeBV.WithMaxSize result = Instancio.create(StringSizeBV.WithMaxSize.class);
        assertThat(result.getValue()).hasSizeLessThanOrEqualTo(1);

    }

    @Test
    void withMaxSizeZero() {
        final StringSizeBV.WithMaxSizeZero result = Instancio.create(StringSizeBV.WithMaxSizeZero.class);
        assertThat(result.getValue()).isEmpty();
    }

    @Test
    void withMinMaxSize() {
        final StringSizeBV.WithMinMaxSize result = Instancio.create(StringSizeBV.WithMinMaxSize.class);
        assertThat(result.getValue()).hasSizeBetween(19, 20);
    }

    @Test
    void withMinMaxEqual() {
        final StringSizeBV.WithMinMaxEqual result = Instancio.create(StringSizeBV.WithMinMaxEqual.class);
        assertThat(result.getValue()).hasSize(5);
    }

    @Test
    void withStringFieldPrefix() {
        final StringSizeBV.WithMinMaxEqual result = Instancio.of(StringSizeBV.WithMinMaxEqual.class)
                .withSettings(Settings.create().set(Keys.STRING_FIELD_PREFIX_ENABLED, true))
                .create();

        final String expectedPrefix = "value_";
        assertThat(result.getValue())
                .hasSize(5 + expectedPrefix.length())
                .startsWith(expectedPrefix);
    }

    @Test
    void annotationShouldNotAffectFieldsThatAreNotAnnotated() {
        final StringSizeBV.ThreeFieldsOneMin result = Instancio.create(StringSizeBV.ThreeFieldsOneMin.class);

        assertThat(result.getS1()).hasSizeLessThanOrEqualTo(Keys.STRING_MAX_LENGTH.defaultValue());
        assertThat(result.getS2()).hasSizeGreaterThanOrEqualTo(20);
        assertThat(result.getS3()).hasSizeLessThanOrEqualTo(Keys.STRING_MAX_LENGTH.defaultValue());
    }
}
