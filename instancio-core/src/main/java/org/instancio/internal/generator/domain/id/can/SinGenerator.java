/*
 * Copyright 2022-2026 the original author or authors.
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
package org.instancio.internal.generator.domain.id.can;

import org.instancio.Random;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.specs.can.SinSpec;
import org.instancio.internal.generator.checksum.LuhnGenerator;
import org.jspecify.annotations.Nullable;

public class SinGenerator extends LuhnGenerator implements SinSpec {

    private static final int SIN_LENGTH = 9;

    private @Nullable Type type;
    private @Nullable String separator;

    public SinGenerator(final GeneratorContext context) {
        super(context);
        length(SIN_LENGTH);
    }

    @Override
    public String apiMethod() {
        return "sin()";
    }

    @Override
    public SinGenerator permanent() {
        type = Type.PERMANENT;
        return this;
    }

    @Override
    public SinGenerator temporary() {
        type = Type.TEMPORARY;
        return this;
    }

    @Override
    public SinGenerator separator(final String separator) {
        this.separator = separator;
        return this;
    }

    @Override
    public SinGenerator nullable() {
        super.nullable();
        return this;
    }

    @Override
    protected String tryGenerateNonNull(final Random random) {
        final String sin = super.tryGenerateNonNull(random);

        if (separator == null) {
            return sin;
        }

        return sin.substring(0, 3) +
                separator +
                sin.substring(3, 6) +
                separator +
                sin.substring(6, 9);
    }

    @Override
    protected String payload(final Random random) {
        final int length = payloadLength();
        final char[] res = new char[length];

        res[0] = getFirstDigit(random);

        for (int i = 1; i < length; i++) {
            res[i] = random.characterRange('0', '9');
        }
        return new String(res);
    }

    private char getFirstDigit(final Random random) {
        final char tempFirstDigit = '9';
        if (type == Type.TEMPORARY) {
            return tempFirstDigit;
        }

        final char permFirstDigit = random.characterRange('1', '7');
        if (type == Type.PERMANENT) {
            return permFirstDigit;
        }
        return random.trueOrFalse() ? tempFirstDigit : permFirstDigit;
    }

    private enum Type {
        PERMANENT, TEMPORARY
    }
}
