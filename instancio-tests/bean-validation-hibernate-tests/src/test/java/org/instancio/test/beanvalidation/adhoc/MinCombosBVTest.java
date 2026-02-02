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
package org.instancio.test.beanvalidation.adhoc;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Negative;
import jakarta.validation.constraints.NegativeOrZero;
import lombok.Data;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.util.HibernateValidatorUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.test.support.util.Constants.SAMPLE_SIZE_DDD;

/**
 * Tests for annotation combos.
 * Should produce valid results regardless of annotation order.
 */
@FeatureTag(Feature.BEAN_VALIDATION)
@ExtendWith(InstancioExtension.class)
class MinCombosBVTest {

    @Test
    @DisplayName("@Min @Negative")
    void minAndNegative() {
        @Data
        class Pojo {
            @Negative
            @Min(-2)
            BigDecimal bd1;

            @Min(-2)
            @Negative
            BigDecimal bd2;
        }

        final Stream<Pojo> results = Instancio.of(Pojo.class)
                .stream()
                .limit(SAMPLE_SIZE_DDD);

        assertThat(results)
                .hasSize(SAMPLE_SIZE_DDD)
                .allSatisfy(HibernateValidatorUtil::assertValid);
    }

    @Test
    @DisplayName("@Min @NegativeOrZero")
    void minAndNegativeOrZero() {
        @Data
        class Pojo {
            @NegativeOrZero
            @Min(-2)
            BigDecimal bd1;

            @Min(-2)
            @NegativeOrZero
            BigDecimal bd2;
        }

        final Stream<Pojo> results = Instancio.of(Pojo.class)
                .stream()
                .limit(SAMPLE_SIZE_DDD);

        assertThat(results)
                .hasSize(SAMPLE_SIZE_DDD)
                .allSatisfy(HibernateValidatorUtil::assertValid);
    }

}
