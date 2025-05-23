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
package org.instancio.generators;

import org.instancio.generator.specs.PathGeneratorSpec;

import java.io.InputStream;
import java.nio.file.Path;

/**
 * Contains built-in generators for {@code java.nio} classes.
 *
 * @since 2.1.0
 */
public interface NioGenerators {

    /**
     * Generator for {@link Path} objects.
     *
     * <p>Note that files or directories will <b>not</b> be created in the file
     * system unless one of the following methods is invoked:
     *
     * <ul>
     *   <li>{@link PathGeneratorSpec#createFile()}</li>
     *   <li>{@link PathGeneratorSpec#createFile(InputStream)}</li>
     *   <li>{@link PathGeneratorSpec#createDirectory()}</li>
     * </ul>
     *
     * @param subdirectories optional parameter for specifying one or more
     *                       parent directories of the generated files
     * @return generator spec for paths
     * @since 2.1.0
     */
    PathGeneratorSpec<Path> path(String... subdirectories);
}
