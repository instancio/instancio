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
package org.instancio.internal.conditional;

import org.instancio.Conditional;
import org.instancio.ConditionalGivenAction;
import org.instancio.GeneratorSpecProvider;
import org.instancio.TargetSelector;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorSpec;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class InternalConditionalGivenAction implements ConditionalGivenAction {

    private final List<InternalConditional> conditionals = new ArrayList<>(3);
    private final TargetSelector origin;
    private final TargetSelector destination;

    public InternalConditionalGivenAction(final TargetSelector origin, final TargetSelector destination) {
        this.origin = origin;
        this.destination = destination;
    }

    @Override
    public <S, T> ConditionalGivenAction generate(Predicate<S> predicate, GeneratorSpecProvider<T> gen) {
        return addConditional(predicate, GeneratorHolder.of(gen));
    }

    @Override
    public <S, T> ConditionalGivenAction generate(Predicate<S> predicate, GeneratorSpec<T> spec) {
        return addConditional(predicate, GeneratorHolder.of(spec));
    }

    @Override
    public <S, T> ConditionalGivenAction set(Predicate<S> predicate, T value) {
        return addConditional(predicate, GeneratorHolder.of(value));
    }

    @Override
    public <S, T> ConditionalGivenAction supply(final Predicate<S> predicate, final Generator<T> generator) {
        return addConditional(predicate, GeneratorHolder.of(generator));
    }

    @Override
    public <S, T> ConditionalGivenAction supply(final Predicate<S> predicate, final Supplier<T> supplier) {
        return addConditional(predicate, GeneratorHolder.of(supplier));
    }

    @Override
    public <T> Conditional elseGenerate(GeneratorSpecProvider<T> gen) {
        return addConditional(negatePredicates(), GeneratorHolder.of(gen));
    }

    @Override
    public <T> Conditional elseGenerate(GeneratorSpec<T> spec) {
        return addConditional(negatePredicates(), GeneratorHolder.of(spec));
    }

    @Override
    public <T> Conditional elseSet(T value) {
        return addConditional(negatePredicates(), GeneratorHolder.of(value));
    }

    @Override
    public <T> Conditional elseSupply(final Generator<T> generator) {
        return addConditional(negatePredicates(), GeneratorHolder.of(generator));
    }

    @Override
    public <T> Conditional elseSupply(final Supplier<T> supplier) {
        return addConditional(negatePredicates(), GeneratorHolder.of(supplier));
    }

    public List<InternalConditional> getConditionals() {
        return conditionals;
    }

    private <T> Predicate<T> negatePredicates() {
        Predicate<T> predicate = v -> false;
        for (InternalConditional conditional : conditionals) {
            predicate = predicate.or(conditional.getOriginPredicate());
        }

        return predicate.negate();
    }

    private <S> ConditionalGivenAction addConditional(Predicate<S> predicate, GeneratorHolder generatorHolder) {
        conditionals.add(
                InternalConditional.builder()
                        .origin(origin)
                        .originPredicate((Predicate<Object>) predicate)
                        .destination(destination)
                        .generatorHolder(generatorHolder)
                        .build());
        return this;
    }

}
