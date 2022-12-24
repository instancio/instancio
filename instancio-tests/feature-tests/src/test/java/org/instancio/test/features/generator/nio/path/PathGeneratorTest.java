/*
 * Copyright 2022 the original author or authors.
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
package org.instancio.test.features.generator.nio.path;

import org.instancio.Instancio;
import org.instancio.internal.util.SystemProperties;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junitpioneer.jupiter.SetSystemProperty;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.root;


@FeatureTag(Feature.PATH_GENERATOR)
@ExtendWith(InstancioExtension.class)
@SetSystemProperty(key = SystemProperties.FAIL_ON_ERROR, value = "true")
@SetSystemProperty(key = "java.io.tmpdir", value = PathGeneratorTest.TMP_DIR)
class PathGeneratorTest {

    static final String TMP_DIR = "tmp-override";

    @Test
    void defaultPath() {
        final Path path = Instancio.create(Path.class);
        assertThat(path.toString()).matches("^[a-z]{16}$");
        assertThat(path).doesNotExist();
    }

    @Test
    void asString() {
        final String result = Instancio.of(String.class)
                .generate(root(), gen -> gen.nio().path().name(random -> "foo").asString())
                .create();

        assertThat(result).isEqualTo("foo");
    }

    @Nested
    class PathTest {
        @Test
        void prefixAndName() {
            final Path path = Instancio.of(Path.class)
                    .generate(root(), gen -> gen.nio().path()
                            .prefix("prefix-")
                            .name(random -> "foo"))
                    .create();

            assertThat(path).doesNotExist();
            assertThat(path.toString()).matches("^prefix-foo$");
        }

        @Test
        void suffix() {
            final Path path = Instancio.of(Path.class)
                    .generate(root(), gen -> gen.nio().path().suffix(".foo"))
                    .create();

            assertThat(path).doesNotExist();
            assertThat(path.toString()).matches("^[a-z]{16}\\.foo$");
        }

        @Test
        void prefixAndSuffix() {
            final Path path = Instancio.of(Path.class)
                    .generate(root(), gen -> gen.nio().path()
                            .prefix("prefix-")
                            .suffix("-suffix"))
                    .create();

            assertThat(path).doesNotExist();
            assertThat(path.toString()).matches("^prefix-[a-z]{16}-suffix$");
        }

        @Test
        void prefixSuffixAndName() {
            final Path path = Instancio.of(Path.class)
                    .generate(root(), gen -> gen.nio().path()
                            .prefix("prefix-")
                            .name(random -> random.digits(3))
                            .suffix("-suffix"))
                    .create();

            assertThat(path).doesNotExist();
            assertThat(path.toString()).matches("^prefix-[0-9]{3}-suffix$");
        }

        @Test
        void directory() {
            final Path path = Instancio.of(Path.class)
                    .generate(root(), gen -> gen.nio().path("foo"))
                    .create();

            assertThat(path).doesNotExist();
            assertThat(path.toString()).matches("^foo.[a-z]{16}$");
        }

        @Test
        void directoriesPrefixNameAndSuffix() {
            final Path path = Instancio.of(Path.class)
                    .generate(root(), gen -> gen.nio()
                            .path("foo", "bar")
                            .prefix("prefix-")
                            .name(random -> "name")
                            .suffix("-suffix"))
                    .create();

            assertThat(path).doesNotExist();
            assertThat(path.toString()).matches("^foo.bar.prefix-name-suffix$");
        }
    }

    @Nested
    class TmpPathTest {
        @Test
        void prefixAndName() {
            final Path path = Instancio.of(Path.class)
                    .generate(root(), gen -> gen.nio().tmp()
                            .prefix("prefix-")
                            .name(random -> "foo"))
                    .create();

            assertThat(path).doesNotExist();
            assertThat(path.toString()).matches("^" + TMP_DIR + ".prefix-foo$");
        }

        @Test
        void suffix() {
            final Path path = Instancio.of(Path.class)
                    .generate(root(), gen -> gen.nio().tmp().suffix(".foo"))
                    .create();

            assertThat(path).doesNotExist();
            assertThat(path.toString()).matches("^" + TMP_DIR + ".[a-z]{16}\\.foo$");
        }

        @Test
        void prefixAndSuffix() {
            final Path path = Instancio.of(Path.class)
                    .generate(root(), gen -> gen.nio().tmp()
                            .prefix("prefix-")
                            .suffix("-suffix"))
                    .create();

            assertThat(path).doesNotExist();
            assertThat(path.toString()).matches("^" + TMP_DIR + ".prefix-[a-z]{16}-suffix$");
        }

        @Test
        void prefixSuffixAndName() {
            final Path path = Instancio.of(Path.class)
                    .generate(root(), gen -> gen.nio().tmp()
                            .prefix("prefix-")
                            .name(random -> random.digits(3))
                            .suffix("-suffix"))
                    .create();

            assertThat(path).doesNotExist();
            assertThat(path.toString()).matches("^" + TMP_DIR + ".prefix-[0-9]{3}-suffix$");
        }

        @Test
        void directory() {
            final Path path = Instancio.of(Path.class)
                    .generate(root(), gen -> gen.nio().tmp("foo"))
                    .create();

            assertThat(path).doesNotExist();
            assertThat(path.toString()).matches("^" + TMP_DIR + ".foo.[a-z]{16}$");
        }

        @Test
        void directoriesPrefixNameAndSuffix() {
            final Path path = Instancio.of(Path.class)
                    .generate(root(), gen -> gen.nio()
                            .tmp("foo", "bar")
                            .prefix("prefix-")
                            .name(random -> "name")
                            .suffix("-suffix"))
                    .create();

            assertThat(path).doesNotExist();
            assertThat(path.toString()).matches("^" + TMP_DIR + ".foo.bar.prefix-name-suffix$");
        }
    }
}
