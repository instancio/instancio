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
package org.instancio.test.features.generator.id.pol;

import org.instancio.GeneratorSpecProvider;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.root;
import static org.instancio.internal.util.NumberUtils.toDigitInt;
import static org.instancio.test.support.conditions.Conditions.EVEN_NUMBER;
import static org.instancio.test.support.conditions.Conditions.ODD_NUMBER;
import static org.instancio.test.support.util.Constants.SAMPLE_SIZE_DD;
import static org.instancio.test.support.util.Constants.SAMPLE_SIZE_DDD;

@FeatureTag(Feature.GENERATOR)
@ExtendWith(InstancioExtension.class)
class PeselGeneratorTest {

    private static String create(final GeneratorSpecProvider<String> spec) {
        return Instancio.of(String.class)
                .generate(root(), spec)
                .create();
    }

    @Test
    void pesel() {
        final String result = create(gen -> gen.id().pol().pesel());

        assertThat(result)
                .hasSize(11)
                .containsOnlyDigits();
    }

    @Test
    void birthdate() {
        final LocalDate localDate = LocalDate.of(1990, 1, 1);
        final String result = create(gen -> gen.id().pol().pesel().birthdate(random -> localDate));

        assertThat(result)
                .containsOnlyDigits()
                .startsWith("900101");
    }

    @Test
    void randomGendersShouldBeGeneratedByDefault() {
        final Set<Integer> result = Stream.generate(() -> create(gen -> gen.id().pol().pesel()))
                .map(s -> toDigitInt(s.charAt(9)))
                .limit(SAMPLE_SIZE_DD)
                .collect(Collectors.toSet());

        assertThat(result)
                .as("Should contain at least one male digit and at least one female digit")
                .haveAtLeastOne(ODD_NUMBER)
                .haveAtLeastOne(EVEN_NUMBER);
    }

    @Test
    void male() {
        final String result = create(gen -> gen.id().pol().pesel().male());
        final int maleDigit = toDigitInt(result.charAt(9));

        assertThat(maleDigit).is(ODD_NUMBER);
    }

    @Test
    void female() {
        final String result = create(gen -> gen.id().pol().pesel().female());
        final int femaleDigit = toDigitInt(result.charAt(9));

        assertThat(femaleDigit).is(EVEN_NUMBER);
    }

    @Test
    void nullable() {
        final Stream<String> result = Stream.generate(() -> create(gen -> gen.id().pol().pesel().nullable()))
                .limit(SAMPLE_SIZE_DDD);

        assertThat(result).containsNull();
    }
}
