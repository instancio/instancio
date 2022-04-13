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
package org.instancio.test.features.generator.seed;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.util.Sonar;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(InstancioExtension.class)
class WithoutSeedAnnotationTest {

    private static final Set<Object> results = new HashSet<>();
    private static final int NUM_INVOCATIONS = 5;

    @AfterAll
    static void afterAll() {
        assertThat(results).hasSize(NUM_INVOCATIONS);
    }

    @RepeatedTest(NUM_INVOCATIONS)
    @SuppressWarnings(Sonar.ADD_ASSERTION)
    @DisplayName("Results Set should contain distinct elements")
    void withSeedAnnotation() {
        results.add(Instancio.create(long.class));
    }

    @Test
    @DisplayName("Separate calls to create() within the same test method should produce distinct values")
    void eachCreateInstanceGeneratesDistinctValue() {
        final String result1 = Instancio.create(String.class);
        final String result2 = Instancio.create(String.class);
        assertThat(result1).isNotEqualTo(result2);
    }
}
