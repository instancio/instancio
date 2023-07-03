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
package org.instancio.test.features.generator.util;

import org.instancio.Instancio;
import org.instancio.test.support.pojo.misc.OptionalIntHolder;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.OptionalInt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.allInts;
import static org.instancio.Select.scope;

@FeatureTag(Feature.GENERATOR)
class OptionalIntGeneratorTest {

    @Test
    void create() {
        final OptionalInt result = Instancio.create(OptionalInt.class);
        assertThat(result).isNotEmpty();
        assertThat(result.getAsInt()).isPositive();
    }

    @Test
    void createCustom() {
        final OptionalInt result = Instancio.of(OptionalInt.class)
                .set(allInts(), -1)
                .create();

        assertThat(result).isNotEmpty();
        assertThat(result.getAsInt()).isEqualTo(-1);
    }

    @Test
    void holder() {
        final OptionalIntHolder result = Instancio.of(OptionalIntHolder.class)
                .set(all(int.class).within(scope(OptionalInt.class)), -1)
                .create();

        assertThat(result.getOptional()).isNotEmpty();
        assertThat(result.getOptional().getAsInt()).isEqualTo(-1);
    }

    @Test
    void listOfHolders() {
        final List<OptionalIntHolder> results = Instancio.ofList(OptionalIntHolder.class)
                .set(all(int.class).within(scope(OptionalInt.class)), -1)
                .create();

        assertThat(results).isNotEmpty().allSatisfy(result -> {
            assertThat(result.getOptional()).isNotEmpty();
            assertThat(result.getOptional().getAsInt()).isEqualTo(-1);
        });
    }
}
