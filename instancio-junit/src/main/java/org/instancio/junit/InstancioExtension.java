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
import org.instancio.junit.internal.InstancioSourceState;
import org.instancio.junit.internal.ObjectCreator;
import org.instancio.junit.internal.ReflectionUtils;
import org.instancio.settings.Settings;
import org.instancio.support.DefaultRandom;
import org.instancio.support.ThreadLocalRandom;
import org.instancio.support.ThreadLocalSettings;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.instancio.junit.internal.Constants.INSTANCIO_NAMESPACE;
import static org.instancio.junit.internal.Constants.INSTANCIO_SOURCE_STATE;

/**
 * The Instancio JUnit extension adds support for additional
 * features when using Instancio with JUnit Jupiter:
 *
 * <ul>
 *   <li>reporting the seed value to allow reproducing failed tests</li>
 *   <li>injecting {@link Settings} using {@link WithSettings @WithSettings} annotation</li>
 *   <li>generating parameterized test arguments using {@link InstancioSource @InstancioSource}</li>
 *   <li>injecting fields and method parameters using {@link Given @Given}  aannotation</li>
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

    private static final Logger LOG = LoggerFactory.getLogger(InstancioExtension.class);
    private static final String ELEMENT_ANNOTATIONS = "elementAnnotations";

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
    public void beforeAll(final ExtensionContext context) {
        final List<Class<?>> testClasses = new ArrayList<>(context.getEnclosingTestClasses());
        testClasses.add(context.getRequiredTestClass());
        testClasses.forEach(testClass ->
                context.getStore(INSTANCIO_NAMESPACE).put(testClass, new FieldAnnotationMap(testClass))
        );
    }

    @Override
    public void beforeEach(final ExtensionContext context) throws IllegalAccessException {
        ExtensionSupport.processAnnotations(context, threadLocalRandom, threadLocalSettings);

        for (Object testInstance : context.getRequiredTestInstances().getAllInstances()) {
            populateTestInstanceFields(testInstance, context);
        }
    }

    @SuppressWarnings(Sonar.ACCESSIBILITY_UPDATE_SHOULD_BE_REMOVED)
    private void populateTestInstanceFields(final Object testInstance, final ExtensionContext context) throws IllegalAccessException {
        final Class<?> testClass = testInstance.getClass();
        final FieldAnnotationMap annotationMap = context.getStore(INSTANCIO_NAMESPACE)
                .get(testClass, FieldAnnotationMap.class);

        for (Field field : testClass.getDeclaredFields()) {
            final List<Annotation> annotations = annotationMap.get(field);

            if (!containsAnnotation(annotations, Given.class)) {
                continue;
            }
            if (Modifier.isStatic(field.getModifiers())) {
                throw Fail.withUsageError("@Given annotation is not supported for static fields");
            }

            final ElementAnnotations elementAnnotations = new ElementAnnotations(annotations);
            final Object fieldValue = new ObjectCreator(threadLocalSettings.get(), threadLocalRandom.get())
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
        threadLocalRandom.remove();
        threadLocalSettings.remove();
        context.getStore(INSTANCIO_NAMESPACE).remove(ELEMENT_ANNOTATIONS, ElementAnnotations.class);

        final InstancioSourceState instancioSourceState = context.getStore(INSTANCIO_NAMESPACE)
                .get(INSTANCIO_SOURCE_STATE, InstancioSourceState.class);

        if (instancioSourceState != null && instancioSourceState.allSamplesGenerated()) {
            context.getStore(INSTANCIO_NAMESPACE).remove(INSTANCIO_SOURCE_STATE);
        }
    }

    @Override
    public void afterTestExecution(final ExtensionContext context) {
        if (context.getExecutionException().isPresent()) {
            final Method testMethod = context.getRequiredTestMethod();

            // Should be safe to cast since we don't expect any other implementations of Random.
            final DefaultRandom random = (DefaultRandom) threadLocalRandom.get();

            final InstancioSourceState instancioSourceState = context.getStore(INSTANCIO_NAMESPACE)
                    .get(INSTANCIO_SOURCE_STATE, InstancioSourceState.class);

            // When using @InstancioSource, all failed samples will report the same seed value.
            // This allows reproducing the entire dataset for all samples using the @Seed annotation.
            //
            // For @Test, @RepeatedTest, and @ParameterizedTest without @InstancioSource,
            // each failed sample reports its own seed. Adding the @Seed annotation to a
            // @ParameterizedTest (without @InstancioSource) ensures the same random data is
            // generated for each run.
            final long seed = instancioSourceState != null
                    ? instancioSourceState.getInitialSeed()
                    : random.getSeed();

            final String seedMsg = String.format("Test method '%s' failed with seed: %d (seed source: %s)%n",
                    testMethod.getName(), seed, random.getSource().getDescription());

            context.publishReportEntry("Instancio", seedMsg);
            LOG.error(seedMsg);
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

        final boolean supportsParameter = containsAnnotation(annotations, Given.class) &&
                // Exclude InstancioSource methods (which can generate arguments) to avoid the
                // "Discovered multiple competing ParameterResolvers for parameter" error
                extensionContext.getTestMethod()
                        .map(m -> m.getDeclaredAnnotation(InstancioSource.class))
                        .isEmpty();

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

        return new ObjectCreator(threadLocalSettings.get(), threadLocalRandom.get())
                .createObject(parameter, targetType, elementAnnotations);
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
