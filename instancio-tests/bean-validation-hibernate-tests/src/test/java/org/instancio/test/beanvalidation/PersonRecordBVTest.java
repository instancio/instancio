/*
 * Copyright 2022-2023 the original author or authors.
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
import org.instancio.junit.InstancioExtension;
import org.instancio.test.pojo.beanvalidation.records.AddressRecordBV;
import org.instancio.test.pojo.beanvalidation.records.PersonRecordBV;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.test.support.util.Constants.SAMPLE_SIZE_DDD;

@FeatureTag(Feature.BEAN_VALIDATION)
@ExtendWith(InstancioExtension.class)
class PersonRecordBVTest {

    @Test
    void personRecord() {
        final int sampleSize = SAMPLE_SIZE_DDD;

        final List<PersonRecordBV> results = Instancio.of(PersonRecordBV.class)
                .stream()
                .limit(sampleSize)
                .collect(Collectors.toList());

        assertThat(results).hasSize(sampleSize)
                .allSatisfy(result -> {
                    assertThat(UUID.fromString(result.uuid())).isNotNull();
                    assertThat(result.name()).hasSizeBetween(2, 10);
                    assertThat(result.age()).isBetween(18, 65);
                    assertThat(result.lastModified()).isBefore(LocalDateTime.now());

                    final AddressRecordBV address = result.address();
                    assertThat(address.address()).hasSizeBetween(5, 100);
                    assertThat(address.city())
                            .isNotBlank()
                            .hasSizeLessThanOrEqualTo(32);

                    assertThat(address.phoneNumbers())
                            .hasSizeBetween(1, 5)
                            .allSatisfy(phone -> {
                                assertThat(phone.countryCode()).hasSizeBetween(1, 2);
                                assertThat(phone.number()).containsOnlyDigits().hasSize(7);
                            });

                    assertThat(result.date()).isInTheFuture();
                    assertThat(result.pets()).hasSizeBetween(0, 3);
                });
    }
}
