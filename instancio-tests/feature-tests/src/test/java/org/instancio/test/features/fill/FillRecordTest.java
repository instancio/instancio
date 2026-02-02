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
package org.instancio.test.features.fill;

import lombok.Data;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.conditions.Conditions;
import org.instancio.test.support.pojo.record.PhoneRecord;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag(Feature.FILL)
@ExtendWith(InstancioExtension.class)
class FillRecordTest {

    @Test
    @DisplayName("fill() should not throw an exception if given a record instance")
    void fillShouldNotThrowingAnExceptionIfGivenARecord() {
        final PhoneRecord object = new PhoneRecord(null, "12345");

        Instancio.fill(object);

        assertThat(object.countryCode()).isNull();
        assertThat(object.number()).isEqualTo("12345");
    }

    @Test
    @DisplayName("fill() should not throw an exception if given a nested record instance")
    void fillShouldNotThrowingAnExceptionIfGivenANestedRecord() {
        final Pojo object = new Pojo();
        object.setPhoneRecord(new PhoneRecord(null, "12345"));

        Instancio.fill(object);

        assertThat(object.value).is(Conditions.RANDOM_STRING);
        assertThat(object.phoneRecord.countryCode()).isNull();
        assertThat(object.phoneRecord.number()).isEqualTo("12345");
    }

    private static @Data class Pojo {
        String value;
        PhoneRecord phoneRecord;
    }
}
