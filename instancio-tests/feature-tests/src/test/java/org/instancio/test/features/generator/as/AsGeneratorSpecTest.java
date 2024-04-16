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
package org.instancio.test.features.generator.as;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.person.Gender;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.allInts;
import static org.instancio.Select.allLongs;
import static org.instancio.Select.allStrings;

@FeatureTag({Feature.GENERATOR, Feature.AS_GENERATOR_SPEC})
@ExtendWith(InstancioExtension.class)
class AsGeneratorSpecTest {

    @Test
    void instantAsMillis() {
        final long now = System.currentTimeMillis();
        final Long result = Instancio.of(Long.class)
                .generate(allLongs(), gen -> gen.temporal().instant().future().as(Instant::toEpochMilli))
                .create();

        assertThat(result).isGreaterThan(now);
    }

    @Test
    void longAsString() {
        final Long expected = -5L;
        final String result = Instancio.of(String.class)
                .generate(allStrings(), gen -> gen.longs().range(expected, expected)
                        .as(Object::toString))
                .create();

        assertThat(result).isEqualTo(expected.toString());
    }

    @Test
    void bigDecimalAsString() {
        final int scale = 10;
        final BigDecimal expected = new BigDecimal("12.99").setScale(scale, RoundingMode.HALF_UP);
        final String result = Instancio.of(String.class)
                .generate(allStrings(), gen -> gen.math().bigDecimal().scale(scale).range(expected, expected)
                        .asString())
                .create();

        assertThat(result).isEqualTo(expected.toString());
    }

    @Test
    void booleanWithToStringFunction() {
        final String result = Instancio.of(String.class)
                .generate(allStrings(), gen -> gen.booleans()
                        .as(b -> b ? "Yes" : "No"))
                .create();

        assertThat(result).isIn("Yes", "No");
    }

    @Test
    void charAsInteger() {
        final Integer result = Instancio.of(Integer.class)
                .generate(allInts(), gen -> gen.chars().range('0', '9')
                        .as(Character::getNumericValue))
                .create();

        assertThat(result).isBetween(0, 9);
    }

    @Test
    void enumWithToStringFunction() {
        final Integer result = Instancio.of(Integer.class)
                .generate(allInts(), gen -> gen.enumOf(Gender.class).excluding(Gender.FEMALE, Gender.MALE)
                        .as(Enum::ordinal))
                .create();

        assertThat(result).isEqualTo(Gender.OTHER.ordinal());
    }
}
