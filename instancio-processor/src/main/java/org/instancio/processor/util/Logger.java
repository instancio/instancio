/*
 * Copyright 2022 the original author or authors.
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

package org.instancio.processor.util;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

public final class Logger {
    private static final String MSG_PREFIX = "Instancio Processor: ";
    private final Messager messager;
    private final boolean verbose;

    public Logger(final Messager messager, final boolean verbose) {
        this.messager = messager;
        this.verbose = verbose;
    }

    public void debug(final String message, final Object... args) {
        if (verbose) {
            messager.printMessage(Diagnostic.Kind.NOTE, formatMessage(message, args));
        }
    }

    public void warn(final String message, Object... args) {
        messager.printMessage(Diagnostic.Kind.WARNING, formatMessage(message, args));
    }

    private String formatMessage(final String message, final Object... args) {
        final String msg = MSG_PREFIX + message;
        return String.format(msg, args);
    }
}
