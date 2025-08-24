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
package org.instancio.test.features.create.record;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.asserts.Asserts;
import org.instancio.test.support.pojo.record.AddressRecord;
import org.instancio.test.support.pojo.record.PersonRecord;
import org.instancio.test.support.pojo.record.PhoneRecord;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.allStrings;
import static org.instancio.Select.field;
import static org.instancio.Select.fields;
import static org.instancio.test.support.asserts.ReflectionAssert.assertThatObject;

/**
 * NOTE: this test fails in IntelliJ, run it using Maven
 */
@FeatureTag({
        Feature.IGNORE,
        Feature.GENERATE,
        Feature.ON_COMPLETE,
        Feature.SET,
        Feature.SUPPLY
})
@ExtendWith(InstancioExtension.class)
class PersonRecordTest {

    @Test
    void createRecord() {
        final PersonRecord result = Instancio.create(PersonRecord.class);
        assertThatObject(result).isFullyPopulated();
    }

    @Test
    void createArrayOfRecords() {
        final PersonRecord[] results = Instancio.create(PersonRecord[].class);

        assertThat(results).isNotEmpty().allSatisfy(result -> {
            assertThatObject(result).isFullyPopulated();
        });
    }

    @Test
    void customiseRecordFields() {
        final PersonRecord result = Instancio.of(PersonRecord.class)
                .generate(field(PhoneRecord.class, "number"), gen -> gen.string().digits().length(7))
                .create();

        assertThat(result.address().phoneNumbers())
                .isNotEmpty()
                .extracting(PhoneRecord::number)
                .allSatisfy(number -> assertThat(number).hasSize(7).containsOnlyDigits());
    }

    @Test
    void ignore() {
        final PersonRecord result = Instancio.of(PersonRecord.class)
                .ignore(all(
                        allStrings(),
                        field("age")))
                .ignore(fields().named("phoneNumbers").declaredIn(AddressRecord.class))
                .create();

        assertThat(result.age()).isZero();

        final AddressRecord address = result.address();
        Asserts.assertAllNulls(
                result.name(),
                address.city(),
                address.street(),
                address.phoneNumbers());
    }

    @Test
    void supplyWholeRecord() {
        final String city = "some-city";
        final String streetPrefix = "street-";

        int[] callbackCount = {0};
        final PersonRecord result = Instancio.of(PersonRecord.class)
                .supply(field("address"), rnd -> new AddressRecord(streetPrefix + rnd.digits(3), city, List.of()))
                .onComplete(all(AddressRecord.class), (AddressRecord address) -> callbackCount[0]++)
                .create();

        assertThat(result.address().city()).isEqualTo(city);
        assertThat(result.address().street()).startsWith(streetPrefix).hasSizeGreaterThan(streetPrefix.length());
        assertThat(callbackCount[0]).isOne();
    }

    @Test
    void verifyCallbackIsCalled() {
        int[] callbackCount = {0};
        Instancio.of(PersonRecord.class)
                .set(field(AddressRecord.class, "city"), "foo")
                .onComplete(all(AddressRecord.class), (AddressRecord address) -> {
                    assertThatObject(address).isFullyPopulated();
                    assertThat(address.city()).isEqualTo("foo");
                    callbackCount[0]++;
                })
                .create();

        assertThat(callbackCount[0]).isOne();
    }
}
