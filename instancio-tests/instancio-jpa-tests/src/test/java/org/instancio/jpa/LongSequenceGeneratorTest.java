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
package org.instancio.jpa;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.instancio.jpa.generator.LongSequenceGenerator;
import org.junit.jupiter.api.Test;

public class LongSequenceGeneratorTest {

    @Test
    void testDefaults() {
        // Given
        LongSequenceGenerator longSequenceGenerator = new LongSequenceGenerator();

        // When
        Long v1 = longSequenceGenerator.generate(null);
        Long v2 = longSequenceGenerator.generate(null);

        // Then
        assertThat(v1).isEqualTo(0L);
        assertThat(v2).isEqualTo(1L);
    }

    @Test
    void testCustomRange() {
        // Given
        LongSequenceGenerator longSequenceGenerator = new LongSequenceGenerator().start(3L).end(10L);

        // When
        Long v1 = longSequenceGenerator.generate(null);
        Long v2 = longSequenceGenerator.generate(null);

        // Then
        assertThat(v1).isEqualTo(3L);
        assertThat(v2).isEqualTo(4L);
    }

    @Test
    void testOverflow() {
        // Given
        LongSequenceGenerator longSequenceGenerator = new LongSequenceGenerator().start(3L).end(4L);

        // When
        Long v1 = longSequenceGenerator.generate(null);
        Long v2 = longSequenceGenerator.generate(null);
        Long v3 = longSequenceGenerator.generate(null);

        // Then
        assertThat(v1).isEqualTo(3L);
        assertThat(v2).isEqualTo(4L);
        assertThat(v3).isEqualTo(3L);
    }
}
