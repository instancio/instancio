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
package org.instancio.test.features.fill;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.conditions.Conditions;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

@FeatureTag({Feature.WITH_UNIQUE, Feature.FILL})
@ExtendWith(InstancioExtension.class)
class FillWithUniqueTest {

    @Test
    @DisplayName("withUnique() is not be evaluated against initialised fields")
    void withUniqueIsNotEvaluatedAgainstInitialisedFields() {
        final List<StringHolder> list = Arrays.asList(
                new StringHolder("foo"),
                new StringHolder(),
                new StringHolder("foo"));

        Instancio.ofObject(list)
                .withUnique(field(StringHolder::getValue))
                .fill();

        final List<String> values = list.stream()
                .map(StringHolder::getValue)
                .collect(Collectors.toList());

        assertThat(values).first().isEqualTo("foo");
        assertThat(values).last().isEqualTo("foo");
        assertThat(values.get(1)).is(Conditions.RANDOM_STRING);
    }
}
