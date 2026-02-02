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
package org.instancio.test.features.ofmap;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.root;

@FeatureTag({Feature.OF_MAP, Feature.STREAM})
@ExtendWith(InstancioExtension.class)
class OfMapStreamTest {

    @Test
    void stream() {
        final int limit = 5;
        final Stream<Map<UUID, String>> results = Instancio.ofMap(UUID.class, String.class)
                .stream()
                .limit(limit);

        assertThat(results)
                .hasSize(limit)
                .allSatisfy(map -> assertThat(map).hasSizeBetween(Constants.MIN_SIZE, Constants.MAX_SIZE));
    }

    @Test
    void streamWithSize() {
        final int limit = 5;
        final int mapSize = 3;

        final Stream<Map<UUID, String>> results = Instancio.ofMap(UUID.class, String.class)
                .generate(root(), gen -> gen.map().size(mapSize))
                .stream()
                .limit(limit);

        assertThat(results)
                .hasSize(limit)
                .allSatisfy(map -> assertThat(map).hasSize(mapSize));
    }
}
