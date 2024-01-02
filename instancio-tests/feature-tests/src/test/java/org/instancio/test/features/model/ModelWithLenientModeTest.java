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
package org.instancio.test.features.model;

import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.allStrings;

@FeatureTag({Feature.MODEL, Feature.MODE})
class ModelWithLenientModeTest {

    @Test
    @DisplayName("Object created from a lenient model should not trigger unused selector error")
    void lenientModel() {
        final Model<Integer> model = Instancio.of(Integer.class)
                .lenient()
                .ignore(allStrings()) // unused selector
                .toModel();

        final Integer result = Instancio.create(model);
        assertThat(result).isNotNull();
    }
}