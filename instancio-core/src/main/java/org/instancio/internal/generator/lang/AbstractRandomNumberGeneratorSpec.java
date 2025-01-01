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
package org.instancio.internal.generator.lang;

import org.instancio.Random;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.specs.NumberGeneratorSpec;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.generator.AbstractGenerator;
import org.instancio.internal.util.Range;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractRandomNumberGeneratorSpec<T extends Number>
        extends AbstractGenerator<T> implements NumberGeneratorSpec<T> {

    private T min;
    private T max;
    private final List<Range<T>> rangeStack = new ArrayList<>();

    protected AbstractRandomNumberGeneratorSpec(
            final GeneratorContext context, final T min, final T max, final boolean nullable) {

        super(context);
        super.nullable(nullable);
        this.min = min;
        this.max = max;
    }

    public final T getMin() {
        return min;
    }

    public final T getMax() {
        return max;
    }

    @Override
    public NumberGeneratorSpec<T> min(final T min) {
        this.min = ApiValidator.notNull(min, "'min' must not be null");
        return this;
    }

    @Override
    public NumberGeneratorSpec<T> max(final T max) {
        this.max = ApiValidator.notNull(max, "'max' must not be null");
        return this;
    }

    @Override
    public AbstractRandomNumberGeneratorSpec<T> nullable() {
        super.nullable();
        return this;
    }

    @Override
    public AbstractRandomNumberGeneratorSpec<T> nullable(final boolean isNullable) {
        super.nullable(isNullable);
        return this;
    }

    @Override
    public NumberGeneratorSpec<T> range(final T min, final T max) {
        this.min = ApiValidator.notNull(min, "'min' must not be null");
        this.max = ApiValidator.notNull(max, "'max' must not be null");
        rangeStack.add(Range.of(min, max));
        return this;
    }

    @Override
    public T generate(final Random random) {
        if (random.diceRoll(isNullable())) {
            return null;
        }
        setRange(random);
        return tryGenerateNonNull(random);
    }

    private void setRange(final Random random) {
        // if range() and min()/max() are used at the same time, range() takes precedence
        // therefore, it's ok to overwrite the min/max fields
        if (!rangeStack.isEmpty()) {
            final Range<T> range = random.oneOf(rangeStack);
            min = range.min();
            max = range.max();
        }
    }
}
