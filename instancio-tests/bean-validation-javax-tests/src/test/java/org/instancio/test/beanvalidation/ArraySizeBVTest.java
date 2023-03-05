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
import org.instancio.test.pojo.beanvalidation.ArraySizeBV;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.test.support.util.Constants.SAMPLE_SIZE_DD;

@FeatureTag(Feature.BEAN_VALIDATION)
@ExtendWith(InstancioExtension.class)
class ArraySizeBVTest {

    @RepeatedTest(SAMPLE_SIZE_DD)
    void withMinSize() {
        final ArraySizeBV.WithMinSize result = Instancio.create(ArraySizeBV.WithMinSize.class);
        assertThat(result.getValue()).hasSizeGreaterThanOrEqualTo(8);
    }

    @RepeatedTest(SAMPLE_SIZE_DD)
    void withMinSizeZeo() {
        final ArraySizeBV.WithMinSizeZero result = Instancio.create(ArraySizeBV.WithMinSizeZero.class);
        assertThat(result.getValue()).hasSizeBetween(0, Keys.ARRAY_MAX_LENGTH.defaultValue());
    }

    @RepeatedTest(SAMPLE_SIZE_DD)
    void withMaxSize() {
        final ArraySizeBV.WithMaxSize result = Instancio.create(ArraySizeBV.WithMaxSize.class);
        assertThat(result.getValue()).hasSizeLessThanOrEqualTo(1);
    }

    @RepeatedTest(SAMPLE_SIZE_DD)
    void withMaxSizeZero() {
        final ArraySizeBV.WithMaxSizeZero result = Instancio.create(ArraySizeBV.WithMaxSizeZero.class);
        assertThat(result.getValue()).isEmpty();
    }

    @RepeatedTest(SAMPLE_SIZE_DD)
    void withMinMaxSize() {
        final ArraySizeBV.WithMinMaxSize result = Instancio.create(ArraySizeBV.WithMinMaxSize.class);
        assertThat(result.getValue()).hasSizeBetween(19, 20);
    }

    @RepeatedTest(SAMPLE_SIZE_DD)
    void withMinMaxEqual() {
        final ArraySizeBV.WithMinMaxEqual result = Instancio.create(ArraySizeBV.WithMinMaxEqual.class);
        assertThat(result.getValue()).hasSize(5);
    }
}
