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

import org.instancio.GeneratorSpecProvider;
import org.instancio.GivenOriginPredicateAction;
import org.instancio.TargetSelector;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorSpec;

import java.util.function.Predicate;
import java.util.function.Supplier;

class InternalGivenOriginPredicateRequiredAction implements GivenOriginPredicateAction {
    private final TargetSelector origin;
    private final Predicate<Object> originPredicate;

    <T> InternalGivenOriginPredicateRequiredAction(final TargetSelector origin, final Predicate<T> originPredicate) {
        this.origin = origin;
        this.originPredicate = (Predicate<Object>) originPredicate;
    }

    @Override
    public <T> GivenOriginPredicateAction supply(final TargetSelector destination, final Generator<T> generator) {
        return create(destination, GeneratorHolder.of(generator));
    }

    @Override
    public <T> GivenOriginPredicateAction supply(final TargetSelector destination, final Supplier<T> supplier) {
        return create(destination, GeneratorHolder.of(supplier));
    }

    @Override
    public <T> GivenOriginPredicateAction set(final TargetSelector destination, final T obj) {
        return create(destination, GeneratorHolder.of(obj));
    }

    @Override
    public <T> GivenOriginPredicateAction generate(final TargetSelector destination, final GeneratorSpecProvider<T> gen) {
        return create(destination, GeneratorHolder.of(gen));
    }

    @Override
    public <T> GivenOriginPredicateAction generate(final TargetSelector destination, final GeneratorSpec<T> spec) {
        return create(destination, GeneratorHolder.of(spec));
    }

    private InternalGivenOriginPredicateAction create(final TargetSelector destination, final GeneratorHolder generatorHolder) {
        return new InternalGivenOriginPredicateAction(origin, originPredicate, destination, generatorHolder);
    }
}
