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
package org.instancio.internal.generator.time;

import org.instancio.generator.GeneratorContext;
import org.instancio.generator.specs.TemporalGeneratorSpec;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.generator.AbstractGenerator;

import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;

abstract class JavaTimeTemporalGenerator<T extends Temporal> extends AbstractGenerator<T>
        implements TemporalGeneratorSpec<T> {

    private final T defaultMin;
    private final T defaultMax;

    protected T min;
    protected T max;
    protected TemporalUnit truncateTo;

    JavaTimeTemporalGenerator(final GeneratorContext context, final T min, final T max) {
        super(context);
        this.defaultMin = min;
        this.defaultMax = max;
        this.min = min;
        this.max = max;
    }

    abstract T getLatestPast();

    abstract T getEarliestFuture();

    abstract void validateRange();

    @Override
    public TemporalGeneratorSpec<T> past() {
        min = defaultMin;
        max = getLatestPast();
        return this;
    }

    @Override
    public TemporalGeneratorSpec<T> future() {
        min = getEarliestFuture();
        max = defaultMax;
        return this;
    }

    @Override
    public TemporalGeneratorSpec<T> min(final T min) {
        this.min = ApiValidator.notNull(min, "'min' must not be null");
        return this;
    }

    @Override
    public TemporalGeneratorSpec<T> max(final T max) {
        this.max = ApiValidator.notNull(max, "'max' must not be null");
        return this;
    }

    @Override
    public TemporalGeneratorSpec<T> range(final T min, final T max) {
        this.min = ApiValidator.notNull(min, "'min' must not be null");
        this.max = ApiValidator.notNull(max, "'max' must not be null");
        validateRange();
        return this;
    }

    @Override
    public JavaTimeTemporalGenerator<T> nullable() {
        super.nullable();
        return this;
    }

    TemporalGeneratorSpec<T> truncatedTo(final TemporalUnit unit) {
        this.truncateTo = unit;
        return this;
    }
}
