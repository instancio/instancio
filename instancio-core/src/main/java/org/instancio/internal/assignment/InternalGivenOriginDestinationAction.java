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
package org.instancio.internal.assignment;

import org.instancio.Assignment;
import org.instancio.GeneratorSpecProvider;
import org.instancio.GivenOriginDestinationAction;
import org.instancio.TargetSelector;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorSpec;
import org.instancio.internal.Flattener;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class InternalGivenOriginDestinationAction
        implements GivenOriginDestinationAction, Flattener<InternalAssignment> {

    private final List<InternalAssignment> assignments = new ArrayList<>(3);
    private final TargetSelector origin;
    private final TargetSelector destination;

    public InternalGivenOriginDestinationAction(final TargetSelector origin, final TargetSelector destination) {
        this.origin = origin;
        this.destination = destination;
    }

    @Override
    public <S, T> GivenOriginDestinationAction generate(Predicate<S> predicate, GeneratorSpecProvider<T> gen) {
        return add(predicate, GeneratorHolder.of(gen));
    }

    @Override
    public <S, T> GivenOriginDestinationAction generate(Predicate<S> predicate, GeneratorSpec<T> spec) {
        return add(predicate, GeneratorHolder.of(spec));
    }

    @Override
    public <S, T> GivenOriginDestinationAction set(Predicate<S> predicate, T value) {
        return add(predicate, GeneratorHolder.of(value));
    }

    @Override
    public <S, T> GivenOriginDestinationAction supply(final Predicate<S> predicate, final Generator<T> generator) {
        return add(predicate, GeneratorHolder.of(generator));
    }

    @Override
    public <S, T> GivenOriginDestinationAction supply(final Predicate<S> predicate, final Supplier<T> supplier) {
        return add(predicate, GeneratorHolder.of(supplier));
    }

    @Override
    public <T> Assignment elseGenerate(GeneratorSpecProvider<T> gen) {
        return add(negatePredicates(), GeneratorHolder.of(gen));
    }

    @Override
    public <T> Assignment elseGenerate(GeneratorSpec<T> spec) {
        return add(negatePredicates(), GeneratorHolder.of(spec));
    }

    @Override
    public <T> Assignment elseSet(T value) {
        return add(negatePredicates(), GeneratorHolder.of(value));
    }

    @Override
    public <T> Assignment elseSupply(final Generator<T> generator) {
        return add(negatePredicates(), GeneratorHolder.of(generator));
    }

    @Override
    public <T> Assignment elseSupply(final Supplier<T> supplier) {
        return add(negatePredicates(), GeneratorHolder.of(supplier));
    }

    @Override
    public List<InternalAssignment> flatten() {
        return assignments;
    }

    private <T> Predicate<T> negatePredicates() {
        Predicate<T> predicate = v -> false;
        for (InternalAssignment assignment : assignments) {
            predicate = predicate.or(assignment.getOriginPredicate());
        }

        return predicate.negate();
    }

    private <S> GivenOriginDestinationAction add(Predicate<S> predicate, GeneratorHolder generatorHolder) {
        assignments.add(InternalAssignment.builder()
                .origin(origin)
                .originPredicate((Predicate<Object>) predicate)
                .destination(destination)
                .generatorHolder(generatorHolder)
                .build());
        return this;
    }

}
