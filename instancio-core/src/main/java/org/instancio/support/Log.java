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
package org.instancio.support;

import org.instancio.InstancioApi;
import org.instancio.documentation.InternalApi;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

/**
 * Provides utilities for logging user-facing diagnostic messages.
 *
 * @since 6.0.0
 */
@InternalApi
public final class Log {

    /**
     * Categories of log messages used by Instancio.
     *
     * <p>Each category defines a unique name and log level that
     * can be filtered or configured via standard logging frameworks.
     *
     * @since 6.0.0
     */
    public enum Category {

        /**
         * Logs a warning when the maximum object graph depth is reached.
         *
         * <p>Useful for diagnosing when Instancio stops
         * populating deeply nested objects.
         *
         * @see Keys#MAX_DEPTH
         * @see InstancioApi#withMaxDepth(int)
         * @since 6.0.0
         */
        ON_MAX_DEPTH_REACHED(Level.WARN, "org.instancio.log.on.max.depth.reached"),

        /**
         * Logs whether the {@code instancio.properties} file was found
         * on the classpath or if default properties are being used.
         *
         * @since 6.0.0
         */
        PROPERTIES(Level.DEBUG, "org.instancio.log.properties"),

        /**
         * Logs the effective seed value and its source.
         *
         * @since 6.0.0
         */
        SEED(Level.TRACE, "org.instancio.log.seed"),

        /**
         * Logs the current {@link Settings} configuration.
         *
         * @since 6.0.0
         */
        SETTINGS(Level.TRACE, "org.instancio.log.settings");

        private final Level level;
        private final String name;

        Category(final Level level, final String name) {
            this.level = level;
            this.name = name;
        }
    }

    public static void msg(final Category category, final String msg, final Object... args) {
        LoggerFactory.getLogger(category.name)
                .atLevel(category.level)
                .log(msg, args);
    }

    private Log() {
        // non-instantiable
    }
}
