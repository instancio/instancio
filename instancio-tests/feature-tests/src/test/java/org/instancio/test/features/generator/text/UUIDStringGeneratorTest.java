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

package org.instancio.test.features.generator.text;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.arrays.TwoArraysOfItemString;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Objects;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.allStrings;
import static org.instancio.Select.root;
import static org.instancio.Select.scope;

@FeatureTag({Feature.GENERATE, Feature.UUID_STRING_GENERATOR})
@ExtendWith(InstancioExtension.class)
class UUIDStringGeneratorTest {

    @Test
    void uuidString() {
        final TwoArraysOfItemString result = Instancio.of(TwoArraysOfItemString.class)
                .generate(allStrings().within(scope(TwoArraysOfItemString.class, "array1")),
                        gen -> gen.text().uuid().withoutDashes().upperCase())
                .generate(allStrings().within(scope(TwoArraysOfItemString.class, "array2")),
                        gen -> gen.string().length(1))
                .create();

        assertThat(result.getArray1()).isNotEmpty().allSatisfy(item ->
                assertThat(item.getValue()).matches("^[A-F0-9]{32}$"));

        assertThat(result.getArray2()).isNotEmpty().allSatisfy(item ->
                assertThat(item.getValue()).hasSize(1));
    }

    @Test
    void nullable() {
        final Stream<String> results = Instancio.of(String.class)
                .generate(root(), gen -> gen.text().uuid().nullable())
                .stream()
                .limit(Constants.SAMPLE_SIZE_DD);

        assertThat(results)
                .containsNull()
                .anyMatch(Objects::nonNull);
    }
}