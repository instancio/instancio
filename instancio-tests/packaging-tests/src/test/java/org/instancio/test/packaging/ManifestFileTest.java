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
package org.instancio.test.packaging;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class ManifestFileTest {

    private static final String MANIFEST_MF_PATH = "META-INF/MANIFEST.MF";
    private static final String BUNDLE_NAME = "Bundle-Name";
    private static final String BUNDLE_SYMBOLIC_NAME = "Bundle-SymbolicName";
    private static final String BUNDLE_DESCRIPTION = "Bundle-Description";
    private static final String EXPORT_PACKAGE = "Export-Package";
    private static final String IMPORT_PACKAGE = "Import-Package";
    private static final String MULTI_RELEASE = "Multi-Release";

    private static final String[] INSTANCIO_CORE_EXPECTED_IMPORTS = {
            "jakarta.persistence;resolution:=optional",
            "jakarta.validation.constraints;resolution:=optional",
            "javax.xml.datatype;resolution:=optional",
            "org.hibernate.validator.constraints.pl;resolution:=optional",
            "org.hibernate.validator.constraints.time;resolution:=optional",
            "org.hibernate.validator.constraints;resolution:=optional",
            "org.instancio.documentation",
            "org.instancio.exception",
            "org.instancio.generator.specs",
            "org.instancio.spi",
            "org.slf4j,",
            "org.slf4j.helpers,",
            "sun.misc;resolution:=optional",
            "sun.reflect;resolution:=optional",
    };

    private static final String[] INSTANCIO_JUNIT_EXPECTED_IMPORTS = {
            "org.instancio.exception,",
            "org.instancio.settings,",
            "org.instancio.support,",
            "org.instancio,",
            "org.junit.jupiter.api.extension,",
            "org.junit.jupiter.params.provider;resolution:=optional",
            "org.junit.jupiter.params.support;resolution:=optional",
            "org.slf4j"
    };

    @Test
    void instancioCoreManifest() throws IOException {
        final String symbolicName = "org.instancio.core";
        final Manifest manifest = getManifest(symbolicName);
        final Attributes attrs = manifest.getMainAttributes();

        assertThat(attrs.getValue(BUNDLE_NAME))
                .isEqualTo("Instancio Core");

        assertThat(attrs.getValue(BUNDLE_DESCRIPTION))
                .isEqualTo("The standalone module of Instancio");

        assertThat(attrs.getOrDefault(MULTI_RELEASE, "false")).isEqualTo("false");

        assertImports(attrs, INSTANCIO_CORE_EXPECTED_IMPORTS);

        assertThat(getExportedPackages(attrs)).containsExactlyInAnyOrder(
                "org.instancio",
                "org.instancio.documentation",
                "org.instancio.exception",
                "org.instancio.generator",
                "org.instancio.generator.hints",
                "org.instancio.generator.specs",
                "org.instancio.generator.specs.can",
                "org.instancio.generator.specs.pol",
                "org.instancio.generator.specs.usa",
                "org.instancio.generators",
                "org.instancio.generators.can",
                "org.instancio.generators.pol",
                "org.instancio.generators.usa",
                "org.instancio.generators.bra",
                "org.instancio.generators.rus",
                "org.instancio.settings",
                "org.instancio.spi",
                "org.instancio.support"
        );
    }

    @Test
    void instancioJUnitManifest() throws IOException {
        final String symbolicName = "org.instancio.junit";
        final Manifest manifest = getManifest(symbolicName);
        final Attributes attrs = manifest.getMainAttributes();

        assertThat(attrs.getValue(BUNDLE_NAME))
                .isEqualTo("Instancio JUnit 5 Support");

        assertThat(attrs.getValue(BUNDLE_DESCRIPTION))
                .isEqualTo("Instancio integration with JUnit 5");

        assertThat(attrs.getOrDefault(MULTI_RELEASE, "false")).isEqualTo("false");

        assertImports(attrs, INSTANCIO_JUNIT_EXPECTED_IMPORTS);

        assertThat(getExportedPackages(attrs)).containsExactlyInAnyOrder(
                "org.instancio.junit",
                "org.junit.jupiter.api.extension",
                "org.junit.jupiter.params.provider"
        );
    }

    private static void assertImports(final Attributes attrs, final String[] expectedImports) {
        final String importPackage = attrs.getValue(IMPORT_PACKAGE);

        for (String packageImport : expectedImports) {
            assertThat(importPackage).contains(packageImport);
        }
    }

    private static Set<String> getExportedPackages(final Attributes attrs) {
        return Arrays.stream(attrs.getValue(EXPORT_PACKAGE).split(","))
                .map(e -> e.split(";")[0])
                .flatMap(s -> Arrays.stream(s.split(",")))
                .map(s -> s.replace("\"", ""))
                .collect(Collectors.toSet());
    }

    private Manifest getManifest(final String symbolicName) throws IOException {
        final Enumeration<URL> resources = getClass().getClassLoader()
                .getResources(MANIFEST_MF_PATH);

        while (resources.hasMoreElements()) {
            try (final InputStream is = resources.nextElement().openStream()) {
                final Manifest manifest = new Manifest(is);
                final Attributes attrs = manifest.getMainAttributes();
                final String value = attrs.getValue(BUNDLE_SYMBOLIC_NAME);
                if (symbolicName.equals(value)) {
                    return manifest;
                }
            }
        }
        return fail("Manifest with symbolic name '%s' not found", symbolicName);
    }
}
