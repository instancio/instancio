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
package org.instancio.test.features.cartesianproduct;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.basic.IntegerHolder;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

@FeatureTag({Feature.CARTESIAN_PRODUCT, Feature.IGNORE})
@ExtendWith(InstancioExtension.class)
class CartesianProductIgnoreTest {

    @Test
    void ignore() {
        final List<IntegerHolder> results = Instancio.ofCartesianProduct(IntegerHolder.class)
                .with(field(IntegerHolder::getPrimitive), 1, 2)
                .ignore(field(IntegerHolder::getWrapper))
                .create();

        assertThat(results)
                .hasSize(2)
                .extracting(IntegerHolder::getWrapper)
                .containsOnlyNulls();

        assertThat(results)
                .extracting(IntegerHolder::getPrimitive)
                .containsExactly(1, 2);
    }
}
