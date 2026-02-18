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
package org.instancio.internal.util;

import org.instancio.exception.InstancioApiException;
import org.instancio.exception.InstancioException;
import org.instancio.exception.InstancioTerminatingException;
import org.instancio.exception.UnresolvedAssignmentException;
import org.jspecify.annotations.Nullable;

/**
 * Utility class for constructing exceptions.
 */
public final class Fail {

    static final String SUBMIT_BUG_REPORT_MSG = """
            Instancio encountered an error.
            Please submit a bug report including the stacktrace:
            https://github.com/instancio/instancio/issues
            """;

    /**
     * Wraps given exception in an {@link InstancioException},
     * including a message to request a bug report submission.
     *
     * <p>This exception will not be propagated to the user
     * unless {@link SystemProperties#FAIL_ON_ERROR} is enabled.
     *
     * @param cause of an error
     * @return a wrapped exception with a message requesting a bug report
     */
    public static InstancioException withInternalError(final Throwable cause) {
        return cause instanceof InstancioException ex
                ? ex
                : new InstancioException(SUBMIT_BUG_REPORT_MSG, cause);
    }

    public static InstancioException withInternalError(final String msg, final Object... args) {
        final ErrorArgs errorArgs = ErrorArgs.unpackArgs(args);
        final String fullErrorMsg = createInternalErrorMessage(msg, errorArgs);
        return new InstancioException(fullErrorMsg, errorArgs.getThrowable());
    }

    /**
     * Creates an exception caused by incorrect usage of the API.
     * This exception should be propagated to the user.
     *
     * <p>If the last argument is an instance of {@link Throwable},
     * it will be used as the cause of the returned exception.
     *
     * @param msg  an error message, allows {@code '%s'} placeholders for the {@code args}
     * @param args error message arguments
     * @return an exception to be propagated to the user
     */
    public static InstancioApiException withUsageError(final String msg, final Object... args) {
        final ErrorArgs errorArgs = ErrorArgs.unpackArgs(args);
        final String fullErrorMsg = createUsageErrorMessage(msg, errorArgs);
        return new InstancioApiException(fullErrorMsg, errorArgs.getThrowable());
    }

    public static UnresolvedAssignmentException withUnresolvedAssignment(final String msg, final Object... args) {
        final ErrorArgs errorArgs = ErrorArgs.unpackArgs(args);
        final String fullErrorMsg = createUsageErrorMessage(msg, errorArgs);
        return new UnresolvedAssignmentException(fullErrorMsg, errorArgs.getThrowable());
    }

    private static String createUsageErrorMessage(final String msg, final ErrorArgs errorArgs) {
        final String location = Format.firstNonInstancioStackTraceLine(new Throwable());
        final String msgWithArgs = String.format(msg, errorArgs.getArgs());
        return String.format("""


                Error creating an object
                 -> at %s

                Reason: %s

                """, location, msgWithArgs);
    }

    private static String createInternalErrorMessage(final String msg, final ErrorArgs errorArgs) {
        final String location = Format.firstNonInstancioStackTraceLine(new Throwable());
        final String msgWithArgs = String.format(msg, errorArgs.getArgs());
        return String.format("""
                Internal error occurred creating an object.

                Internal errors are suppressed by default and
                can be ignored if not applicable to the current test
                 -> at %s

                Reason: %s

                """, location, msgWithArgs);
    }

    /**
     * Creates an exception due to an internal error.
     * This exception should be propagated to the user.
     *
     * <p>If the last argument is an instance of {@link Throwable},
     * it will be used as the cause of the returned exception.
     *
     * @param msg  an error message, allows {@code '%s'} placeholders for the {@code args}
     * @param args error message arguments
     * @return an exception to be propagated to the user
     */
    public static InstancioTerminatingException withFataInternalError(final String msg, final @Nullable Object... args) {
        final ErrorArgs errorArgs = ErrorArgs.unpackArgs(args);
        final String msgWithArgs = String.format(msg, errorArgs.getArgs());
        final String fullErrorMsg = String.format("""

                %s

                 -> %s
                """, SUBMIT_BUG_REPORT_MSG, msgWithArgs);
        return new InstancioTerminatingException(fullErrorMsg, errorArgs.getThrowable());
    }

    private Fail() {
        // non-instantiable
    }
}
