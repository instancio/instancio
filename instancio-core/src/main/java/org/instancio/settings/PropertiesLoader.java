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
package org.instancio.settings;

import org.instancio.exception.InstancioException;
import org.instancio.util.Verify;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader {
    private static final Logger LOG = LoggerFactory.getLogger(PropertiesLoader.class);

    public Properties load(final String file) {
        Verify.notNull(file, "Properties file must not be null");

        final Properties properties = new Properties();
        try (InputStream inStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(file)) {
            if (inStream == null) {
                throw new InstancioException(String.format("Unable to load properties from '%s'", file));
            }
            properties.load(inStream);
            return properties;
        } catch (IOException ex) {
            LOG.debug("Failed loading {}", file);
        }
        return properties;
    }
}
