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

import org.instancio.Gen;
import org.instancio.Random;
import org.instancio.generator.GeneratorContext;
import org.instancio.internal.util.LuhnUtils;
import org.instancio.internal.util.StringUtils;
import org.instancio.settings.Settings;
import org.instancio.support.DefaultRandom;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag(Feature.GENERATOR)
class CreditCardNumberGeneratorTest {
    private static final Settings settings = Settings.defaults();
    private final Random random = new DefaultRandom();
    private final CreditCardNumberGenerator generator = new CreditCardNumberGenerator(new GeneratorContext(settings, random));

    @Test
    void apiMethod() {
        assertThat(generator.apiMethod()).isEqualTo("creditCard()");
    }

    @Test
    void generate() {
        for (int i = 0; i < Constants.SAMPLE_SIZE_DDD; i++) {
            final CCTypeImpl cardType = Gen.oneOf(CCTypeImpl.values()).get();

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

            assertThat(LuhnUtils.isLuhnValid(result))
                    .as("%s card number '%s' failed Luhn check", cardType, result)
                    .isTrue();
        }
    }

    @Test
    void nullable() {
        final Stream<String> results = Stream.generate(() -> generator.nullable().generate(random))
                .limit(Constants.SAMPLE_SIZE_DDD);

        assertThat(results).containsNull();
    }
}
