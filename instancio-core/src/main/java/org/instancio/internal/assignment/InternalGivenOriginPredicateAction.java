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
package org.instancio.internal.assignment;

import org.instancio.Assignment;
import org.instancio.GeneratorSpecProvider;
import org.instancio.GivenOriginPredicateAction;
import org.instancio.TargetSelector;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorSpec;
import org.instancio.internal.Flattener;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class InternalGivenOriginPredicateAction
        implements Assignment, GivenOriginPredicateAction, Flattener<InternalAssignment> {

    private final TargetSelector origin;
    private final Predicate<Object> originPredicate;
    private final List<InternalAssignment> assignments = new ArrayList<>(3);

    <T> InternalGivenOriginPredicateAction(
            final TargetSelector origin,
            final Predicate<Object> originPredicate,
            final TargetSelector destination,
            final GeneratorHolder generatorHolder) {

        this.origin = origin;
        this.originPredicate = originPredicate;
        addAction(destination, generatorHolder);
    }

    private GivenOriginPredicateAction addAction(final TargetSelector destination, final GeneratorHolder generatorHolder) {
        assignments.add(InternalAssignment.builder()
                .origin(origin)
                .originPredicate(originPredicate)
                .destination(destination)
                .generatorHolder(generatorHolder)
                .build());

        return this;
    }

    @Override
    public List<InternalAssignment> flatten() {
        return assignments;
    }

    @Override
    public <T> GivenOriginPredicateAction supply(final TargetSelector selector, final Generator<T> generator) {
        return addAction(selector, GeneratorHolder.of(generator));
    }

    @Override
    public <T> GivenOriginPredicateAction supply(final TargetSelector selector, final Supplier<T> supplier) {
        return addAction(selector, GeneratorHolder.of(supplier));
    }

    @Override
    public <T> GivenOriginPredicateAction set(final TargetSelector selector, final T obj) {
        return addAction(selector, GeneratorHolder.of(obj));
    }

    @Override
    public <T> GivenOriginPredicateAction generate(final TargetSelector selector, final GeneratorSpecProvider<T> gen) {
        return addAction(selector, GeneratorHolder.of(gen));
    }

    @Override
    public <T> GivenOriginPredicateAction generate(final TargetSelector selector, final GeneratorSpec<T> spec) {
        return addAction(selector, GeneratorHolder.of(spec));
    }
}
