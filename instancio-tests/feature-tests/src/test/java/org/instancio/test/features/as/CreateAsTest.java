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
package org.instancio.test.features.as;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.root;

@FeatureTag(Feature.AS)
@ExtendWith(InstancioExtension.class)
class CreateAsTest {

    @Test
    void stringAsInteger() {
        final Integer result = Instancio.of(String.class)
                .generate(root(), gen -> gen.string().digits().prefix("1").length(2))
                .as(Integer::parseInt);

        assertThat(result).isBetween(100, 999);
    }

    @Test
    void ofSetAsList() {
        final List<String> result = Instancio.ofSet(String.class)
                .size(10)
                .as(ArrayList::new);

        assertThat(result).hasSize(10).doesNotContainNull();
    }

    @Test
    void withNullableResult() {
        final Set<String> results = Stream.generate(() ->
                        Instancio.of(Integer.class)
                                .withNullable(root())
                                .as(r -> r == null ? null : r.toString()))
                .limit(Constants.SAMPLE_SIZE_DD)
                .collect(Collectors.toSet());

        assertThat(results)
                .hasSizeGreaterThan(1)
                .containsNull();
    }
}
