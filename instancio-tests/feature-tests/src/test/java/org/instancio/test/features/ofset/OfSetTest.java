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
package org.instancio.test.features.ofset;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.person.Phone;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.instancio.Select.root;

/**
 * Minimal tests for generating Sets.
 * Most of the collection API is verified by {@link Feature#OF_LIST} tests.
 */
@FeatureTag(Feature.OF_SET)
@ExtendWith(InstancioExtension.class)
class OfSetTest {

    @Test
    void ofSetWithoutSize() {
        assertThat(Instancio.ofSet(Phone.class).create())
                .hasSizeBetween(Constants.MIN_SIZE, Constants.MAX_SIZE);
    }

    @Test
    void ofSet() {
        final Set<Phone> results = Instancio.ofSet(Phone.class).size(5)
                .subtype(root(), LinkedHashSet.class)
                .set(field(Phone::getCountryCode), "+1")
                .generate(field(Phone::getNumber), gen -> gen.string().digits().length(7))
                .create();

        assertThat(results)
                .hasSize(5)
                .isExactlyInstanceOf(LinkedHashSet.class)
                .allSatisfy(phone -> {
                    assertThat(phone.getCountryCode()).isEqualTo("+1");
                    assertThat(phone.getNumber()).containsOnlyDigits().hasSize(7);
                });
    }
}