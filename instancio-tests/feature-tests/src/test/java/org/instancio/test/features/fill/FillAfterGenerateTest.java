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
package org.instancio.test.features.fill;

import org.instancio.Instancio;
import org.instancio.generator.AfterGenerate;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.Keys;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The value of the {@link AfterGenerate} setting should not
 * affect the behaviour of the {@code fill()} method.
 */
@FeatureTag({Feature.AFTER_GENERATE, Feature.FILL})
@ExtendWith(InstancioExtension.class)
class FillAfterGenerateTest {

    @EnumSource(value = AfterGenerate.class)
    @ParameterizedTest
    void afterGenerateShouldHaveNoEffectOnFill(final AfterGenerate afterGenerate) {
        final StringHolder object = new StringHolder();

        Instancio.ofObject(object)
                .withSetting(Keys.AFTER_GENERATE_HINT, afterGenerate)
                .fill();

        assertThat(object.getValue()).isNotBlank();
    }

}