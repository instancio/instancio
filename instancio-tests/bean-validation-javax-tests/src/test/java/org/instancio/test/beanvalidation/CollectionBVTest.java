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
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.pojo.beanvalidation.CollectionBV;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.test.support.util.Constants.SAMPLE_SIZE_D;

@FeatureTag(Feature.BEAN_VALIDATION)
@ExtendWith(InstancioExtension.class)
class CollectionBVTest {

    @WithSettings
    private final Settings settings = Settings.create()
            .set(Keys.STRING_NULLABLE, false)
            .set(Keys.STRING_MAX_LENGTH, 10);

    @Test
    void withMinSize() {
        final CollectionBV.WithMinSize result = Instancio.create(CollectionBV.WithMinSize.class);
        assertThat(result.getValue()).hasSizeGreaterThanOrEqualTo(8);
    }

    @Test
    void withMinSizeZeo() {
        final CollectionBV.WithMinSizeZero result = Instancio.create(CollectionBV.WithMinSizeZero.class);
        assertThat(result.getValue()).hasSizeBetween(0, Keys.COLLECTION_MAX_SIZE.defaultValue());
    }

    @Test
    void withMaxSize() {
        final CollectionBV.WithMaxSize result = Instancio.create(CollectionBV.WithMaxSize.class);
        assertThat(result.getValue()).hasSizeLessThanOrEqualTo(1);
    }

    @Test
    void withMaxSizeZero() {
        final CollectionBV.WithMaxSizeZero result = Instancio.create(CollectionBV.WithMaxSizeZero.class);
        assertThat(result.getValue()).isEmpty();
    }

    @Test
    void withMinMaxSize() {
        final CollectionBV.WithMinMaxSize result = Instancio.create(CollectionBV.WithMinMaxSize.class);
        assertThat(result.getValue()).hasSizeBetween(19, 20);
    }

    @Test
    void withMinMaxEqual() {
        final CollectionBV.WithMinMaxEqual result = Instancio.create(CollectionBV.WithMinMaxEqual.class);
        assertThat(result.getValue()).hasSize(5);
    }

    @RepeatedTest(SAMPLE_SIZE_D)
    void typeUse() {
        final CollectionBV.TypeUse result = Instancio.create(CollectionBV.TypeUse.class);

        assertThat(result.getValue())
                .hasSize(3)
                .allSatisfy(v -> assertThat(v).matches("\\d{7}\\.\\d{5}"));
    }
}
