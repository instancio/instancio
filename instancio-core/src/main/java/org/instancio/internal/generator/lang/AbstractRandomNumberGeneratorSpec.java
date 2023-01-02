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
package org.instancio.internal.generator.lang;

import org.instancio.Random;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.specs.NumberGeneratorSpec;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.generator.AbstractGenerator;

public abstract class AbstractRandomNumberGeneratorSpec<T extends Number>
        extends AbstractGenerator<T> implements NumberGeneratorSpec<T> {

    private T min;
    private T max;
    private boolean nullable;

    protected AbstractRandomNumberGeneratorSpec(
            final GeneratorContext context, final T min, final T max, final boolean nullable) {

        super(context);
        this.min = min;
        this.max = max;
        this.nullable = nullable;
    }

    protected abstract T generateNonNullValue(Random random);

    protected T getMin() {
        return min;
    }

    protected T getMax() {
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
    public NumberGeneratorSpec<T> range(final T min, final T max) {
        this.min = ApiValidator.notNull(min, "'min' must not be null");
        this.max = ApiValidator.notNull(max, "'max' must not be null");
        return this;
    }

    @Override
    public NumberGeneratorSpec<T> nullable() {
        this.nullable = true;
        return this;
    }

    @Override
    public final T generate(final Random random) {
        return random.diceRoll(nullable) ? null : generateNonNullValue(random);
    }
}
