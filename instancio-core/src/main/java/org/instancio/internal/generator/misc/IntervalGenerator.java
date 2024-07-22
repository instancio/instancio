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
package org.instancio.internal.generator.misc;

import org.instancio.IntervalSupplier;
import org.instancio.Random;
import org.instancio.RandomUnaryOperator;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.specs.IntervalSpec;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.generator.AbstractGenerator;
import org.instancio.internal.util.PropertyBitSet;

import java.util.function.Supplier;

public class IntervalGenerator<T> extends AbstractGenerator<IntervalSupplier<T>> implements IntervalSpec<T> {

    private final T startingValue;
    private RandomUnaryOperator<T> nextStartFunction;
    private RandomUnaryOperator<T> nextEndFunction;

    public IntervalGenerator(final GeneratorContext context, final T startingValue) {
        super(context);
        this.startingValue = ApiValidator.notNull(startingValue, "starting value must not be null");
    }

    @Override
    public String apiMethod() {
        // no validation needed for this method since
        // it's only available via Instancio.gen()
        return null;
    }

    @Override
    public IntervalGenerator<T> nextStart(final RandomUnaryOperator<T> fn) {
        this.nextStartFunction = fn;
        return this;
    }

    @Override
    public IntervalGenerator<T> nextEnd(final RandomUnaryOperator<T> fn) {
        this.nextEndFunction = fn;
        return this;
    }

    @Override
    public IntervalGenerator<T> nullable() {
        super.nullable();
        return this;
    }

    @Override
    protected IntervalSupplier<T> tryGenerateNonNull(final Random random) {
        ApiValidator.notNull(nextStartFunction, "'nextStart' function must not be null");
        ApiValidator.notNull(nextEndFunction, "'nextEnd' function must not be null");

        return new IntervalSupplierImpl<>(random, startingValue, nextStartFunction, nextEndFunction);
    }

    private static final class IntervalSupplierImpl<T> implements IntervalSupplier<T> {
        private static final String START = "start";
        private static final String END = "end";

        private final PropertyBitSet propertyBitSet = new PropertyBitSet();
        private final Random random;
        private final RandomUnaryOperator<T> nextStartFunction;
        private final RandomUnaryOperator<T> nextEndFunction;
        private T intervalStart;
        private T intervalEnd;

        private IntervalSupplierImpl(
                final Random random,
                final T startingValue,
                final RandomUnaryOperator<T> nextStartFunction,
                final RandomUnaryOperator<T> nextEndFunction) {

            this.random = random;
            this.nextStartFunction = nextStartFunction;
            this.nextEndFunction = nextEndFunction;
            this.intervalStart = startingValue;
            this.intervalEnd = nextEndFunction.apply(startingValue, random);
        }

        @Override
        public Supplier<T> start() {
            return () -> {
                updateState(START);
                return intervalStart;
            };
        }

        @Override
        public Supplier<T> end() {
            return () -> {
                updateState(END);
                return intervalEnd;
            };
        }

        private void updateState(final String property) {
            if (propertyBitSet.get(property)) {
                propertyBitSet.clear();
                intervalStart = nextStartFunction.apply(intervalEnd, random);
                intervalEnd = nextEndFunction.apply(intervalStart, random);
            }
            propertyBitSet.set(property);
        }
    }
}
