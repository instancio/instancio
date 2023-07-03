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
import org.instancio.test.support.pojo.misc.OptionalLongHolder;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;

import java.util.OptionalLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.allLongs;
import static org.instancio.Select.scope;

@FeatureTag(Feature.GENERATOR)
class OptionalLongGeneratorTest {

    @Test
    void create() {
        final OptionalLong result = Instancio.create(OptionalLong.class);
        assertThat(result).isNotEmpty();
        assertThat(result.getAsLong()).isPositive();
    }

    @Test
    void createCustom() {
        final OptionalLong result = Instancio.of(OptionalLong.class)
                .set(allLongs(), -1L)
                .create();

        assertThat(result).isNotEmpty();
        assertThat(result.getAsLong()).isEqualTo(-1);
    }

    @Test
    void holder() {
        final OptionalLongHolder result = Instancio.of(OptionalLongHolder.class)
                .set(all(long.class).within(scope(OptionalLong.class)), -1L)
                .create();

        assertThat(result.getOptional()).isNotEmpty();
        assertThat(result.getOptional().getAsLong()).isEqualTo(-1);
    }
}
