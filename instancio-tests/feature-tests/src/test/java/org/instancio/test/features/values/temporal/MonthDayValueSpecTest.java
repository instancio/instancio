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
package org.instancio.test.features.values.temporal;

import org.instancio.Instancio;
import org.instancio.generator.specs.MonthDaySpec;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.features.values.AbstractValueSpecTestTemplate;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.MonthDay;

import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag(Feature.VALUE_SPEC)
@ExtendWith(InstancioExtension.class)
class MonthDayValueSpecTest extends AbstractValueSpecTestTemplate<MonthDay> {

    private final MonthDay min = MonthDay.of(1, 15);
    private final MonthDay max = MonthDay.of(1, 20);

    @Override
    protected MonthDaySpec spec() {
        return Instancio.gen().temporal().monthDay();
    }

    @Test
    void minMax() {
        final MonthDay actual = spec().min(min).max(max).get();

        assertThat(actual).isBetween(min, max);
    }

    @Test
    void range() {
        final MonthDay actual = spec().range(min, max).get();

        assertThat(actual).isBetween(min, max);
    }
}
