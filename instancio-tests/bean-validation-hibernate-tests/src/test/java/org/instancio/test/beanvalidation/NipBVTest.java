/*
 * Copyright 2022-2025 the original author or authors.
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
import org.instancio.settings.Keys;
import org.instancio.test.pojo.beanvalidation.NipBV;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.util.HibernateValidatorUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.test.support.util.Constants.SAMPLE_SIZE_DDD;

@FeatureTag(Feature.BEAN_VALIDATION)
@ExtendWith(InstancioExtension.class)
class NipBVTest {

    @Test
    void nip(@Given Stream<NipBV> results) {
        assertThat(results.limit(SAMPLE_SIZE_DDD))
                .hasSize(SAMPLE_SIZE_DDD)
                .allSatisfy(HibernateValidatorUtil::assertValid);
    }

    @Test
    void whenFailOnMaxGenerationAttemptsReachedDisabled_shouldGenerateDefaultNip() {
        var results = Instancio.ofList(NipBV.class)
                .size(SAMPLE_SIZE_DDD)
                .withSetting(Keys.MAX_GENERATION_ATTEMPTS, 0)
                .withSetting(Keys.COLLECTION_NULLABLE, false)
                .withSetting(Keys.FAIL_ON_MAX_GENERATION_ATTEMPTS_REACHED, false)
                .create();

        assertThat(results)
                .hasSize(SAMPLE_SIZE_DDD)
                .allSatisfy(HibernateValidatorUtil::assertValid)
                .allSatisfy(n -> assertThat(n.getNip()).isEqualTo("1234563218"));
    }
}
