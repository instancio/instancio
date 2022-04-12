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

import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

class MetaModelSourceWriterTest {

    @TempDir
    private Path buildDir;

    private MetaModelSourceWriter sourceWriter;

    @BeforeEach
    void setUp() {
        sourceWriter = new MetaModelSourceWriter(buildDir);
    }

    @Test
    void writeSource() throws IOException {
        final String expectedContent = "foo\nbar\nbaz";
        final MetaModelClass metaModelClass = Instancio.create(MetaModelClass.class);
        sourceWriter.writeSource(metaModelClass, expectedContent);

        final Path outputPath = Paths.get(
                buildDir.toString(),
                "generated-test-sources",
                "test-annotations",
                metaModelClass.getPackageName(),
                metaModelClass.getSimpleName() + "_.java");

        assertThat(new String(Files.readAllBytes(outputPath))).isEqualTo(expectedContent);
    }
}
