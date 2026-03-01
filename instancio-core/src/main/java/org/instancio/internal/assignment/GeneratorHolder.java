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
package org.instancio.internal.assignment;

import org.instancio.GeneratorSpecProvider;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorSpec;
import org.instancio.generators.Generators;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.generator.misc.GeneratorDecorator;
import org.jspecify.annotations.Nullable;

import java.util.function.Supplier;

public final class GeneratorHolder {

    private final @Nullable Generator<?> generator;
    private final @Nullable GeneratorSpecProvider<?> specProvider;

    private GeneratorHolder(
            @Nullable final Generator<?> generator,
            @Nullable final GeneratorSpecProvider<?> specProvider) {

        this.generator = generator;
        this.specProvider = specProvider;
    }

    static GeneratorHolder of(final Generator<?> generator) {
        ApiValidator.validateGeneratorNotNull(generator);
        return new GeneratorHolder(generator, null);
    }

    static GeneratorHolder of(final GeneratorSpec<?> spec) {
        ApiValidator.validateGenerateSecondArgument(spec);
        return new GeneratorHolder((Generator<?>) spec, null);
    }

    static <T> GeneratorHolder of(final Supplier<T> supplier) {
        ApiValidator.validateSupplierNotNull(supplier);
        final Generator<T> generator = GeneratorDecorator.decorate(supplier);
        return of(generator);
    }

    static <T> GeneratorHolder of(final GeneratorSpecProvider<T> specProvider) {
        ApiValidator.validateGenerateSecondArgument(specProvider);
        return new GeneratorHolder(null, specProvider);
    }

    static <T> GeneratorHolder of(final T obj) {
        final Supplier<T> supplier = () -> obj;
        return of(supplier);
    }

    // Either generator or specProvider guaranteed to be not null
    @SuppressWarnings({"unchecked", "NullAway"})
    public <T> Generator<T> getGenerator(final Generators generators) {
        return generator != null
                ? (Generator<T>) generator
                : (Generator<T>) specProvider.getSpec(generators);

    }
}
