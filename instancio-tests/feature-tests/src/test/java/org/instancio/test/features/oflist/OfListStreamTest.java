/*
 * Copyright 2022 the original author or authors.
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
package org.instancio.test.features.oflist;

import org.instancio.Instancio;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.root;

@FeatureTag({Feature.OF_LIST, Feature.STREAM})
class OfListStreamTest {

    @Test
    void stream() {
        final int limit = 5;
        final Stream<List<String>> results = Instancio.ofList(String.class)
                .stream()
                .limit(limit);

        assertThat(results)
                .hasSize(limit)
                .allSatisfy(innerList -> assertThat(innerList)
                        .hasSizeBetween(Constants.MIN_SIZE, Constants.MAX_SIZE)
                        .doesNotContainNull());
    }

    @Test
    void streamWithSize() {
        final int limit = 5;
        final int listSize = 3;

        final Stream<List<String>> results = Instancio.ofList(String.class)
                .generate(root(), gen -> gen.collection().size(listSize))
                .stream()
                .limit(limit);

        assertThat(results)
                .hasSize(limit)
                .allSatisfy(nested -> assertThat(nested)
                        .hasSize(listSize)
                        .doesNotContainNull());
    }
}
