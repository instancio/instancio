/*
 * Copyright 2022-2024 the original author or authors.
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

import org.instancio.exception.InstancioTerminatingException;
import org.instancio.internal.context.ModelContext;
import org.instancio.internal.util.Fail;
import org.instancio.internal.util.Format;
import org.instancio.internal.util.Sonar;
import org.instancio.internal.util.SystemProperties;
import org.instancio.settings.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.function.Supplier;

@SuppressWarnings(Sonar.CATCH_EXCEPTION_INSTEAD_OF_THROWABLE)
final class ErrorHandler {
    private static final Logger LOG = LoggerFactory.getLogger(ErrorHandler.class);

    private static final String SUPPRESSION_REASON = String.format("" +
            "Suppressed error because Keys.FAIL_ON_ERROR (%s) is disabled." +
            "%n -> To propagate the error, set Keys.FAIL_ON_ERROR setting to true. " +
            "%n -> To display the stack trace, run in verbose() mode or with TRACE logging." +
            "%n", Keys.FAIL_ON_ERROR.propertyKey());

    private final ModelContext<?> context;
    private final boolean isFailOnErrorSettingEnabled;

    ErrorHandler(final ModelContext<?> context) {
        this.context = context;
        this.isFailOnErrorSettingEnabled = context.getSettings().get(Keys.FAIL_ON_ERROR);
    }

    /**
     * Extracts value from given {@code supplier} while handling
     * errors (if any) thrown by the supplier. An error will be propagated
     * if at least one of the following conditions is {@code true}:
     *
     * <ul>
     *   <li>{@link SystemProperties#FAIL_ON_ERROR} is enabled</li>
     *   <li>{@link Keys#FAIL_ON_ERROR} is enabled</li>
     * </ul>
     *
     * @param supplier containing value to extract
     * @param <T>      the type of value returned by the supplier
     * @return an optional containing the value from the supplier
     */
    public <T> Optional<T> conditionalFailOnError(final Supplier<T> supplier) {
        try {
            return Optional.ofNullable(supplier.get());
        } catch (AssertionError | InstancioTerminatingException ex) {
            throw ex;
        } catch (Throwable ex) { //NOPMD
            if (isShouldFailOnError()) {
                throw Fail.withInternalError(ex);
            }
            logSuppressed(ex);
        }
        return Optional.empty();
    }

    private boolean isShouldFailOnError() {
        return isFailOnErrorSettingEnabled || SystemProperties.isFailOnErrorEnabled();
    }

    @SuppressWarnings("all")
    private void logSuppressed(final Throwable t) {
        if (context.isVerbose()) {
            final String errorMsg = String.format("" +
                            "Exception occurred while generating the root object of type %s.%n" +
                            "Printing stacktrace because verbose() mode is enabled.%n" +
                            "To propagate internal errors, set Keys.FAIL_ON_ERROR setting to true.%n",
                    Format.withoutPackage(context.getRootType()));

            System.err.println(errorMsg);
            t.printStackTrace();
        } else {
            if (LOG.isTraceEnabled()) {
                LOG.trace(SUPPRESSION_REASON, t);
            } else {
                LOG.debug("{}\n{}: {}", SUPPRESSION_REASON, t.getClass().getName(), t.getMessage());
            }
        }
    }
}
