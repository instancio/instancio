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
package org.instancio.util;

import org.instancio.exception.InstancioApiException;
import org.instancio.exception.InstancioException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.function.Supplier;

import static org.instancio.util.SystemProperties.isFailOnError;

@SuppressWarnings("PMD.AvoidRethrowingException")
public final class ExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(ExceptionHandler.class);

    private ExceptionHandler() {
        // non-instantiable
    }

    public static <T> Optional<T> conditionalFailOnError(final Supplier<T> supplier) {
        try {
            return Optional.of(supplier.get());
        } catch (InstancioApiException ex) {
            throw ex;
        } catch (InstancioException ex) {
            if (isFailOnError()) {
                throw ex;
            }
            logSuppressed(ex);
        } catch (Exception ex) {
            if (isFailOnError()) {
                throw InstancioException.unhandledException(ex);
            }
            logSuppressed(ex);
        }
        return Optional.empty();
    }

    @SuppressWarnings(Sonar.CATCH_EXCEPTION_INSTEAD_OF_THROWABLE)
    public static void conditionalFailOnError(final VoidFunction function) {
        try {
            function.invoke();
        } catch (InstancioApiException ex) {
            throw ex;
        } catch (InstancioException ex) {
            if (isFailOnError()) {
                throw ex;
            }
            logSuppressed(ex);
        } catch (Throwable ex) { //NOPMD
            if (isFailOnError()) {
                throw InstancioException.unhandledException(ex);
            }
            logSuppressed(ex);
        }
    }

    private static void logSuppressed(final Throwable t) {
        LOG.debug("Suppressed error because '{}' system property is disabled",
                SystemProperties.FAIL_ON_ERROR, t);
    }
}
