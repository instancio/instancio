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

import org.instancio.exception.InstancioException;
import org.instancio.internal.ThreadLocalRandomProvider;
import org.instancio.internal.ThreadLocalSettings;
import org.instancio.internal.random.RandomProviderImpl;
import org.instancio.settings.Settings;
import org.instancio.util.ReflectionUtils;
import org.instancio.util.SeedUtil;
import org.instancio.util.Sonar;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.joining;

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
    private final ThreadLocalSettings threadLocalSettings;

    /**
     * Default constructor; required for JUnit extensions.
     */
    @SuppressWarnings("unused")
    public InstancioExtension() {
        threadLocalRandomProvider = ThreadLocalRandomProvider.getInstance();
        threadLocalSettings = ThreadLocalSettings.getInstance();
    }

    // used by unit test only
    @SuppressWarnings("unused")
    InstancioExtension(final ThreadLocalRandomProvider threadLocalRandomProvider,
                       final ThreadLocalSettings threadLocalSettings) {
        this.threadLocalRandomProvider = threadLocalRandomProvider;
        this.threadLocalSettings = threadLocalSettings;
    }

    @Override
    public void beforeEach(final ExtensionContext context) throws Exception {
        processWithSettingsAnnotation(context);
        processSeedAnnotation(context);
    }

    private void processSeedAnnotation(final ExtensionContext context) {
        final Optional<Method> testMethod = context.getTestMethod();
        if (testMethod.isPresent()) {
            final Seed seedAnnotation = testMethod.get().getAnnotation(Seed.class);
            final int seed = seedAnnotation == null
                    ? SeedUtil.randomSeed()
                    : seedAnnotation.value();

            threadLocalRandomProvider.set(new RandomProviderImpl(seed));
        }
    }

    @SuppressWarnings(Sonar.ACCESSIBILITY_UPDATE_SHOULD_BE_REMOVED)
    private void processWithSettingsAnnotation(final ExtensionContext context) throws IllegalAccessException {
        final Optional<Class<?>> testClass = context.getTestClass();
        final Optional<Object> testInstance = context.getTestInstance();
        if (!testClass.isPresent() || !testInstance.isPresent()) {
            return;
        }

        final List<Field> fields = ReflectionUtils.getAnnotatedFields(testClass.get(), WithSettings.class);

        if (fields.size() > 1) {
            throw new InstancioException("\nFound more than one field annotated '@WithSettings':\n\n"
                    + fields.stream().map(Field::toString).collect(joining("\n")));
        } else if (fields.size() == 1) {
            final Field field = fields.get(0);
            field.setAccessible(true);
            final Object obj = field.get(testInstance.get());
            if (obj == null) {
                throw new InstancioException("\n@WithSettings must be annotated on a non-null field.");
            }
            if (!(obj instanceof Settings)) {
                throw new InstancioException("\n@WithSettings must be annotated on a Settings field." +
                        "\n\nFound annotation on: " + field);
            }
            final Settings settings = (Settings) obj;
            threadLocalSettings.set(settings);
        }
    }

    @Override
    public void afterEach(final ExtensionContext context) {
        threadLocalRandomProvider.remove();
        threadLocalSettings.remove();
    }

    @Override
    public void afterTestExecution(final ExtensionContext context) {
        if (context.getExecutionException().isPresent()) {
            final Optional<Method> testMethod = context.getTestMethod();
            if (!testMethod.isPresent()) {
                return;
            }

            final int seed = threadLocalRandomProvider.get().getSeed();
            final String msg = String.format("Test method '%s' failed with seed: %d%n",
                    testMethod.get().getName(), seed);

            context.publishReportEntry("Instancio", msg);
            LOG.debug(msg);
        }
    }
}
