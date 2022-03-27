/*
 * Copyright 2022 the original author or authors.
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
package org.instancio.generators.coretypes;

import org.instancio.generators.AbstractRandomGenerator;
import org.instancio.internal.GeneratedHints;
import org.instancio.internal.model.ModelContext;
import org.instancio.util.Verify;

public abstract class AbstractRandomNumberGeneratorSpec<T extends Number>
        extends AbstractRandomGenerator<T> implements NumberGeneratorSpec<T> {

    protected T min;
    protected T max;
    protected boolean nullable;

    AbstractRandomNumberGeneratorSpec(final ModelContext<?> context, final T min, final T max, final boolean nullable) {
        super(context);
        this.min = min;
        this.max = max;
        this.nullable = nullable;
    }

    abstract T generateNonNullValue();

    @Override
    public NumberGeneratorSpec<T> min(final T min) {
        this.min = Verify.notNull(min);
        return this;
    }

    @Override
    public NumberGeneratorSpec<T> max(final T max) {
        this.max = Verify.notNull(max);
        return this;
    }

    @Override
    public NumberGeneratorSpec<T> nullable() {
        this.nullable = true;
        return this;
    }

    @Override
    public T generate() {
        return random().diceRoll(nullable) ? null : generateNonNullValue();
    }

    @Override
    public GeneratedHints getHints() {
        return GeneratedHints.builder()
                .nullableResult(nullable)
                .ignoreChildren(true)
                .build();
    }
}
