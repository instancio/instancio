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

import org.instancio.documentation.VisibleForTesting;
import org.instancio.internal.util.Fail;
import org.instancio.internal.util.StringUtils;
import org.instancio.settings.Keys;
import org.instancio.support.Log;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.function.UnaryOperator;

/**
 * Loads global properties from, in increasing order of precedence:
 * a properties file, {@code INSTANCIO_*} environment variables,
 * and {@code instancio.*} system properties.
 */
public final class PropertiesLoader {

    private static final String DEFAULT_PROPERTIES_FILE = "instancio.properties";
    private static final String SYSTEM_PROPERTY_PREFIX = "instancio.";
    private static final String ENV_VAR_PREFIX = "INSTANCIO_";
    private static final String SUBTYPE_ENV_VAR_PREFIX = ENV_VAR_PREFIX + "SUBTYPE_";
    private static final String CONFIG_FILE_SYSTEM_PROPERTY = SYSTEM_PROPERTY_PREFIX + "config.file";
    private static final String CONFIG_FILE_ENV_VAR = ENV_VAR_PREFIX + "CONFIG_FILE";

    public record LoadedProperties(Map<Object, Object> properties, @Nullable String seedSource) {
    }

    private record Property(String key, Object value, String source) {
    }

    private PropertiesLoader() {
        // non-instantiable
    }

    public static LoadedProperties load() {
        return load(System.getProperties(), System.getenv());
    }

    /**
     * Blank environment variables and system properties count as unset,
     * e.g. a CI variable that expanded to nothing.
     */
    @VisibleForTesting
    static LoadedProperties load(final Properties systemProperties, final Map<String, String> environment) {
        final String configFile = getConfigFile(systemProperties, environment);
        final String fileSource = configFile == null ? DEFAULT_PROPERTIES_FILE : configFile;

        // In increasing order of precedence. Lower-case environment variables
        // come first, so that the upper-case name wins if both are set
        final List<Property> properties = new ArrayList<>();
        loadPropertiesFile(configFile, fileSource)
                .forEach((key, value) -> properties.add(new Property(key.toString(), value, fileSource)));
        properties.addAll(getEnvironmentVariables(environment, name -> name.toLowerCase(Locale.ROOT)));
        properties.addAll(getEnvironmentVariables(environment, name -> name.toUpperCase(Locale.ROOT)));
        properties.addAll(getSystemProperties(systemProperties));

        final Map<Object, Object> merged = new LinkedHashMap<>();
        String seedSource = null;
        for (Property property : properties) {
            // Re-insert so that higher-precedence values are applied last: setting
            // one bound of a range (e.g. collection.min.size) adjusts the other
            merged.remove(property.key());
            merged.put(property.key(), property.value());
            if (Keys.SEED.propertyKey().equals(property.key())) {
                seedSource = property.source();
            }
        }
        return new LoadedProperties(merged, seedSource);
    }

    /**
     * Only all-upper-case or all-lower-case names are accepted: lower case matters on Windows,
     * where variable names are case-insensitive, while mixed case would allow ambiguous duplicates.
     */
    private static List<Property> getEnvironmentVariables(
            final Map<String, String> environment,
            final UnaryOperator<String> toCase) {

        final String prefix = toCase.apply(ENV_VAR_PREFIX);
        final List<Property> properties = new ArrayList<>();
        environment.forEach((name, value) -> {
            if (!name.startsWith(prefix) || !name.equals(toCase.apply(name))
                    || name.equals(toCase.apply(CONFIG_FILE_ENV_VAR)) || StringUtils.isBlank(value)) {
                return;
            }
            if (name.startsWith(toCase.apply(SUBTYPE_ENV_VAR_PREFIX))) {
                // class names are case-sensitive and contain dots, so they cannot be recovered
                throw Fail.withUsageError("subtypes cannot be mapped via environment variables: %s%n"
                        + "Use a '%ssubtype.*' system property or a properties file instead",
                        name, SYSTEM_PROPERTY_PREFIX);
            }
            properties.add(new Property(toPropertyKey(name), value, name));
            Log.msg(Log.Category.PROPERTIES, "Found environment variable {}", name);
        });
        return properties;
    }

    private static List<Property> getSystemProperties(final Properties systemProperties) {
        final List<Property> properties = new ArrayList<>();
        for (String name : systemProperties.stringPropertyNames()) {
            final String value = systemProperties.getProperty(name);
            if (name.startsWith(SYSTEM_PROPERTY_PREFIX) && !CONFIG_FILE_SYSTEM_PROPERTY.equals(name)
                    && !StringUtils.isBlank(value)) {
                final String key = name.substring(SYSTEM_PROPERTY_PREFIX.length());
                properties.add(new Property(key, value, "-D" + name));
                Log.msg(Log.Category.PROPERTIES, "Found system property {}", name);
            }
        }
        return properties;
    }

    private static @Nullable String getConfigFile(final Properties systemProperties, final Map<String, String> environment) {
        final String fromSystemProperty = systemProperties.getProperty(CONFIG_FILE_SYSTEM_PROPERTY);
        if (!StringUtils.isBlank(fromSystemProperty)) {
            return fromSystemProperty;
        }
        final String fromEnvironment = environment.get(CONFIG_FILE_ENV_VAR);
        if (!StringUtils.isBlank(fromEnvironment)) {
            return fromEnvironment;
        }
        final String fromLowerCaseEnvironment = environment.get(CONFIG_FILE_ENV_VAR.toLowerCase(Locale.ROOT));
        return StringUtils.isBlank(fromLowerCaseEnvironment) ? null : fromLowerCaseEnvironment;
    }

    private static String toPropertyKey(final String envVar) {
        return envVar.substring(ENV_VAR_PREFIX.length()).toLowerCase(Locale.ROOT).replace('_', '.');
    }

    private static Properties loadPropertiesFile(@Nullable final String configFile, final String source) {
        final Properties properties = new Properties();
        try (InputStream inStream = configFile == null
                ? Thread.currentThread().getContextClassLoader().getResourceAsStream(DEFAULT_PROPERTIES_FILE)
                : Files.newInputStream(Path.of(configFile))) {

            if (inStream == null) {
                Log.msg(Log.Category.PROPERTIES, "No '{}' found on classpath", DEFAULT_PROPERTIES_FILE);
            } else {
                properties.load(inStream);
                Log.msg(Log.Category.PROPERTIES, "Loaded '{}'", source);
            }
        } catch (IOException | InvalidPathException ex) {
            throw Fail.withUsageError("failed loading properties file '%s'", source, ex);
        }
        return properties;
    }
}
