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

import org.instancio.Assignment;
import org.instancio.Random;
import org.instancio.RandomFunction;
import org.instancio.TargetSelector;
import org.instancio.ValueOfOriginDestination;
import org.instancio.ValueOfOriginDestinationPredicate;
import org.instancio.internal.Flattener;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class InternalValueOfOriginDestination
        implements ValueOfOriginDestination, Assignment, Flattener<InternalAssignment> {

    private final TargetSelector origin;
    private final TargetSelector destination;
    private Predicate<?> predicate;
    private RandomFunction<?, ?> valueMapper;

    public InternalValueOfOriginDestination(final TargetSelector origin, final TargetSelector destination) {
        this.origin = origin;
        this.destination = destination;
    }

    @Override
    public <T, R> ValueOfOriginDestinationPredicate as(final Function<T, R> f) {
        this.valueMapper = (T arg, Random random) -> f.apply(arg);
        return new InternalValueOfOriginDestinationPredicate(origin, destination, predicate, valueMapper);
    }

    @Override
    public <T, R> ValueOfOriginDestinationPredicate as(final RandomFunction<T, R> valueMapper) {
        this.valueMapper = valueMapper;
        return new InternalValueOfOriginDestinationPredicate(origin, destination, predicate, valueMapper);
    }

    @Override
    public <T> Assignment when(final Predicate<T> predicate) {
        this.predicate = predicate;
        return this;
    }

    @Override
    public List<InternalAssignment> flatten() {
        return Collections.singletonList(InternalAssignment.builder()
                .origin(origin)
                .destination(destination)
                .valueMapper(valueMapper)
                .originPredicate(predicate)
                .build());
    }
}
