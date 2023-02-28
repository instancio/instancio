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
package org.instancio.spi;

import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorContext;

import java.util.Map;

/**
 * A Service Provider Interface for mapping classes to custom {@link Generator}
 * implementations.
 *
 * <p>Implementations of this class can be registered by placing a file named
 * {@code org.instancio.spi.GeneratorProvider} under {@code /META-INF/services}.
 * The file must contain the fully-qualified name of the implementing class.</p>
 *
 * <p>For example, the following sample implementation:</p>
 *
 * <pre>{@code
 * package com.example;
 *
 * // imports omitted
 *
 * public class SampleGeneratorProvider implements GeneratorProvider {
 *
 *     @Override
 *     public Map<Class<?>, Generator<?>> getGenerators() {
 *         Map<Class<?>, Generator<?>> map = new HashMap<>();
 *         map.put(Book.class, new BookGenerator());
 *         map.put(Author.class, new AuthorGenerator());
 *         return map;
 *     }
 * }
 * }</pre>
 *
 * <p>can be registered by creating a file
 * {@code /META-INF/services/org.instancio.spi.GeneratorProvider}
 * containing the following line:</p>
 *
 * <pre>{@code
 * com.example.SampleGeneratorProvider
 * }</pre>
 *
 * @see Generator
 * @since 1.2.0
 * @deprecated use {@link InstancioServiceProvider} instead.
 * This class will be removed in version {@code 3.0.0}.
 */
@Deprecated
public interface GeneratorProvider {

    /**
     * Provides a map of generators to register.
     *
     * @param context generator context
     * @return class to generator mapping
     * @since 1.2.0
     * @deprecated this method will be removed in version {@code 3.0.0};
     * use {@link InstancioServiceProvider#getGeneratorProvider()} instead.
     */
    @Deprecated
    Map<Class<?>, Generator<?>> getGenerators(GeneratorContext context);
}
