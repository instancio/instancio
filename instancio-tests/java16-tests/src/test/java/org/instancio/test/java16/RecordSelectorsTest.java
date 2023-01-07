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
import static org.instancio.Select.allStrings;
import static org.instancio.Select.field;
import static org.instancio.Select.root;
import static org.instancio.Select.types;
import static org.instancio.test.support.asserts.ReflectionAssert.assertThatObject;

/**
 * NOTE: this test fails in IntelliJ, run it using Maven
 */
@FeatureTag({
        Feature.SELECTOR,
        Feature.PREDICATE_SELECTOR,
        Feature.ROOT_SELECTOR,
        Feature.SCOPE,
        Feature.TO_SCOPE
})
@ExtendWith(InstancioExtension.class)
class RecordSelectorsTest {

    @Test
    void selectRoot() {
        final PersonRecord result = Instancio.of(PersonRecord.class)
                .set(root(), null)
                .create();

        assertThat(result).isNull();
    }

    @Test
    void predicateTargetingRecord() {
        final PhoneRecord expected = Instancio.create(PhoneRecord.class);
        final PersonRecord result = Instancio.of(PersonRecord.class)
                .set(types().of(PhoneRecord.class), expected)
                .create();

        assertThat(result.address().phoneNumbers())
                .isNotEmpty().containsOnly(expected);
    }

    @Test
    void methodReferenceSelectors() {
        final PhoneRecord expected = Instancio.create(PhoneRecord.class);
        final PhoneRecord result = Instancio.of(PhoneRecord.class)
                .set(field(PhoneRecord::countryCode), expected.countryCode())
                .set(field(PhoneRecord::number), expected.number())
                .create();

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void withinScope() {
        final PersonRecord result = Instancio.of(PersonRecord.class)
                .set(allStrings().within(all(PhoneRecord.class).toScope()), "foo")
                .create();

        assertThat(result.address().city()).isNotEqualTo("foo");
        assertThat(result.address().phoneNumbers()).isNotEmpty()
                .allSatisfy(phone -> assertThatObject(phone).hasAllFieldsOfTypeEqualTo(String.class, "foo"));
    }
}
