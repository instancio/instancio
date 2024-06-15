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
package org.instancio.internal.generator.nio.file;

import org.instancio.Random;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.specs.PathSpec;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.generator.AbstractGenerator;
import org.instancio.internal.util.CollectionUtils;
import org.instancio.internal.util.Fail;
import org.instancio.internal.util.IOUtils;
import org.instancio.internal.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Locale;

public class PathGenerator extends AbstractGenerator<Path> implements PathSpec {

    private static final int DEFAULT_NAME_LENGTH = 16;

    private enum CreatePathType {
        FILE, DIRECTORY
    }

    private final List<String> directories;
    private boolean isTemp;
    private String prefix;
    private String suffix;
    private Generator<String> nameGenerator;
    private CreatePathType createPathType;
    private InputStream inputStream;

    // Required constructor to instantiating generator via reflection
    public PathGenerator(final GeneratorContext context) {
        super(context);
        this.directories = Collections.emptyList();
    }

    public PathGenerator(final GeneratorContext context, final String... directories) {
        super(context);
        this.directories = CollectionUtils.asUnmodifiableList(directories);
    }

    @Override
    public String apiMethod() {
        return "path()";
    }

    @Override
    public PathGenerator tmp() {
        this.isTemp = true;
        return this;
    }

    @Override
    public PathGenerator prefix(final String prefix) {
        this.prefix = prefix;
        return this;
    }

    @Override
    public PathGenerator suffix(final String suffix) {
        this.suffix = suffix;
        return this;
    }

    @Override
    public PathGenerator name(final Generator<String> nameGenerator) {
        this.nameGenerator = nameGenerator;
        return this;
    }

    @Override
    public PathGenerator createFile(final InputStream content) {
        this.inputStream = content;
        this.createPathType = CreatePathType.FILE;
        return this;
    }

    @Override
    public PathGenerator createFile() {
        createPathType = CreatePathType.FILE;
        return this;
    }

    @Override
    public PathGenerator createDirectory() {
        createPathType = CreatePathType.DIRECTORY;
        return this;
    }

    @Override
    public PathGenerator nullable() {
        super.nullable();
        return this;
    }

    @Override
    public Path tryGenerateNonNull(final Random random) {
        final Path directoryPath = getDirectoryPath();
        final Path leafNameAsPath = getLeafNameAsPath(random);
        final Path completePath = directoryPath == null
                ? leafNameAsPath
                : directoryPath.resolve(leafNameAsPath);

        return createIfNeeded(directoryPath, completePath);
    }

    private Path createIfNeeded(final Path directoryPath, final Path completePath) {
        try {
            return createPath(directoryPath, completePath);
        } catch (IOException ex) {
            throw Fail.withUsageError(String.format(
                    "error generating %s: %s",
                    createPathType.name().toLowerCase(Locale.ENGLISH), completePath), ex);
        }
    }

    private Path createPath(final Path directoryPath, final Path completePath) throws IOException {
        if (createPathType == null) {
            return completePath;
        }

        if (!Files.exists(directoryPath)) {
            Files.createDirectories(directoryPath);
        }
        if (!Files.exists(completePath)) {
            if (createPathType == CreatePathType.FILE) {
                final Path file = Files.createFile(completePath);
                if (inputStream != null) {
                    IOUtils.writeTo(file, inputStream);
                }
                return file;
            } else if (createPathType == CreatePathType.DIRECTORY) {
                return Files.createDirectory(completePath);
            }
        }
        return completePath;
    }

    private Path getLeafNameAsPath(final Random random) {
        final String name = nameGenerator == null
                ? random.lowerCaseAlphabetic(DEFAULT_NAME_LENGTH)
                : nameGenerator.generate(random);

        final String pathName = StringUtils.concatNonNull(prefix, name, suffix);
        ApiValidator.isFalse(StringUtils.isBlank(pathName), "generated name must not be blank");
        return Paths.get(pathName);
    }

    private Path getDirectoryPath() {
        Path result = null;

        final Deque<String> dirs = getDirectories();

        if (dirs.size() == 1) {
            result = Paths.get(dirs.pollFirst());
        } else if (dirs.size() > 1) {
            final String first = dirs.pollFirst();
            final String[] remainder = new String[dirs.size()];

            for (int i = 0; i < remainder.length; i++) {
                remainder[i] = dirs.pollFirst();
            }

            result = Paths.get(first, remainder);
        }

        return result;
    }

    private Deque<String> getDirectories() {
        final Deque<String> path = new ArrayDeque<>(directories);
        if (isTemp) {
            final String tmpDir = ApiValidator.notNull(System.getProperty("java.io.tmpdir"),
                    "Cannot resolve temporary directory: 'java.io.tmpdir' system property is null");
            path.addFirst(tmpDir);
        }
        return path;
    }
}
