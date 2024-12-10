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
package org.instancio.test.beanvalidation;

import org.instancio.Instancio;
import org.instancio.junit.Given;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.pojo.beanvalidation.LuhnCheckBV.WithDefaults;
import org.instancio.test.pojo.beanvalidation.LuhnCheckBV.WithEndAndCheckDigitIndicesEqual;
import org.instancio.test.pojo.beanvalidation.LuhnCheckBV.WithStartEndAndCheckDigitIndices;
import org.instancio.test.pojo.beanvalidation.LuhnCheckBV.WithStartEndIndices;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.util.HibernateValidatorUtil;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Instancio.gen;
import static org.instancio.Select.field;
import static org.instancio.test.support.util.Constants.SAMPLE_SIZE_DDD;

@FeatureTag(Feature.BEAN_VALIDATION)
@ExtendWith(InstancioExtension.class)
class LuhnCheckBVTest {

    @Test
    void withDefaults(@Given Stream<WithDefaults> results) {
        assertThat(results.limit(SAMPLE_SIZE_DDD))
                .hasSize(SAMPLE_SIZE_DDD)
                .allSatisfy(HibernateValidatorUtil::assertValid);
    }

    @Test
    void withStartEndIndices(@Given Stream<WithStartEndIndices> results) {
        assertThat(results.limit(SAMPLE_SIZE_DDD))
                .hasSize(SAMPLE_SIZE_DDD)
                .allSatisfy(HibernateValidatorUtil::assertValid);
    }

    @Test
    void withStartEndAndCheckDigitIndices(@Given Stream<WithStartEndAndCheckDigitIndices> results) {
        assertThat(results.limit(SAMPLE_SIZE_DDD))
                .hasSize(SAMPLE_SIZE_DDD)
                .allSatisfy(HibernateValidatorUtil::assertValid);
    }

    @Disabled("https://hibernate.atlassian.net/browse/HV-1945")
    @Test
    void withEndAndCheckDigitIndicesEqual(@Given Stream<WithEndAndCheckDigitIndicesEqual> results) {
        assertThat(results.limit(SAMPLE_SIZE_DDD))
                .hasSize(SAMPLE_SIZE_DDD)
                .allSatisfy(HibernateValidatorUtil::assertValid);
    }

    @Nested
    class LuhnGeneratorSpecTest {

        @Test
        void withDefaults() {
            final Stream<WithDefaults> results = Instancio.of(WithDefaults.class)
                    .generate(field(WithDefaults::getValue), gen -> gen.checksum().luhn())
                    .stream()
                    .limit(SAMPLE_SIZE_DDD);

            assertThat(results).hasSize(SAMPLE_SIZE_DDD).allSatisfy(HibernateValidatorUtil::assertValid);
        }

        @Test
        void withStartEndIndices() {
            final Stream<WithStartEndIndices> results = Instancio.of(WithStartEndIndices.class)
                    .generate(field(WithStartEndIndices::getValue0), gen -> gen.checksum().luhn()
                            .startIndex(0).endIndex(7))

                    .generate(field(WithStartEndIndices::getValue1), gen -> gen.checksum().luhn()
                            .startIndex(5).endIndex(10))

                    .generate(field(WithStartEndIndices::getValue2), gen -> gen.checksum().luhn()
                            .startIndex(1).endIndex(21))

                    .generate(field(WithStartEndIndices::getValue3), gen -> gen.checksum().luhn()
                            .startIndex(100).endIndex(105))

                    .stream()
                    .limit(SAMPLE_SIZE_DDD);

            assertThat(results).hasSize(SAMPLE_SIZE_DDD).allSatisfy(HibernateValidatorUtil::assertValid);
        }

        @Test
        void withStartEndIndicesAndLength() {
            final Stream<WithStartEndIndices> results = Instancio.of(WithStartEndIndices.class)
                    .generate(field(WithStartEndIndices::getValue0), gen -> gen.checksum().luhn()
                            .length(10)
                            .startIndex(0).endIndex(7))

                    .generate(field(WithStartEndIndices::getValue1), gen -> gen.checksum().luhn()
                            .length(15, 20)
                            .startIndex(5).endIndex(10))

                    .generate(field(WithStartEndIndices::getValue2), gen -> gen.checksum().luhn()
                            .length(25)
                            .startIndex(1).endIndex(21))

                    .generate(field(WithStartEndIndices::getValue3), gen -> gen.checksum().luhn()
                            .length(106)
                            .startIndex(100).endIndex(105))

                    .stream()
                    .limit(SAMPLE_SIZE_DDD);

            assertThat(results).hasSize(SAMPLE_SIZE_DDD).allSatisfy(result -> {
                HibernateValidatorUtil.assertValid(result);

                assertThat(result.getValue0()).hasSize(10);
                assertThat(result.getValue1()).hasSizeBetween(15, 20);
                assertThat(result.getValue2()).hasSize(25);
                assertThat(result.getValue3()).hasSize(106);
            });
        }

