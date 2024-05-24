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
package org.instancio.test.java16;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.java16.record.PersonRecord;
import org.instancio.test.support.java16.record.PhoneRecord;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;

@FeatureTag(Feature.BLANK)
@ExtendWith(InstancioExtension.class)
class BlankRecordTest {

    @Test
    void blankRecord() {
        final PersonRecord result = Instancio.createBlank(PersonRecord.class);

        assertThat(result.name()).isNull();
        assertThat(result.age()).isZero();
        assertThat(result.address().street()).isNull();
        assertThat(result.address().city()).isNull();
        assertThat(result.address().phoneNumbers()).isEmpty();
    }

    @Test
    void blankRecordWithSelector() {
        final PersonRecord result = Instancio.of(PersonRecord.class)
                .setBlank(all(PhoneRecord.class))
                .create();

        assertThat(result.name()).isNotBlank();
        assertThat(result.age()).isPositive();
        assertThat(result.address().street()).isNotBlank();
        assertThat(result.address().city()).isNotBlank();
        assertThat(result.address().phoneNumbers()).isNotEmpty().allSatisfy(phone -> {
            assertThat(phone.countryCode()).isNull();
            assertThat(phone.number()).isNull();
        });
    }
}