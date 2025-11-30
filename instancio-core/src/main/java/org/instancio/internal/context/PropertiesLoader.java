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
package org.instancio.internal.context;

import org.instancio.support.Log;
import org.instancio.documentation.VisibleForTesting;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class PropertiesLoader {

    private static final String DEFAULT_PROPERTIES_FILE = "instancio.properties";

    private PropertiesLoader() {
        // non-instantiable
    }

    public static Properties loadDefaultPropertiesFile() {
        return load(DEFAULT_PROPERTIES_FILE);
    }

    @VisibleForTesting
    static Properties load(final String file) {
        final Properties properties = new Properties();
        try (InputStream inStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(file)) {
            if (inStream == null) {
                Log.msg(Log.Category.PROPERTIES,
                        "No custom '{}' found on classpath. Using default settings.", file);

                return properties;
            }

            Log.msg(Log.Category.PROPERTIES, "Found '{}' on classpath", file);
            properties.load(inStream);
            return properties;
        } catch (IOException ex) {
            Log.msg(Log.Category.SUPPRESSED_ERROR, "Failed loading {}", file, ex);
        }
        return properties;
    }
}
