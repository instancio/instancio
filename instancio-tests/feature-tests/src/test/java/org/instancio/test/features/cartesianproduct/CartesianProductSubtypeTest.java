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
package org.instancio.test.features.cartesianproduct;

import lombok.Data;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

@FeatureTag({Feature.CARTESIAN_PRODUCT, Feature.SUBTYPE})
@ExtendWith(InstancioExtension.class)
class CartesianProductSubtypeTest {

    @Data
    private static class NumberHolder {
        Number x, y;
    }

    @Test
    void subtype() {
        final List<NumberHolder> results = Instancio.ofCartesianProduct(NumberHolder.class)
                .with(field(NumberHolder::getX), 1, 2, 3)
                .subtype(field(NumberHolder::getY), Long.class)
                .list();

        assertThat(results)
                .hasSize(3)
                .allSatisfy(result -> assertThat(result.y).isInstanceOf(Long.class))
                .extracting(NumberHolder::getX)
                .containsExactly(1, 2, 3);
    }
}
