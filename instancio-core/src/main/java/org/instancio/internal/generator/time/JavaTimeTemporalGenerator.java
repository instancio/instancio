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
package org.instancio.internal.generator.time;

import org.instancio.generator.GeneratorContext;
import org.instancio.generator.specs.TemporalAaGeneratorSpec;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.generator.AbstractGenerator;

import java.time.temporal.Temporal;

abstract class JavaTimeTemporalGenerator<T extends Temporal> extends AbstractGenerator<T>
        implements TemporalAaGeneratorSpec<T> {

    private final T defaultMin;
    private final T defaultMax;

    protected T min;
    protected T max;

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
    public TemporalAaGeneratorSpec<T> past() {
        min = defaultMin;
        max = getLatestPast();
        return this;
    }

    @Override
    public TemporalAaGeneratorSpec<T> future() {
        min = getEarliestFuture();
        max = defaultMax;
        return this;
    }

    @Override
    public TemporalAaGeneratorSpec<T> range(final T start, final T end) {
        min = ApiValidator.notNull(start, "Start parameter must not be null");
        max = ApiValidator.notNull(end, "End parameter must not be null");
        validateRange();
        return this;
    }
}
