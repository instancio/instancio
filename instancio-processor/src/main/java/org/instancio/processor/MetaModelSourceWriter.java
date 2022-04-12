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
package org.instancio.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

class MetaModelSourceWriter {
    private static final Logger LOG = LoggerFactory.getLogger(MetaModelSourceWriter.class);
    private static final String GENERATED_SOURCES_DIR = "generated-test-sources";
    private static final String ANNOTATIONS_DIR = "test-annotations";
    private static final String META_MODEL_FILE_SUFFIX = "_";
    private static final String JAVA_EXTENSION = ".java";

    private final Path buildDirectory;

    MetaModelSourceWriter(final Path buildDirectory) {
        this.buildDirectory = buildDirectory;
    }

    void writeSource(final MetaModelClass metaModelClass, final String source) throws IOException {
        final Path destinationDirectory = getDestinationDirectory(metaModelClass.getPackageName());
        final String fileName = metaModelClass.getSimpleName() + META_MODEL_FILE_SUFFIX + JAVA_EXTENSION;
        final Path outputFile = Paths.get(destinationDirectory.toString(), fileName);
        Files.createDirectories(destinationDirectory);
        Files.write(outputFile, source.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);
        LOG.debug("Wrote {}", outputFile);
    }

    private Path getDestinationDirectory(final String packageName) {
        final Path generatedSourcesDir = Paths.get(buildDirectory.toString(), GENERATED_SOURCES_DIR, ANNOTATIONS_DIR);
        final Path packageDir = getPackagePath(packageName);
        return packageDir == null
                ? generatedSourcesDir
                : Paths.get(generatedSourcesDir.toString(), packageDir.toString());
    }

    private Path getPackagePath(final String packageName) {
        return packageName == null ? null : Paths.get(packageName.replace('.', File.separatorChar));
    }

}
