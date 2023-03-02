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
import org.instancio.generator.specs.LongSpec;
import org.instancio.settings.Keys;
import org.instancio.support.Global;

public class LongGenerator extends AbstractRandomComparableNumberGeneratorSpec<Long>
        implements LongSpec {

    public LongGenerator() {
        this(Global.generatorContext());
    }

    public LongGenerator(final GeneratorContext context) {
        super(context,
                context.getSettings().get(Keys.LONG_MIN),
                context.getSettings().get(Keys.LONG_MAX),
                context.getSettings().get(Keys.LONG_NULLABLE));
    }

    @Override
    public String apiMethod() {
        return "longs()";
    }

    @Override
    public LongGenerator min(final Long min) {
        super.min(min);
        return this;
    }

    @Override
    public LongGenerator max(final Long max) {
        super.max(max);
        return this;
    }

    @Override
    public LongGenerator range(final Long min, final Long max) {
        super.range(min, max);
        return this;
    }

    @Override
    public LongGenerator nullable() {
        super.nullable();
        return this;
    }

    @Override
    public LongGenerator nullable(final boolean isNullable) {
        super.nullable(isNullable);
        return this;
    }

    @Override
    protected Long tryGenerateNonNull(final Random random) {
        return random.longRange(getMin(), getMax());
    }
}
