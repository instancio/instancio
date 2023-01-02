/*
 * Copyright 2022-2023 the original author or authors.
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
package org.instancio.test.features.generator.array;

import org.instancio.Instancio;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;

@FeatureTag({Feature.ARRAY_GENERATOR_NULLABLE, Feature.GENERATE})
class ArrayGeneratorNullableTest {

    private static final int SAMPLE_SIZE = 500;

    @Test
    void nullableArray() {
        final Set<String[]> results = Instancio.of(String[].class)
                .generate(all(String[].class), gen -> gen.array().nullable())
                .stream()
                .limit(SAMPLE_SIZE)
                .collect(Collectors.toSet());

        assertThat(results).containsNull();
    }

}
