/*
 * Copyright 2022-2023 the original author or authors.
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
package org.instancio.test.features.values.nio;

import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Gen.nio;

@FeatureTag(Feature.VALUE_SPEC)
class PathSpecTest {

    @Test
    void get() {
        assertThat(nio().path().get()).isNotNull();
    }

    @Test
    void list() {
        final int size = 10;
        final List<Path> results = nio().path().list(size);
        assertThat(results).hasSize(size);
    }

    @Test
    void map() {
        final URI result = nio().path().map(Path::toUri);
        assertThat(result).isNotNull();
    }

    @Test
    void name() {
        assertThat(nio().path().name(r -> "foo").get()).hasFileName("foo");
    }

    @Test
    void prefix() {
        assertThat(nio().path().prefix("prefix").get().toString()).startsWith("prefix");
    }

    @Test
    void suffix() {
        assertThat(nio().path().suffix("suffix").get().toString()).endsWith("suffix");
    }

    @Test
    void tmp() {
        assertThat(nio().path().tmp().get().toString()).contains(System.getProperty("java.io.tmpdir"));
    }

    @Test
    void createDirectory() {
        final Path actual = nio().path().tmp().createDirectory().get();
        assertThat(actual).exists().isDirectory();
    }

    @Test
    void createFile() {
        final Path actual = nio().path().tmp().createFile().get();
        assertThat(actual).exists().isEmptyFile();
    }

    @Test
    void createFileWithContent() {
        final InputStream is = new ByteArrayInputStream("foo".getBytes());
        final Path actual = nio().path().tmp().createFile(is).get();
        assertThat(actual).hasContent("foo");
    }
}
