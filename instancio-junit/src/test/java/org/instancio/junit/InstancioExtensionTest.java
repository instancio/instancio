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
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InstancioExtensionTest {

    private static final String METHOD_WITHOUT_SEED_ANNOTATION = "methodWithoutSeedAnnotation";
    private static final String METHOD_WITH_SEED_ANNOTATION = "methodWithSeedAnnotation";
    private static final int SEED_ANNOTATION_VALUE = -2345212;

    @Mock
    private ThreadLocalRandomProvider threadLocalRandomProvider;

    @Mock
    private ExtensionContext context;

    @Captor
    private ArgumentCaptor<RandomProvider> randomProviderCaptor;

    @InjectMocks
    private InstancioExtension extension;

    @Test
    void beforeEachWithSeedAnnotation() throws NoSuchMethodException {
        final Method method = Dummy.class.getDeclaredMethod(METHOD_WITH_SEED_ANNOTATION);
        when(context.getRequiredTestMethod()).thenReturn(method);

        // Method under test
        extension.beforeEach(context);

        verify(threadLocalRandomProvider).set(randomProviderCaptor.capture());
        assertThat(randomProviderCaptor.getValue().getSeed()).isEqualTo(SEED_ANNOTATION_VALUE);
    }

    @Test
    void beforeEachWithoutSeedAnnotation() throws NoSuchMethodException {
        final Method method = Dummy.class.getDeclaredMethod(METHOD_WITHOUT_SEED_ANNOTATION);
        when(context.getRequiredTestMethod()).thenReturn(method);

        // Method under test
        extension.beforeEach(context);

        verify(threadLocalRandomProvider).set(randomProviderCaptor.capture());
        assertThat(randomProviderCaptor.getValue().getSeed()).isNotEqualTo(SEED_ANNOTATION_VALUE);
    }

    @Test
    void afterEach() {
        extension.afterEach(context);
        verify(threadLocalRandomProvider).remove();
    }

    @Test
    void afterTestExecutionWithFailedTest() throws NoSuchMethodException {
        final int expectedSeed = 789;
        final Method method = Dummy.class.getDeclaredMethod(METHOD_WITH_SEED_ANNOTATION);

        when(context.getExecutionException()).thenReturn(Optional.of(new Throwable()));
        when(context.getRequiredTestMethod()).thenReturn(method);
        when(threadLocalRandomProvider.get()).thenReturn(new RandomProvider(expectedSeed));

        // Method under test
        extension.afterTestExecution(context);

        final String expectedMsg = String.format("'%s' failed with seed: %s", method.getName(), expectedSeed);
        verify(context).publishReportEntry(eq("Instancio"), contains(expectedMsg));
    }

    @Test
    void afterTestExecutionWithSuccessfulTest() {
        when(context.getExecutionException()).thenReturn(Optional.empty());

        // Method under test
        extension.afterTestExecution(context);

        verifyNoMoreInteractions(context);
        verifyNoInteractions(threadLocalRandomProvider);
    }

    static class Dummy {
        @Seed(SEED_ANNOTATION_VALUE)
        void methodWithSeedAnnotation() {
        }

        void methodWithoutSeedAnnotation() {
        }
    }
}
