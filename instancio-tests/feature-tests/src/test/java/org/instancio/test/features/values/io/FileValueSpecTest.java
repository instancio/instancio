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
package org.instancio.test.features.values.io;

import org.instancio.Instancio;
import org.instancio.generator.specs.FileSpec;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.features.values.AbstractValueSpecTestTemplate;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag(Feature.VALUE_SPEC)
@ExtendWith(InstancioExtension.class)
class FileValueSpecTest extends AbstractValueSpecTestTemplate<File> {

    @Override
    protected FileSpec spec() {
        return Instancio.gen().io().file();
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
        final File actual = spec().tmp().createDirectory().get();
        assertThat(actual).exists().isDirectory();
    }

    @Test
    void createFile() {
        final File actual = spec().tmp().createFile().get();
        assertThat(actual).exists().isEmpty();
    }

    @Test
    void createFileWithContent() {
        final InputStream is = new ByteArrayInputStream("foo".getBytes());
        final File actual = spec().tmp().createFile(is).get();
        assertThat(actual).hasContent("foo");
    }

    @Test
    void subdirectories() {
        final File actual = Instancio.gen().io().file("foo", "bar").get();

        assertThat(actual).hasParent("foo/bar");
    }
}
