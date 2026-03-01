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
package org.instancio.internal.generator.domain.finance;

import org.instancio.Random;
import org.instancio.documentation.VisibleForTesting;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.specs.CreditCardSpec;
import org.instancio.internal.generator.checksum.BaseModCheckGenerator;
import org.instancio.internal.util.Verify;
import org.jspecify.annotations.Nullable;

public class CreditCardNumberGenerator extends BaseModCheckGenerator implements CreditCardSpec {

    private @Nullable CCTypeImpl cardType;

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
    protected String payload(final Random random) {
        if (cardType == null) {
            cardType = random.oneOf(CCTypeImpl.values());
        }

        final String payload = super.payload(random);
        final String prefix = random.oneOf(cardType.getPrefixes()).toString();
        return prefix + payload.substring(prefix.length());
    }

    @Override
    protected int payloadLength() {
        return Verify.notNull(cardType, "cardType is null").getLength() - 1;
    }
}