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
package org.instancio.test.features.ofmap;

import org.instancio.Instancio;
import org.instancio.Select;
import org.instancio.TargetSelector;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.person.Phone;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatObject;

@FeatureTag({Feature.OF_MAP, Feature.ON_COMPLETE})
@ExtendWith(InstancioExtension.class)
class OfMapOnCompleteTest {

    private static Stream<Arguments> selectors() {
        return Stream.of(
                Arguments.of(Select.root()),
                Arguments.of(Select.all(Map.class)));
    }

    @ParameterizedTest
    @MethodSource("selectors")
    void onComplete(final TargetSelector selector) {
        final int expectedSize = 3;
        final int[] callbackCount = {0};

        final Map<UUID, Phone> results = Instancio.ofMap(UUID.class, Phone.class)
                .size(expectedSize)
                .onComplete(selector, (Map<UUID, Phone> phones) -> {
                    callbackCount[0]++;
                    assertThat(phones.values())
                            .hasSize(expectedSize)
                            .allSatisfy(phone -> assertThatObject(phone).hasNoNullFieldsOrProperties());
                })
                .create();

        assertThat(callbackCount[0]).isOne();
        assertThat(results).isNotEmpty();
    }
}
