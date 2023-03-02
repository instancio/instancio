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
import org.instancio.generator.specs.CreditCardSpec;
import org.instancio.internal.generator.AbstractGenerator;
import org.instancio.internal.util.LuhnUtils;
import org.instancio.support.Global;
import org.jetbrains.annotations.VisibleForTesting;

public class CreditCardNumberGenerator extends AbstractGenerator<String>
        implements CreditCardSpec {

    private CCTypeImpl cardType;

    public CreditCardNumberGenerator() {
        super(Global.generatorContext());
    }

    public CreditCardNumberGenerator(final GeneratorContext context) {
        super(context);
    }

    @Override
    public String apiMethod() {
        return "creditCard()";
    }

    @Override
    public CreditCardNumberGenerator visa() {
        return cardType(CCTypeImpl.CC_VISA);
    }

    @Override
    public CreditCardNumberGenerator masterCard() {
        return cardType(CCTypeImpl.CC_MASTERCARD);
    }

    @VisibleForTesting
    CreditCardNumberGenerator cardType(final CCTypeImpl cardType) {
        this.cardType = cardType;
        return this;
    }

    @Override
    public CreditCardNumberGenerator nullable() {
        super.nullable();
        return this;
    }

    @Override
    protected String tryGenerateNonNull(final Random random) {
        final CCTypeImpl type = cardType == null
                ? random.oneOf(CCTypeImpl.values())
                : cardType;

        final String prefix = random.oneOf(type.getPrefixes()).toString();
        final int lengthWithoutCheckDigit = type.getLength() - prefix.length();
        final String withoutCheckDigit = prefix + random.digits(lengthWithoutCheckDigit);
        final char[] payload = withoutCheckDigit.toCharArray();
        final int checkDigit = LuhnUtils.getCheckDigit(payload);
        payload[payload.length - 1] = (char) (checkDigit + '0');
        return new String(payload);
    }
}