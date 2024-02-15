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
import org.instancio.test.pojo.beanvalidation.MapBV;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.test.support.util.Constants.SAMPLE_SIZE_D;
import static org.instancio.test.support.util.Constants.SAMPLE_SIZE_DD;

@FeatureTag(Feature.BEAN_VALIDATION)
@ExtendWith(InstancioExtension.class)
class MapBVTest {

    @WithSettings
    private final Settings settings = Settings.create()
            .set(Keys.STRING_NULLABLE, false)
            .set(Keys.STRING_ALLOW_EMPTY, false)
            .set(Keys.LONG_NULLABLE, false)
            .set(Keys.CHARACTER_NULLABLE, false)
            .set(Keys.DOUBLE_NULLABLE, false)
            .set(Keys.STRING_MAX_LENGTH, 10);

    @RepeatedTest(SAMPLE_SIZE_DD)
    void withMinSize() {
        final MapBV.WithMinSize result = Instancio.create(MapBV.WithMinSize.class);
        assertThat(result.getValue()).hasSizeGreaterThanOrEqualTo(8);
    }

    @RepeatedTest(SAMPLE_SIZE_DD)
    void withMinSizeZeo() {
        final MapBV.WithMinSizeZero result = Instancio.create(MapBV.WithMinSizeZero.class);
        assertThat(result.getValue()).hasSizeBetween(0, Keys.MAP_MAX_SIZE.defaultValue());
    }

    @RepeatedTest(SAMPLE_SIZE_DD)
    void withMaxSize() {
        final MapBV.WithMaxSize result = Instancio.create(MapBV.WithMaxSize.class);
        assertThat(result.getValue()).hasSizeLessThanOrEqualTo(1);
    }

    @RepeatedTest(SAMPLE_SIZE_DD)
    void withMaxSizeZero() {
        final MapBV.WithMaxSizeZero result = Instancio.create(MapBV.WithMaxSizeZero.class);
        assertThat(result.getValue()).isEmpty();
    }

    @RepeatedTest(SAMPLE_SIZE_DD)
    void withMinMaxSize() {
        final MapBV.WithMinMaxSize result = Instancio.create(MapBV.WithMinMaxSize.class);
        assertThat(result.getValue()).hasSizeBetween(19, 20);
    }

    @RepeatedTest(SAMPLE_SIZE_DD)
    void withMinMaxEqual() {
        final MapBV.WithMinMaxEqual result = Instancio.create(MapBV.WithMinMaxEqual.class);
        assertThat(result.getValue()).hasSize(5);
    }

    @RepeatedTest(SAMPLE_SIZE_D)
    void typeUse() {
        final MapBV.TypeUse result = Instancio.create(MapBV.TypeUse.class);

        assertThat(result.getValue()).allSatisfy((k, v) -> {
            assertThat(k).matches("\\d{7}\\.\\d{5}");
            assertThat(v).matches("\\d{3}\\.\\d{2}");
        });
    }
}
