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

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.basic.IntegerHolder;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.tags.NonDeterministicTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.allInts;
import static org.instancio.Select.field;

@FeatureTag({Feature.CARTESIAN_PRODUCT, Feature.NULLABILITY})
@ExtendWith(InstancioExtension.class)
class CartesianProductNullableTest {

    @Test
    @NonDeterministicTag("May run without producing a null/zero result due to small sample size")
    void withNullable() {
        final Integer[] range = IntStream.rangeClosed(1, 10).boxed().toArray(Integer[]::new);

        final List<IntegerHolder> results = Instancio.ofCartesianProduct(IntegerHolder.class)
                .with(field(IntegerHolder::getPrimitive), range)
                .with(field(IntegerHolder::getWrapper), range)
                .withNullable(allInts())
                .create();

        assertThat(results)
                .hasSize(range.length * range.length)
                .doesNotContainNull()
                .anyMatch(r -> r.getWrapper() == null)
                .anyMatch(r -> r.getPrimitive() == 0);
    }

    @Test
    void withNullableElement() {
        final Integer[] range = IntStream.rangeClosed(1, 10).boxed().toArray(Integer[]::new);

        final List<IntegerHolder> results = Instancio.ofCartesianProduct(IntegerHolder.class)
                .with(field(IntegerHolder::getPrimitive), range)
                .with(field(IntegerHolder::getWrapper), range)
                .withNullable(allInts())
                .withSettings(Settings.create().set(Keys.COLLECTION_ELEMENTS_NULLABLE, true))
                .create();

        assertThat(results)
                .hasSize(range.length * range.length)
                .containsNull()
                .anyMatch(r -> r != null && r.getWrapper() == null)
                .anyMatch(r -> r != null && r.getPrimitive() == 0);
    }
}
