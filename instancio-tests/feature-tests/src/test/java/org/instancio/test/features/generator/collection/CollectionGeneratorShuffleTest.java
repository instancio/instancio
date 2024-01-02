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
package org.instancio.test.features.generator.collection;

import org.instancio.Instancio;
import org.instancio.Random;
import org.instancio.TypeToken;
import org.instancio.generator.Generator;
import org.instancio.generator.Hints;
import org.instancio.generator.hints.CollectionHint;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;

@FeatureTag({Feature.GENERATOR, Feature.COLLECTION_GENERATOR_SHUFFLE})
@ExtendWith(InstancioExtension.class)
class CollectionGeneratorShuffleTest {

    @Test
    void verifyShuffle() {
        final List<Long> sorted = LongStream.range(1, 100).boxed().collect(Collectors.toList());

        final List<Long> results = Instancio.of(new TypeToken<List<Long>>() {})
                .supply(all(List.class), new Generator<List<Long>>() {
                    @Override
                    public List<Long> generate(final Random random) {
                        return new ArrayList<>(sorted);
                    }

                    @Override
                    public Hints hints() {
                        return Hints.builder()
                                .with(CollectionHint.builder().shuffle(true).build())
                                .build();
                    }
                })
                .create();

        assertThat(results).containsAll(sorted).isNotEqualTo(sorted);
    }
}
