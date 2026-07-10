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

import org.instancio.internal.util.Fail;
import org.instancio.internal.util.Sonar;
import org.instancio.junit.internal.ElementAnnotations;
import org.instancio.junit.internal.ExtensionSupport;
import org.instancio.junit.internal.FieldAnnotationMap;
import org.instancio.junit.internal.ObjectCreator;
import org.instancio.junit.internal.ReflectionUtils;
import org.instancio.settings.Settings;
import org.instancio.support.DefaultRandom;
import org.instancio.support.InternalTestContext;
import org.instancio.support.Log;
import org.instancio.support.ThreadLocalTestContext;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.instancio.junit.internal.Constants.INSTANCIO_NAMESPACE;

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
@SuppressWarnings("PMD.ExcessiveImports")
public class InstancioExtension implements
        BeforeAllCallback,
        BeforeEachCallback,
        AfterAllCallback,
        AfterEachCallback,
        AfterTestExecutionCallback,
        ParameterResolver {

    private static final String ELEMENT_ANNOTATIONS = "elementAnnotations";

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

    @Override
    public void beforeAll(final ExtensionContext context) {
        final List<Class<?>> testClasses = new ArrayList<>(context.getEnclosingTestClasses());
        testClasses.add(context.getRequiredTestClass());
        testClasses.forEach(testClass ->
                context.getStore(INSTANCIO_NAMESPACE).put(testClass, new FieldAnnotationMap(testClass))
        );
    }

    @Override
    public void beforeEach(final ExtensionContext context) throws IllegalAccessException {
        ExtensionSupport.processAnnotations(context, threadLocalTestContext);

        for (Object testInstance : context.getRequiredTestInstances().getAllInstances()) {
            populateTestInstanceFields(testInstance, context);
        }
    }

    @SuppressWarnings(Sonar.ACCESSIBILITY_UPDATE_SHOULD_BE_REMOVED)
    private void populateTestInstanceFields(final Object testInstance, final ExtensionContext context) throws IllegalAccessException {
        final Class<?> testClass = testInstance.getClass();
        final FieldAnnotationMap annotationMap = context.getStore(INSTANCIO_NAMESPACE)
                .get(testClass, FieldAnnotationMap.class);

        if (annotationMap == null) {
            return;
        }

        for (Field field : testClass.getDeclaredFields()) {
            final List<Annotation> annotations = annotationMap.get(field);

            if (!containsAnnotation(annotations, Given.class)) {
                continue;
            }
            if (Modifier.isStatic(field.getModifiers())) {
                throw Fail.withUsageError("@Given annotation is not supported for static fields");
            }

            final ElementAnnotations elementAnnotations = new ElementAnnotations(annotations);
            final InternalTestContext internalTestContext = requireNonNull(threadLocalTestContext.get());
            final Object fieldValue = new ObjectCreator(internalTestContext.getSettings(), internalTestContext.getRandom())
                    .createObject(field, field.getGenericType(), elementAnnotations);

            ReflectionUtils.setAccessible(field).set(testInstance, fieldValue);
        }
    }

    @Override
    public void afterAll(final ExtensionContext context) {
        final List<Class<?>> testClasses = new ArrayList<>(context.getEnclosingTestClasses());
        testClasses.add(context.getRequiredTestClass());
        testClasses.forEach(testClass ->
                context.getStore(INSTANCIO_NAMESPACE).remove(testClass, FieldAnnotationMap.class)
        );
    }

    @Override
    public void afterEach(final ExtensionContext context) {
        threadLocalTestContext.remove();
        context.getStore(INSTANCIO_NAMESPACE).remove(ELEMENT_ANNOTATIONS, ElementAnnotations.class);
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

        if (parameterContext.getDeclaringExecutable() instanceof Constructor) {
            return false;
        }

        final Parameter parameter = parameterContext.getParameter();
        final List<Annotation> annotations = ReflectionUtils.collectionAnnotations(parameter);

        final boolean supportsParameter = containsAnnotation(annotations, Given.class);

        if (supportsParameter) {
            extensionContext.getStore(INSTANCIO_NAMESPACE)
                    .put(ELEMENT_ANNOTATIONS, new ElementAnnotations(annotations));
        }
        return supportsParameter;
    }

    @Override
    public Object resolveParameter(
            final ParameterContext parameterContext,
            final ExtensionContext extensionContext) {

        final Parameter parameter = parameterContext.getParameter();

        final ElementAnnotations elementAnnotations = extensionContext.getStore(INSTANCIO_NAMESPACE)
                .get(ELEMENT_ANNOTATIONS, ElementAnnotations.class);

        final Type targetType = parameter.getParameterizedType();

        final InternalTestContext internalTestContext = requireNonNull(threadLocalTestContext.get());
        return new ObjectCreator(internalTestContext.getSettings(), internalTestContext.getRandom())
                .createObject(parameter, targetType, requireNonNull(elementAnnotations));
    }

    private static boolean containsAnnotation(
            final List<Annotation> annotations,
            final Class<? extends Annotation> annotationType) {

        for (Annotation a : annotations) {
            if (a.annotationType() == annotationType) {
                return true;
            }
        }
        return false;
    }
}
