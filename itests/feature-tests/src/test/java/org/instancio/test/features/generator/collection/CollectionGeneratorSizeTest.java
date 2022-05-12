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
package org.instancio.test.features.generator.collection;

import org.instancio.Instancio;
import org.instancio.generator.util.CollectionGeneratorSpec;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.collections.lists.ListLong;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.util.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;

@FeatureTag({
        Feature.COLLECTION_GENERATOR_MIN_SIZE,
        Feature.COLLECTION_GENERATOR_MAX_SIZE,
        Feature.COLLECTION_GENERATOR_SIZE})
@ExtendWith(InstancioExtension.class)
class CollectionGeneratorSizeTest {

    private static final int EXPECTED_SIZE = 50;

    @Test
    void size() {
        assertSize(spec -> spec.size(EXPECTED_SIZE), EXPECTED_SIZE);
    }

    @Test
    void sizeZero() {
        assertSize(spec -> spec.size(0), 0);
    }

    @Test
    void minSize() {
        final int maxSize = EXPECTED_SIZE + EXPECTED_SIZE * Constants.RANGE_ADJUSTMENT_PERCENTAGE / 100;
        assertSizeBetween(spec -> spec.minSize(EXPECTED_SIZE), EXPECTED_SIZE, maxSize);
    }

    @Test
    void maxSize() {
        assertSize(spec -> spec.maxSize(1), 1);
    }

    @Test
    void minSizeEqualToMaxSize() {
        assertSizeBetween(spec -> spec.minSize(EXPECTED_SIZE).maxSize(EXPECTED_SIZE), EXPECTED_SIZE, EXPECTED_SIZE);
    }

    private void assertSize(Function<CollectionGeneratorSpec<?>, CollectionGeneratorSpec<?>> fn, int expectedSize) {
        assertSizeBetween(fn, expectedSize, expectedSize);
    }

    private void assertSizeBetween(Function<CollectionGeneratorSpec<?>, CollectionGeneratorSpec<?>> fn, int minSize, int maxSize) {
        final ListLong result = Instancio.of(ListLong.class)
                .generate(all(List.class), gen -> fn.apply(gen.collection()))
                .create();

        assertThat(result.getList()).hasSizeBetween(minSize, maxSize);
    }
}