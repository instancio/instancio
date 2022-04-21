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
import org.instancio.generator.util.MapGeneratorSpec;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.collections.maps.MapStringPerson;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Bindings.all;

@FeatureTag({
        Feature.MAP_GENERATOR_MIN_SIZE,
        Feature.MAP_GENERATOR_MAX_SIZE,
        Feature.MAP_GENERATOR_SIZE})
@ExtendWith(InstancioExtension.class)
class MapGeneratorSizeTest {

    // Longer string to minimise chance of test failure due to string collisions
    private static final int STRING_LENGTH = 15;

    private static final int EXPECTED_SIZE = 50;

    @WithSettings
    private static final Settings settings = Settings.create()
            .set(Keys.STRING_MIN_LENGTH, STRING_LENGTH)
            .set(Keys.STRING_MAX_LENGTH, STRING_LENGTH + 1);

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
