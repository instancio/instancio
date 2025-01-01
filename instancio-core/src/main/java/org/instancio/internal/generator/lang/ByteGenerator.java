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
package org.instancio.internal.generator.lang;

import org.instancio.Random;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.specs.ByteSpec;
import org.instancio.settings.Keys;

public class ByteGenerator extends AbstractRandomComparableNumberGeneratorSpec<Byte>
        implements ByteSpec {

    public ByteGenerator(final GeneratorContext context) {
        super(context,
                context.getSettings().get(Keys.BYTE_MIN),
                context.getSettings().get(Keys.BYTE_MAX),
                context.getSettings().get(Keys.BYTE_NULLABLE));
    }

    @Override
    public String apiMethod() {
        return "bytes()";
    }

    @Override
    public ByteGenerator min(final Byte min) {
        super.min(min);
        return this;
    }

    @Override
    public ByteGenerator max(final Byte max) {
        super.max(max);
        return this;
    }

    @Override
    public ByteGenerator range(final Byte min, final Byte max) {
        super.range(min, max);
        return this;
    }

    @Override
    public ByteGenerator nullable() {
        super.nullable();
        return this;
    }

    @Override
    public ByteGenerator nullable(final boolean isNullable) {
        super.nullable(isNullable);
        return this;
    }

    @Override
    protected Byte tryGenerateNonNull(final Random random) {
        return random.byteRange(getMin(), getMax());
    }
}
