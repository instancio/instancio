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
package org.instancio.internal.generator.io;

import org.instancio.Random;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.specs.FileSpec;
import org.instancio.internal.generator.AbstractGenerator;
import org.instancio.internal.generator.nio.file.PathGenerator;

import java.io.File;
import java.io.InputStream;

public class FileGenerator extends AbstractGenerator<File> implements FileSpec {

    private final PathGenerator delegate;

    // Required constructor to instantiating generator via reflection
    public FileGenerator(final GeneratorContext context) {
        super(context);
        delegate = new PathGenerator(context);
    }

    public FileGenerator(final GeneratorContext context, final String... directories) {
        super(context);
        delegate = new PathGenerator(context, directories);
    }

    @Override
    public String apiMethod() {
        return "file()";
    }

    @Override
    public FileGenerator tmp() {
        delegate.tmp();
        return this;
    }

    @Override
    public FileGenerator prefix(final String prefix) {
        delegate.prefix(prefix);
        return this;
    }

    @Override
    public FileGenerator suffix(final String suffix) {
        delegate.suffix(suffix);
        return this;
    }

    @Override
    public FileGenerator name(final Generator<String> nameGenerator) {
        delegate.name(nameGenerator);
        return this;
    }

    @Override
    public FileGenerator createFile(final InputStream content) {
        delegate.createFile(content);
        return this;
    }

    @Override
    public FileGenerator createFile() {
        delegate.createFile();
        return this;
    }

    @Override
    public FileGenerator createDirectory() {
        delegate.createDirectory();
        return this;
    }

    @Override
    public FileGenerator nullable() {
        super.nullable();
        return this;
    }

    @Override
    protected File tryGenerateNonNull(final Random random) {
        return delegate.tryGenerateNonNull(random).toFile();
    }
}
