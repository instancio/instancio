/*
 * Copyright 2022-2024 the original author or authors.
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
package org.instancio.test.features.generator.finance;

import org.instancio.GeneratorSpecProvider;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.root;

@FeatureTag(Feature.GENERATOR)
@ExtendWith(InstancioExtension.class)
class CreditCardNumberGeneratorTest {

    private static String create(final GeneratorSpecProvider<String> spec) {
        return Instancio.of(String.class)
                .generate(root(), spec)
                .create();
    }

    @Test
    void randomCardTypesShouldBeGeneratedByDefault() {
        final Set<String> result = Stream.generate(() -> create(gen -> gen.finance().creditCard()))
                .limit(Constants.SAMPLE_SIZE_DD)
                .map(s -> s.substring(0, 1))
                .collect(Collectors.toSet());

        assertThat(result)
                .as("Should contain Visa and MasterCard prefixes")
                .contains("4", "5");
    }

    @Test
    void visa() {
        final String result = create(gen -> gen.finance().creditCard().visa());
        assertThat(result).startsWith("4");
    }

    @Test
    void masterCard() {
        final String result = create(gen -> gen.finance().creditCard().masterCard());
        assertThat(result).startsWith("5");
    }

    @Test
    void nullable() {
        final Stream<String> result = Stream.generate(() -> create(gen -> gen.finance().creditCard().nullable()))
                .limit(Constants.SAMPLE_SIZE_DDD);

        assertThat(result).containsNull();
    }
}
