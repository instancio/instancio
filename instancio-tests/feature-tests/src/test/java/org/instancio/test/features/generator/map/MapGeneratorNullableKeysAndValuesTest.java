/*
 *  Copyright 2022 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.instancio.test.features.generator.map;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.collections.maps.MapIntegerString;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.tags.NonDeterministicTag;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;

@NonDeterministicTag
@FeatureTag({
        Feature.GENERATE,
        Feature.MAP_GENERATOR_NULLABLE_KEY,
        Feature.MAP_GENERATOR_NULLABLE_VALUE})
@ExtendWith(InstancioExtension.class)
class MapGeneratorNullableKeysAndValuesTest {

    private static final int MAP_SIZE = 1000;

    @Test
    void nullableKeys() {
        final MapIntegerString result = Instancio.of(MapIntegerString.class)
                .generate(all(Map.class), gen -> gen.map().size(MAP_SIZE).nullableKeys())
                .create();

        assertThat(result.getMap().keySet()).containsNull();
    }

    @Test
    void nullableValues() {
        final MapIntegerString result = Instancio.of(MapIntegerString.class)
                .generate(all(Map.class), gen -> gen.map().size(MAP_SIZE).nullableValues())
                .create();

        assertThat(result.getMap().values()).containsNull();
    }

    /**
     * The probability of generating a (null, null) entry is low since there's a higher chance
     * that a null key will be associated with a non-null value.
     */
    @Test
    @Disabled("Slow due to low odds of generating a (null, null) entry")
    void nullableKeysAndValues() {
        final MapIntegerString result = Instancio.of(MapIntegerString.class)
                .generate(all(Map.class), gen -> gen.map().size(MAP_SIZE * 10000).nullableKeys().nullableValues())
                .create();

        assertThat(result.getMap()).containsEntry(null, null);
    }

}