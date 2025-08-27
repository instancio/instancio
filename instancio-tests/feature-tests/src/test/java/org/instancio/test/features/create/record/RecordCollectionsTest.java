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
import org.instancio.TypeToken;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.record.GenericPairRecord;
import org.instancio.test.support.pojo.record.PersonRecord;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatObject;
import static org.instancio.Select.field;

/**
 * NOTE: this test fails in IntelliJ, run it using Maven
 */
@FeatureTag(Feature.OF_LIST)
@ExtendWith(InstancioExtension.class)
class RecordCollectionsTest {

    @Test
    void listWithGenericElement() {
        final List<GenericPairRecord<String, Long>> result = Instancio.create(new TypeToken<>() {});

        assertThat(result).isNotEmpty()
                .allSatisfy(pair -> assertThatObject(pair).hasNoNullFieldsOrProperties());
    }

    @Test
    void listUsingNonGenericElement() {
        final List<PersonRecord> result = Instancio.of(new TypeToken<List<PersonRecord>>() {})
                .set(field(PersonRecord.class, "name"), "foo")
                .create();

        assertThat(result)
                .isNotEmpty()
                .allSatisfy(person -> assertThatObject(person).hasNoNullFieldsOrProperties());
    }

    @Test
    void ofList() {
        final List<PersonRecord> result = Instancio.ofList(PersonRecord.class).size(3).create();

        assertThat(result)
                .hasSize(3)
                .allSatisfy(person -> assertThatObject(person).hasNoNullFieldsOrProperties());
    }
}
