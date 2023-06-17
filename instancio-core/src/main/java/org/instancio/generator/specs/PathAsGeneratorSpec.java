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
package org.instancio.generator.specs;

import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorSpec;

import java.io.InputStream;

/**
 * Generator spec for path values that supports {@link AsGeneratorSpec}.
 *
 * @since 2.6.0
 */
public interface PathAsGeneratorSpec<T>
        extends PathGeneratorSpec<T>, AsGeneratorSpec<T> {

    @Override
    PathAsGeneratorSpec<T> name(Generator<String> nameGenerator);

    @Override
    PathAsGeneratorSpec<T> prefix(String prefix);

    @Override
    PathAsGeneratorSpec<T> suffix(String suffix);

    @Override
    PathAsGeneratorSpec<T> tmp();

    @Override
    GeneratorSpec<T> createDirectory();

    @Override
    GeneratorSpec<T> createFile();

    @Override
    GeneratorSpec<T> createFile(InputStream content);
}
