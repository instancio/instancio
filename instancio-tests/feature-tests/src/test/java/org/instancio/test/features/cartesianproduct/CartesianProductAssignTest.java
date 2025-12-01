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
package org.instancio.test.features.cartesianproduct;

import org.instancio.Assign;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.basic.IntegerHolder;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Assign.given;
import static org.instancio.Select.field;

@FeatureTag({Feature.CARTESIAN_PRODUCT, Feature.ASSIGN})
@ExtendWith(InstancioExtension.class)
class CartesianProductAssignTest {

    /**
     * Assign should win because it has higher precedence.
     */
    @Test
    void overwriteUsingAssign() {
        final List<IntegerHolder> results = Instancio.ofCartesianProduct(IntegerHolder.class)
                .with(field(IntegerHolder::getPrimitive), 1, 2)
                .with(field(IntegerHolder::getWrapper), 3, 4)
                .assign(Assign.valueOf(IntegerHolder::getPrimitive).set(8))
                .assign(Assign.valueOf(IntegerHolder::getWrapper).set(9))
                .create();

        assertThat(results).extracting(IntegerHolder::getPrimitive).containsExactly(8, 8, 8, 8);
        assertThat(results).extracting(IntegerHolder::getWrapper).containsExactly(9, 9, 9, 9);
    }

    @Test
    void conditionalAssign() {
        final List<IntegerHolder> results = Instancio.ofCartesianProduct(IntegerHolder.class)
                .with(field(IntegerHolder::getPrimitive), 1, 2)
                .with(field(IntegerHolder::getWrapper), null, null) // placeholders to be overwritten by assign()
                .assign(given(IntegerHolder::getPrimitive).is(1).set(field(IntegerHolder::getWrapper), 10))
                .assign(given(IntegerHolder::getPrimitive).is(2).set(field(IntegerHolder::getWrapper), 20))
                .create();

        assertThat(results).extracting(IntegerHolder::getPrimitive).containsExactly(1, 1, 2, 2);
        assertThat(results).extracting(IntegerHolder::getWrapper).containsExactly(10, 10, 20, 20);
    }
}
