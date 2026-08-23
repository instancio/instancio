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

import org.instancio.junit.internal.ExtensionSupport;
import org.instancio.junit.internal.Fail;
import org.instancio.junit.internal.GivenAnnotations;
import org.instancio.junit.internal.ObjectCreator;
import org.instancio.settings.Settings;
import org.instancio.support.DefaultRandom;
import org.instancio.support.InternalTestContext;
import org.instancio.support.Log;
import org.instancio.support.ThreadLocalTestContext;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.TestInstantiationAwareExtension;
import org.junit.platform.commons.support.HierarchyTraversalMode;
import org.junit.platform.commons.support.ReflectionSupport;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * The Instancio JUnit extension adds support for additional
 * features when using Instancio with the JUnit framework:
 *
 * <ul>
 *   <li>reporting the seed value to allow reproducing failed tests</li>
 *   <li>injecting {@link Settings} using {@link WithSettings @WithSettings} annotation</li>
 *   <li>injecting fields and method parameters using {@link Given @Given} annotation</li>
 * </ul>
 *
 * <h2>Reproducing failed tests</h2>
 *
 * <p>The extension generates a seed for each test method. When a test fails,
 * the extension reports this seed in the output. Using the {@link Seed @Seed}
 * annotation, the test can be re-run with the reported seed to reproduce
 * the data that caused the failure.
 *
 * <p>For example, given the following test class:
 *
 * <pre>{@code
 * @ExtendWith(InstancioExtension.class)
 * class ExampleTest {
 *
 *     @Test
 *     void verifyPerson() {
 *         Person person = Instancio.create(Person.class);
 *         // some test code...
 *         // ... some assertion fails
 *     }
 * }
 * }</pre>
 *
 * <p>The failed test will report the seed value that was used, for example:
 *
 * <p><b>{@code "Test method 'verifyPerson' failed with seed: 12345"}</b>
 *
 * <p>Subsequently, the failing test can be reproduced by annotating the test method
 * with the {@link Seed} annotation:
 *
 * <pre>{@code
 * @Test
 * @Seed(12345) // will reproduce previously generated data
 * void verifyPerson() {
 *     Person person = Instancio.create(Person.class);
 *     // snip...
 * }
 * }</pre>
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

    private final ThreadLocalTestContext threadLocalTestContext;

    /**
     * Default constructor; required for JUnit extensions.
     */
    @SuppressWarnings("unused")
    public InstancioExtension() {
        threadLocalTestContext = ThreadLocalTestContext.getInstance();
    }

    // Constructor used by unit test only
    InstancioExtension(final ThreadLocalTestContext threadLocalTestContext) {
        this.threadLocalTestContext = threadLocalTestContext;
    }

    /**
     * Opting in to the test-method scope is recommended by JUnit for forward
     * compatibility, as it is due to become the default behaviour.
     */
    @Override
    public TestInstantiationAwareExtension.ExtensionContextScope getTestInstantiationExtensionContextScope(
            final ExtensionContext rootContext) {

        return TestInstantiationAwareExtension.ExtensionContextScope.TEST_METHOD;
    }

    @Override
    public void beforeEach(final ExtensionContext context) throws IllegalAccessException {
        threadLocalTestContext.set(ExtensionSupport.createTestContext(context));

        for (Object testInstance : context.getRequiredTestInstances().getAllInstances()) {
            populateTestInstanceFields(testInstance);
        }
    }

    private void populateTestInstanceFields(final Object testInstance) throws IllegalAccessException {
        final List<Field> fields = ReflectionSupport.findFields(
                testInstance.getClass(), GivenAnnotations::isAnnotated, HierarchyTraversalMode.TOP_DOWN);

        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) {
                throw Fail.givenOnStaticField(field);
            }

            final Object fieldValue = createObject(field, field.getGenericType());

            ReflectionSupport.makeAccessible(field).set(testInstance, fieldValue);
        }
    }

    @Override
    public void afterEach(final ExtensionContext context) {
        threadLocalTestContext.remove();
    }

    @Override
    public void afterTestExecution(final ExtensionContext context) {
        if (context.getExecutionException().isPresent()) {
            final Method testMethod = context.getRequiredTestMethod();

            final DefaultRandom random = requireNonNull(threadLocalTestContext.get()).getRandom();

            // For @Test, @RepeatedTest, and @ParameterizedTest, each failed sample
            // reports its own seed. Adding the @Seed annotation to a @ParameterizedTest
            // ensures the same random data is generated for each run.
            final String seedMsg = String.format("Test method '%s' failed with seed: %d (seed source: %s)\n",
                    testMethod.getName(), random.getSeed(), random.getSource().getDescription());

            context.publishReportEntry("Instancio", seedMsg);
            Log.msg(Log.Category.TEST_FAILURE_SEED, seedMsg);
        }
    }

    /**
     * For methods, JUnit invokes (1) beforeEach(), (2) resolveParameter()
     * For constructors, the order is reverse. As a result, when
     * resolveParameter() is called, the setup logic hasn't been run yet.
     * For this reason, constructor parameters are not supported.
     */
    @Override
    public boolean supportsParameter(
            final ParameterContext parameterContext,
            final ExtensionContext extensionContext) {

        return !(parameterContext.getDeclaringExecutable() instanceof Constructor)
                && GivenAnnotations.isAnnotated(parameterContext.getParameter());
    }

    @Override
    public Object resolveParameter(
            final ParameterContext parameterContext,
            final ExtensionContext extensionContext) {

        final Parameter parameter = parameterContext.getParameter();

        return createObject(parameter, parameter.getParameterizedType());
    }

    private Object createObject(final AnnotatedElement element, final Type targetType) {
        final InternalTestContext internalTestContext = requireNonNull(threadLocalTestContext.get());

        return new ObjectCreator(internalTestContext.getSettings(), internalTestContext.getRandom())
                .createObject(element, targetType);
    }
}
