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
package org.instancio.test.java16.maxdepth;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.java16.record.PersonRecord;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * NOTE: this test fails in IntelliJ, run it using Maven
 */
@FeatureTag(Feature.MAX_DEPTH)
@ExtendWith(InstancioExtension.class)
class MaxDepthWithRecordTest {

    private static PersonRecord createWithDepth(final int depth) {
        return Instancio.of(PersonRecord.class)
                .withSettings(Settings.create().set(Keys.MAX_DEPTH, depth))
                .create();
    }

    @Test
    void depth0() {
        final PersonRecord result = createWithDepth(0);

        assertThat(result).hasAllNullFieldsOrPropertiesExcept("age");
        assertThat(result.age()).isZero();
    }

    @Test
    void depth1() {
        final PersonRecord result = createWithDepth(1);

        assertThat(result).hasNoNullFieldsOrProperties();
        assertThat(result.age()).isNotZero();
        assertThat(result.address()).hasAllNullFieldsOrProperties();
    }

    @Test
    void depth2() {
        final PersonRecord result = createWithDepth(2);

        assertThat(result).hasNoNullFieldsOrProperties();
        assertThat(result.age()).isNotZero();
        assertThat(result.address()).hasNoNullFieldsOrProperties();
        assertThat(result.address().phoneNumbers()).isEmpty();
    }

    @Test
    void depth3() {
        final PersonRecord result = createWithDepth(3);

        assertThat(result).hasNoNullFieldsOrProperties();
        assertThat(result.age()).isNotZero();
        assertThat(result.address()).hasNoNullFieldsOrProperties();
        assertThat(result.address().phoneNumbers()).isNotEmpty()
                .allSatisfy(phone -> assertThat(phone).hasAllNullFieldsOrProperties());
    }

    @Test
    void depth4() {
        final PersonRecord result = createWithDepth(4);

        assertThat(result).hasNoNullFieldsOrProperties();
        assertThat(result.age()).isNotZero();
        assertThat(result.address()).hasNoNullFieldsOrProperties();
        assertThat(result.address().phoneNumbers()).isNotEmpty()
                .allSatisfy(phone -> assertThat(phone).hasNoNullFieldsOrProperties());
    }
}
