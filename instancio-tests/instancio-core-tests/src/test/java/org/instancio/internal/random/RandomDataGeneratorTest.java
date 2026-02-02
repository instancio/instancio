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
package org.instancio.internal.random;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RandomDataGeneratorTest {

    private final Random random = new Random();

    @Test
    void nextLongValidation() {
        assertThatThrownBy(() -> RandomDataGenerator.nextLong(random, 3, 2))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @RepeatedTest(10)
    void nextLong() {
        assertThat(RandomDataGenerator.nextLong(random, 3, 3)).isEqualTo(3);
        assertThat(RandomDataGenerator.nextLong(random, Long.MAX_VALUE, Long.MAX_VALUE)).isEqualTo(Long.MAX_VALUE);
    }

    @RepeatedTest(10)
    void nextDouble() {
        assertThat(RandomDataGenerator.nextDouble(random, 3, 3)).isEqualTo(3);
        assertThat(RandomDataGenerator.nextDouble(random, Long.MAX_VALUE, Long.MAX_VALUE)).isEqualTo((double) Long.MAX_VALUE);
    }
}
