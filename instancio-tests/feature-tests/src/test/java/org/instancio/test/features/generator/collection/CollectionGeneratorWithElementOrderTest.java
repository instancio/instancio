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
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.Seed;
import org.instancio.test.support.pojo.collections.lists.ListLong;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;

// Casts added to suppress "non-varargs call of varargs" during the build
@SuppressWarnings("RedundantCast")
@FeatureTag({Feature.GENERATE, Feature.COLLECTION_GENERATOR_WITH_ELEMENTS})
@ExtendWith(InstancioExtension.class)
class CollectionGeneratorWithElementOrderTest {
    private static final Long[] EXPECTED_LONGS = {1L, 2L, 3L};

    private static final Set<List<Long>> RESULTS = new LinkedHashSet<>();

    @Seed(-12345)
    @RepeatedTest(5)
    void shuffledElementShouldBeInTheSameOrderForAGivenSeed() {
        final ListLong result = Instancio.of(ListLong.class)
                .generate(all(List.class), gen -> gen.collection().maxSize(5).with((Object[]) EXPECTED_LONGS))
                .create();

        RESULTS.add(result.getList());

        assertThat(RESULTS)
                .as("Same shuffled sequence should be generated each time")
                .hasSize(1);
    }


}