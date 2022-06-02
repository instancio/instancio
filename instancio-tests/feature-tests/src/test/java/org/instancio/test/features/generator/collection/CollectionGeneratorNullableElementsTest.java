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
import org.instancio.test.support.pojo.collections.lists.ListLong;
import org.instancio.test.support.pojo.collections.sets.SetLong;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.tags.NonDeterministicTag;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;

@NonDeterministicTag
@FeatureTag({Feature.GENERATE, Feature.COLLECTION_GENERATOR_NULLABLE_ELEMENT})
@ExtendWith(InstancioExtension.class)
class CollectionGeneratorNullableElementsTest {
    private static final int COLLECTION_SIZE = 1000;

    @Test
    void listWithNullableElements() {
        final ListLong result = Instancio.of(ListLong.class)
                .generate(all(List.class), gen -> gen.collection().size(COLLECTION_SIZE).nullableElements())
                .create();

        assertThat(result.getList()).hasSize(COLLECTION_SIZE).containsNull();
        assertThat(new HashSet<>(result.getList())).hasSizeGreaterThan(COLLECTION_SIZE / 2);
    }

    /**
     * Since some generated elements might be null, generated set might be of smaller size than requested.
     * In some cases it may not even be possible to generate a set of requested size.
     *
     * @see CollectionGeneratorSizeTest#impossibleSetSize()
     */
    @Test
    @Disabled("Requesting a set of size 10 with nullable elements may not produce a set of the requested size")
    void setWithNullableElements() {
        final int expectedSize = 10;
        final SetLong result = Instancio.of(SetLong.class)
                .generate(all(Set.class), gen -> gen.collection().size(expectedSize).nullableElements())
                .create();

        assertThat(result.getSet()).hasSize(expectedSize).containsNull();
    }
}