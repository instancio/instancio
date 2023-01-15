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
package org.instancio.test.features.ofmap;

import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.test.support.pojo.person.Phone;
import org.instancio.test.support.pojo.person.Phone_;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag({Feature.OF_MAP, Feature.MODEL})
class OfMapToModelTest {

    @Test
    void model() {
        final int expectedSize = 3;
        final Model<Map<UUID, Phone>> model = Instancio.ofMap(UUID.class, Phone.class)
                .size(expectedSize)
                .set(Phone_.countryCode, "+1")
                .toModel();

        final Map<UUID, Phone> results = Instancio.create(model);

        assertThat(results.values())
                .hasSize(expectedSize)
                .extracting(Phone::getCountryCode)
                .containsOnly("+1");
    }
}
