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
package org.instancio.test.features.ofmap;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.collections.maps.MapIntegerItemOfString;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.allStrings;
import static org.instancio.Select.root;
import static org.instancio.Select.scope;

@FeatureTag({Feature.OF_MAP, Feature.ROOT_SELECTOR})
@ExtendWith(InstancioExtension.class)
class OfMapSelectorsTest {

    @Test
    void selectRoot() {
        final int expectedSize = 1;
        final Map<UUID, Integer> results = Instancio.ofMap(UUID.class, Integer.class)
                .generate(root(), gen -> gen.map().size(expectedSize))
                .create();

        assertThat(results).hasSize(expectedSize);
    }

    @Test
    void selectAllMaps() {
        final Map<UUID, Integer> results = Instancio.ofMap(UUID.class, Integer.class)
                .set(all(Map.class), null)
                .create();

        assertThat(results).isNull();
    }

    @Test
    void selectAllMapsWithScope() {
        final Map<UUID, MapIntegerItemOfString> results = Instancio.ofMap(UUID.class, MapIntegerItemOfString.class)
                .set(all(Map.class).within(scope(MapIntegerItemOfString.class)), null)
                .create();

        assertThat(results.values())
                .extracting(MapIntegerItemOfString::getMap)
                .isNotEmpty()
                .containsOnlyNulls();
    }

    @Test
    void applySelectorsToElements() {
        final String prefix = "_";
        final Map<UUID, String> results = Instancio.ofMap(UUID.class, String.class)
                .generate(allStrings(), gen -> gen.string().prefix(prefix))
                .create();

        assertThat(results.values())
                .isNotEmpty()
                .allSatisfy(s -> assertThat(s).startsWith(prefix));
    }

    @Test
    void withElements() {
        final Map<String, Integer> results = Instancio.ofMap(String.class, Integer.class)
                .generate(root(), gen -> gen.map()
                        .with("foo", 1)
                        .with("bar", 2))
                .create();

        assertThat(results).hasSizeGreaterThan(2)
                .containsEntry("foo", 1)
                .containsEntry("bar", 2);
    }

    @Test
    void subtype() {
        final Map<String, Integer> results = Instancio.ofMap(String.class, Integer.class)
                .subtype(root(), TreeMap.class)
                .create();

        assertThat(results)
                .isNotEmpty()
                .isExactlyInstanceOf(TreeMap.class)
                .doesNotContainKey(null)
                .doesNotContainValue(null);
    }
}
