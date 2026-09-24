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
package org.instancio.junit.internal;

import org.instancio.documentation.InternalApi;
import org.instancio.support.Log;
import org.instancio.support.Seeds;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

import static org.instancio.internal.util.Constants.NL;

/**
 * Collects the seeds of failed tests and logs them as a summary
 * when the root context's store is closed at the end of the test run.
 */
@InternalApi
public final class SeedSummary implements AutoCloseable {

    private static final ExtensionContext.Namespace NAMESPACE =
            ExtensionContext.Namespace.create(SeedSummary.class);

    private final Queue<FailedTest> failedTests = new ConcurrentLinkedQueue<>();

    private SeedSummary() {
        // created via getInstance()
    }

    public static SeedSummary getInstance(final ExtensionContext context) {
        // computeIfAbsent(Class) would instantiate via reflection, which fails
        // on the module path since this package is not exported
        return context.getRoot().getStore(NAMESPACE)
                .computeIfAbsent(SeedSummary.class, key -> new SeedSummary(), SeedSummary.class);
    }

    public void add(final String testName, final long seed, final Seeds.Source seedSource) {
        failedTests.add(new FailedTest(testName, seed, seedSource));
    }

    @Override
    public void close() {
        if (!failedTests.isEmpty()) {
            Log.msg(Log.Category.TEST_FAILURE_SEED, buildSummary());
        }
    }

    private String buildSummary() {
        final int count = failedTests.size();
        final String header = count + (count == 1 ? " test" : " tests")
                + " failed. A failure can be reproduced by annotating the test method with @Seed:";

        return failedTests.stream()
                .map(test -> "  @Seed(%dL) %s (seed source: %s)".formatted(
                        test.seed(), test.testName(), test.seedSource().description()))
                .collect(Collectors.joining(NL, header + NL + NL, NL));
    }

    private record FailedTest(String testName, long seed, Seeds.Source seedSource) {
    }
}
