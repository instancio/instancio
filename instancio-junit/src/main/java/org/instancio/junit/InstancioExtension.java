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

import org.instancio.Instancio;
import org.instancio.Random;
import org.instancio.internal.util.Fail;
import org.instancio.internal.util.ReflectionUtils;
import org.instancio.junit.internal.ExtensionObjectFactory;
import org.instancio.junit.internal.ExtensionSupport;
import org.instancio.settings.Settings;
import org.instancio.support.DefaultRandom;
import org.instancio.support.ThreadLocalRandom;
import org.instancio.support.ThreadLocalSettings;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Optional;

/**
 * The Instancio JUnit extension adds support for additional
 * features when using Instancio with JUnit Jupiter:
 *
 * <ul>
 *   <li>reporting the seed value to allow reproducing failed tests</li>
 *   <li>injecting {@link Settings} using {@link WithSettings @WithSettings} annotation</li>
 *   <li>generating parameterized test arguments using {@link InstancioSource @InstancioSource}</li>
 * </ul>
 *
 * <h2>Reproducing failed tests</h2>
 *
 * <p>The extension generates a seed for each test method. When a test fails,
 * the extension reports this seed in the output. Using the {@link Seed}
 * annotation, the test can be re-run with the reported seed to reproduce
 * the data that caused the failure.
 *
 * <p>For example, given the following test class:
 *
 * <pre><code>
 * &#064;ExtendWith(InstancioExtension.class)
 * class ExampleTest {
 *
 *     &#064;Test
 *     void verifyPerson() {
 *         Person person = Instancio.create(Person.class);
 *         // some test code...
 *         // ... some assertion fails
 *     }
 * }
 * </code></pre>
 *
 * <p>The failed test will report the seed value that was used, for example:
 *
 * <p><b>{@code "Test method 'verifyPerson' failed with seed: 12345"}</b>
 *
 * <p>Subsequently, the failing test can be reproduced by annotating the test method
 * with the {@link Seed} annotation:
 *
 * <pre><code>
 * &#064;Test
 * &#064;Seed(12345) // will reproduce previously generated data
 * void verifyPerson() {
 *     Person person = Instancio.create(Person.class);
 *     // snip...
 * }
 * </code></pre>
 *
 * <p>See the
 * <a href="https://www.instancio.org/user-guide/#junit-jupiter-integration">user guide</a>
 * for more details.
 *
 * @since 1.1.0
 */
public class InstancioExtension implements
        BeforeEachCallback,
        AfterEachCallback,
        AfterTestExecutionCallback,
        ParameterResolver {

    private static final Logger LOG = LoggerFactory.getLogger(InstancioExtension.class);
    private final ThreadLocalRandom threadLocalRandom;
    private final ThreadLocalSettings threadLocalSettings;

    /**
     * Default constructor; required for JUnit extensions.
     */
    @SuppressWarnings("unused")
    public InstancioExtension() {
        threadLocalRandom = ThreadLocalRandom.getInstance();
        threadLocalSettings = ThreadLocalSettings.getInstance();
    }

    // Constructor used by unit test only
    InstancioExtension(final ThreadLocalRandom threadLocalRandom,
                       final ThreadLocalSettings threadLocalSettings) {
        this.threadLocalRandom = threadLocalRandom;
        this.threadLocalSettings = threadLocalSettings;
    }

    @Override
    public void beforeEach(final ExtensionContext context) throws IllegalAccessException {
        ExtensionSupport.processAnnotations(context, threadLocalRandom, threadLocalSettings);

        final Object testInstance = context.getRequiredTestInstance();

        for (Field field : testInstance.getClass().getDeclaredFields()) {
            if (field.getDeclaredAnnotation(Generate.class) == null) {
                continue;
            }
            if (Modifier.isStatic(field.getModifiers())) {
                throw Fail.withUsageError("@Generated annotation is not supported for static fields");
            }
            setGeneratedFieldValue(testInstance, field);
        }
    }

    @Override
    public void afterEach(final ExtensionContext context) {
        threadLocalRandom.remove();
        threadLocalSettings.remove();
    }

    @Override
    public void afterTestExecution(final ExtensionContext context) {
        if (context.getExecutionException().isPresent()) {
            final Optional<Method> testMethod = context.getTestMethod();
            if (!testMethod.isPresent()) {
                return;
            }

            // Should be safe to case. We don't  expect any other implementations of Random.
            final DefaultRandom random = (DefaultRandom) threadLocalRandom.get();
            final long seed = random.getSeed();

            final String seedMsg = String.format("Test method '%s' failed with seed: %d (seed source: %s)%n",
                    testMethod.get().getName(), seed, random.getSource().getDescription());

            context.publishReportEntry("Instancio", seedMsg);
            LOG.error(seedMsg);
        }
    }

    @Override
    public boolean supportsParameter(
            final ParameterContext parameterContext,
            final ExtensionContext extensionContext) {

        return parameterContext.isAnnotated(Generate.class) &&
                // Exclude InstancioSource methods (which can generate arguments) to avoid the
                // "Discovered multiple competing ParameterResolvers for parameter" error
                !extensionContext.getTestMethod()
                        .map(m -> m.getDeclaredAnnotation(InstancioSource.class))
                        .isPresent();

    }

    @Override
    public Object resolveParameter(
            final ParameterContext parameterContext,
            final ExtensionContext extensionContext) {

        final Parameter parameter = parameterContext.getParameter();
        return Instancio.create(parameter::getParameterizedType);
    }

    private static void setGeneratedFieldValue(final Object testInstance, final Field field)
            throws IllegalAccessException {

        final Random random = ThreadLocalRandom.getInstance().get();
        final Settings settings = ThreadLocalSettings.getInstance().get();

        final Object fieldValue = ExtensionObjectFactory.createObject(field.getGenericType(), random, settings);
        ReflectionUtils.setAccessible(field).set(testInstance, fieldValue); //NOSONAR
    }
}
