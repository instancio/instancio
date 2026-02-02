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
package org.instancio.test.features.generator.collection;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.collections.lists.ListLong;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.tags.NonDeterministicTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;

@NonDeterministicTag
@FeatureTag({Feature.GENERATE, Feature.COLLECTION_GENERATOR_NULLABLE})
@ExtendWith(InstancioExtension.class)
class CollectionGeneratorNullableTest {
    private static final int SAMPLE_SIZE = 500;

    @Test
    void nullableCollection() {
        final Set<List<Long>> results = Instancio.of(ListLong.class)
                .generate(all(List.class), gen -> gen.collection().nullable())
                .stream()
                .map(ListLong::getList)
                .limit(SAMPLE_SIZE)
                .collect(Collectors.toSet());

        assertThat(results).containsNull();
    }

}