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
package org.instancio.test.jpa;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.Keys;
import org.instancio.test.pojo.jpa.ColumnLengthJPA.WithDefaultLength;
import org.instancio.test.pojo.jpa.ColumnLengthJPA.WithLength;
import org.instancio.test.pojo.jpa.ColumnLengthJPA.WithLengthAndSize;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag(Feature.JPA)
@ExtendWith(InstancioExtension.class)
class ColumnLengthJPATest {

    @RepeatedTest(Constants.SAMPLE_SIZE_DD)
    void withDefaultLength() {
        final WithDefaultLength result = Instancio.create(WithDefaultLength.class);

        assertThat(result.getS()).hasSizeLessThanOrEqualTo(Keys.STRING_MAX_LENGTH.defaultValue());
    }

    @RepeatedTest(Constants.SAMPLE_SIZE_DD)
    void withLength() {
        final WithLength result = Instancio.create(WithLength.class);

        assertThat(result.getS1()).hasSizeLessThanOrEqualTo(1);
        assertThat(result.getS2()).hasSizeLessThanOrEqualTo(2);
        assertThat(result.getS3()).hasSizeLessThanOrEqualTo(Keys.STRING_MAX_LENGTH.defaultValue());
        assertThat(result.getS4()).hasSizeLessThanOrEqualTo(Keys.STRING_MAX_LENGTH.defaultValue());
    }

    @RepeatedTest(Constants.SAMPLE_SIZE_DD)
    void withLengthAndSize() {
        final WithLengthAndSize result = Instancio.create(WithLengthAndSize.class);

        assertThat(result.getS1()).hasSize(1);
        assertThat(result.getS2()).hasSize(2);
    }
}
