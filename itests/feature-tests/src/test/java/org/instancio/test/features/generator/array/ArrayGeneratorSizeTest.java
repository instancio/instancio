/*
 *  Copyright 2022 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.instancio.test.features.generator.array;

import org.instancio.Instancio;
import org.instancio.generator.array.ArrayGeneratorSpec;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.arrays.ArrayLong;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.util.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;

@FeatureTag({
        Feature.ARRAY_GENERATOR_MIN_LENGTH,
        Feature.ARRAY_GENERATOR_MAX_LENGTH,
        Feature.ARRAY_GENERATOR_LENGTH})
@ExtendWith(InstancioExtension.class)
class ArrayGeneratorSizeTest {

    private static final int EXPECTED_SIZE = 50;

    @Test
    void size() {
        assertSize(spec -> spec.length(EXPECTED_SIZE), EXPECTED_SIZE);
    }

    @Test
    void sizeZero() {
        assertSize(spec -> spec.length(0), 0);
    }

    @Test
    void minLength() {
        final int maxSize = EXPECTED_SIZE + EXPECTED_SIZE * Constants.RANGE_ADJUSTMENT_PERCENTAGE / 100;
        assertSizeBetween(spec -> spec.minLength(EXPECTED_SIZE), EXPECTED_SIZE, maxSize);
    }

    @Test
    void maxLength() {
        assertSize(spec -> spec.maxLength(1), 1);
    }

    private void assertSize(Function<ArrayGeneratorSpec<?>, ArrayGeneratorSpec<?>> fn, int size) {
        assertSizeBetween(fn, size, size);
    }

    private void assertSizeBetween(Function<ArrayGeneratorSpec<?>, ArrayGeneratorSpec<?>> fn, int minSize, int maxSize) {
        final ArrayLong result = Instancio.of(ArrayLong.class)
                .generate(all(long[].class), gen -> fn.apply(gen.array()))
                .generate(all(Long[].class), gen -> fn.apply(gen.array()))
                .create();

        assertThat(result.getPrimitive()).hasSizeBetween(minSize, maxSize);
        assertThat(result.getWrapper()).hasSizeBetween(minSize, maxSize);
    }
}