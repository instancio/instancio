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

import org.instancio.Random;
import org.instancio.exception.InstancioApiException;
import org.instancio.internal.ThreadLocalRandom;
import org.instancio.internal.ThreadLocalSettings;
import org.instancio.internal.random.DefaultRandom;
import org.instancio.settings.Settings;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InstancioExtensionTest {

    private static final int SEED_ANNOTATION_VALUE = -2345212;
    private static final String METHOD_WITHOUT_SEED_ANNOTATION = "methodWithoutSeedAnnotation";
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

    @InjectMocks
    private InstancioExtension extension;

    @Test
    @DisplayName("Verify @Seed annotation's value is passed to thread local")
    void beforeEachWithSeedAnnotation() throws Exception {
        final Method method = DummyTest.class.getDeclaredMethod(METHOD_WITH_SEED_ANNOTATION);
        doReturn(Optional.of(method)).when(context).getTestMethod();

        // Method under test
        extension.beforeEach(context);

        verify(threadLocalRandom).set(randomCaptor.capture());
        assertThat(randomCaptor.getValue().getSeed()).isEqualTo(SEED_ANNOTATION_VALUE);
    }

    @Test
    @DisplayName("Verify annotated Settings are passed to thread local")
    void beforeEachWithSettingsAnnotation() {
        doReturn(Optional.of(DummyTest.class)).when(context).getTestClass();
        doReturn(Optional.of(new DummyTest())).when(context).getTestInstance();

        // Method under test
        extension.beforeEach(context);

        verify(threadLocalSettings).set(settingsCaptor.capture());
        assertThat(settingsCaptor.getValue()).isSameAs(SETTINGS);
    }

    @Test
    @DisplayName("Verify exception is thrown if test class has more than one field annotated @WithSettings")
    void moreThanOneFieldAnnotatedWithSettings() {
        doReturn(Optional.of(DummyWithTwoSettingsTest.class)).when(context).getTestClass();

        // Method under test
        assertThatThrownBy(() -> extension.beforeEach(context))
                .isInstanceOf(InstancioApiException.class)
                .hasMessageContainingAll("\nFound more than one field annotated '@WithSettings':\n\n",
                        "private final org.instancio.settings.Settings org.instancio.junit.InstancioExtensionTest$DummyWithTwoSettingsTest.settings1\n",
                        "private final org.instancio.settings.Settings org.instancio.junit.InstancioExtensionTest$DummyWithTwoSettingsTest.settings2");
    }


    @Test
    @DisplayName("Verify exception is thrown if @WithSettings is not on Settings field")
    void withSettingsOnWrongFieldType() {
        doReturn(Optional.of(DummyWithSettingsOnWrongFieldTypeTest.class)).when(context).getTestClass();
        doReturn(Optional.of(new DummyWithSettingsOnWrongFieldTypeTest())).when(context).getTestInstance();

        // Method under test
        assertThatThrownBy(() -> extension.beforeEach(context))
                .isInstanceOf(InstancioApiException.class)
                .hasMessageContainingAll("\n@WithSettings must be annotated on a Settings field.\n\n",
                        "Found annotation on: private final java.lang.String org.instancio.junit.InstancioExtensionTest$DummyWithSettingsOnWrongFieldTypeTest.settings");
    }

    @Test
    @DisplayName("Verify exception is thrown if @WithSettings is on a field with null value")
    void withSettingsOnNullField() {
        doReturn(Optional.of(DummyWithNullSettingsTest.class)).when(context).getTestClass();
        doReturn(Optional.of(new DummyWithNullSettingsTest())).when(context).getTestInstance();

        // Method under test
        assertThatThrownBy(() -> extension.beforeEach(context))
                .isInstanceOf(InstancioApiException.class)
                .hasMessage("\n@WithSettings must be annotated on a non-null field.");
    }

    @Test
    @DisplayName("If @Seed annotation is absent, should use a random seed")
    void beforeEachWithoutSeedAnnotation() throws Exception {
        final Method method = DummyTest.class.getDeclaredMethod(METHOD_WITHOUT_SEED_ANNOTATION);
        doReturn(Optional.of(method)).when(context).getTestMethod();

        // Method under test
        extension.beforeEach(context);

        verify(threadLocalRandom).set(randomCaptor.capture());
        assertThat(randomCaptor.getValue().getSeed()).isNotEqualTo(SEED_ANNOTATION_VALUE);
    }

    @Test
    @DisplayName("Thread locals should be cleared after each test method")
    void afterEach() {
        extension.afterEach(context);
        verify(threadLocalRandom).remove();
        verify(threadLocalSettings).remove();
    }

    @Test
    @DisplayName("Verify seed value is reported if a test fails")
    void afterTestExecutionWithFailedTest() throws NoSuchMethodException {
        final int expectedSeed = 789;
        final Method method = DummyTest.class.getDeclaredMethod(METHOD_WITH_SEED_ANNOTATION);

        when(context.getExecutionException()).thenReturn(Optional.of(new Throwable()));
        when(context.getTestMethod()).thenReturn(Optional.of(method));
        when(threadLocalRandom.get()).thenReturn(new DefaultRandom(expectedSeed));

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

    static class DummyTest {
        @WithSettings
        private final Settings settings = SETTINGS;

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

    static class DummyWithNullSettingsTest {
        @WithSettings
        private Settings settings;
    }

    static class DummyWithSettingsOnWrongFieldTypeTest {
        @WithSettings
        private final String settings = "bad";
    }

}
