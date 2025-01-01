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
package org.instancio.test.features.setbackreference;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.cyclic.CyclicArray;
import org.instancio.test.support.pojo.cyclic.CyclicKeyMap;
import org.instancio.test.support.pojo.cyclic.CyclicList;
import org.instancio.test.support.pojo.cyclic.CyclicValueMap;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag(Feature.SET_BACK_REFERENCES)
@ExtendWith(InstancioExtension.class)
class SetBackReferenceWithCyclicContainersTest {

    @WithSettings
    private final Settings settings = Settings.create()
            .set(Keys.SET_BACK_REFERENCES, true);

    @Test
    void cyclicArray() {
        final CyclicArray result = Instancio.create(CyclicArray.class);

        assertThat(result.getItems())
                .isNotEmpty()
                .extracting(CyclicArray::getItems)
                .allSatisfy(items -> assertThat(items).containsOnly(result));
    }

    @Test
    void cyclicList() {
        final CyclicList result = Instancio.create(CyclicList.class);

        assertThat(result.getItems())
                .isNotEmpty()
                .extracting(CyclicList::getItems)
                .allSatisfy(items -> assertThat(items).containsOnly(result));
    }

    @Test
    void cyclicKeyMap() {
        final CyclicKeyMap result = Instancio.create(CyclicKeyMap.class);

        assertThat(result.getMap().keySet())
                .isNotEmpty()
                .allSatisfy(key -> assertThat(key.getMap().keySet()).containsOnly(result));
    }

    @Test
    void cyclicValueMap() {
        final CyclicValueMap result = Instancio.create(CyclicValueMap.class);

        assertThat(result.getMap().values())
                .isNotEmpty()
                .allSatisfy(value -> assertThat(value.getMap().values()).containsOnly(result));
    }
}
