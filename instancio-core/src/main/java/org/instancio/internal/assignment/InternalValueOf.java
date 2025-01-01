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
package org.instancio.internal.assignment;

import org.instancio.Assignment;
import org.instancio.GeneratorSpecProvider;
import org.instancio.GetMethodSelector;
import org.instancio.Select;
import org.instancio.TargetSelector;
import org.instancio.ValueOf;
import org.instancio.ValueOfOriginDestination;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorSpec;

import java.util.function.Supplier;

public class InternalValueOf implements ValueOf {
    private final TargetSelector target;

    public InternalValueOf(final TargetSelector target) {
        this.target = target;
    }

    @Override
    public ValueOfOriginDestination to(final TargetSelector destination) {
        return new InternalValueOfOriginDestination(target, destination);
    }

    @Override
    public <T, R> ValueOfOriginDestination to(final GetMethodSelector<T, R> destination) {
        return new InternalValueOfOriginDestination(target, Select.field(destination));
    }

    @Override
    public <T> Assignment generate(final GeneratorSpecProvider<T> gen) {
        return create(GeneratorHolder.of(gen));
    }

    @Override
    public <T> Assignment generate(final GeneratorSpec<T> spec) {
        return create(GeneratorHolder.of(spec));
    }

    @Override
    public <T> Assignment set(final T value) {
        return create(GeneratorHolder.of(value));
    }

    @Override
    public <T> Assignment supply(final Generator<T> generator) {
        return create(GeneratorHolder.of(generator));
    }

    @Override
    public <T> Assignment supply(final Supplier<T> supplier) {
        return create(GeneratorHolder.of(supplier));
    }

    private Assignment create(final GeneratorHolder generatorHolder) {
        // Use root() since there's no origin selector for this assignment
        return InternalAssignment.builder()
                .origin(Select.root())
                .destination(target)
                .generatorHolder(generatorHolder)
                .build();
    }
}
