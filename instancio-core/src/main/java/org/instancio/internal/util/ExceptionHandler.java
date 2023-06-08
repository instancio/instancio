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

import org.instancio.exception.InstancioTerminatingException;
import org.jetbrains.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

import java.util.Optional;
import java.util.function.Supplier;

import static org.instancio.internal.util.SystemProperties.shouldFailOnError;

@SuppressWarnings(Sonar.CATCH_EXCEPTION_INSTEAD_OF_THROWABLE)
public final class ExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(ExceptionHandler.class);

    private static final String SUPPRESSION_REASON = String.format(
            "Suppressed error because system property '%s' is disabled", SystemProperties.FAIL_ON_ERROR);

    private ExceptionHandler() {
        // non-instantiable
    }

    public static <T> Optional<T> conditionalFailOnError(final Supplier<T> supplier) {
        try {
            return Optional.ofNullable(supplier.get());
        } catch (AssertionError | InstancioTerminatingException ex) {
            throw ex;
        } catch (Throwable ex) { //NOPMD
            if (shouldFailOnError()) {
                throw Fail.withInternalError(ex);
            }
            logSuppressed(ex);
        }
        return Optional.empty();
    }

    public static void conditionalFailOnError(final VoidFunction function) {
        try {
            function.invoke();
        } catch (AssertionError | InstancioTerminatingException ex) {
            throw ex;
        } catch (Throwable ex) { //NOPMD
            if (shouldFailOnError()) {
                throw Fail.withInternalError(ex);
            }
            logSuppressed(ex);
        }
    }

    private static void logSuppressed(final Throwable t) {
        if (LOG.isTraceEnabled()) {
            LOG.trace(SUPPRESSION_REASON, t);
        } else {
            LOG.debug("{}. {}: {}", SUPPRESSION_REASON, t.getClass().getName(), t.getMessage());
        }
    }

    /**
     * Logs exception stacktrace if trace is enabled,
     * otherwise log exception class name and message.
     *
     * @param msg  log message
     * @param t    exception to log
     * @param args message arguments
     */
    public static void logException(final String msg, final Throwable t, final Object... args) {
        final String formatted = MessageFormatter.arrayFormat(msg, args).getMessage();
        if (LOG.isTraceEnabled()) {
            LOG.trace(formatted, t);
        } else {
            LOG.debug("{} [{}]", formatted, getCausedBy(t));
        }
    }

    /**
     * Run the provided {@code action} ignoring the
     * {@link NoClassDefFoundError} if it will be thrown.
     *
     * @param action the provided action
     */
    public static void runIgnoringTheNoClassDefFoundError(Runnable action) {
        try {
            action.run();
        } catch (NoClassDefFoundError error) {
            LOG.trace("Error is ignored: {}", error.toString());
        }
    }

    @VisibleForTesting
    static String getCausedBy(final Throwable throwable) {
        final StringBuilder sb = new StringBuilder();

        for (Throwable t = throwable; t != null; t = t.getCause()) {
            sb.append(Constants.NL)
                    .append(" => caused by: ")
                    .append(t.getClass().getSimpleName());

            if (t.getMessage() != null) {
                sb.append(": \"").append(t.getMessage()).append('"');
            }
        }
        return sb.toString();
    }
}
