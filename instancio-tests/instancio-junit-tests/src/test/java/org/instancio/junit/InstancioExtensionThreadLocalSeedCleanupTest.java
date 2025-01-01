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
package org.instancio.junit;

import org.instancio.Instancio;
import org.instancio.support.ThreadLocalRandom;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.testkit.engine.EngineTestKit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

class InstancioExtensionThreadLocalSeedCleanupTest {

    @Test
    void threadLocalRandomCleanupWithSeedAnnotation() {
        assertThreadLocalIsNull(WithSeedAnnotationTest.class);
    }

    @Test
    void threadLocalRandomCleanupWithoutSeedAnnotation() {
        assertThreadLocalIsNull(WithoutSeedAnnotationTest.class);
    }

    private static void assertThreadLocalIsNull(final Class<?> testClass) {
        EngineTestKit.engine("junit-jupiter")
                .selectors(selectClass(testClass))
                .execute()
                .testEvents()
                .assertStatistics(stats -> stats.succeeded(1));

        assertThat(ThreadLocalRandom.getInstance().get())
                .as("Expected thread local Random to be removed after the test is done")
                .isNull();
    }

    @Nested
    @ExtendWith(InstancioExtension.class)
    class WithSeedAnnotationTest {
        @Test
        @Seed(1234)
        @DisplayName("Dummy test method to verify thread local is cleared in afterAll()")
        void dummy() {
            assertThat(Instancio.create(String.class)).isNotNull();
        }
    }

    @Nested
    @ExtendWith(InstancioExtension.class)
    class WithoutSeedAnnotationTest {
        @Test
        @DisplayName("Dummy test method to verify thread local is cleared in afterAll()")
        void dummy() {
            final long seed1 = ThreadLocalRandom.getInstance().get().getSeed();
            final long seed2 = ThreadLocalRandom.getInstance().get().getSeed();
            assertThat(seed1)
                    .as("Same seed should be used within the test method")
                    .isEqualTo(seed2);
        }
    }
}
