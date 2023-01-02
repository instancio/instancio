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
package org.instancio.test.features.oflist;

import org.instancio.Instancio;
import org.instancio.test.support.pojo.person.Address;
import org.instancio.test.support.pojo.person.Phone;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.allStrings;
import static org.instancio.Select.root;
import static org.instancio.Select.scope;

@FeatureTag({Feature.OF_LIST, Feature.ROOT_SELECTOR})
class OfListSelectorsTest {

    @Test
    void selectRoot() {
        final int expectedSize = 1;
        final List<Phone> results = Instancio.ofList(Phone.class)
                .generate(root(), gen -> gen.collection().size(expectedSize))
                .create();

        assertThat(results).hasSize(expectedSize);
    }

    @Test
    void selectAllLists() {
        final List<Phone> results = Instancio.ofList(Phone.class)
                .set(all(List.class), null)
                .create();

        assertThat(results).isNull();
    }

    @Test
    void selectAllListsWithScope() {
        final List<Address> results = Instancio.ofList(Address.class)
                .set(all(List.class).within(scope(Address.class)), null)
                .create();

        assertThat(results)
                .isNotEmpty()
                .extracting(Address::getPhoneNumbers)
                .containsOnlyNulls();
    }

    @Test
    void applySelectorsToElements() {
        final String prefix = "_";
        final List<String> results = Instancio.ofList(String.class)
                .generate(allStrings(), gen -> gen.string().prefix(prefix))
                .create();

        assertThat(results)
                .isNotEmpty()
                .allSatisfy(s -> assertThat(s).startsWith(prefix));
    }

    @Test
    void withElements() {
        final List<String> results = Instancio.ofList(String.class)
                .generate(root(), gen -> gen.collection().with("foo", "bar"))
                .create();

        assertThat(results).hasSizeGreaterThan(2).contains("foo", "bar");
    }

    @Test
    void subtype() {
        final List<String> results = Instancio.ofList(String.class)
                .subtype(root(), LinkedList.class)
                .create();

        assertThat(results)
                .isNotEmpty()
                .isExactlyInstanceOf(LinkedList.class)
                .doesNotContainNull();
    }
}
