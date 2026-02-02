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
package org.instancio.internal.generator.sequence;

import org.instancio.Random;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.specs.NumericSequenceSpec;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.generator.AbstractGenerator;

import java.util.function.Function;
import java.util.function.UnaryOperator;

public abstract class AbstractNumericSequenceGenerator<T extends Number & Comparable<T>>
        extends AbstractGenerator<T>
        implements NumericSequenceSpec<T> {

    private T seq;
    private Function<T, T> next;

    AbstractNumericSequenceGenerator(final GeneratorContext context) {
        super(context);
    }

    @Override
    public AbstractNumericSequenceGenerator<T> start(final T start) {
        this.seq = ApiValidator.notNull(start, "sequence 'start' value must not be null");
        return this;
    }

    @Override
    public AbstractNumericSequenceGenerator<T> next(final UnaryOperator<T> next) {
        this.next = ApiValidator.notNull(next, "sequence 'next' function must not be null");
        return this;
    }

    @Override
    public AbstractNumericSequenceGenerator<T> nullable() {
        super.nullable();
        return this;
    }

    @Override
    protected T tryGenerateNonNull(final Random random) {
        final T curr = seq;
        seq = next.apply(seq);
        return curr;
    }
}
