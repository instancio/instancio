/*
 * Copyright 2022-2024 the original author or authors.
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
package org.instancio.test.features.generator.io;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.root;

/**
 * Tests verifying creation of temporary files/directories.
 */
@FeatureTag(Feature.FILE_GENERATOR)
@ExtendWith(InstancioExtension.class)
class FileGeneratorCreateTest {

    private static final String TMP_DIR = System.getProperty("java.io.tmpdir");

    private static String pattern(final String name) {
        return ".*[\\\\|/]{1}" + name + "[\\\\|/]{1}[a-z]{16}";
    }

    @Test
    void createTemporaryDirectory() {
        final String expectedName = "a-temp-dir";
        final File file = Instancio.of(File.class)
                .generate(root(), gen -> gen.io().file(expectedName)
                        .tmp()
                        .createDirectory())
                .create();

        assertThat(file).isNotNull().isDirectory();
        assertThat(file.toString()).startsWith(TMP_DIR).matches(pattern(expectedName));
    }

    @Test
    void createTemporaryFile() {
        final String expectedName = "a-temp-file";
        final File file = Instancio.of(File.class)
                .generate(root(), gen -> gen.io().file(expectedName)
                        .tmp()
                        .createFile())
                .create();

        assertThat(file.toString()).startsWith(TMP_DIR).matches(pattern(expectedName));
        assertThat(file).isEmpty();
    }

    @Test
    void createTemporaryFileWithContent() {
        final String content = "hello world";
        final AtomicBoolean wasClosed = new AtomicBoolean();
        final InputStream in = new ByteArrayInputStream(content.getBytes()) {
            @Override
            public void close() {
                wasClosed.set(true);
            }
        };
        final File file = Instancio.of(File.class)
                .generate(root(), gen -> gen.io().file().tmp().createFile(in))
                .create();

        assertThat(file).hasContent(content);
        assertThat(in).isEmpty();
        assertThat(wasClosed).isTrue();
    }
}
