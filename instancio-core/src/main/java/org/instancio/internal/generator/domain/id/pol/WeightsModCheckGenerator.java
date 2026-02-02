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
package org.instancio.internal.generator.domain.id.pol;

import org.instancio.generator.GeneratorContext;
import org.instancio.internal.generator.checksum.BaseModCheckGenerator;
import org.instancio.internal.util.NumberUtils;

import java.util.List;

abstract class WeightsModCheckGenerator extends BaseModCheckGenerator {

    protected WeightsModCheckGenerator(final GeneratorContext context) {
        super(context);
    }

    protected abstract List<Integer> weights();

    @Override
    protected int payloadLength() {
        return weights().size();
    }

    @Override
    protected char getCheckDigit(final String payload) {
        final int result = modulo(payload);
        if (result == 10) {
            return '0';
        }
        return NumberUtils.toDigitChar(result);
    }

    @Override
    protected int modulo(final String payload) {
        int sum = 0;
        for (int i = 0; i < payload.length(); i++) {
            int n = NumberUtils.toDigitInt(payload.charAt(i));
            n *= weights().get(i);
            sum += n;
        }
        return sum % base();
    }

    @Override
    protected int base() {
        return 11;
    }
}
