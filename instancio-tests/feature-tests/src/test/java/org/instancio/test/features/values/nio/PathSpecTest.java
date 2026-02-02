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
package org.instancio.test.features.values.nio;

import org.instancio.Instancio;
import org.instancio.generator.specs.PathSpec;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.features.values.AbstractValueSpecTestTemplate;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag(Feature.VALUE_SPEC)
@ExtendWith(InstancioExtension.class)
class PathSpecTest extends AbstractValueSpecTestTemplate<Path> {

    @Override
    protected PathSpec spec() {
        return Instancio.gen().nio().path();
    }

    @Test
    void name() {
        assertThat(spec().name(r -> "foo").get()).hasFileName("foo");
    }

    @Test
    void prefix() {
        assertThat(spec().prefix("prefix").get().toString()).startsWith("prefix");
    }

    @Test
    void suffix() {
        assertThat(spec().suffix("suffix").get().toString()).endsWith("suffix");
    }

    @Test
    void tmp() {
        assertThat(spec().tmp().get().toString()).contains(System.getProperty("java.io.tmpdir"));
    }

    @Test
    void createDirectory() {
        final Path actual = spec().tmp().createDirectory().get();
        assertThat(actual).exists().isDirectory();
    }

    @Test
    void createFile() {
        final Path actual = spec().tmp().createFile().get();
        assertThat(actual).exists().isEmptyFile();
    }

    @Test
    void createFileWithContent() {
        final InputStream is = new ByteArrayInputStream("foo".getBytes());
        final Path actual = spec().tmp().createFile(is).get();
        assertThat(actual).hasContent("foo");
    }

    @Test
    void subdirectories() {
        final Path actual = Instancio.gen().nio().path("foo", "bar").get();

        assertThat(actual.toFile()).hasParent("foo/bar");
    }
}
