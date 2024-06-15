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

@FeatureTag({Feature.CARTESIAN_PRODUCT, Feature.SUPPLY, Feature.GENERATE})
@ExtendWith(InstancioExtension.class)
class CartesianProductSupplyGenerateTest {

    @Test
    void generateWithGeneratorSpecProvider() {
        final List<IntegerHolder> results = Instancio.ofCartesianProduct(IntegerHolder.class)
                .with(field(IntegerHolder::getPrimitive), 1, 2)
                .generate(field(IntegerHolder::getWrapper), gen -> gen.ints().max(-1))
                .create();

        assertThat(results)
                .hasSize(2)
                .allSatisfy(result -> assertThat(result.getWrapper()).isNegative())
                .extracting(IntegerHolder::getPrimitive)
                .containsExactly(1, 2);
    }

    @Test
    void generateWithGeneratorSpec() {
        final List<IntegerHolder> results = Instancio.ofCartesianProduct(IntegerHolder.class)
                .with(field(IntegerHolder::getPrimitive), 1, 2)
                .generate(field(IntegerHolder::getWrapper), Instancio.gen().ints().range(-1, -1))
                .create();

        assertThat(results)
                .hasSize(2)
                .allSatisfy(result -> assertThat(result.getWrapper()).isEqualTo(-1))
                .extracting(IntegerHolder::getPrimitive)
                .containsExactly(1, 2);
    }

    @Test
    void supplyWithSupplier() {
        final List<IntegerHolder> results = Instancio.ofCartesianProduct(IntegerHolder.class)
                .with(field(IntegerHolder::getPrimitive), 1, 2)
                .supply(field(IntegerHolder::getWrapper), () -> -1)
                .create();

        assertThat(results)
                .hasSize(2)
                .allSatisfy(result -> assertThat(result.getWrapper()).isEqualTo(-1))
                .extracting(IntegerHolder::getPrimitive)
                .containsExactly(1, 2);
    }

    @Test
    void supplyWithGenerator() {
        final List<IntegerHolder> results = Instancio.ofCartesianProduct(IntegerHolder.class)
                .with(field(IntegerHolder::getPrimitive), 1, 2)
                .supply(field(IntegerHolder::getWrapper), random -> -1)
                .create();

        assertThat(results)
                .hasSize(2)
                .allSatisfy(result -> assertThat(result.getWrapper()).isEqualTo(-1))
                .extracting(IntegerHolder::getPrimitive)
                .containsExactly(1, 2);
    }
}
