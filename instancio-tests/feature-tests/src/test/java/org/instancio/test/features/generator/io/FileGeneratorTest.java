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
package org.instancio.test.features.generator.io;

import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.exception.InstancioApiException;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.root;

@FeatureTag(Feature.FILE_GENERATOR)
@ExtendWith(InstancioExtension.class)
class FileGeneratorTest {

    @Test
    void defaultFile() {
        final File file = Instancio.create(File.class);
        assertThat(file.toString()).matches("^[a-z]{16}$");
        assertThat(file).doesNotExist();
    }

    @Test
    void asString() {
        final String result = Instancio.of(String.class)
                .generate(root(), gen -> gen.io().file().name(random -> "foo").asString())
                .create();

        assertThat(result).isEqualTo("foo");
    }

    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    @ParameterizedTest
    void nameValidation(final String name) {
        final InstancioApi<File> api = Instancio.of(File.class)
                .generate(root(), gen -> gen.io().file().name(random -> name));

        assertThatThrownBy(api::create)
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("generated name must not be blank");
    }

    @Nested
    class FileTest {
        @Test
        void prefixAndName() {
            final File file = Instancio.of(File.class)
                    .generate(root(), gen -> gen.io().file()
                            .prefix("prefix-")
                            .name(random -> "foo"))
                    .create();

            assertThat(file).doesNotExist();
            assertThat(file.toString()).matches("^prefix-foo$");
        }

        @Test
        void suffix() {
            final File file = Instancio.of(File.class)
                    .generate(root(), gen -> gen.io().file().suffix(".foo"))
                    .create();

            assertThat(file).doesNotExist();
            assertThat(file.toString()).matches("^[a-z]{16}\\.foo$");
        }

        @Test
        void prefixAndSuffix() {
            final File file = Instancio.of(File.class)
                    .generate(root(), gen -> gen.io().file()
                            .prefix("prefix-")
                            .suffix("-suffix"))
                    .create();

            assertThat(file).doesNotExist();
            assertThat(file.toString()).matches("^prefix-[a-z]{16}-suffix$");
        }

        @Test
        void prefixSuffixAndName() {
            final File file = Instancio.of(File.class)
                    .generate(root(), gen -> gen.io().file()
                            .prefix("prefix-")
                            .name(random -> random.digits(3))
                            .suffix("-suffix"))
                    .create();

            assertThat(file).doesNotExist();
            assertThat(file.toString()).matches("^prefix-[0-9]{3}-suffix$");
        }

        @Test
        void directory() {
            final File file = Instancio.of(File.class)
                    .generate(root(), gen -> gen.io().file("foo"))
                    .create();

            assertThat(file).doesNotExist();
            assertThat(file.toString()).matches("^foo.[a-z]{16}$");
        }

        @Test
        void directoriesPrefixNameAndSuffix() {
            final File file = Instancio.of(File.class)
                    .generate(root(), gen -> gen.io()
                            .file("foo", "bar")
                            .prefix("prefix-")
                            .name(random -> "name")
                            .suffix("-suffix"))
                    .create();

            assertThat(file).doesNotExist();
            assertThat(file.toString()).matches("^foo.bar.prefix-name-suffix$");
        }
    }
}
