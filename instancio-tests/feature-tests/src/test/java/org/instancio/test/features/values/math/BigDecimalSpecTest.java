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
package org.instancio.test.features.values.math;

import org.instancio.Instancio;
import org.instancio.generator.specs.BigDecimalSpec;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.features.values.AbstractValueSpecTestTemplate;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag(Feature.VALUE_SPEC)
@ExtendWith(InstancioExtension.class)
class BigDecimalSpecTest extends AbstractValueSpecTestTemplate<BigDecimal> {

    @Override
    protected BigDecimalSpec spec() {
        return Instancio.gen().math().bigDecimal();
    }

    @Test
    void scale() {
        final BigDecimal result = spec().scale(10).get();
        assertThat(result).hasScaleOf(10);
    }

    @Test
    void precision() {
        final BigDecimal result = spec().precision(15).get();
        assertThat(result.precision()).isEqualTo(15);
    }

    @Test
    void min() {
        final BigDecimal result = spec().min(new BigDecimal(Integer.MAX_VALUE)).get();
        assertThat(result).isGreaterThanOrEqualTo(new BigDecimal(Integer.MAX_VALUE));
    }

    @Test
    void max() {
        final BigDecimal result = spec().max(new BigDecimal(Integer.MIN_VALUE)).get();
        assertThat(result).isLessThanOrEqualTo(new BigDecimal(Integer.MIN_VALUE));
    }

    @Test
    void range() {
        final BigDecimal result = spec().range(BigDecimal.ZERO, BigDecimal.ONE).get();
        assertThat(result).isBetween(BigDecimal.ZERO, BigDecimal.ONE);
    }
}
