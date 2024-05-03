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
package org.instancio.internal.generator.sql;

import org.instancio.Random;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.specs.TemporalGeneratorSpec;
import org.instancio.internal.generator.AbstractGenerator;
import org.instancio.internal.generator.time.InstantGenerator;

import java.sql.Timestamp;

public class TimestampGenerator extends AbstractGenerator<Timestamp> implements TemporalGeneratorSpec<Timestamp> {

    private final InstantGenerator delegate;

    public TimestampGenerator(final GeneratorContext context) {
        super(context);
        this.delegate = new InstantGenerator(context);
    }

    @Override
    public String apiMethod() {
        return "timestamp()";
    }

    @Override
    public TimestampGenerator past() {
        delegate.past();
        return this;
    }

    @Override
    public TimestampGenerator future() {
        delegate.future();
        return this;
    }

    @Override
    public TimestampGenerator min(final Timestamp min) {
        delegate.min(min.toInstant());
        return this;
    }

    @Override
    public TimestampGenerator max(final Timestamp max) {
        delegate.max(max.toInstant());
        return this;
    }

    @Override
    public TimestampGenerator range(final Timestamp min, final Timestamp max) {
        delegate.range(min.toInstant(), max.toInstant());
        return this;
    }

    @Override
    public TimestampGenerator nullable() {
        super.nullable();
        return this;
    }

    @Override
    public Timestamp tryGenerateNonNull(final Random random) {
        return Timestamp.from(delegate.tryGenerateNonNull(random));
    }
}
