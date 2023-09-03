/*
 *  Copyright 2022-2023 the original author or authors.
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
package org.instancio.quickcheck.engine;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.stream.IntStream;

import org.instancio.Instancio;
import org.instancio.quickcheck.api.ForAll;
import org.instancio.quickcheck.api.Property;
import org.instancio.quickcheck.api.artbitrary.Arbitrary;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.platform.commons.JUnitException;
import org.junit.platform.engine.discovery.ClassNameFilter;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.testkit.engine.EngineTestKit;

public class InstancioQuickcheckTestEngineTest {
    @DisplayName("Run selected class that has no @Property methods")
    @Test
    void runSelectedClassNoAnnotatedMethods() {
        EngineTestKit
            .engine("instancio-quickcheck")
            .selectors(DiscoverySelectors.selectClass(InstancioNonQuickcheckTest.class))
            .execute()
            .testEvents()
            .assertStatistics(stats -> stats
                    .started(0)
                    .succeeded(0)
                    .failed(0)
            );
    }

    @DisplayName("Run selected class")
    @Test
    void runSelectedClass() {
        EngineTestKit
            .engine("instancio-quickcheck")
            .selectors(DiscoverySelectors.selectClass(InstancioQuickcheckTest.class))
            .execute()
            .testEvents()
            .assertStatistics(stats -> stats
                    .started(2)
                    .succeeded(1)
                    .failed(1)
            );
    }

    @DisplayName("Run selected method")
    @Test
    void runSelectedMethod() {
        EngineTestKit
            .engine("instancio-quickcheck")
            .selectors(DiscoverySelectors.selectMethod("org.instancio.quickcheck.InstancioQuickcheckTest#positiveIntegers(java.lang.Integer)"))
            .execute()
            .testEvents()
            .assertStatistics(stats -> stats
                .started(1)
                .succeeded(1)
                .failed(0)
            );
    }

    @DisplayName("Run selected inner class (not implemented yet)")
    @Test
    void runSelectedInnerClass() {
        EngineTestKit
            .engine("instancio-quickcheck")
            .selectors(DiscoverySelectors.selectNestedClass(Arrays.asList(InstancioQuickcheckTest.class), InstancioQuickcheckTestEngineTest.class))
            .execute()
            .testEvents()
            .assertStatistics(stats -> stats
                .started(0)
                .succeeded(0)
                .failed(0)
            );
    }

    @DisplayName("Run selected inner class (not implemented yet)")
    @Test
    void runSelectedClassesWithFilter() {
        EngineTestKit
            .engine("instancio-quickcheck")
            .selectors(DiscoverySelectors.selectClass(org.instancio.quickcheck.InstancioQuickcheckTest.class))
            .filters(ClassNameFilter.excludeClassNamePatterns("org.instancio.quickcheck.InstancioQuickcheckTest"))
            .execute()
            .testEvents()
            .assertStatistics(stats -> stats
                .started(0)
                .succeeded(0)
                .failed(0)
            );
    }

    @DisplayName("Run selected nested class (not implemented yet)")
    @Test
    void runSelectedNestedClass() {
        EngineTestKit
            .engine("instancio-quickcheck")
            .selectors(DiscoverySelectors.selectNestedClass(Arrays.asList(InstancioQuickcheckNestedTest.class), InstancioQuickcheckTestEngineTest.class))
            .execute()
            .testEvents()
            .assertStatistics(stats -> stats
                .started(0)
                .succeeded(0)
                .failed(0)
            );
    }

    @DisplayName("Run selected nested method (not implemented yet)")
    @Test
    void runSelectedNestedMethod() {
        EngineTestKit
            .engine("instancio-quickcheck")
            .selectors(DiscoverySelectors.selectNestedMethod(Arrays.asList(InstancioQuickcheckNestedTest.class), InstancioQuickcheckTestEngineTest.class, "succeeding"))
            .execute()
            .testEvents()
            .assertStatistics(stats -> stats
                .started(0)
                .succeeded(0)
                .failed(0)
            );
    }

    @DisplayName("Run selected unique Id")
    @Test
    void runSelecteUniqueId() {
        EngineTestKit
            .engine("instancio-quickcheck")
            .selectors(DiscoverySelectors.selectUniqueId("[engine:instancio-quickcheck]/"
                + "[class:org.instancio.quickcheck.InstancioQuickcheckTest]/"
                + "[method:positiveIntegers(java.lang.Integer)]"))
            .execute()
            .testEvents()
            .assertStatistics(stats -> stats
                .started(1)
                .succeeded(1)
                .failed(0)
            );
    }
    
    @DisplayName("Run selected non annotated method)")
    @Test
    void runSelectedNonAnnotatedMethod() {
        EngineTestKit
            .engine("instancio-quickcheck")
            .selectors(DiscoverySelectors.selectMethod(org.instancio.quickcheck.InstancioQuickcheckTest.class, "odds"))
            .execute()
            .testEvents()
            .assertStatistics(stats -> stats
                .started(0)
                .succeeded(0)
                .failed(0)
            );
    }

    @DisplayName("Run selected nested method unique Id (not implemented yet)")
    @Test
    void runSelecteNestedMethodUniqueId() {
        assertThrows(JUnitException.class, () ->
            EngineTestKit
                .engine("instancio-quickcheck")
                .selectors(DiscoverySelectors.selectUniqueId("[engine:instancio-quickcheck]/"
                    + "[class:org.instancio.quickcheck.engine.InstancioQuickcheckTestEngineTest]/"
                    + "[class:org.instancio.quickcheck.InstancioQuickcheckNestedTest]/[method:succeeding(java.lang.Integer)]"))
                .execute()
            );
    }

    @DisplayName("Run with configuration")
    @Test
    void runSelectedWithConfiguration() {
        EngineTestKit
            .engine("instancio-quickcheck")
            .configurationParameter("seed", "0")
            .selectors(DiscoverySelectors.selectClass(InstancioSeedQuickcheckTest.class))
            .execute()
            .testEvents()
            .assertStatistics(stats -> stats
                    .started(1)
                    .succeeded(1)
                    .failed(0)
            );
    }

    @DisplayName("Run with arbitrary generator")
    @Test
    void runSelectedWithTimeout() {
        EngineTestKit
            .engine("instancio-quickcheck")
            .configurationParameter("seed", "0")
            .selectors(DiscoverySelectors.selectClass(InstancioArbitraryQuickcheckTest.class))
            .execute()
            .testEvents()
            .assertStatistics(stats -> stats
                    .started(1)
                    .succeeded(1)
                    .failed(0)
            );
    }

    private static class InstancioNonQuickcheckTest {
        @SuppressWarnings("unused")
        public void test(@ForAll Integer i) {
        }
    }

    private static class InstancioSeedQuickcheckTest {
        @Property(samples = 1)
        public void succeeding(@ForAll Integer i) {
            // seed = 0 should always generate same number 1361
            assertThat(i).isEqualTo(1361);
        }
    }

    @Nested
    private static class InstancioQuickcheckNestedTest {
        @Property(samples = 100)
        public void succeeding(@ForAll Integer i) {
            assertThat(i).isGreaterThan(0);
        }
        
        @Property(samples = 100)
        public void failing(@ForAll Integer i) {
            assertThat(i % 2).isEqualTo(0);
        }
    }

    private static class InstancioQuickcheckTest {
        @Property(samples = 100)
        public void succeeding(@ForAll Integer i) {
            assertThat(i).isGreaterThan(0);
        }
        
        @Property(samples = 100)
        public void failing(@ForAll Integer i) {
            assertThat(i % 2).isEqualTo(0);
        }
    }

    private static class InstancioArbitraryQuickcheckTest {
        @Property(samples = 100)
        @Timeout(1000)
        public void succeeding(@ForAll("positive") Integer i) {
            assertThat(i).isGreaterThan(0);
        }
        
        @SuppressWarnings("unused")
        public Arbitrary<Integer> positive() {
            return Arbitrary.fromStream(IntStream
                .generate(() -> Instancio.create(Integer.class))
                .filter(i -> i > 0));
        }
    }
}
