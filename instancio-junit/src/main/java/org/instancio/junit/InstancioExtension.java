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
package org.instancio.junit;

import org.instancio.internal.ThreadLocalRandomProvider;
import org.instancio.internal.random.RandomProvider;
import org.instancio.util.SeedUtil;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * Instancio JUnit extension.
 * <p>
 * Adds support for reporting Random Number Generator's seed value
 * in case of a test failure. This allows the failed test to be reproduced
 * by re-running it with the same seed value.
 * <p>
 * For example, given the following test class:
 *
 * <pre class="code"><code class="java">
 *     <b>&#064;ExtendWith(InstancioExtension.class)</b>
 *     class ExampleTest {
 *
 *         &#064;Test
 *         void verifyPerson() {
 *             Person person = Instancio.create(Person.class);
 *             // some test code...
 *             // ... some assertion fails
 *         }
 *     }
 * </code></pre>
 * <p>
 * The failed test will report the seed value that was used, for example:
 * <b>{@code "Test method 'verifyPerson' failed with seed: 12345"}</b>.
 * <p>
 * Subsequently, the failing test can be reproduced by annotating the test method
 * with the {@link Seed} annotation:
 *
 * <pre class="code"><code class="java">
 *         &#064;Test
 *         <b>&#064;Seed(12345)</b> // will reproduce previously generated data
 *         void verifyPerson() {
 *             Person person = Instancio.create(Person.class);
 *             // snip...
 *         }
 * </code></pre>
 */
public class InstancioExtension implements BeforeEachCallback, AfterEachCallback, AfterTestExecutionCallback {

    private static final Logger LOG = LoggerFactory.getLogger(InstancioExtension.class);
    private final ThreadLocalRandomProvider threadLocalRandomProvider;

    /**
     * Default constructor; required for JUnit extensions.
     */
    @SuppressWarnings("unused")
    public InstancioExtension() {
        threadLocalRandomProvider = ThreadLocalRandomProvider.getInstance();
    }

    // used by unit test only
    @SuppressWarnings("unused")
    InstancioExtension(final ThreadLocalRandomProvider threadLocal) {
        threadLocalRandomProvider = threadLocal;
    }

    @Override
    public void beforeEach(final ExtensionContext context) {
        final Method method = context.getRequiredTestMethod();
        final Seed seedAnnotation = method.getAnnotation(Seed.class);
        final int seed = seedAnnotation == null
                ? SeedUtil.randomSeed()
                : seedAnnotation.value();

        threadLocalRandomProvider.set(new RandomProvider(seed));
    }

    @Override
    public void afterEach(final ExtensionContext context) {
        threadLocalRandomProvider.remove();
    }

    @Override
    public void afterTestExecution(final ExtensionContext context) {
        if (context.getExecutionException().isPresent()) {
            final Method testMethod = context.getRequiredTestMethod();
            final int seed = threadLocalRandomProvider.get().getSeed();
            final String msg = String.format("Test method '%s' failed with seed: %d%n", testMethod.getName(), seed);
            context.publishReportEntry("Instancio", msg);
            LOG.debug(msg);
        }
    }

}

