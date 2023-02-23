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
package org.instancio.internal.generator.sql;

import org.instancio.Random;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.specs.TemporalGeneratorSpec;
import org.instancio.internal.generator.AbstractGenerator;
import org.instancio.internal.generator.time.LocalDateGenerator;

import java.sql.Date;

public class SqlDateGenerator extends AbstractGenerator<Date> implements TemporalGeneratorSpec<Date> {

    private final LocalDateGenerator delegate;

    public SqlDateGenerator(final GeneratorContext context) {
        super(context);
        this.delegate = new LocalDateGenerator(context);
    }

    @Override
    public String apiMethod() {
        return "sqlDate()";
    }

    @Override
    public SqlDateGenerator past() {
        delegate.past();
        return this;
    }

    @Override
    public SqlDateGenerator future() {
        delegate.future();
        return this;
    }

    @Override
    public SqlDateGenerator range(final Date start, final Date end) {
        delegate.range(start.toLocalDate(), end.toLocalDate());
        return this;
    }

    @Override
    public SqlDateGenerator nullable() {
        super.nullable();
        return this;
    }

    @Override
    public Date tryGenerateNonNull(final Random random) {
        return Date.valueOf(delegate.tryGenerateNonNull(random));
    }
}
