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
package org.instancio.internal.generator.domain.finance;

import org.instancio.Random;
import org.instancio.generator.GeneratorContext;
import org.instancio.internal.generator.AbstractGenerator;
import org.instancio.internal.util.LuhnUtils;

public class CreditCardNumberGenerator extends AbstractGenerator<String> {

    // Default to Visa since apparently it is the most common card
    private CreditCardType cardType = CreditCardType.VISA16;

    public CreditCardNumberGenerator(final GeneratorContext context) {
        super(context);
    }

    @Override
    public String apiMethod() {
        return null;
    }

    public CreditCardNumberGenerator cardType(final CreditCardType cardType) {
        this.cardType = cardType;
        return this;
    }

    @Override
    public CreditCardNumberGenerator nullable() {
        super.nullable();
        return this;
    }

    @Override
    public String generate(final Random random) {
        if (random.diceRoll(isNullable())) {
            return null;
        }

        final String prefix = random.oneOf(cardType.getPrefixes()).toString();
        final int lengthWithoutCheckDigit = cardType.getLength() - prefix.length();
        final String withoutCheckDigit = prefix + random.digits(lengthWithoutCheckDigit);
        final char[] payload = withoutCheckDigit.toCharArray();
        final int checkDigit = LuhnUtils.getCheckDigit(payload);
        payload[payload.length - 1] = (char) (checkDigit + '0');
        return new String(payload);
    }
}