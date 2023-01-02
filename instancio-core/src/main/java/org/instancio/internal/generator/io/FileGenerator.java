/*
 * Copyright 2022-2023 the original author or authors.
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
import org.instancio.generator.GeneratorSpec;
import org.instancio.generator.specs.PathGeneratorSpec;
import org.instancio.internal.generator.AbstractGenerator;
import org.instancio.internal.generator.nio.file.PathGenerator;

import java.io.File;
import java.io.InputStream;

public class FileGenerator extends AbstractGenerator<File> implements PathGeneratorSpec<File> {

    private final PathGenerator delegate;

    public FileGenerator(final GeneratorContext context, final String... directories) {
        super(context);
        delegate = new PathGenerator(context, directories);
    }

    @Override
    public String apiMethod() {
        return "file()";
    }

    @Override
    public PathGeneratorSpec<File> tmp() {
        delegate.tmp();
        return this;
    }

    @Override
    public PathGeneratorSpec<File> prefix(final String prefix) {
        delegate.prefix(prefix);
        return this;
    }

    @Override
    public PathGeneratorSpec<File> suffix(final String suffix) {
        delegate.suffix(suffix);
        return this;
    }

    @Override
    public PathGeneratorSpec<File> name(final Generator<String> nameGenerator) {
        delegate.name(nameGenerator);
        return this;
    }

    @Override
    public PathGeneratorSpec<File> createFile(final InputStream content) {
        delegate.createFile(content);
        return this;
    }

    @Override
    public GeneratorSpec<File> createFile() {
        delegate.createFile();
        return this;
    }

    @Override
    public GeneratorSpec<File> createDirectory() {
        delegate.createDirectory();
        return this;
    }

    @Override
    public File generate(final Random random) {
        return delegate.generate(random).toFile();
    }
}
