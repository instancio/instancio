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
package org.instancio.test.features.nullable;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.allStrings;

/**
 * Tests for a type nullable via generator spec.
 *
 * <ul>
 *   <li>values should be nullable for fields</li>
 *   <li>values should NOT be nullable for collection elements</li>
 * </ul>
 */
@FeatureTag({Feature.NULLABILITY, Feature.GENERATOR_SPEC_NULLABLE})
@ExtendWith(InstancioExtension.class)
class NullableTypeViaGeneratorSpecTest {

    private static final int SAMPLE_SIZE = 1000;

    @Test
    void withField() {
        final Stream<String> results = Instancio.of(StringHolder.class)
                .generate(allStrings(), gen -> gen.string().nullable())
                .stream()
                .limit(SAMPLE_SIZE)
                .map(StringHolder::getValue);

        assertThat(results).containsNull();
    }

    @Test
    void arrayElements() {
        final String[] results = Instancio.of(String[].class)
                .generate(all(String[].class), gen -> gen.array().length(SAMPLE_SIZE))
                .generate(allStrings(), gen -> gen.string().nullable())
                .create();

        assertThat(results)
                .hasSize(SAMPLE_SIZE)
                .doesNotContainNull();
    }

    @Test
    void collectionElements() {
        final Set<String> results = Instancio.ofSet(String.class)
                .size(SAMPLE_SIZE)
                .generate(allStrings(), gen -> gen.string().nullable())
                .create();

        assertThat(results)
                .hasSize(SAMPLE_SIZE)
                .doesNotContainNull();
    }

    @Test
    void mapKeys() {
        final Map<String, UUID> results = Instancio.ofMap(String.class, UUID.class)
                .size(SAMPLE_SIZE)
                .generate(allStrings(), gen -> gen.string().nullable())
                .create();

        assertThat(results.keySet())
                .hasSize(SAMPLE_SIZE)
                .doesNotContainNull();
    }
}
