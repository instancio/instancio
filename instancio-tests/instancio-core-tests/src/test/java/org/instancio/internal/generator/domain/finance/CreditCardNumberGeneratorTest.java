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

import org.instancio.Instancio;
import org.instancio.internal.generator.AbstractGeneratorTestTemplate;
import org.instancio.internal.util.StringUtils;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.RepeatedTest;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class CreditCardNumberGeneratorTest extends AbstractGeneratorTestTemplate<String, CreditCardNumberGenerator> {

    private final CreditCardNumberGenerator generator = new CreditCardNumberGenerator(getGeneratorContext());

    @Override
    protected String getApiMethod() {
        return "creditCard()";
    }

    @Override
    protected CreditCardNumberGenerator generator() {
        return generator;
    }

    @RepeatedTest(Constants.SAMPLE_SIZE_DDD)
    void generate() {
        final CCTypeImpl cardType = Instancio.gen().oneOf(CCTypeImpl.values()).get();

        final String[] prefixes = cardType.getPrefixes().stream()
                .map(Object::toString)
                .toArray(String[]::new);

        final String result = generator.cardType(cardType).generate(random);

        assertThat(result)
                .as("Invalid length for %s card number '%s'", cardType, result)
                .hasSize(cardType.getLength());

        assertThat(StringUtils.startsWithAny(result, prefixes))
                .as("Expected %s card number '%s' to start with one of the prefixes: %s",
                        cardType, result, Arrays.toString(prefixes))
                .isTrue();
    }
}
