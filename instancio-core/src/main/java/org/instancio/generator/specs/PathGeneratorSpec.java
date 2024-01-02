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
package org.instancio.generator.specs;

import org.instancio.documentation.ExperimentalApi;
import org.instancio.exception.InstancioApiException;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorSpec;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;

/**
 * Generator spec for customising {@link File Files} and {@link Path Paths}.
 *
 * @since 2.1.0
 */
public interface PathGeneratorSpec<T> extends GeneratorSpec<T> {

    /**
     * Generator for the file or directory name.
     * <p>
     * Generated path names are formed using the following elements:
     *
     * <pre>
     *     [prefix][name][suffix]
     * </pre>
     *
     * <ul>
     *   <li>Prefix and suffix are optional and are {@code null} by default.</li>
     *   <li>If no generator is supplied using this method, a random 16-character
     *       lowercase name will be generated.</li>
     *   <li>The concatenation of prefix, name, and suffix must not be blank.</li>
     * </ul>
     * <p>
     * Example:
     *
     * <pre>{@code
     * List<Path> paths = Instancio.ofList(Path.class)
     *         .generate(all(Path.class), gen -> gen.nio().path()
     *                 .prefix("queued_")
     *                 .name(random -> random.alphanumeric(8))
     *                 .suffix(".csv"))
     *         .create();
     *
     * // Sample paths: queued_t5LG4AUw.csv, queued_o0nBWkej.csv, etc
     * }</pre>
     *
     * @param nameGenerator for generating the name
     * @return spec builder
     * @since 2.1.0
     */
    PathGeneratorSpec<T> name(Generator<String> nameGenerator);

    /**
     * File or directory name prefix.
     * No prefix is added by default.
     *
     * @param prefix to add to the file or directory name
     * @return spec builder
     * @since 2.1.0
     */
    PathGeneratorSpec<T> prefix(String prefix);

    /**
     * File or directory name suffix.
     * No suffix is added by default.
     *
     * @param suffix to add to the file or directory name
     * @return spec builder
     * @since 2.1.0
     */
    PathGeneratorSpec<T> suffix(String suffix);

    /**
     * Generate path with {@code java.io.tmpdir} as the parent directory.
     *
     * @return spec builder
     * @since 2.1.0
     */
    PathGeneratorSpec<T> tmp();

    /**
     * Terminal method to indicate that the generated path, including parent
     * directories (if any), should be created as a directory in the file system.
     * <p>
     * If the directory already exists, then no action will be taken.
     *
     * @return completed spec with no further methods
     * @throws InstancioApiException if an error occurs creating the path
     * @see #createFile()
     * @see #createFile(InputStream)
     * @since 2.1.0
     */
    @ExperimentalApi
    GeneratorSpec<T> createDirectory();

    /**
     * Terminal method to indicate that the generated path, including parent
     * directories (if any), should be created as a file in the file system.
     * <p>
     * If the file already exists, then no action will be taken.
     *
     * @return completed spec with no further methods
     * @throws InstancioApiException if an error occurs creating the path
     * @see #createFile(InputStream)
     * @see #createDirectory()
     * @since 2.1.0
     */
    @ExperimentalApi
    GeneratorSpec<T> createFile();

    /**
     * Terminal method to indicate that the generated path, including parent
     * directories (if any), should be created as a file in the file system
     * and content provided by the input stream written to the file.
     * <p>
     * If the file already exists, then no action will be taken.
     *
     * @param content to write to the file
     * @return completed spec with no further methods
     * @throws InstancioApiException if an error occurs creating the path
     *                               or writing content to the file
     * @see #createFile()
     * @see #createDirectory()
     * @since 2.1.0
     */
    @ExperimentalApi
    GeneratorSpec<T> createFile(InputStream content);
}
