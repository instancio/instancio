/*
 * Copyright 2022-2025 the original author or authors.
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

import java.util.List;

/**
 * Utility class for constructing exceptions.
 */
public final class Fail {

    static final String SUBMIT_BUG_REPORT_MSG = String.format("" +
            "Instancio encountered an error.%n" +
            "Please submit a bug report including the stacktrace:%n" +
            "https://github.com/instancio/instancio/issues");


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
        if (cause instanceof InstancioException) {
            return (InstancioException) cause;
        }
        return new InstancioException(SUBMIT_BUG_REPORT_MSG, cause);
    }

    public static InstancioException withInternalError(final String msg, final Object... args) {
        final ErrorArgs errorArgs = unpackArgs(args);
        final String fullErrorMsg = createInternalErrorMessage(msg, errorArgs);
        return new InstancioException(fullErrorMsg, errorArgs.throwable);
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
        final ErrorArgs errorArgs = unpackArgs(args);
        final String fullErrorMsg = createUsageErrorMessage(msg, errorArgs);
        return new InstancioApiException(fullErrorMsg, errorArgs.throwable);
    }

    public static UnresolvedAssignmentException withUnresolvedAssignment(final String msg, final Object... args) {
        final ErrorArgs errorArgs = unpackArgs(args);
        final String fullErrorMsg = createUsageErrorMessage(msg, errorArgs);
        return new UnresolvedAssignmentException(fullErrorMsg, errorArgs.throwable);
    }

    private static String createUsageErrorMessage(final String msg, final ErrorArgs errorArgs) {
        final String location = Format.firstNonInstancioStackTraceLine(new Throwable());
        final String msgWithArgs = String.format(msg, errorArgs.args);
        return String.format("" +
                "%n" +
                "%nError creating an object" +
                "%n -> at %s" +
                "%n" +
                "%nReason: %s" +
                "%n" +
                "%n", location, msgWithArgs);
    }

    private static String createInternalErrorMessage(final String msg, final ErrorArgs errorArgs) {
        final String location = Format.firstNonInstancioStackTraceLine(new Throwable());
        final String msgWithArgs = String.format(msg, errorArgs.args);
        return String.format("Internal error occurred creating an object." +
                "%n" +
                "%nInternal errors are suppressed by default and" +
                "%ncan be ignored if not applicable to the current test" +
                "%n -> at %s" +
                "%n" +
                "%nReason: %s" +
                "%n" +
                "%n", location, msgWithArgs);
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
    public static InstancioTerminatingException withFataInternalError(final String msg, final Object... args) {
        final ErrorArgs errorArgs = unpackArgs(args);
        final String msgWithArgs = String.format(msg, errorArgs.args);
        final String fullErrorMsg = String.format("" +
                "%n" +
                "%s" +
                "%n" +
                "%n -> %s" +
                "%n", SUBMIT_BUG_REPORT_MSG, msgWithArgs);
        return new InstancioTerminatingException(fullErrorMsg, errorArgs.throwable);
    }

    private static ErrorArgs unpackArgs(final Object... args) {
        final int len = args.length;
        if (len > 0 && args[len - 1] instanceof Throwable) {
            return new ErrorArgs(copyWithoutLastElement(args), (Throwable) args[len - 1]);
        }
        return new ErrorArgs(args, null);
    }

    private static Object[] copyWithoutLastElement(final Object... args) {
        final List<Object> list = CollectionUtils.asArrayList(args);
        list.remove(args.length - 1);
        return list.toArray();
    }

    private static final class ErrorArgs {
        private final Object[] args;
        private final Throwable throwable;

        private ErrorArgs(final Object[] args, final Throwable throwable) {
            this.args = args;
            this.throwable = throwable;
        }
    }

    private Fail() {
        // non-instantiable
    }
}