        @Test
        void withStartEndAndCheckDigitIndices() {
            final Stream<WithStartEndAndCheckDigitIndices> results = Instancio.of(WithStartEndAndCheckDigitIndices.class)
                    .generate(field(WithStartEndAndCheckDigitIndices::getValue0), gen -> gen.checksum().luhn()
                            .startIndex(0).endIndex(7).checkDigitIndex(8))

                    .generate(field(WithStartEndAndCheckDigitIndices::getValue1), gen -> gen.checksum().luhn()
                            .startIndex(5).endIndex(10).checkDigitIndex(3))

                    .generate(field(WithStartEndAndCheckDigitIndices::getValue2), gen -> gen.checksum().luhn()
                            .startIndex(1).endIndex(21).checkDigitIndex(0))

                    .generate(field(WithStartEndAndCheckDigitIndices::getValue3), gen -> gen.checksum().luhn()
                            .startIndex(100).endIndex(105).checkDigitIndex(150))

                    .stream()
                    .limit(SAMPLE_SIZE_DDD);

            assertThat(results).hasSize(SAMPLE_SIZE_DDD).allSatisfy(HibernateValidatorUtil::assertValid);
        }
    }

    @Nested
    class LuhnSpecTest {

        @Test
        void withDefaults() {
            final Stream<WithDefaults> results = Instancio.of(WithDefaults.class)
                    .generate(field(WithDefaults::getValue), gen().checksum().luhn())
                    .stream()
                    .limit(SAMPLE_SIZE_DDD);

            assertThat(results).hasSize(SAMPLE_SIZE_DDD).allSatisfy(HibernateValidatorUtil::assertValid);
        }

        @Test
        void withStartEndIndices() {
            final Stream<WithStartEndIndices> results = Instancio.of(WithStartEndIndices.class)
                    .generate(field(WithStartEndIndices::getValue0), gen().checksum().luhn()
                            .startIndex(0).endIndex(7))

                    .generate(field(WithStartEndIndices::getValue1), gen().checksum().luhn()
                            .startIndex(5).endIndex(10))

                    .generate(field(WithStartEndIndices::getValue2), gen().checksum().luhn()
                            .startIndex(1).endIndex(21))

                    .generate(field(WithStartEndIndices::getValue3), gen().checksum().luhn()
                            .startIndex(100).endIndex(105))

                    .stream()
                    .limit(SAMPLE_SIZE_DDD);

            assertThat(results).hasSize(SAMPLE_SIZE_DDD).allSatisfy(HibernateValidatorUtil::assertValid);
        }

        @Test
        void withStartEndIndicesAndLength() {
            final Stream<WithStartEndIndices> results = Instancio.of(WithStartEndIndices.class)
                    .generate(field(WithStartEndIndices::getValue0), gen().checksum().luhn()
                            .length(10)
                            .startIndex(0).endIndex(7))

                    .generate(field(WithStartEndIndices::getValue1), gen().checksum().luhn()
                            .length(15, 20)
                            .startIndex(5).endIndex(10))

                    .generate(field(WithStartEndIndices::getValue2), gen().checksum().luhn()
                            .length(25)
                            .startIndex(1).endIndex(21))

                    .generate(field(WithStartEndIndices::getValue3), gen().checksum().luhn()
                            .length(106)
                            .startIndex(100).endIndex(105))

                    .stream()
                    .limit(SAMPLE_SIZE_DDD);

            assertThat(results).hasSize(SAMPLE_SIZE_DDD).allSatisfy(result -> {
                HibernateValidatorUtil.assertValid(result);

                assertThat(result.getValue0()).hasSize(10);
                assertThat(result.getValue1()).hasSizeBetween(15, 20);
                assertThat(result.getValue2()).hasSize(25);
                assertThat(result.getValue3()).hasSize(106);
            });
        }

        @Test
        void withStartEndAndCheckDigitIndices() {
            final Stream<WithStartEndAndCheckDigitIndices> results = Instancio.of(WithStartEndAndCheckDigitIndices.class)
                    .generate(field(WithStartEndAndCheckDigitIndices::getValue0), gen().checksum().luhn()
                            .startIndex(0).endIndex(7).checkDigitIndex(8))

                    .generate(field(WithStartEndAndCheckDigitIndices::getValue1), gen().checksum().luhn()
                            .startIndex(5).endIndex(10).checkDigitIndex(3))

                    .generate(field(WithStartEndAndCheckDigitIndices::getValue2), gen().checksum().luhn()
                            .startIndex(1).endIndex(21).checkDigitIndex(0))

                    .generate(field(WithStartEndAndCheckDigitIndices::getValue3), gen().checksum().luhn()
                            .startIndex(100).endIndex(105).checkDigitIndex(150))

                    .stream()
                    .limit(SAMPLE_SIZE_DDD);

            assertThat(results).hasSize(SAMPLE_SIZE_DDD).allSatisfy(HibernateValidatorUtil::assertValid);
        }
    }
}
