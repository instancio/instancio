/*
 * Copyright 2022-2023 the original author or authors.
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
package org.instancio.internal;

import org.instancio.TargetSelector;
import org.instancio.exception.InstancioApiException;
import org.instancio.exception.InstancioException;
import org.instancio.exception.InstancioTerminatingException;
import org.instancio.exception.UnusedSelectorException;
import org.instancio.internal.context.ModelContext;
import org.instancio.internal.util.SystemProperties;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.asserts.Asserts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junitpioneer.jupiter.ClearSystemProperty;
import org.junitpioneer.jupiter.SetSystemProperty;

import java.util.Collections;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ErrorHandlerTest {

    private static final String[] SUBMIT_BUG_REPORT_MESSAGE = {
            "Instancio encountered an error.",
            "Please submit a bug report including the stacktrace:",
            "https://github.com/instancio/instancio/issues"
    };

    private static final String EXCEPTION_MSG = "Test exception";
    private static final InstancioException INSTANCIO_EXCEPTION = new InstancioException(EXCEPTION_MSG);
    private static final InstancioException INSTANCIO_API_EXCEPTION = new InstancioApiException(EXCEPTION_MSG);
    private static final RuntimeException RUNTIME_EXCEPTION = new RuntimeException(EXCEPTION_MSG);

    /**
     * <ul>
     *   <li>{@link Keys#FAIL_ON_ERROR} disabled</li>
     *   <li>{@link SystemProperties#FAIL_ON_ERROR} disabled</li>
     * </ul>
     */
    @Nested
    @ClearSystemProperty(key = SystemProperties.FAIL_ON_ERROR)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class FailOnErrorSystemPropertyAndSettingDisabledTest {

        private final ErrorHandler errorHandler = new ErrorHandler(
                ModelContext.builder(String.class).build());

        @Test
        void doesNotFailOnErrorWithSupplier() {
            assertThat(errorHandler.conditionalFailOnError(supplierThrowing(RUNTIME_EXCEPTION))).isEmpty();
            assertThat(errorHandler.conditionalFailOnError(supplierThrowing(INSTANCIO_EXCEPTION))).isEmpty();
        }

        @Test
        @DisplayName("Verify AssertionError is always propagated, even if 'fail on error' is disabled")
        @SetSystemProperty(key = SystemProperties.FAIL_ON_ERROR, value = "false")
        void propagatesAssertionError() {
            final AssertionError error = new AssertionError();

            final Supplier<?> supplier = supplierThrowing(error);
            assertThatThrownBy(() -> errorHandler.conditionalFailOnError(supplier))
                    .isSameAs(error);
        }

        @ParameterizedTest
        @MethodSource("shouldPropagateExceptions")
        @DisplayName("InstancioTerminatingException is always propagated, even if 'fail on error' is disabled")
        <T extends InstancioTerminatingException> void propagatesTerminatingException(final T ex) {
            final Supplier<?> supplier = supplierThrowing(ex);
            assertThatThrownBy(() -> errorHandler.conditionalFailOnError(supplier))
                    .isSameAs(ex);
        }

        private Stream<Arguments> shouldPropagateExceptions() {
            final Set<TargetSelector> emptySet = Collections.emptySet();
            return Stream.of(
                    Arguments.of(INSTANCIO_API_EXCEPTION),
                    Arguments.of(new InstancioTerminatingException(EXCEPTION_MSG)),
                    Arguments.of(new UnusedSelectorException(EXCEPTION_MSG, emptySet, emptySet, emptySet, emptySet, emptySet))
            );
        }
    }

    /**
     * <ul>
     *   <li>{@link Keys#FAIL_ON_ERROR} enabled</li>
     *   <li>{@link SystemProperties#FAIL_ON_ERROR} disabled</li>
     * </ul>
     */
    @Nested
    @ClearSystemProperty(key = SystemProperties.FAIL_ON_ERROR)
    class FailOnErrorSettingsTest {

        private final ErrorHandler errorHandler = new ErrorHandler(
                ModelContext.builder(String.class)
                        .withSettings(Settings.create().set(Keys.FAIL_ON_ERROR, true))
                        .build());

        @Test
        void shouldNotThrowErrorIfSupplierReturnsNull() {
            assertThat(errorHandler.conditionalFailOnError(() -> null)).isEmpty();
        }

        @Test
        @DisplayName("Verify errors are wrapped in InstancioException and include a request to submit a bug report")
        void errorShouldBeWrappedInInstancioException() {
            final Supplier<?> supplier = supplierThrowing(RUNTIME_EXCEPTION);
            assertThatThrownBy(() -> errorHandler.conditionalFailOnError(supplier))
                    .isExactlyInstanceOf(InstancioException.class)
                    .hasCause(RUNTIME_EXCEPTION)
                    .hasMessageContainingAll(SUBMIT_BUG_REPORT_MESSAGE);
        }
    }

    /**
     * <ul>
     *   <li>{@link Keys#FAIL_ON_ERROR} disabled</li>
     *   <li>{@link SystemProperties#FAIL_ON_ERROR} enabled</li>
     * </ul>
     */
    @Nested
    class FailOnErrorSystemPropertyTest {

        private final ErrorHandler errorHandler = new ErrorHandler(
                ModelContext.builder(String.class).build());

        @Test
        @SetSystemProperty(key = SystemProperties.FAIL_ON_ERROR, value = "true")
        void shouldNotThrowErrorIfSupplierReturnsNull() {
            assertThat(errorHandler.conditionalFailOnError(() -> null)).isEmpty();
        }

        @Test
        @DisplayName("Verify errors are wrapped in InstancioException and include a request to submit a bug report")
        void errorShouldBeWrappedInInstancioException() {
            final Supplier<?> supplier = supplierThrowing(RUNTIME_EXCEPTION);
            Asserts.assertWithFailOnErrorEnabled(() -> errorHandler.conditionalFailOnError(supplier))
                    .isExactlyInstanceOf(InstancioException.class)
                    .hasCause(RUNTIME_EXCEPTION)
                    .hasMessageContainingAll(SUBMIT_BUG_REPORT_MESSAGE);
        }
    }

    private static Supplier<?> supplierThrowing(final RuntimeException ex) {
        return () -> {
            throw ex;
        };
    }

    private static Supplier<?> supplierThrowing(final Error e) {
        return () -> {
            throw e;
        };
    }
}

