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
package org.instancio.test.features.generator.map;

import org.instancio.Instancio;
import org.instancio.TypeToken;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.collections.maps.MapIntegerString;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;

@FeatureTag({
        Feature.GENERATE,
        Feature.MAP_GENERATOR_WITH_ENTRIES
})
@ExtendWith(InstancioExtension.class)
class MapGeneratorWithEntriesTest {

    private static final Map<Integer, String> EXPECTED_ENTRIES = new HashMap<Integer, String>() {{
        put(-1, "foo");
        put(-2, "bar");
        put(-3, "baz");
    }};

    @Test
    void withClassContaining() {
        final MapIntegerString result = Instancio.of(MapIntegerString.class)
                .generate(all(Map.class), gen -> gen.map()
                        .with(-1, "foo")
                        .with(-2, "bar")
                        .with(-3, "baz"))
                .create();

        assertThat(result.getMap()).containsAllEntriesOf(EXPECTED_ENTRIES);
    }

    @Test
    void createMapDirectly() {
        final Map<Integer, String> result = Instancio.of(new TypeToken<Map<Integer, String>>() {})
                .generate(all(Map.class), gen -> gen.map()
                        .with(-1, "foo")
                        .with(-2, "bar")
                        .with(-3, "baz"))
                .create();

        assertThat(result).containsAllEntriesOf(EXPECTED_ENTRIES);
    }

    @Test
    void mapWithEntriesAndMaxSizeZero() {
        final MapIntegerString result = Instancio.of(MapIntegerString.class)
                .generate(all(Map.class), gen -> gen.map().maxSize(0)
                        .with(-1, "foo")
                        .with(-2, "bar")
                        .with(-3, "baz"))
                .create();

        assertThat(result.getMap()).containsAllEntriesOf(EXPECTED_ENTRIES);
    }

    @Test
    void shouldAllowNullKeyAndValue() {
        final Map<Integer, String> result = Instancio.of(new TypeToken<Map<Integer, String>>() {})
                .generate(all(Map.class), gen -> gen.map().with(null, null))
                .create();

        assertThat(result)
                .hasSizeGreaterThan(1)
                .containsEntry(null, null);
    }
}