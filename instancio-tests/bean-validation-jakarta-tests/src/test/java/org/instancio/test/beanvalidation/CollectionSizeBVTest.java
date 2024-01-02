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
import org.instancio.test.pojo.beanvalidation.CollectionSizeBV;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.test.support.util.Constants.SAMPLE_SIZE_DD;

@FeatureTag(Feature.BEAN_VALIDATION)
@ExtendWith(InstancioExtension.class)
class CollectionSizeBVTest {

    @WithSettings
    private final Settings settings = Settings.create()
            .set(Keys.STRING_NULLABLE, false)
            .set(Keys.STRING_MAX_LENGTH, 10);

    @RepeatedTest(SAMPLE_SIZE_DD)
    void withMinSize() {
        final CollectionSizeBV.WithMinSize result = Instancio.create(CollectionSizeBV.WithMinSize.class);
        assertThat(result.getValue()).hasSizeGreaterThanOrEqualTo(8);
    }

    @RepeatedTest(SAMPLE_SIZE_DD)
    void withMinSizeZeo() {
        final CollectionSizeBV.WithMinSizeZero result = Instancio.create(CollectionSizeBV.WithMinSizeZero.class);
        assertThat(result.getValue()).hasSizeBetween(0, Keys.COLLECTION_MAX_SIZE.defaultValue());
    }

    @RepeatedTest(SAMPLE_SIZE_DD)
    void withMaxSize() {
        final CollectionSizeBV.WithMaxSize result = Instancio.create(CollectionSizeBV.WithMaxSize.class);
        assertThat(result.getValue()).hasSizeLessThanOrEqualTo(1);
    }

    @RepeatedTest(SAMPLE_SIZE_DD)
    void withMaxSizeZero() {
        final CollectionSizeBV.WithMaxSizeZero result = Instancio.create(CollectionSizeBV.WithMaxSizeZero.class);
        assertThat(result.getValue()).isEmpty();
    }

    @RepeatedTest(SAMPLE_SIZE_DD)
    void withMinMaxSize() {
        final CollectionSizeBV.WithMinMaxSize result = Instancio.create(CollectionSizeBV.WithMinMaxSize.class);
        assertThat(result.getValue()).hasSizeBetween(19, 20);
    }

    @RepeatedTest(SAMPLE_SIZE_DD)
    void withMinMaxEqual() {
        final CollectionSizeBV.WithMinMaxEqual result = Instancio.create(CollectionSizeBV.WithMinMaxEqual.class);
        assertThat(result.getValue()).hasSize(5);
    }
}
