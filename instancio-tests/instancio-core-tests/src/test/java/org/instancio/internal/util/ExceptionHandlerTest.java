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
package org.instancio.internal.util;

import org.instancio.TargetSelector;
import org.instancio.exception.InstancioApiException;
import org.instancio.exception.InstancioException;
import org.instancio.exception.InstancioTerminatingException;
import org.instancio.exception.UnusedSelectorException;
import org.instancio.test.support.asserts.Asserts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
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
import static org.instancio.internal.util.ExceptionHandler.conditionalFailOnError;

@ClearSystemProperty(key = SystemProperties.FAIL_ON_ERROR)
class ExceptionHandlerTest {

    private static final String[] SUBMIT_BUG_REPORT_MESSAGE = {
            "Instancio encountered an error.",
            "Please submit a bug report including the stacktrace:",
            "https://github.com/instancio/instancio/issues"
    };

    private static final String EXCEPTION_MSG = "Test exception";
    private static final InstancioException INSTANCIO_EXCEPTION = new InstancioException(EXCEPTION_MSG);
    private static final InstancioException INSTANCIO_API_EXCEPTION = new InstancioApiException(EXCEPTION_MSG);
    private static final RuntimeException RUNTIME_EXCEPTION = new RuntimeException(EXCEPTION_MSG);

    @Nested
    class CausedByTest {
        @Test
        void getCausedIsNull() {
            assertThat(ExceptionHandler.getCausedBy(null)).isEmpty();
        }

        @Test
        void singleCauseWithNoMessage() {
            assertThat(ExceptionHandler.getCausedBy(new Throwable()))
                    .isEqualTo(String.format("%n => caused by: Throwable"));
        }

        @Test
        void multipleCausesWithMessages() {
            final InstancioException ex = new InstancioException("top",
                    new RuntimeException("cause",
                            new NullPointerException("root")));

            assertThat(ExceptionHandler.getCausedBy(ex))
                    .isEqualTo(String.format("%n" +
                            " => caused by: InstancioException: \"top\"%n" +
                            " => caused by: RuntimeException: \"cause\"%n" +
                            " => caused by: NullPointerException: \"root\""));
        }

        @Test
        void multipleCausesWithoutMessages() {
            final InstancioException ex = new InstancioException("top",
                    new RuntimeException(
                            new NullPointerException()));

            assertThat(ExceptionHandler.getCausedBy(ex))
                    .isEqualTo(String.format("%n" +
                            " => caused by: InstancioException: \"top\"%n" +
                            " => caused by: RuntimeException: \"java.lang.NullPointerException\"%n" +
                            " => caused by: NullPointerException"));
        }
    }

    @Test
    @SetSystemProperty(key = SystemProperties.FAIL_ON_ERROR, value = "true")
    void shouldNotThrowErrorIfSupplierReturnsNull() {
        assertThat(conditionalFailOnError(() -> null)).isEmpty();
    }

    @Test
    @DisplayName("Verify errors are suppressed by default when 'fail on error' property is not set")
    void doesNotFailOnErrorWithSupplier() {
        assertThat(conditionalFailOnError(supplierThrowing(RUNTIME_EXCEPTION))).isEmpty();
        assertThat(conditionalFailOnError(supplierThrowing(INSTANCIO_EXCEPTION))).isEmpty();
    }

    private static Stream<Arguments> shouldPropagateExceptions() {
        final Set<TargetSelector> emptySet = Collections.emptySet();
        return Stream.of(
                Arguments.of(INSTANCIO_API_EXCEPTION),
                Arguments.of(new InstancioTerminatingException(EXCEPTION_MSG)),
                Arguments.of(new UnusedSelectorException(EXCEPTION_MSG, emptySet, emptySet, emptySet, emptySet, emptySet))
        );
    }

    @ParameterizedTest
    @MethodSource("shouldPropagateExceptions")
    @DisplayName("Verify API exception is propagated even if 'fail on error' is disabled")
    @SetSystemProperty(key = SystemProperties.FAIL_ON_ERROR, value = "false")
    void propagatesApiException(final RuntimeException ex) {
        final Supplier<?> supplier = supplierThrowing(ex);
        assertThatThrownBy(() -> conditionalFailOnError(supplier))
                .isSameAs(ex);
    }

    @Test
    @DisplayName("Verify AssertionError is propagated even if 'fail on error' is disabled")
    @SetSystemProperty(key = SystemProperties.FAIL_ON_ERROR, value = "false")
    void propagatesAssertionError() {
        final AssertionError error = new AssertionError();

        final Supplier<?> supplier = supplierThrowing(error);
        assertThatThrownBy(() -> conditionalFailOnError(supplier))
                .isSameAs(error);
    }

    @Test
    @DisplayName("Verify errors are wrapped in InstancioException and include a request to submit a bug report")
    void failWithSupplier() {
        final Supplier<?> supplier = supplierThrowing(RUNTIME_EXCEPTION);
        Asserts.assertWithFailOnErrorEnabled(() -> conditionalFailOnError(supplier))
                .isExactlyInstanceOf(InstancioException.class)
                .hasCause(RUNTIME_EXCEPTION)
                .hasMessageContainingAll(SUBMIT_BUG_REPORT_MESSAGE);
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
