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

package org.instancio.test.features.generator.temporal;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.root;

@FeatureTag({Feature.GENERATE, Feature.TEMPORAL_GENERATOR})
@ExtendWith(InstancioExtension.class)
class ZonedDateTimeGeneratorTest {

    @Test
    void truncatedTo() {
        final ZonedDateTime result = Instancio.of(ZonedDateTime.class)
                .generate(root(), gen -> gen.temporal().zonedDateTime().truncatedTo(ChronoUnit.HOURS))
                .create();

        assertThat(result.getMinute()).isZero();
        assertThat(result.getSecond()).isZero();
    }

    @Test
    void truncatedToAs() {
        final Integer result = Instancio.of(Integer.class)
                .generate(root(), gen -> gen.temporal().zonedDateTime()
                        .truncatedTo(ChronoUnit.HOURS)
                        .as(ZonedDateTime::getMinute))
                .create();

        assertThat(result).isZero();
    }

    @Test
    void pastWithMin() {
        final ZonedDateTime from = ZonedDateTime.now(ZoneOffset.UTC).minusMinutes(1);

        final ZonedDateTime result = Instancio.of(ZonedDateTime.class)
                .generate(root(), gen -> gen.temporal().zonedDateTime().past().min(from))
                .create();

        assertThat(result).isBetween(from, ZonedDateTime.now());
    }
}