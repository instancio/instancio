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

import org.instancio.documentation.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

@SuppressWarnings(Sonar.CATCH_EXCEPTION_INSTEAD_OF_THROWABLE)
public final class ExceptionUtils {
    private static final Logger LOG = LoggerFactory.getLogger(ExceptionUtils.class);

    private ExceptionUtils() {
        // non-instantiable
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
            LOG.debug("{} {}", formatted, getCausedBy(t));
        }
    }

    @VisibleForTesting
    static String getCausedBy(final Throwable throwable) {
        final StringBuilder sb = new StringBuilder(1024);

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
