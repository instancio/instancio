/*
 *  Copyright 2022-2024 the original author or authors.
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

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.instancio.Random;
import org.instancio.exception.InstancioApiException;
import org.instancio.junit.internal.ElementAnnotations;
import org.instancio.junit.internal.FieldAnnotationMap;
import org.instancio.settings.Settings;
import org.instancio.support.DefaultRandom;
import org.instancio.support.Seeds;
import org.instancio.support.ThreadLocalRandom;
import org.instancio.support.ThreadLocalSettings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.create;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InstancioExtensionTest {

    private static final long SEED_ANNOTATION_VALUE = -2345212;
    private static final String METHOD_WITH_SEED_ANNOTATION = "methodWithSeedAnnotation";
    private static final Settings SETTINGS = Settings.defaults();

    @Mock
    private ThreadLocalRandom threadLocalRandom;

    @Mock
    private ThreadLocalSettings threadLocalSettings;

    @Mock
    private ExtensionContext context;

    @Captor
    private ArgumentCaptor<Random> randomCaptor;

    @Captor
    private ArgumentCaptor<Settings> settingsCaptor;

    private InstancioExtension extension;

    @BeforeEach
    void setUp() {
        extension = new InstancioExtension(threadLocalRandom, threadLocalSettings);
    }

    @Test
    void supportsParameterReturnsShouldForConstructor() throws NoSuchMethodException {
        final ParameterContext parameterContext = mock(ParameterContext.class);
        final Constructor<DummyTest> constructor = DummyTest.class.getConstructor();
        doReturn(constructor).when(parameterContext).getDeclaringExecutable();

        final boolean result = extension.supportsParameter(parameterContext, null);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Verify @Seed annotation's value is passed to thread local")
    void beforeEachWithSeedAnnotation() throws Exception {
        final Method method = DummyTest.class.getDeclaredMethod(METHOD_WITH_SEED_ANNOTATION);
        doReturn(Optional.of(method)).when(context).getTestMethod();
        doReturn(DummyTest.class).when(context).getRequiredTestClass();

        final ExtensionContext.Store store = mock(ExtensionContext.Store.class);
        doReturn(store).when(context).getStore(create("org.instancio"));
        doReturn(new FieldAnnotationMap(DummyTest.class))
                .when(store)
                .get("annotationMap", FieldAnnotationMap.class);

        // Method under test
        extension.beforeEach(context);

        verify(threadLocalRandom).set(randomCaptor.capture());
        final DefaultRandom random = (DefaultRandom) randomCaptor.getValue();
        assertThat(random.getSeed()).isEqualTo(SEED_ANNOTATION_VALUE);
        assertThat(random.getSource()).isEqualTo(Seeds.Source.SEED_ANNOTATION);
    }

    @Test
    @DisplayName("Verify annotated Settings are passed to thread local")
    void beforeEachWithSettingsAnnotation() throws IllegalAccessException {
        doReturn(Optional.of(DummyTest.class)).when(context).getTestClass();
        doReturn(Optional.of(new DummyTest())).when(context).getTestInstance();
        doReturn(DummyTest.class).when(context).getRequiredTestClass();

        final ExtensionContext.Store store = mock(ExtensionContext.Store.class);
        doReturn(store).when(context).getStore(create("org.instancio"));
        doReturn(new FieldAnnotationMap(DummyTest.class))
                .when(store)
                .get("annotationMap", FieldAnnotationMap.class);

        // Method under test
        extension.beforeEach(context);

        verify(threadLocalSettings).set(settingsCaptor.capture());
        assertThat(settingsCaptor.getValue()).isSameAs(SETTINGS);
    }

    @Test
    @DisplayName("Verify exception is thrown if test class has more than one field annotated @WithSettings")
    void moreThanOneFieldAnnotatedWithSettings() {
        doReturn(Optional.of(DummyWithTwoSettingsTest.class)).when(context).getTestClass();

        final String expectedMsg = """
                Error running test

                Cause:
                 -> Found more than one field annotated '@WithSettings'

                    (1) private final org.instancio.settings.Settings org.instancio.junit.InstancioExtensionTest$DummyWithTwoSettingsTest.settings1
                    (2) private final org.instancio.settings.Settings org.instancio.junit.InstancioExtensionTest$DummyWithTwoSettingsTest.settings2

                Only one annotated Settings field is expected
                """;

        assertApiExceptionWithMessage(() -> extension.beforeEach(context), expectedMsg);
    }

    @Test
    @DisplayName("Verify exception is thrown if @WithSettings is not on Settings field")
    void withSettingsOnWrongFieldType() {
        doReturn(Optional.of(DummyWithSettingsOnWrongFieldTypeTest.class)).when(context).getTestClass();
        doReturn(Optional.of(new DummyWithSettingsOnWrongFieldTypeTest())).when(context).getTestInstance();

        final String expectedMsg = """
                Error running test

                Cause:
                 -> @WithSettings must be annotated on a Settings field.

                Found annotation on:
                 -> private final java.lang.String org.instancio.junit.InstancioExtensionTest$DummyWithSettingsOnWrongFieldTypeTest.settings
                """;

        assertApiExceptionWithMessage(() -> extension.beforeEach(context), expectedMsg);
    }

    @Test
    @DisplayName("Verify exception is thrown if @WithSettings is on a field with null value")
    void withSettingsOnNullField() {
        doReturn(Optional.of(DummyWithNullSettingsTest.class)).when(context).getTestClass();
        doReturn(Optional.of(new DummyWithNullSettingsTest())).when(context).getTestInstance();

        final String expectedMsg = """
                Error running test

                Cause:
                 -> @WithSettings must be annotated on a non-null field.

                """;

        assertApiExceptionWithMessage(() -> extension.beforeEach(context), expectedMsg);
    }

    /**
     * Mimics a test class with a {@code ParameterizedTest} and a non-static
     * {@code Settings} field (a JUnit extension can only access static fields
     * when ParameterizedTest is executed).
     */
    @Test
    @DisplayName("Verify exception is thrown if @WithSettings is on a non-static field")
    void withNonStaticSettingsField() {
        doReturn(Optional.of(DummyWithNonStaticSettingsTest.class)).when(context).getTestClass();
        doReturn(Optional.empty()).when(context).getTestInstance();

        final String expectedMsg = """
                Error running test

                Possible causes:
                 -> @WithSettings must be annotated on a non-null field.
                 -> If @WithSettings is used in a test class that contains a @ParameterizedTest,
                    the annotated Settings field must be static.
                """;

        assertApiExceptionWithMessage(() -> extension.beforeEach(context), expectedMsg);
    }

    private static void assertApiExceptionWithMessage(final ThrowingCallable throwingCallable, final String expectedMsg) {
        assertThatThrownBy(throwingCallable)
                .isInstanceOf(InstancioApiException.class)
                .extracting(ex -> ex.getMessage().replace("\r", ""))
                .isEqualTo(expectedMsg);
    }

    @Test
    @DisplayName("Resources should be cleared after each test method")
    void afterEach() {
        ExtensionContext.Store store = mock(ExtensionContext.Store.class);
        doReturn(store).when(context).getStore(create("org.instancio"));

        extension.afterEach(context);
        verify(threadLocalRandom).remove();
        verify(threadLocalSettings).remove();
        verify(store).remove("elementAnnotations", ElementAnnotations.class);
    }

    @Test
    @DisplayName("Verify seed value is reported if a test fails")
    void afterTestExecutionWithFailedTest() throws NoSuchMethodException {
        final long expectedSeed = 789;
        final Method method = DummyTest.class.getDeclaredMethod(METHOD_WITH_SEED_ANNOTATION);

        when(context.getExecutionException()).thenReturn(Optional.of(new Throwable()));
        when(context.getRequiredTestMethod()).thenReturn(method);
        when(threadLocalRandom.get()).thenReturn(new DefaultRandom(expectedSeed, Seeds.Source.SEED_ANNOTATION));

        // Method under test
        extension.afterTestExecution(context);

        final String expectedMsg = String.format("'%s' failed with seed: %s", method.getName(), expectedSeed);
        verify(context).publishReportEntry(eq("Instancio"), contains(expectedMsg));
    }

    @Test
    @DisplayName("Should have nothing to do if test was successful")
    void afterTestExecutionWithSuccessfulTest() {
        when(context.getExecutionException()).thenReturn(Optional.empty());

        // Method under test
        extension.afterTestExecution(context);

        verifyNoMoreInteractions(context);
        verifyNoInteractions(threadLocalRandom);
        verifyNoInteractions(threadLocalSettings);
    }

    @SuppressWarnings("unused")
    static class DummyTest {
        @WithSettings
        private final Settings settings = SETTINGS;

        public DummyTest() {
        }

        @Seed(SEED_ANNOTATION_VALUE)
        void methodWithSeedAnnotation() {
        }

        void methodWithoutSeedAnnotation() {
        }
    }

    static class DummyWithTwoSettingsTest {
        @WithSettings
        private final Settings settings1 = SETTINGS;
        @WithSettings
        private final Settings settings2 = SETTINGS;
    }

    static class DummyWithNonStaticSettingsTest {
        @WithSettings
        private Settings settings = SETTINGS;
    }

    static class DummyWithNullSettingsTest {
        @WithSettings
        private Settings settings;
    }

    static class DummyWithSettingsOnWrongFieldTypeTest {
        @WithSettings
        private final String settings = "bad";
    }

}
