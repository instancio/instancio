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
import java.io.PrintWriter;
import java.io.StringWriter;

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

    public void warn(final String message, final Object... args) {
        String msg = formatMessage(message, args);

        final Exception ex = unpackException(args);
        if (ex != null) {
            if (verbose) {
                msg += String.format("%nStacktrace:%n%s", getStackTraceAsString(ex));
            } else {
                final String cause = ex.getClass().getName() + ": " + ex.getMessage();
                msg += String.format(". Caused by %s", cause);
            }
        }

        messager.printMessage(Diagnostic.Kind.WARNING, msg);
    }

    private String formatMessage(final String message, final Object... args) {
        final String msg = MSG_PREFIX + message;
        return String.format(msg, args);
    }

    private static Exception unpackException(final Object... args) {
        if (args.length > 0 && args[args.length - 1] instanceof Exception) {
            return (Exception) args[args.length - 1];
        }
        return null;
    }

    private static String getStackTraceAsString(final Exception ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        return sw.toString();
    }
}
