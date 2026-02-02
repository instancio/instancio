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
package org.instancio.test.java21;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.basic.SupportedTemporalTypes;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(InstancioExtension.class)
class Java21SupportedTemporalTypesTest {

    @RepeatedTest(10)
    void create() {
        final SupportedTemporalTypes result = Instancio.create(SupportedTemporalTypes.class);

        assertThat(result.getTemporal()).isNotNull();
        assertThat(result.getInstant()).isNotNull();
        assertThat(result.getLocalTime()).isNotNull();
        assertThat(result.getLocalDate()).isNotNull();
        assertThat(result.getLocalDateTime()).isNotNull();
        assertThat(result.getOffsetTime()).isNotNull();
        assertThat(result.getOffsetDateTime()).isNotNull();
        assertThat(result.getZonedDateTime()).isNotNull();
        assertThat(result.getMonthDay()).isNotNull();
        assertThat(result.getYearMonth()).isNotNull();
        assertThat(result.getYear()).isNotNull();
        assertThat(result.getPeriod()).isNotNull();
        assertThat(result.getDate()).isNotNull();
        assertThat(result.getSqlDate()).isNotNull();
        assertThat(result.getTimestamp()).isNotNull();
        assertThat(result.getTimeZone()).isNotNull();
        assertThat(result.getCalendar()).isNotNull();

        assertThat(result.getZoneId()).isNotNull();
        assertThat(result.getZoneId().getId()).isNotNull();
        assertThat(result.getZoneId().getRules()).isNotNull();

        assertThat(result.getZoneOffset()).isNotNull();
        assertThat(result.getZoneOffset().getId()).isNotNull();
        assertThat(result.getZoneOffset().getRules()).isNotNull();
    }
}
