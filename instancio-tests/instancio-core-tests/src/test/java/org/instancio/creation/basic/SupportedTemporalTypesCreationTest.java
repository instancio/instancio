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
package org.instancio.creation.basic;

import org.instancio.test.support.pojo.basic.SupportedTemporalTypes;
import org.instancio.testsupport.templates.CreationTestTemplate;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class SupportedTemporalTypesCreationTest extends CreationTestTemplate<SupportedTemporalTypes> {

    @Override
    protected void verify(final SupportedTemporalTypes result) {
        assertThat(result.getTemporal())
                .as("Default temporal implementation")
                .isExactlyInstanceOf(LocalDate.class);
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
        assertThat(result.getZoneOffset()).isNotNull();
    }
}
