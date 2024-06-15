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
package org.instancio.test.java16.assign;

import org.instancio.Assign;
import org.instancio.Instancio;
import org.instancio.When;
import org.instancio.test.support.java16.record.AddressRecord;
import org.instancio.test.support.java16.record.PhoneRecord;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;
import static org.instancio.Select.all;
import static org.instancio.Select.field;

@FeatureTag(Feature.ASSIGN)
class AssignWithCollectionsRecordTest {

    @Test
    void recordCollectionElement() {
        final String city1 = Instancio.gen().string().length(16).get();
        final String city2 = Instancio.gen().string().length(16).get();

        final List<AddressRecord> results = Instancio.ofList(AddressRecord.class)
                .generate(field(AddressRecord::city), gen -> gen.oneOf(city1, city2))
                .assign(Assign.given(field(AddressRecord::city), all(PhoneRecord.class))
                        .supply(When.is(city1), () -> new PhoneRecord("+1", "foo"))
                        .supply(When.is(city2), () -> new PhoneRecord("+2", "bar")))
                .create();

        assertThat(results).isNotEmpty().allSatisfy(result -> {
            if (result.city().equals(city1)) {
                assertThat(result.phoneNumbers()).isNotEmpty().allSatisfy(phone -> {
                    assertThat(phone.countryCode()).isEqualTo("+1");
                    assertThat(phone.number()).isEqualTo("foo");
                });

            } else if (result.city().equals(city2)) {
                assertThat(result.phoneNumbers()).isNotEmpty().allSatisfy(phone -> {
                    assertThat(phone.countryCode()).isEqualTo("+2");
                    assertThat(phone.number()).isEqualTo("bar");
                });

            } else {
                fail("Unexpected city: '%s'", result.city());
            }
        });
    }
}
