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
package org.instancio.generator.specs;

import org.instancio.documentation.ExperimentalApi;
import org.instancio.exception.InstancioApiException;
import org.instancio.generator.AsStringGeneratorSpec;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorSpec;

import java.nio.file.Path;

/**
 * Generator spec for customising {@link Path Paths}.
 *
 * @since 2.1.0
 */
public interface PathGeneratorSpec extends AsStringGeneratorSpec<Path> {

    /**
     * File or directory name prefix.
     * No prefix is added by default.
     *
     * @param prefix to add to the file or directory name
     * @return spec builder
     * @since 2.1.0
     */
    PathGeneratorSpec prefix(String prefix);

    /**
     * File or directory name suffix.
     * No suffix is added by default.
     *
     * @param suffix to add to the file or directory name
     * @return spec builder
     * @since 2.1.0
     */
    PathGeneratorSpec suffix(String suffix);

    /**
     * Generator for the file or directory name.
     * If no generator is specified, a random 16-character lowercase file name
     * will be generated.
     *
     * @param nameGenerator for generating the file or directory name
     * @return spec builder
     * @since 2.1.0
     */
    PathGeneratorSpec name(Generator<String> nameGenerator);

    /**
     * Indicates that the generated path, including parent directories (if any),
     * should be created as a file in the file system.
     * <p>
     * If the file already exists, then no action will be taken.
     *
     * @return completed spec with no further methods
     * @throws InstancioApiException if an error occurs creating the path
     * @since 2.1.0
     */
    @ExperimentalApi
    GeneratorSpec<Path> createFile();

    /**
     * Indicates that the generated path, including parent directories (if any),
     * should be created as a directory in the file system.
     * <p>
     * If the directory already exists, then no action will be taken.
     *
     * @return completed spec with no further methods
     * @throws InstancioApiException if an error occurs creating the path
     * @since 2.1.0
     */
    @ExperimentalApi
    GeneratorSpec<Path> createDirectory();
}
