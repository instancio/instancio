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
package org.instancio.internal.generator.util;

import org.instancio.Random;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.specs.TemporalSpec;
import org.instancio.internal.generator.AbstractGenerator;
import org.instancio.internal.generator.time.InstantGenerator;

import java.util.Date;

public class DateGenerator extends AbstractGenerator<Date> implements TemporalSpec<Date> {

    private final InstantGenerator delegate;

    public DateGenerator(final GeneratorContext context) {
        super(context);
        this.delegate = new InstantGenerator(context);
    }

    @Override
    public String apiMethod() {
        return "date()";
    }

    @Override
    public DateGenerator past() {
        delegate.past();
        return this;
    }

    @Override
    public DateGenerator future() {
        delegate.future();
        return this;
    }

    @Override
    public DateGenerator min(final Date min) {
        delegate.min(min.toInstant());
        return this;
    }

    @Override
    public DateGenerator max(final Date max) {
        delegate.max(max.toInstant());
        return this;
    }

    @Override
    public DateGenerator range(final Date min, final Date max) {
        delegate.range(min.toInstant(), max.toInstant());
        return this;
    }

    @Override
    public DateGenerator nullable() {
        super.nullable();
        return this;
    }

    @Override
    protected Date tryGenerateNonNull(final Random random) {
        return Date.from((delegate).tryGenerateNonNull(random));
    }
}
