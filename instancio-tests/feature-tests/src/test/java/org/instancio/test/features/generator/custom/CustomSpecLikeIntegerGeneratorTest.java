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
package org.instancio.test.features.generator.custom;

import org.instancio.Instancio;
import org.instancio.Random;
import org.instancio.generator.Generator;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.collections.lists.TwoListsOfInteger;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.allInts;
import static org.instancio.Select.scope;

@FeatureTag(Feature.GENERATOR)
@ExtendWith(InstancioExtension.class)
class CustomSpecLikeIntegerGeneratorTest {

    private static class SpecLikeGenerator implements Generator<Integer> {
        private int min = 1;
        private int max = 10;

        SpecLikeGenerator min(final int min) {
            this.min = min;
            return this;
        }

        SpecLikeGenerator max(final int max) {
            this.max = max;
            return this;
        }

        @Override
        public Integer generate(final Random random) {
            return random.intRange(min, max);
        }
    }

    private static SpecLikeGenerator ints() {
        return new SpecLikeGenerator();
    }

    @Test
    void customSpecLikeGenerator() {
        final TwoListsOfInteger result = Instancio.of(TwoListsOfInteger.class)
                .supply(allInts().within(scope(TwoListsOfInteger.class, "list1")), ints())
                .supply(allInts().within(scope(TwoListsOfInteger.class, "list2")), ints().min(100).max(110))
                .create();

        assertThat(result.getList1()).allSatisfy(e -> assertThat(e).isBetween(1, 10));
        assertThat(result.getList2()).allSatisfy(e -> assertThat(e).isBetween(100, 110));
    }
}