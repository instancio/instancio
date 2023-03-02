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
import org.instancio.generator.specs.ShortSpec;
import org.instancio.settings.Keys;
import org.instancio.support.Global;

public class ShortGenerator extends AbstractRandomComparableNumberGeneratorSpec<Short>
        implements ShortSpec {

    public ShortGenerator() {
        this(Global.generatorContext());
    }

    public ShortGenerator(final GeneratorContext context) {
        super(context,
                context.getSettings().get(Keys.SHORT_MIN),
                context.getSettings().get(Keys.SHORT_MAX),
                context.getSettings().get(Keys.SHORT_NULLABLE));
    }

    @Override
    public String apiMethod() {
        return "shorts()";
    }

    @Override
    public ShortGenerator min(final Short min) {
        super.min(min);
        return this;
    }

    @Override
    public ShortGenerator max(final Short max) {
        super.max(max);
        return this;
    }

    @Override
    public ShortGenerator range(final Short min, final Short max) {
        super.range(min, max);
        return this;
    }

    @Override
    public ShortGenerator nullable() {
        super.nullable();
        return this;
    }

    @Override
    public ShortGenerator nullable(final boolean isNullable) {
        super.nullable(isNullable);
        return this;
    }

    @Override
    protected Short tryGenerateNonNull(final Random random) {
        return random.shortRange(getMin(), getMax());
    }
}
