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
package org.instancio.test.features.values.temporal;

import org.instancio.Instancio;
import org.instancio.generator.specs.PeriodSpec;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.features.values.AbstractValueSpecTestTemplate;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.Period;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag(Feature.VALUE_SPEC)
@ExtendWith(InstancioExtension.class)
class PeriodValueSpecTest extends AbstractValueSpecTestTemplate<Period> {

    private static final int MIN = 1;
    private static final int MAX = 9;

    @Override
    protected PeriodSpec spec() {
        return Instancio.gen().temporal().period();
    }

    @Test
    void days() {
        final List<Period> actual = spec().days(MIN, MAX).list(Constants.SAMPLE_SIZE_DDD);

        assertThat(actual)
                .hasSize(Constants.SAMPLE_SIZE_DDD)
                .allSatisfy(p -> assertThat(p.getDays()).isBetween(MIN, MAX));
    }

    @Test
    void months() {
        final List<Period> actual = spec().months(MIN, MAX).list(Constants.SAMPLE_SIZE_DDD);

        assertThat(actual)
                .hasSize(Constants.SAMPLE_SIZE_DDD)
                .allSatisfy(p -> assertThat(p.getMonths()).isBetween(MIN, MAX));
    }

    @Test
    void years() {
        final List<Period> actual = spec().years(MIN, MAX).list(Constants.SAMPLE_SIZE_DDD);

        assertThat(actual)
                .hasSize(Constants.SAMPLE_SIZE_DDD)
                .allSatisfy(p -> assertThat(p.getYears()).isBetween(MIN, MAX));
    }
}
