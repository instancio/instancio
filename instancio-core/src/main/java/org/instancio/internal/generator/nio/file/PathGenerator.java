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
package org.instancio.internal.generator.nio.file;

import org.instancio.Random;
import org.instancio.exception.InstancioApiException;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.GeneratorSpec;
import org.instancio.generator.specs.PathGeneratorSpec;
import org.instancio.internal.generator.AbstractGenerator;
import org.instancio.internal.util.CollectionUtils;
import org.instancio.internal.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Locale;

public class PathGenerator extends AbstractGenerator<Path> implements PathGeneratorSpec {
    private static final int DEFAULT_NAME_LENGTH = 16;

    private enum CreatePathType {
        FILE, DIRECTORY
    }

    private final boolean isTemp;
    private final List<String> directories;
    private String prefix;
    private String suffix;
    private Generator<String> nameGenerator;
    private CreatePathType createPathType;

    public PathGenerator(final GeneratorContext context) {
        this(context, false);
    }

    public PathGenerator(
            final GeneratorContext context,
            final boolean isTemp,
            final String... directories) {

        super(context);
        this.isTemp = isTemp;
        this.directories = CollectionUtils.asList(directories);
    }

    @Override
    public PathGeneratorSpec prefix(final String prefix) {
        this.prefix = prefix;
        return this;
    }

    @Override
    public PathGeneratorSpec suffix(final String suffix) {
        this.suffix = suffix;
        return this;
    }

    @Override
    public PathGeneratorSpec name(final Generator<String> nameGenerator) {
        this.nameGenerator = nameGenerator;
        return this;
    }

    @Override
    public GeneratorSpec<Path> createFile() {
        createPathType = CreatePathType.FILE;
        return this;
    }

    @Override
    public GeneratorSpec<Path> createDirectory() {
        createPathType = CreatePathType.DIRECTORY;
        return this;
    }

    @Override
    public Path generate(final Random random) {
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
            throw new InstancioApiException(String.format(
                    "Error creating %s: %s",
                    createPathType.name().toLowerCase(Locale.ENGLISH), completePath), ex);
        }
    }

    Path createPath(final Path directoryPath, final Path completePath) throws IOException {
        if (createPathType == null) {
            return completePath;
        }

        if (!Files.exists(directoryPath)) {
            Files.createDirectories(directoryPath);
        }
        if (!Files.exists(completePath)) {
            if (createPathType == CreatePathType.FILE) {
                return Files.createFile(completePath);
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

        return Paths.get(StringUtils.concatNonNull(prefix, name, suffix));
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
            path.addFirst(System.getProperty("java.io.tmpdir"));
        }
        return path;
    }
}
