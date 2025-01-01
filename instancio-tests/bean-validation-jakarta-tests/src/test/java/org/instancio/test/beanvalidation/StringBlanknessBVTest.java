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
import org.instancio.test.pojo.beanvalidation.StringBlanknessBV;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.test.support.util.Constants.SAMPLE_SIZE_DDD;

@FeatureTag(Feature.BEAN_VALIDATION)
@ExtendWith(InstancioExtension.class)
class StringBlanknessBVTest {

    @WithSettings
    private final Settings allowNullAndEmpty = Settings.create()
            .set(Keys.STRING_MIN_LENGTH, 0)
            .set(Keys.STRING_NULLABLE, true)
            .set(Keys.STRING_ALLOW_EMPTY, true)
            .set(Keys.COLLECTION_NULLABLE, false)
            .lock();

    @Test
    void notBlank() {
        final List<StringBlanknessBV.WithNotBlank> results = Instancio.ofList(StringBlanknessBV.WithNotBlank.class)
                .size(SAMPLE_SIZE_DDD)
                .create();

        assertThat(results)
                .hasSize(SAMPLE_SIZE_DDD)
                .extracting(StringBlanknessBV.WithNotBlank::getValue)
                .doesNotContain(null, "");
    }

    @Test
    void notEmpty() {
        final List<StringBlanknessBV.WithNotEmpty> results = Instancio.ofList(StringBlanknessBV.WithNotEmpty.class)
                .size(SAMPLE_SIZE_DDD)
                .create();

        assertThat(results)
                .hasSize(SAMPLE_SIZE_DDD)
                .extracting(StringBlanknessBV.WithNotEmpty::getValue)
                .doesNotContain(null, "");
    }

    @Test
    void notNull() {
        final List<StringBlanknessBV.WithNotNull> results = Instancio.ofList(StringBlanknessBV.WithNotNull.class)
                .size(SAMPLE_SIZE_DDD)
                .withSettings(Settings.create().set(Keys.STRING_NULLABLE, true))
                .create();

        assertThat(results)
                .hasSize(SAMPLE_SIZE_DDD)
                .extracting(StringBlanknessBV.WithNotNull::getValue)
                .doesNotContainNull();
    }
}
