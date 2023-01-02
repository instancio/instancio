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
package org.instancio.test.features.generator.asstring;

import org.instancio.Instancio;
import org.instancio.test.support.pojo.person.Gender;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.allStrings;

@FeatureTag({Feature.GENERATOR, Feature.AS_STRING_GENERATOR_SPEC})
class AsStringGeneratorSpecTest {

    @Test
    void localDate() {
        final LocalDate expected = LocalDate.now();
        final String result = Instancio.of(String.class)
                .generate(allStrings(), gen -> gen.temporal().localDate().range(expected, expected).asString())
                .create();

        assertThat(result).isEqualTo(expected.toString());
    }

    @Test
    void longs() {
        final Long expected = -5L;
        final String result = Instancio.of(String.class)
                .generate(allStrings(), gen -> gen.longs().range(expected, expected).asString())
                .create();

        assertThat(result).isEqualTo(expected.toString());
    }

    @Test
    void bigDecimal() {
        final int scale = 10;
        final BigDecimal expected = new BigDecimal("12.99").setScale(scale, RoundingMode.HALF_UP);
        final String result = Instancio.of(String.class)
                .generate(allStrings(), gen -> gen.math().bigDecimal().scale(scale).range(expected, expected).asString())
                .create();

        assertThat(result).isEqualTo(expected.toString());
    }

    @Test
    void booleanWithToStringFunction() {
        final String result = Instancio.of(String.class)
                .generate(allStrings(), gen -> gen.booleans()
                        .asString(b -> b ? "Yes" : "No"))
                .create();

        assertThat(result).isIn("Yes", "No");
    }

    @Test
    void charWithToStringFunction() {
        final String result = Instancio.of(String.class)
                .generate(allStrings(), gen -> gen.chars()
                        .asString(c -> String.valueOf(Character.toLowerCase(c))))
                .create();

        assertThat(result).isLowerCase();
    }

    @Test
    void enumWithToStringFunction() {
        final String result = Instancio.of(String.class)
                .generate(allStrings(), gen -> gen.enumOf(Gender.class)
                        .excluding(Gender.FEMALE, Gender.MALE)
                        .asString(g -> g.name().toUpperCase()))
                .create();

        assertThat(result).isEqualTo(Gender.OTHER.name().toUpperCase());
    }

}
