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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junitpioneer.jupiter.SetSystemProperty;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.root;

@FeatureTag(Feature.PATH_GENERATOR)
@ExtendWith(InstancioExtension.class)
@SetSystemProperty(key = SystemProperties.FAIL_ON_ERROR, value = "true")
class PathGeneratorCreateTest {

    @Test
    void createTmpFile() {
        final Path path = Instancio.of(Path.class)
                .generate(root(), gen -> gen.nio()
                        .tmp("test")
                        .createFile())
                .create();

        assertThat(path.toString()).matches(".tmp.test.[a-z]{16}");
        assertThat(path).isEmptyFile();
    }

    @Test
    void createTmpDirectory() {
        final Path path = Instancio.of(Path.class)
                .generate(root(), gen -> gen.nio()
                        .tmp("test")
                        .createDirectory())
                .create();

        assertThat(path.toString()).matches(".tmp.test.[a-z]{16}");
        assertThat(path).isDirectory();
    }
}
