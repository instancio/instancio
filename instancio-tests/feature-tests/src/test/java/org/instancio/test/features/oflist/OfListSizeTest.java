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
package org.instancio.test.features.oflist;

import org.instancio.Instancio;
import org.instancio.InstancioCollectionsApi;
import org.instancio.exception.InstancioApiException;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.person.Phone;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatObject;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@FeatureTag(Feature.OF_LIST)
@ExtendWith(InstancioExtension.class)
class OfListSizeTest {

    @Test
    void withoutSpecifyingSize() {
        final List<Phone> results = Instancio.ofList(Phone.class).create();

        assertThat(results)
                .hasSizeBetween(Constants.MIN_SIZE, Constants.MAX_SIZE)
                .allSatisfy(phone -> assertThatObject(phone).hasNoNullFieldsOrProperties());
    }

    @Test
    void withSize() {
        final int expectedSize = 10;
        final List<Phone> results = Instancio.ofList(Phone.class).size(expectedSize).create();

        assertThat(results)
                .hasSize(expectedSize)
                .allSatisfy(phone -> assertThatObject(phone).hasNoNullFieldsOrProperties());
    }

    @Test
    void withSizeZero() {
        assertThat(Instancio.ofList(Phone.class).size(0).create()).isEmpty();
    }

    @Test
    void withNegativeSize() {
        final InstancioCollectionsApi<List<Phone>> api = Instancio.ofList(Phone.class).size(-1);

        assertThatThrownBy(api::create)
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("size must not be negative: -1");
    }

}
