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
package org.instancio.test.client.generator.map;

import org.instancio.Instancio;
import org.instancio.generator.util.MapGeneratorSpec;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.pojo.collections.maps.MapStringPerson;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Bindings.all;

@FeatureTag({
        Feature.MAP_GENERATOR_MIN_SIZE,
        Feature.MAP_GENERATOR_MAX_SIZE,
        Feature.MAP_GENERATOR_SIZE})
class MapGeneratorSizeTest {

    private static final int EXPECTED_SIZE = 50;

    @Test
    void size() {
        assertSize(spec -> spec.size(EXPECTED_SIZE), EXPECTED_SIZE);
    }

    @Test
    void sizeZero() {
        assertSize(spec -> spec.size(0), 0);
    }

    @Test
    void minSize() {
        assertSize(spec -> spec.minSize(EXPECTED_SIZE), EXPECTED_SIZE);
    }

    @Test
    void maxSize() {
        assertSize(spec -> spec.maxSize(1), 1);
    }

    private void assertSize(Function<MapGeneratorSpec<?, ?>, MapGeneratorSpec<?, ?>> fn, int expectedSize) {
        final MapStringPerson result = Instancio.of(MapStringPerson.class)
                .generate(all(Map.class), gen -> fn.apply(gen.map()))
                .create();

        assertThat(result.getMap()).hasSize(expectedSize);
    }
}
