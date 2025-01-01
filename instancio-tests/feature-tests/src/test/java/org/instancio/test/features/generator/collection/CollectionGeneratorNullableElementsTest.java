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
package org.instancio.test.features.generator.collection;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.collections.lists.ListLong;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.tags.NonDeterministicTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.allInts;
import static org.instancio.Select.root;

@FeatureTag({Feature.GENERATE, Feature.COLLECTION_GENERATOR_NULLABLE_ELEMENTS})
@ExtendWith(InstancioExtension.class)
class CollectionGeneratorNullableElementsTest {

    @Test
    @NonDeterministicTag
    void listWithNullableElements() {
        final int collectionSize = 1000;
        final ListLong result = Instancio.of(ListLong.class)
                .generate(all(List.class), gen -> gen.collection().size(collectionSize).nullableElements())
                .create();

        assertThat(result.getList()).hasSize(collectionSize).containsNull();
        assertThat(new HashSet<>(result.getList())).hasSizeGreaterThan(collectionSize / 2);
    }

    /**
     * Tests generating a Set of requested size where randomly generated elements
     * have a high probability of collisions.
     */
    @Test
    void setWithNullableElements() {
        final int collectionSize = 10;
        final int sampleSize = 500;

        // generate a Set of size 10 containing integers between [0..10], with null values allowed
        final List<Set<Integer>> results = Instancio.ofSet(Integer.class)
                .generate(allInts(), gen -> gen.ints().range(0, 10))
                .generate(root(), gen -> gen.collection().size(collectionSize).nullableElements())
                .stream()
                .limit(sampleSize)
                .collect(Collectors.toList());

        assertThat(results)
                .hasSize(sampleSize)
                .allMatch(set -> set.size() == collectionSize)
                .anyMatch(set -> set.contains(null));
    }
}