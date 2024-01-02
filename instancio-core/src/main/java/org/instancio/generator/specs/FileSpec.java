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

import org.instancio.generator.Generator;
import org.instancio.generator.ValueSpec;

import java.io.File;
import java.io.InputStream;

/**
 * Spec for generating {@link File} values.
 *
 * @since 2.6.0
 */
public interface FileSpec extends FilePathSpec<File> {

    @Override
    FileSpec name(Generator<String> nameGenerator);

    @Override
    FileSpec prefix(String prefix);

    @Override
    FileSpec suffix(String suffix);

    @Override
    FileSpec tmp();

    @Override
    ValueSpec<File> createDirectory();

    @Override
    ValueSpec<File> createFile();

    @Override
    ValueSpec<File> createFile(InputStream content);
}
