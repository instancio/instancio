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

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.instancio.Instancio;
import org.instancio.TypeToken;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag(Feature.BEAN_VALIDATION)
@ExtendWith(InstancioExtension.class)
class GenericFieldBVTest {

    private static class WithSize<T> {
        @Size(min = 1, max = 3)
        @NotNull
        private T value;
    }

    private static class WithMinMax<T> {
        @Min(1)
        @Max(2)
        @NotNull
        private T value;
    }

    @Test
    void numericItemMinMax() {
        final WithMinMax<Integer> result = Instancio.create(new TypeToken<WithMinMax<Integer>>() {});

        assertThat(result.value).isBetween(1, 2);
    }

    @Test
    void stringItemSize() {
        final WithSize<String> result = Instancio.create(new TypeToken<WithSize<String>>() {});

        assertThat(result.value).hasSizeBetween(1, 3);
    }
}
