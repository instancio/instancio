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
package org.instancio.test.features.maxdepth;

import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.exception.InstancioApiException;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.Keys;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@FeatureTag({Feature.MAX_DEPTH, Feature.SETTINGS})
@ExtendWith(InstancioExtension.class)
class OnMaxDepthReachedTest {

    @Test
    void onMaxDepthReachedIgnoreByDefault() {
        final StringHolder result = Instancio.of(StringHolder.class)
                .withSetting(Keys.MAX_DEPTH, 0)
                .create();

        // Message should be logged at `warn` level, though we have no assertion for it
        assertThat(result).isNotNull().hasAllNullFieldsOrProperties();
    }

    @Test
    void onMaxDepthReachedFail() {
        final InstancioApi<StringHolder> api = Instancio.of(StringHolder.class)
                .withSetting(Keys.MAX_DEPTH, 0)
                .withSetting(Keys.FAIL_ON_MAX_DEPTH_REACHED, true);

        assertThatThrownBy(api::create)
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("max depth reached");
    }
}
