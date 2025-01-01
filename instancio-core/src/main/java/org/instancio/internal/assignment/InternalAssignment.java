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
import org.instancio.RandomFunction;
import org.instancio.TargetSelector;
import org.instancio.documentation.InternalApi;
import org.instancio.generator.Generator;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.Flattener;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

@InternalApi
public final class InternalAssignment implements Assignment, Flattener<InternalAssignment> {
    private final TargetSelector origin;
    private final Predicate<?> originPredicate;
    private final TargetSelector destination;
    private final GeneratorHolder generatorHolder;
    private final Generator<?> generator;
    private final RandomFunction<?, ?> valueMapper;

    private InternalAssignment(final Builder builder) {
        origin = builder.origin;
        originPredicate = builder.originPredicate;
        destination = builder.destination;
        generatorHolder = builder.generatorHolder;
        generator = builder.generator;
        valueMapper = builder.valueMapper;
    }

    public TargetSelector getOrigin() {
        return origin;
    }

    @SuppressWarnings("unchecked")
    public <T> Predicate<T> getOriginPredicate() {
        return (Predicate<T>) originPredicate;
    }

    public TargetSelector getDestination() {
        return destination;
    }

    public GeneratorHolder getGeneratorHolder() {
        return generatorHolder;
    }

    @SuppressWarnings("unchecked")
    public <T> Generator<T> getGenerator() {
        return (Generator<T>) generator;
    }

    @SuppressWarnings("unchecked")
    public <S, T> RandomFunction<S, T> getValueMapper() {
        return (RandomFunction<S, T>) valueMapper;
    }

    @Override
    public List<InternalAssignment> flatten() {
        return Collections.singletonList(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        Builder builder = new Builder();
        builder.origin = this.origin;
        builder.originPredicate = this.originPredicate;
        builder.destination = this.destination;
        builder.generatorHolder = this.generatorHolder;
        builder.generator = this.generator;
        builder.valueMapper = this.valueMapper;
        return builder;
    }

    public static final class Builder {
        private TargetSelector origin;
        private Predicate<?> originPredicate;
        private TargetSelector destination;
        private GeneratorHolder generatorHolder;
        private Generator<?> generator;
        private RandomFunction<?, ?> valueMapper;

        private Builder() {
        }

        public Builder origin(final TargetSelector origin) {
            this.origin = ApiValidator.notNull(origin, "origin selector must not be null");
            return this;
        }

        public <T> Builder originPredicate(final Predicate<T> originPredicate) {
            this.originPredicate = originPredicate;
            return this;
        }

        public Builder destination(final TargetSelector destination) {
            this.destination = ApiValidator.notNull(destination, "destination selector must not be null");
            return this;
        }

        public Builder generatorHolder(final GeneratorHolder generatorHolder) {
            this.generatorHolder = generatorHolder;
            return this;
        }

        public Builder generator(final Generator<?> generator) {
            this.generator = generator;
            return this;
        }

        public Builder valueMapper(final RandomFunction<?, ?> valueMapper) {
            this.valueMapper = valueMapper;
            return this;
        }

        public InternalAssignment build() {
            return new InternalAssignment(this);
        }
    }

    @Override
    public String toString() {
        return String.format("InternalAssignment[origin=%s, destination=%s]", origin, destination);
    }
}
