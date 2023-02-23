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

import org.instancio.generator.GeneratorContext;
import org.instancio.generator.specs.NumberAsGeneratorSpec;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.generator.AbstractGenerator;

public abstract class AbstractRandomNumberGeneratorSpec<T extends Number>
        extends AbstractGenerator<T> implements NumberAsGeneratorSpec<T> {

    private T min;
    private T max;

    protected AbstractRandomNumberGeneratorSpec(
            final GeneratorContext context, final T min, final T max, final boolean nullable) {

        super(context);
        super.nullable(nullable);
        this.min = min;
        this.max = max;
    }

    protected T getMin() {
        return min;
    }

    protected T getMax() {
        return max;
    }

    @Override
    public NumberAsGeneratorSpec<T> min(final T min) {
        this.min = ApiValidator.notNull(min, "'min' must not be null");
        return this;
    }

    @Override
    public NumberAsGeneratorSpec<T> max(final T max) {
        this.max = ApiValidator.notNull(max, "'max' must not be null");
        return this;
    }

    @Override
    public NumberAsGeneratorSpec<T> nullable() {
        super.nullable();
        return this;
    }

    @Override
    public NumberAsGeneratorSpec<T> nullable(final boolean isNullable) {
        super.nullable(isNullable);
        return this;
    }

    @Override
    public NumberAsGeneratorSpec<T> range(final T min, final T max) {
        this.min = ApiValidator.notNull(min, "'min' must not be null");
        this.max = ApiValidator.notNull(max, "'max' must not be null");
        return this;
    }
}
