/*
 * Copyright 2022-2026 the original author or authors.
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
package org.instancio.test.features.size;

import org.instancio.Instancio;
import org.instancio.Size;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

@FeatureTag({Feature.SIZE, Feature.CARTESIAN_PRODUCT})
@ExtendWith(InstancioExtension.class)
class SizeWithCartesianProductTest {

    private static final int SIZE = 7;

    private record Sample(int x, List<String> list) {}

    @Test
    void sizeInteger() {
        final List<Sample> results = Instancio.ofCartesianProduct(Sample.class)
                .with(field(Sample::x), 1, 2, 3)
                .size(field(Sample::list), SIZE)
                .create();

        assertThat(results)
                .allSatisfy(r -> assertThat(r.list()).hasSize(SIZE))
                .extracting(r -> r.x)
                .containsExactly(1, 2, 3);
    }

    @Test
    void sizeObject() {
        final List<Sample> results = Instancio.ofCartesianProduct(Sample.class)
                .with(field(Sample::x), 1, 2, 3)
                .size(field(Sample::list), Size.of(SIZE))
                .create();

        assertThat(results)
                .allSatisfy(r -> assertThat(r.list()).hasSize(SIZE))
                .extracting(r -> r.x)
                .containsExactly(1, 2, 3);
    }
}
