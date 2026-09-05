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
package org.instancio.junit;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.instancio.internal.util.Sonar;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.platform.testkit.engine.EngineTestKit;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

@SuppressWarnings({"JUnitMalformedDeclaration", "NewClassNamingConvention"})
class SeedSummaryTest {

    private static final String SUMMARY_LOGGER = "org.instancio.log.test.failure.seed";
    private static final long SEED = 12345;
    private static final int CONCURRENT_FAILURES = 10;

    private final ListAppender<ILoggingEvent> appender = new ListAppender<>();

    @BeforeEach
    void setUp() {
        appender.start();
        summaryLogger().addAppender(appender);
    }

    @AfterEach
    void tearDown() {
        summaryLogger().detachAppender(appender);
        appender.stop();
    }

    @Test
    void reportsTheSeedOfEachFailedTest() {
        execute(FailingTest.class);

        assertThat(summary())
                .startsWith("3 tests failed. A failure can be reproduced "
                        + "by annotating the test method with @Seed:")
                .contains("  @Seed(12345L) SeedSummaryTest$FailingTest.failingTest (seed source: @Seed)")
                // the rest of the invocation display name is rendered by JUnit
                .contains("  @Seed(12345L) SeedSummaryTest$FailingTest.failingParameterizedTest [1]")
                .contains("  @Seed(12345L) SeedSummaryTest$FailingTest.failingParameterizedTest [2]");
    }

    @Test
    void reportsTheSeedSourceOfEachFailedTest() {
        execute(ConcurrentlyFailingTest.class);

        assertThat(summary().lines().filter(line -> line.contains("@Seed(")))
                .isNotEmpty()
                .allMatch(line -> line.endsWith("(seed source: random seed)"));
    }

    @Test
    void doesNotReportPassingOrAbortedTests() {
        execute(FailingTest.class);

        assertThat(summary())
                .doesNotContain("passingTest")
                .doesNotContain("abortedTest");
    }

    @Test
    void appendsTheDisplayNameOfTemplateInvocationsOnly() {
        execute(DisplayNameTest.class);

        assertThat(summary())
                .contains("  @Seed(12345L) SeedSummaryTest$DisplayNameTest.failingTestWithDisplayName (seed source")
                .contains("  @Seed(12345L) SeedSummaryTest$DisplayNameTest.failingRepeatedTest repetition 1 of 2 (seed source")
                .contains("  @Seed(12345L) SeedSummaryTest$DisplayNameTest.failingRepeatedTest repetition 2 of 2 (seed source")
                .doesNotContain("custom display name");
    }

    @Test
    void reportsNothingIfNoTestFailed() {
        execute(PassingTest.class);

        assertThat(appender.list).isEmpty();
    }

    @Test
    void reportsASingleFailedTestInTheSingular() {
        execute(SingleFailureTest.class);

        assertThat(summary()).startsWith("1 test failed.");
    }

    @Test
    void reportsEachTestRunSeparately() {
        execute(SingleFailureTest.class);
        execute(FailingTest.class);

        assertThat(appender.list).hasSize(2);

        assertThat(appender.list.get(0).getFormattedMessage())
                .startsWith("1 test failed.")
                .contains("SingleFailureTest.failingTest");

        // the seeds collected by the first run must not leak into the second
        assertThat(appender.list.get(1).getFormattedMessage())
                .startsWith("3 tests failed.")
                .doesNotContain("SingleFailureTest");
    }

    @Test
    void collectsSeedsOfTestsExecutedInParallel() {
        execute(ConcurrentlyFailingTest.class);

        final String summary = summary();

        assertThat(summary).startsWith(CONCURRENT_FAILURES + " tests failed.");
        assertThat(summary.lines().filter(line -> line.contains("@Seed(")))
                .hasSize(CONCURRENT_FAILURES)
                .allMatch(line -> line.contains("ConcurrentlyFailingTest.failingTest"));
    }

    private String summary() {
        assertThat(appender.list).hasSize(1);
        return appender.list.get(0).getFormattedMessage();
    }

    private static Logger summaryLogger() {
        return (Logger) LoggerFactory.getLogger(SUMMARY_LOGGER);
    }

    private static void execute(final Class<?> testClass) {
        EngineTestKit.engine("junit-jupiter")
                .selectors(selectClass(testClass))
                .execute();
    }

    @ExtendWith(InstancioExtension.class)
    @SuppressWarnings(Sonar.ADD_ASSERTION)
    static class FailingTest {

        @Test
        void passingTest() {
            // no-op
        }

        @Test
        void abortedTest() {
            assumeTrue(false, "aborted on purpose");
        }

        @Test
        @Seed(SEED)
        void failingTest() {
            fail("failed on purpose");
        }

        @ParameterizedTest
        @ValueSource(strings = {"foo", "bar"})
        @Seed(SEED)
        void failingParameterizedTest(final String value) {
            fail("failed on purpose: " + value);
        }
    }

    @ExtendWith(InstancioExtension.class)
    static class DisplayNameTest {

        @Test
        @Seed(SEED)
        @DisplayName("custom display name")
        void failingTestWithDisplayName() {
            fail("failed on purpose");
        }

        @RepeatedTest(2)
        @Seed(SEED)
        void failingRepeatedTest() {
            fail("failed on purpose");
        }
    }

    @ExtendWith(InstancioExtension.class)
    @SuppressWarnings(Sonar.ADD_ASSERTION)
    static class PassingTest {

        @Test
        void passingTest() {
            // no-op
        }
    }

    @ExtendWith(InstancioExtension.class)
    static class SingleFailureTest {

        @Test
        @Seed(SEED)
        void failingTest() {
            fail("failed on purpose");
        }
    }

    @ExtendWith(InstancioExtension.class)
    @Execution(ExecutionMode.CONCURRENT)
    static class ConcurrentlyFailingTest {

        @ParameterizedTest
        @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10})
        void failingTest(final int value) {
            fail("failed on purpose: " + value);
        }
    }
}
