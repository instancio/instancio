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
package org.instancio.test.beanvalidation;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.pojo.beanvalidation.LuhnCheckBV.WithDefaults;
import org.instancio.test.pojo.beanvalidation.LuhnCheckBV.WithStartEndAndCheckDigitIndices;
import org.instancio.test.pojo.beanvalidation.LuhnCheckBV.WithEndAndCheckDigitIndicesEqual;
import org.instancio.test.pojo.beanvalidation.LuhnCheckBV.WithStartEndIndices;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.util.HibernateValidatorUtil;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.test.support.util.Constants.SAMPLE_SIZE_DDD;

@FeatureTag(Feature.BEAN_VALIDATION)
@ExtendWith(InstancioExtension.class)
class LuhnCheckBVTest {

    @Test
    void withDefaults() {
        final Stream<WithDefaults> results = Instancio.of(WithDefaults.class)
                .stream()
                .limit(SAMPLE_SIZE_DDD);

        assertThat(results).hasSize(SAMPLE_SIZE_DDD).allSatisfy(HibernateValidatorUtil::assertValid);
    }

    @Test
    void withStartEndIndices() {
        final Stream<WithStartEndIndices> results = Instancio.of(WithStartEndIndices.class)
                .stream()
                .limit(SAMPLE_SIZE_DDD);

        assertThat(results).hasSize(SAMPLE_SIZE_DDD).allSatisfy(HibernateValidatorUtil::assertValid);
    }

    @Test
    void withStartEndAndCheckDigitIndices() {
        final Stream<WithStartEndAndCheckDigitIndices> results = Instancio.of(WithStartEndAndCheckDigitIndices.class)
                .stream()
                .limit(SAMPLE_SIZE_DDD);

        assertThat(results).hasSize(SAMPLE_SIZE_DDD).allSatisfy(HibernateValidatorUtil::assertValid);
    }

    @Disabled("https://hibernate.atlassian.net/browse/HV-1945")
    @Test
    void withEndAndCheckDigitIndicesEqual() {
        final Stream<WithEndAndCheckDigitIndicesEqual> results = Instancio.of(WithEndAndCheckDigitIndicesEqual.class)
                .stream()
                .limit(SAMPLE_SIZE_DDD);

        assertThat(results).hasSize(SAMPLE_SIZE_DDD).allSatisfy(HibernateValidatorUtil::assertValid);
    }
}
