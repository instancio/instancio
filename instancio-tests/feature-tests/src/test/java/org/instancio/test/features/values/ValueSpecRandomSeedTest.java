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
package org.instancio.test.features.values;

import org.instancio.Gen;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@FeatureTag(Feature.VALUE_SPEC)
class ValueSpecRandomSeedTest {

    private static final int SAMPLE_SIZE = 10;
    private static final Set<String> results = new HashSet<>();

    @Order(1)
    @RepeatedTest(SAMPLE_SIZE)
    void shouldGenerateDifferentValues() {
        final int length = 50;
        final String result = Gen.string().length(length).get();
        assertThat(result).isNotBlank().hasSizeGreaterThanOrEqualTo(length);
        results.add(result);
    }

    @Order(2)
    @Test
    void shouldHaveExpectedSize() {
        assertThat(results).hasSize(SAMPLE_SIZE);
    }
}
