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
package org.instancio.test.features.oflist;

import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.person.Phone;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.allStrings;
import static org.instancio.Select.field;

@FeatureTag({Feature.OF_LIST, Feature.MODEL})
@ExtendWith(InstancioExtension.class)
class OfListToModelTest {

    @Test
    void model() {
        final int expectedSize = 3;
        final Model<List<Phone>> model = Instancio.ofList(Phone.class)
                .size(expectedSize)
                .set(field(Phone::getCountryCode), "+1")
                .toModel();

        final List<Phone> results = Instancio.create(model);

        assertThat(results)
                .hasSize(expectedSize)
                .extracting(Phone::getCountryCode)
                .containsOnly("+1");
    }

    @Test
    void nestedListsFromModel() {
        final Model<List<String>> model = Instancio.ofList(String.class)
                .generate(allStrings(), gen -> gen.string().length(1))
                .toModel();

        final List<List<String>> results = Instancio.ofList(model).create();

        assertThat(results).isNotEmpty().allSatisfy(nested ->
                assertThat(nested).isNotEmpty().allSatisfy(s ->
                        assertThat(s).hasSize(1)));
    }
}
