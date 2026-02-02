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
package org.instancio.test.features.filter;

import org.instancio.Instancio;
import org.instancio.TypeToken;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.Keys;
import org.instancio.test.support.conditions.Conditions;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.pojo.generics.basic.Item;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.allStrings;
import static org.instancio.Select.root;

@FeatureTag(Feature.FILTER)
@ExtendWith(InstancioExtension.class)
class FilterBasicTest {

    @Test
    void collectionElement() {
        final int expectedLength = 5;
        final List<String> results = Instancio.ofList(String.class)
                .filter(allStrings(), (String s) -> s.length() == expectedLength)
                .create();

        assertThat(results).isNotEmpty().allMatch(s -> s.length() == expectedLength);
    }

    @Test
    void pojo() {
        final int expectedSize = 100;
        final List<Item<StringHolder>> results = Instancio.ofList(new TypeToken<Item<StringHolder>>() {})
                .size(expectedSize)
                .generate(allStrings(), gen -> gen.oneOf("A", "B"))
                .filter(all(StringHolder.class), (StringHolder holder) -> holder.getValue().equals("B"))
                .create();

        assertThat(results)
                .hasSize(expectedSize)
                .allMatch(item -> item.getValue().getValue().equals("B"));
    }

    @RepeatedTest(5)
    void rootObject() {
        final Boolean result = Instancio.of(Boolean.class)
                .filter(root(), (Boolean val) -> val) // reject false
                .create();

        assertThat(result).isTrue();
    }

    @Test
    void whenFailOnMaxGenerationAttemptsReached_isFalse_shouldFallBackToRandomValue() {
        final String result = Instancio.of(String.class)
                .withSetting(Keys.FAIL_ON_MAX_GENERATION_ATTEMPTS_REACHED, false)
                .filter(root(), (String s) -> s.equals("will never match"))
                .create();

        assertThat(result).is(Conditions.RANDOM_STRING);
    }
}