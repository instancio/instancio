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

import org.instancio.exception.InstancioApiException;
import org.instancio.internal.context.PropertiesLoader.LoadedProperties;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PropertiesLoaderTest {

    @TempDir
    private Path tempDir;

    private Path writeFile(final String content) throws IOException {
        return Files.writeString(Files.createTempFile(tempDir, "settings", ""), content);
    }

    private static LoadedProperties loadWithContextClassLoader(final ClassLoader classLoader) {
        final Thread thread = Thread.currentThread();
        final ClassLoader original = thread.getContextClassLoader();
        thread.setContextClassLoader(classLoader);
        try {
            return PropertiesLoader.load(new Properties(), Map.of());
        } finally {
            thread.setContextClassLoader(original);
        }
    }

    private static Properties systemProperties(final String... keyValues) {
        final Properties properties = new Properties();
        for (int i = 0; i < keyValues.length; i += 2) {
            properties.setProperty(keyValues[i], keyValues[i + 1]);
        }
        return properties;
    }

    @Test
    void defaultPropertiesFile() {
        final LoadedProperties result = PropertiesLoader.load(new Properties(), Map.of());

        assertThat(result.properties()).containsEntry("user.defined.key", "12345");
    }

    @Test
    void seedSourceFromDefaultPropertiesFile() {
        final ClassLoader classLoader = new ClassLoader(null) {
            @Override
            public InputStream getResourceAsStream(final String name) {
                return new ByteArrayInputStream("seed=1".getBytes(StandardCharsets.ISO_8859_1));
            }
        };

        assertThat(loadWithContextClassLoader(classLoader).seedSource()).isEqualTo("instancio.properties");
    }

    @Test
    void seedSourceFollowsPrecedence() throws IOException {
        final Path file = writeFile("seed=1");
        final Properties systemProperties = systemProperties("instancio.config.file", file.toString());

        assertThat(PropertiesLoader.load(systemProperties, Map.of()).seedSource())
                .isEqualTo(file.toString());
        assertThat(PropertiesLoader.load(systemProperties, Map.of("INSTANCIO_SEED", "2")).seedSource())
                .isEqualTo("INSTANCIO_SEED");

        systemProperties.setProperty("instancio.seed", "3");
        assertThat(PropertiesLoader.load(systemProperties, Map.of("INSTANCIO_SEED", "2")).seedSource())
                .isEqualTo("-Dinstancio.seed");
    }

    @Test
    void noSeedSourceWithoutSeed() {
        assertThat(PropertiesLoader.load(new Properties(), Map.of()).seedSource()).isNull();
    }

    @Test
    void defaultPropertiesFileNotFound() {
        final LoadedProperties result = loadWithContextClassLoader(new ClassLoader(null) {});

        assertThat(result.properties()).isEmpty();
    }

    @Test
    void defaultPropertiesFileUnreadable() {
        final ClassLoader classLoader = new ClassLoader(null) {
            @Override
            public InputStream getResourceAsStream(final String name) {
                return new InputStream() {
                    @Override
                    public int read() throws IOException {
                        throw new IOException("expected");
                    }
                };
            }
        };

        assertThatThrownBy(() -> loadWithContextClassLoader(classLoader))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("failed loading properties file 'instancio.properties'")
                .hasRootCauseMessage("expected");
    }

    @Test
    void configFileReplacesDefaultFile() throws IOException {
        final Path file = writeFile("long.max=12");

        final LoadedProperties result = PropertiesLoader.load(
                systemProperties("instancio.config.file", file.toString()), Map.of());

        assertThat(result.properties()).containsExactly(Map.entry("long.max", "12"));
    }

    @Test
    void configFileFromEnvironmentVariable() throws IOException {
        final Path file = writeFile("long.max=12");

        final LoadedProperties result = PropertiesLoader.load(
                new Properties(), Map.of("INSTANCIO_CONFIG_FILE", file.toString()));

        assertThat(result.properties()).containsExactly(Map.entry("long.max", "12"));
    }

    @Test
    void configFileSystemPropertyTakesPrecedenceOverEnvironmentVariable() throws IOException {
        final Path fromSystemProperty = writeFile("long.max=12");
        final Path fromEnvironment = writeFile("long.max=34");

        final LoadedProperties result = PropertiesLoader.load(
                systemProperties("instancio.config.file", fromSystemProperty.toString()),
                Map.of("INSTANCIO_CONFIG_FILE", fromEnvironment.toString()));

        assertThat(result.properties()).containsEntry("long.max", "12");
    }

    @ParameterizedTest
    @ValueSource(strings = {"non-existent.properties", "classpath:custom-instancio-test.properties"})
    void configFileNotFound(final String location) {
        final Properties systemProperties = systemProperties("instancio.config.file", location);
        final Map<String, String> environment = Map.of();

        assertThatThrownBy(() -> PropertiesLoader.load(systemProperties, environment))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("failed loading properties file '%s'", location);
    }

    @Test
    void configFileWithInvalidPath() {
        final Properties systemProperties = systemProperties("instancio.config.file", "invalid\0path");
        final Map<String, String> environment = Map.of();

        assertThatThrownBy(() -> PropertiesLoader.load(systemProperties, environment))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("failed loading properties file 'invalid");
    }

    @Test
    void blankConfigFileIsIgnored() throws IOException {
        final Path file = writeFile("long.max=12");

        assertThat(PropertiesLoader.load(systemProperties("instancio.config.file", " "), Map.of()).properties())
                .as("blank system property and no environment variable: default file")
                .containsEntry("user.defined.key", "12345");

        assertThat(PropertiesLoader.load(new Properties(), Map.of("INSTANCIO_CONFIG_FILE", "")).properties())
                .as("blank environment variable: default file")
                .containsEntry("user.defined.key", "12345");

        assertThat(PropertiesLoader.load(
                systemProperties("instancio.config.file", ""),
                Map.of("INSTANCIO_CONFIG_FILE", file.toString())).properties())
                .as("blank system property: environment variable is used")
                .containsExactly(Map.entry("long.max", "12"));
    }

    /**
     * Setting one bound of a range adjusts the other, so an override of a single bound
     * must be applied after the file's values, whichever order the keys hash in.
     */
    @ParameterizedTest
    @CsvSource({
            "string.min.length, string.max.length",
            "collection.min.size, collection.max.size",
            "map.min.size, map.max.size",
            "array.min.size, array.max.size",
            "integer.min, integer.max",
            "long.min, long.max"
    })
    void singleRangeBoundOverridesFile(final String minKey, final String maxKey) throws IOException {
        final Path file = writeFile(minKey + "=3\n" + maxKey + "=9");
        final String minEnvVar = "INSTANCIO_" + minKey.toUpperCase(Locale.ROOT).replace('.', '_');

        final Settings raisedMin = Settings.from(PropertiesLoader.load(
                systemProperties("instancio.config.file", file.toString()),
                Map.of(minEnvVar, "20")).properties());

        assertThat(((Number) raisedMin.get(Keys.get(minKey))).longValue()).isEqualTo(20);
        assertThat(((Number) raisedMin.get(Keys.get(maxKey))).longValue()).isGreaterThanOrEqualTo(20);

        final Settings loweredMax = Settings.from(PropertiesLoader.load(
                systemProperties("instancio.config.file", file.toString(), "instancio." + maxKey, "1"),
                Map.of()).properties());

        assertThat(((Number) loweredMax.get(Keys.get(maxKey))).longValue()).isEqualTo(1);
        assertThat(((Number) loweredMax.get(Keys.get(minKey))).longValue()).isLessThanOrEqualTo(1);
    }

    @Test
    void environmentVariables() {
        final LoadedProperties result = PropertiesLoader.load(new Properties(), Map.of(
                "INSTANCIO_STRING_MIN_LENGTH", "5",
                "INSTANCIO_MY_CUSTOM_KEY", "foo"));

        assertThat(result.properties())
                .containsEntry("string.min.length", "5")
                .containsEntry("my.custom.key", "foo");
    }

    @Test
    void blankEnvironmentVariablesAndSystemPropertiesAreIgnored() throws IOException {
        final Path file = writeFile("long.max=12");

        final LoadedProperties result = PropertiesLoader.load(
                systemProperties(
                        "instancio.config.file", file.toString(),
                        "instancio.overwrite.existing.values", " "),
                Map.of(
                        "INSTANCIO_LONG_MAX", "",
                        "instancio_seed", "",
                        "INSTANCIO_SUBTYPE_JAVA_UTIL_LIST", ""));

        assertThat(result.properties()).containsExactly(Map.entry("long.max", "12"));
    }

    @Test
    void blankUpperCaseEnvironmentVariableDoesNotHideLowerCase() {
        final LoadedProperties result = PropertiesLoader.load(new Properties(), Map.of(
                "INSTANCIO_SEED", "",
                "instancio_seed", "5"));

        assertThat(result.properties()).containsEntry("seed", "5");
        assertThat(result.seedSource()).isEqualTo("instancio_seed");
    }

    @Test
    void lowerCaseEnvironmentVariables() {
        final LoadedProperties result = PropertiesLoader.load(new Properties(), Map.of(
                "instancio_string_min_length", "5"));

        assertThat(result.properties()).containsEntry("string.min.length", "5");
    }

    @Test
    void upperCaseEnvironmentVariableTakesPrecedenceOverLowerCase() {
        final LoadedProperties result = PropertiesLoader.load(new Properties(), Map.of(
                "INSTANCIO_SEED", "1",
                "instancio_seed", "2"));

        assertThat(result.properties()).containsEntry("seed", "1");
        assertThat(result.seedSource()).isEqualTo("INSTANCIO_SEED");
    }

    @Test
    void configFileFromLowerCaseEnvironmentVariable() throws IOException {
        final Path upperCase = writeFile("long.max=12");
        final Path lowerCase = writeFile("long.max=34");

        assertThat(PropertiesLoader.load(new Properties(), Map.of("instancio_config_file", lowerCase.toString())).properties())
                .containsExactly(Map.entry("long.max", "34"));

        assertThat(PropertiesLoader.load(new Properties(), Map.of(
                "INSTANCIO_CONFIG_FILE", upperCase.toString(),
                "instancio_config_file", lowerCase.toString())).properties())
                .as("upper case takes precedence")
                .containsExactly(Map.entry("long.max", "12"));
    }

    @Test
    void systemProperties() {
        final LoadedProperties result = PropertiesLoader.load(systemProperties(
                "instancio.string.min.length", "5",
                "instancio.subtype.java.util.List", "java.util.LinkedList"), Map.of());

        assertThat(result.properties())
                .containsEntry("string.min.length", "5")
                .containsEntry("subtype.java.util.List", "java.util.LinkedList");
    }

    @Test
    void precedence() throws IOException {
        final Path file = writeFile("long.nullable=true\nlong.max=1\nlong.min=-1");

        final LoadedProperties result = PropertiesLoader.load(
                systemProperties(
                        "instancio.config.file", file.toString(),
                        "instancio.long.min", "-3"),
                Map.of(
                        "INSTANCIO_LONG_MIN", "-2",
                        "INSTANCIO_LONG_MAX", "2"));

        assertThat(result.properties())
                .containsEntry("long.nullable", "true")
                .containsEntry("long.max", "2")
                .containsEntry("long.min", "-3");
    }

    @Test
    void ignoresUnprefixedAndMixedCaseNamesAndConfigFileLocation() throws IOException {
        final Path file = writeFile("");

        final LoadedProperties result = PropertiesLoader.load(
                systemProperties(
                        "seed", "1",
                        "instancio.config.file", file.toString()),
                Map.of(
                        "SEED", "1",
                        "Instancio_Seed", "1",
                        "INSTANCIO_Seed", "1",
                        "INSTANCIO_CONFIG_FILE", file.toString(),
                        "instancio_config_file", file.toString()));

        assertThat(result.properties()).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"INSTANCIO_SUBTYPE_JAVA_UTIL_LIST", "instancio_subtype_java_util_list"})
    void subtypeFromEnvironmentVariableIsNotSupported(final String name) {
        final Properties systemProperties = new Properties();
        final Map<String, String> environment = Map.of(name, "java.util.LinkedList");

        assertThatThrownBy(() -> PropertiesLoader.load(systemProperties, environment))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("subtypes cannot be mapped via environment variables: " + name);
    }
}
