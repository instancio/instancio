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
package org.instancio.test.features.generator.util;

import org.instancio.Instancio;
import org.instancio.test.support.pojo.misc.OptionalDoubleHolder;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;

import java.util.OptionalDouble;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.allDoubles;
import static org.instancio.Select.scope;

@FeatureTag(Feature.GENERATOR)
class OptionalDoubleGeneratorTest {

    @Test
    void create() {
        final OptionalDouble result = Instancio.create(OptionalDouble.class);
        assertThat(result).isNotEmpty();
        assertThat(result.getAsDouble()).isPositive();
    }

    @Test
    void createCustom() {
        final OptionalDouble result = Instancio.of(OptionalDouble.class)
                .set(allDoubles(), -1.0)
                .create();

        assertThat(result).isNotEmpty();
        assertThat(result.getAsDouble()).isEqualTo(-1);
    }

    @Test
    void holder() {
        final OptionalDoubleHolder result = Instancio.of(OptionalDoubleHolder.class)
                .set(all(double.class).within(scope(OptionalDouble.class)), -1.0)
                .create();

        assertThat(result.getOptional()).isNotEmpty();
        assertThat(result.getOptional().getAsDouble()).isEqualTo(-1);
    }
}
