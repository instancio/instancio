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
package org.instancio.junit;

import org.instancio.internal.util.Sonar;
import org.instancio.support.ThreadLocalRandom;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies that each test method uses a different seed.
 * <p>
 * This test has a very small probability of failure if seeds happen to clash.
 */
@SuppressWarnings(Sonar.ADD_ASSERTION)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(InstancioExtension.class)
class EachMethodShouldGenerateItsOwnSeedTest {

    private static final Set<Long> seeds = new HashSet<>();
    private static int seedCount;

    @Test
    @Order(1)
    void method1() {
        seeds.add(ThreadLocalRandom.getInstance().get().getSeed());
        seedCount++;
    }

    @Test
    @Order(2)
    void method2() {
        seeds.add(ThreadLocalRandom.getInstance().get().getSeed());
        seedCount++;
    }

    // new seed per invocation
    @RepeatedTest(2)
    @Order(3)
    void method4() {
        seeds.add(ThreadLocalRandom.getInstance().get().getSeed());
        seedCount++;
    }

    @Test
    @Order(4)
    void verifyNumberOfSeeds() {
        seeds.add(ThreadLocalRandom.getInstance().get().getSeed());
        seedCount++;
        assertThat(seeds).hasSize(seedCount);
    }
}
