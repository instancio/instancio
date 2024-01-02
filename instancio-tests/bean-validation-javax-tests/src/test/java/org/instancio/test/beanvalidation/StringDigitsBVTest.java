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
import org.instancio.test.pojo.beanvalidation.StringDigitsBV;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag(Feature.BEAN_VALIDATION)
@ExtendWith(InstancioExtension.class)
class StringDigitsBVTest {

    @Test
    void onString() {
        final StringDigitsBV.OnString result = Instancio.create(StringDigitsBV.OnString.class);

        assertThat(result.getS0()).matches("\\.\\d{2}");
        assertThat(result.getS1()).matches("\\d");
        assertThat(result.getS2()).matches("\\d{15}");
        assertThat(result.getS3()).matches("\\d\\.\\d");
        assertThat(result.getS4()).matches("\\d{15}\\.\\d{20}");
    }

    @Test
    void onCharSequence() {
        final StringDigitsBV.OnCharSequence result = Instancio.create(StringDigitsBV.OnCharSequence.class);

        assertThat(result.getValue()).matches("\\d{3}\\.\\d{5}");
    }
}
