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
package org.instancio.test.features.oflist;

import org.instancio.Instancio;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.root;

@FeatureTag({
        Feature.OF_LIST,
        Feature.GENERATOR_SPEC_NULLABLE,
        Feature.WITH_NULLABLE
})
class OfListNullableTest {

    private static final int SAMPLE_SIZE = 500;

    @Test
    void withNullableRoot() {
        final Set<List<String>> results = Instancio.ofList(String.class)
                .withNullable(root())
                .stream()
                .limit(SAMPLE_SIZE)
                .collect(Collectors.toSet());

        assertThat(results).containsNull();
    }

    @Test
    void withNullableViaGenerator() {
        final Set<List<String>> results = Instancio.ofList(String.class)
                .generate(all(List.class), gen -> gen.collection().nullable())
                .stream()
                .limit(SAMPLE_SIZE)
                .collect(Collectors.toSet());

        assertThat(results).containsNull();
    }

}
