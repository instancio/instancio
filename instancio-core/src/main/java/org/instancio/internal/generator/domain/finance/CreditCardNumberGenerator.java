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
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

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
    protected Layout layout(final Random random) {
        return Layout.of(resolveCardType(random).getLength() - 1);
    }

    @Override
    protected String payload(final Random random, final int length) {
        final String payload = super.payload(random, length);

        // Resolved again rather than remembered from layout(): generating a value
        // must not modify the generator's state, otherwise the first generated value
        // would pin the card type for every subsequent value of a reused generator.
        // Restricting the choice to types of the required length keeps the prefix
        // consistent with the layout.
        final CCTypeImpl type = resolveCardTypeOfLength(random, length + 1);
        final String prefix = random.oneOf(type.getPrefixes()).toString();

        return prefix + payload.substring(prefix.length());
    }

    private CCTypeImpl resolveCardType(final Random random) {
        return cardType == null ? random.oneOf(CCTypeImpl.values()) : cardType;
    }

    private CCTypeImpl resolveCardTypeOfLength(final Random random, final int length) {
        if (cardType != null) {
            return cardType;
        }
        final List<CCTypeImpl> candidates = new ArrayList<>();
        for (CCTypeImpl type : CCTypeImpl.values()) {
            if (type.getLength() == length) {
                candidates.add(type);
            }
        }
        return requireNonNull(random.oneOf(candidates), "no card type of length " + length);
    }
}